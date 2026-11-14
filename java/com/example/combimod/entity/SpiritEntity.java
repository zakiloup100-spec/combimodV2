package com.example.combimod.entity;

import com.example.combimod.TraitEffects;
import com.example.combimod.VisualFx;
import net.minecraft.sounds.SoundEvents;
import com.example.combimod.core.Spirits;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

/**
 * Esprit invoqué. Passif avec son invocateur et ses alliés (il ne les prend jamais pour cible),
 * hostile aux monstres et aux joueurs qui ne sont pas alliés. Toutes les 3 secondes, il inflige
 * ses effets à tous les ennemis proches.
 */
public class SpiritEntity extends Zombie {

    private static final int PULSE_TICKS = 60;
    private static final double RANGE = 6.0;

    private final Spirits.Kind kind;
    private final UUID owner;

    public SpiritEntity(EntityType<? extends Zombie> type, Level level, Spirits.Kind kind, UUID owner) {
        super(type, level);
        this.kind = kind;
        this.owner = owner;
        setCustomName(Component.literal(kind.label).withStyle(color(kind)));
        setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes().add(Attributes.MAX_HEALTH, 20.0);
    }

    static ChatFormatting color(Spirits.Kind kind) {
        return switch (kind) {
            case DARK -> ChatFormatting.DARK_PURPLE;
            case GUARD -> ChatFormatting.GOLD;
            case SPIRIT -> ChatFormatting.AQUA;
        };
    }

    @Override
    public void setTarget(LivingEntity target) {
        // Passif avec les alliés : on ne les prend jamais pour cible
        if (TraitEffects.isFriend(owner, target)) return;
        super.setTarget(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel serverLevel) || !isAlive()) return;
        if (tickCount % PULSE_TICKS != 0) return;

        VisualFx.ring(serverLevel, position(), 1.2, VisualFx.colorOf(kind), 16);
        VisualFx.sound(serverLevel, position(), SoundEvents.SOUL_ESCAPE, 0.9f);
        AABB area = getBoundingBox().inflate(RANGE);
        for (LivingEntity enemy : serverLevel.getEntitiesOfClass(LivingEntity.class, area, this::isEnemy)) {
            for (var trait : kind.pulse) TraitEffects.apply(enemy, trait);
        }
    }

    private boolean isEnemy(LivingEntity e) {
        if (e == this || !e.isAlive()) return false;
        if (e instanceof SpiritEntity other) return !other.owner.equals(owner);
        if (e instanceof Player) return !TraitEffects.isFriend(owner, e);
        return e instanceof Monster;
    }
}
