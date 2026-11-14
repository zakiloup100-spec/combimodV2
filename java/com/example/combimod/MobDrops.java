package com.example.combimod;

import com.example.combimod.core.Chests;
import com.example.combimod.core.Coins;
import com.example.combimod.entity.BossZombie;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

import java.util.Random;

/** Pièces et drops : petits sur les monstres, gros sur les boss. */
public final class MobDrops {

    private static final Random RNG = new Random();
    private static final double MOB_DROP_CHANCE = 0.03;
    private static final double BOSS_BONUS_DROP_CHANCE = 0.25;

    private MobDrops() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((LivingEntity entity, DamageSource source) -> {
            Entity killer = source.getEntity();
            if (!(killer instanceof ServerPlayer player)) return;

            String pool = bossPool(entity);
            if (pool != null) {
                CombiMod.BOSSES.rollDrop(pool).ifPresent(d -> Rewards.give(player, d));
                if (RNG.nextDouble() < BOSS_BONUS_DROP_CHANCE) {
                    CombiMod.BOSSES.rollDrop(pool).ifPresent(d -> Rewards.give(player, d));
                }
                Rewards.coins(player, Coins.boss(RNG));
            } else if (entity instanceof Monster) {
                Rewards.coins(player, Coins.mob(RNG));
                if (RNG.nextDouble() < MOB_DROP_CHANCE) {
                    Rewards.give(player, Chests.rollMob(RNG));
                }
            }
        });
    }

    /** Associe un boss à sa table de drop. */
    static String bossPool(LivingEntity entity) {
        if (entity instanceof BossZombie zombie) {
            return switch (zombie.power()) {
                case BLAST, LEVITATE -> "boss_magie";
                case FREEZE -> "boss_soin";
                case LEECH -> "boss_melee";
                case POISON, WITHER -> "boss_ombre";
                case BLIND, WEAKEN -> "boss_meute";
            };
        }
        EntityType<?> type = entity.getType();
        if (type == EntityType.WITHER) return "boss_magie";
        if (type == EntityType.ENDER_DRAGON) return "boss_melee";
        if (type == EntityType.ELDER_GUARDIAN) return "boss_soin";
        return null;
    }
}
