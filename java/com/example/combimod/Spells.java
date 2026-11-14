package com.example.combimod;

import com.example.combimod.core.Affix;
import com.example.combimod.core.ItemDef;
import com.example.combimod.core.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import com.example.combimod.core.Traits;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Sort instantané : frappe la cible la plus proche dans le champ de vision. */
public final class Spells {

    private static final double RANGE = 16.0;
    private static final Map<UUID, Long> LAST_CAST = new HashMap<>();

    private Spells() {}

    public static InteractionResult cast(ServerLevel level, Player player, ItemStack stack) {
        ItemDef def = ItemUtil.defOf(stack);
        if (def == null || !def.tags().contains(Tag.MAGIC)) return InteractionResult.PASS;

        long now = level.getGameTime();
        Long last = LAST_CAST.get(player.getUUID());
        if (last != null && now - last < cooldownTicks(stack)) return InteractionResult.FAIL;
        if (!Mana.spend(player, com.example.combimod.core.ManaPool.SPELL_COST)) return InteractionResult.FAIL;
        LAST_CAST.put(player.getUUID(), now);

        // Dégâts = base 4 + puissance des sorts (déjà multipliée par la rareté)
        float damage = (float) Math.max(1.0, 4.0 + ItemUtil.bonus(stack, Affix.SPELL_POWER));

        Vec3 look = player.getLookAngle();
        Vec3 eye = player.getEyePosition();
        AABB area = player.getBoundingBox().inflate(RANGE);

        LivingEntity target = null;
        double best = Double.MAX_VALUE;
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive())) {
            Vec3 to = entity.position().add(0, entity.getBbHeight() / 2.0, 0).subtract(eye);
            double dist = to.length();
            if (dist == 0 || dist > RANGE) continue;
            if (look.dot(to.normalize()) < 0.9) continue; // hors du champ de vision
            if (entity instanceof ServerPlayer other && CombiMod.ALLIES.isAlly(player.getUUID(), other.getUUID())) continue; // pas de tir allié
            if (dist < best) {
                best = dist;
                target = entity;
            }
        }
        Set<Traits.Trait> traits = Traits.of(def);
        int rgb = VisualFx.colorOf(traits);
        Vec3 origin = eye.add(look.scale(0.5));
        if (target != null) {
            Vec3 aim = target.position().add(0, target.getBbHeight() / 2.0, 0);
            VisualFx.beam(level, origin, aim, rgb);
            VisualFx.burst(level, aim, rgb, 14, 0.4);
            target.hurtServer(level, player.damageSources().playerAttack(player), damage);
            if (!TraitEffects.isFriend(player.getUUID(), target)) {
                TraitEffects.applyAll(target, traits);
            }
        } else {
            VisualFx.beam(level, origin, origin.add(look.scale(8)), rgb);
        }
        VisualFx.sound(level, origin, SoundEvents.EVOKER_CAST_SPELL, 1.2f);
        return InteractionResult.SUCCESS;
    }

    public static void reset() {
        LAST_CAST.clear();
    }

    /** Recharge réduite par la puissance des sorts, minimum 8 ticks. */
    static long cooldownTicks(ItemStack stack) {
        double power = ItemUtil.bonus(stack, Affix.SPELL_POWER);
        return Math.max(8, (long) (30 - power));
    }
}
