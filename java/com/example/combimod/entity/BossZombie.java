package com.example.combimod.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Zombie boss : un pouvoir toutes les 5 secondes sur les joueurs proches, et une barre de boss
 * colorée (nom + pouvoir + vie) visible quand on s'approche.
 */
public class BossZombie extends Zombie {

    public enum BossPower {
        BLAST("Zombie Explosif", "repousse", ChatFormatting.RED, BossEvent.BossBarColor.RED),
        FREEZE("Zombie Glacé", "ralentit", ChatFormatting.AQUA, BossEvent.BossBarColor.BLUE),
        LEECH("Zombie Vampire", "draine la vie", ChatFormatting.DARK_RED, BossEvent.BossBarColor.PINK),
        POISON("Zombie Venimeux", "empoisonne", ChatFormatting.GREEN, BossEvent.BossBarColor.GREEN),
        BLIND("Zombie Aveuglant", "aveugle", ChatFormatting.GRAY, BossEvent.BossBarColor.WHITE),
        WITHER("Zombie Flétrisseur", "flétrit", ChatFormatting.DARK_PURPLE, BossEvent.BossBarColor.PURPLE),
        WEAKEN("Zombie Affaiblisseur", "affaiblit", ChatFormatting.YELLOW, BossEvent.BossBarColor.YELLOW),
        LEVITATE("Zombie Lévitant", "fait léviter", ChatFormatting.LIGHT_PURPLE, BossEvent.BossBarColor.BLUE);

        public final String label;
        public final String description;
        public final ChatFormatting color;
        public final BossEvent.BossBarColor barColor;

        BossPower(String label, String description, ChatFormatting color, BossEvent.BossBarColor barColor) {
            this.label = label;
            this.description = description;
            this.color = color;
            this.barColor = barColor;
        }
    }

    private static final int PULSE_INTERVAL = 100; // 5 secondes
    private static final double RANGE = 6.0;
    /** Distance à partir de laquelle la barre de boss apparaît. */
    private static final double BAR_RANGE = 24.0;

    /** Boss actifs côté serveur, pour nettoyer leur barre quand ils meurent ou disparaissent. */
    private static final List<BossZombie> ACTIVE = new ArrayList<>();

    private final BossPower power;
    private final ServerBossEvent bossBar;

    public BossZombie(EntityType<? extends Zombie> type, Level level, BossPower power) {
        super(type, level);
        this.power = power;
        setCustomName(Component.literal(power.label).withStyle(power.color));
        setCustomNameVisible(true);
        this.bossBar = new ServerBossEvent(
                Component.literal(power.label + " : " + power.description).withStyle(power.color),
                power.barColor,
                BossEvent.BossBarOverlay.PROGRESS);
        if (!level.isClientSide()) ACTIVE.add(this);
    }

    public BossPower power() {
        return power;
    }

    public static AttributeSupplier.Builder createAttributes(BossPower power) {
        AttributeSupplier.Builder builder = Zombie.createAttributes().add(Attributes.MAX_HEALTH, 60.0);
        switch (power) {
            case BLAST -> builder.add(Attributes.MAX_HEALTH, 80.0);
            case LEECH -> builder.add(Attributes.ATTACK_DAMAGE, 6.0);
            case WITHER -> builder.add(Attributes.MAX_HEALTH, 90.0).add(Attributes.ATTACK_DAMAGE, 5.0);
            default -> { }
        }
        return builder;
    }

    /** À l'arrêt du serveur : retire toutes les barres et vide la liste des boss actifs. */
    public static void reset() {
        for (BossZombie boss : ACTIVE) boss.bossBar.removeAllPlayers();
        ACTIVE.clear();
    }

    /** À appeler régulièrement côté serveur : retire la barre des boss morts ou déchargés. */
    public static void cleanup() {
        Iterator<BossZombie> it = ACTIVE.iterator();
        while (it.hasNext()) {
            BossZombie boss = it.next();
            if (boss.isRemoved() || !boss.isAlive()) {
                boss.bossBar.removeAllPlayers();
                it.remove();
            }
        }
    }

    private void updateBar(ServerLevel serverLevel) {
        for (ServerPlayer p : serverLevel.players()) {
            boolean near = distanceToSqr(p) < BAR_RANGE * BAR_RANGE;
            boolean shown = bossBar.getPlayers().contains(p);
            if (near && !shown) bossBar.addPlayer(p);
            else if (!near && shown) bossBar.removePlayer(p);
        }
        // Joueurs partis dans une autre dimension ou déconnectés
        for (ServerPlayer p : new ArrayList<>(bossBar.getPlayers())) {
            if (!serverLevel.players().contains(p)) bossBar.removePlayer(p);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel serverLevel)) return;
        if (!isAlive()) {
            bossBar.removeAllPlayers();
            return;
        }

        if (tickCount % 5 == 0) {
            bossBar.setProgress(Math.max(0.0f, Math.min(1.0f, getHealth() / getMaxHealth())));
        }
        if (tickCount % 10 == 0) updateBar(serverLevel);
        if (tickCount % PULSE_INTERVAL != 0) return;

        AABB area = getBoundingBox().inflate(RANGE);
        for (Player player : serverLevel.getEntitiesOfClass(Player.class, area)) {
            switch (power) {
                case BLAST -> player.knockback(1.2, getX() - player.getX(), getZ() - player.getZ());
                case FREEZE -> player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
                case LEECH -> {
                    player.hurtServer(serverLevel, damageSources().mobAttack(this), 2.0f);
                    heal(2.0f);
                }
                case POISON -> player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                case BLIND -> player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0));
                case WITHER -> player.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 0));
                case WEAKEN -> player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
                case LEVITATE -> player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 0));
            }
        }
    }
}
