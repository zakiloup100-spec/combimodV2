package com.example.combimod;

import com.example.combimod.core.Affix;
import com.example.combimod.core.ItemDef;
import com.example.combimod.core.Rarity;
import com.example.combimod.core.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Capacités actives au clic droit, selon les types de l'objet (sans classes) :
 * Soin = soigne les alliés proches, Soutien = vitesse et force, Défense = résistance et absorption.
 */
public final class Abilities {

    private static final Map<UUID, Long> LAST_USE = new HashMap<>();

    private Abilities() {}

    public static void reset() {
        LAST_USE.clear();
    }

    /** Recharge : 20 s pour un objet commun, jusqu'à 10 s pour un mythique. */
    static long cooldownTicks(ItemDef def) {
        return Math.max(200, 400 - 50L * def.rarity().ordinal());
    }

    static final double RANGE = 8.0;

    /** Le lanceur, plus les alliés connectés dans le même monde et à portée. */
    static List<ServerPlayer> affected(ServerLevel level, ServerPlayer caster) {
        List<ServerPlayer> out = new ArrayList<>();
        out.add(caster);
        for (ServerPlayer other : level.players()) {
            if (other == caster) continue;
            if (!CombiMod.ALLIES.isAlly(caster.getUUID(), other.getUUID())) continue;
            if (other.distanceToSqr(caster) > RANGE * RANGE) continue;
            out.add(other);
        }
        return out;
    }

    public static InteractionResult use(ServerLevel level, ServerPlayer player, ItemStack stack) {
        ItemDef def = ItemUtil.defOf(stack);
        if (def == null) return InteractionResult.PASS;
        boolean heal = def.tags().contains(Tag.HEAL);
        boolean support = def.tags().contains(Tag.SUPPORT);
        boolean defense = def.tags().contains(Tag.DEFENSE);
        if (!heal && !support && !defense) return InteractionResult.PASS;

        long now = level.getGameTime();
        Long last = LAST_USE.get(player.getUUID());
        if (last != null && now - last < cooldownTicks(def)) return InteractionResult.FAIL;
        LAST_USE.put(player.getUUID(), now);

        // Soin : 2 + 3 x soin par seconde de l'objet (rareté incluse)
        float healAmount = (float) (2.0 + 3.0 * ItemUtil.bonus(stack, Affix.HEAL_PER_SECOND));
        // Les objets légendaires et mythiques donnent un palier de plus
        int tier = def.rarity().ordinal() >= Rarity.LEGENDARY.ordinal() ? 1 : 0;

        // Le lanceur et ses alliés proches (invitation mutuelle, voir /ally). Jamais les autres joueurs.
        for (Player ally : affected(level, player)) {
            if (heal) {
                ally.heal(healAmount);
                VisualFx.burst(level, ally.position().add(0, 1, 0), VisualFx.HEAL, 12, 0.5);
                VisualFx.sound(level, ally.position(), SoundEvents.EXPERIENCE_ORB_PICKUP, 1.4f);
            }
            if (support) {
                ally.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, tier));
                ally.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 200, tier));
            }
            if (defense) {
                ally.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 120, tier));
                ally.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, tier));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
