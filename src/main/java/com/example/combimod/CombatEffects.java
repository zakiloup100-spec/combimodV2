package com.example.combimod;

import com.example.combimod.core.Affix;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Effets de combat : dégâts bonus, vol de vie (attaquant) et épines (défenseur).
 * Limite de AFTER_DAMAGE : la valeur ne tient pas compte de la réduction d'armure.
 */
public final class CombatEffects {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    /** Empêche les dégâts bonus de redéclencher les effets en boucle. */
    private static boolean inCombo = false;

    private CombatEffects() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, source, baseDamage, damageTaken, blocked) -> {
                    if (inCombo || damageTaken <= 0) return;
                    inCombo = true;
                    try {
                        onHit(victim, source, damageTaken);
                    } finally {
                        inCombo = false;
                    }
                });
    }

    private static void onHit(LivingEntity victim, DamageSource source, float damageTaken) {
        if (!(victim.level() instanceof ServerLevel level)) return;
        Entity causer = source.getEntity();

        // Attaquant joueur, coup direct : dégâts bonus et vol de vie de l'arme en main
        if (causer instanceof ServerPlayer attacker && source.getDirectEntity() == attacker) {
            ItemStack weapon = attacker.getMainHandItem();

            double extra = ItemUtil.bonus(weapon, Affix.DAMAGE);
            if (extra > 0 && victim.isAlive()) {
                victim.hurtServer(level, source, (float) extra);
            }

            // Effets propres à l'arme (ex. Lenteur, Poison) sur les ennemis seulement
            com.example.combimod.core.ItemDef weaponDef = ItemUtil.defOf(weapon);
            if (weaponDef != null && victim instanceof LivingEntity target
                    && !TraitEffects.isFriend(attacker.getUUID(), target)) {
                TraitEffects.applyAll(target, com.example.combimod.core.Traits.of(weaponDef));
                VisualFx.burst(level, target.position().add(0, target.getBbHeight() / 2.0, 0),
                        VisualFx.colorOf(com.example.combimod.core.Traits.of(weaponDef)), 8, 0.3);
            }

            double steal = ItemUtil.bonus(weapon, Affix.LIFESTEAL);
            if (steal > 0) {
                attacker.heal((float) (damageTaken * steal));
            }
        }

        // Défenseur joueur : épines de l'armure renvoyées à l'attaquant vivant
        if (victim instanceof ServerPlayer defender
                && causer instanceof LivingEntity thornsTarget
                && thornsTarget != defender && thornsTarget.isAlive()) {
            double thorns = thornsOf(defender);
            if (thorns > 0) {
                thornsTarget.hurtServer(level, defender.damageSources().magic(), (float) thorns);
            }
        }
    }

    static double thornsOf(ServerPlayer player) {
        double total = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            total += ItemUtil.bonus(player.getItemBySlot(slot), Affix.THORNS);
        }
        return total;
    }
}
