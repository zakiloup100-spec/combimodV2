package com.example.combimod;

import com.example.combimod.core.ArmorDefs;
import com.example.combimod.core.ItemDef;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Set;

/** Bonus de set : appliqué quand les 4 pièces d'un set sont portées. */
public final class ArmorSets {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private ArmorSets() {}

    public static void apply(ServerPlayer player) {
        // Durée de 60 ticks : rafraîchie à chaque appel (toutes les secondes)
        if (countWorn(player, ArmorDefs.ZEUS_SET) == 4) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, 1));
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 60, 1));
        }
        if (countWorn(player, ArmorDefs.MAILLE_SET) == 4) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 0));
        }
    }

    static int countWorn(ServerPlayer player, Set<String> setIds) {
        int count = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemDef def = ItemUtil.defOf(player.getItemBySlot(slot));
            if (def != null && setIds.contains(def.id())) count++;
        }
        return count;
    }
}
