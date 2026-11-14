package com.example.combimod;

import com.example.combimod.core.Affix;
import com.example.combimod.core.Ids;
import com.example.combimod.core.ItemDef;
import com.example.combimod.core.Modifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Traduit les affixes en vrais attributs vanilla (vie max, vitesse d'attaque, armure).
 * Chaque objet a ses propres identifiants de modificateur, pour que deux objets différents se cumulent.
 */
public final class AttributeStats {

    private AttributeStats() {}

    public static void apply(ItemStack stack, ItemDef def) {
        boolean isArmor = ModArmor.itemFor(def.id()) != null;
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        boolean any = false;

        for (Modifier m : def.modifiers()) {
            double value = m.value() * def.rarity().power;
            if (m.affix() == Affix.MAX_HEALTH) {
                builder.add(Attributes.MAX_HEALTH, modifier(def, "max_health", value), EquipmentSlotGroup.ANY);
                any = true;
            } else if (m.affix() == Affix.ATTACK_SPEED) {
                builder.add(Attributes.ATTACK_SPEED, modifier(def, "attack_speed", value), EquipmentSlotGroup.MAINHAND);
                any = true;
            } else if (m.affix() == Affix.DEFENSE && !isArmor) {
                // Les pièces d'armure ont déjà leur défense dans leur matériau : pas de double comptage
                builder.add(Attributes.ARMOR, modifier(def, "armor", value), EquipmentSlotGroup.ANY);
                any = true;
            }
        }

        if (any) {
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
        }
    }

    private static AttributeModifier modifier(ItemDef def, String name, double amount) {
        return new AttributeModifier(
                Identifier.fromNamespaceAndPath(CombiMod.MOD_ID, "gear/" + Ids.hash(def.id()) + "/" + name),
                amount,
                AttributeModifier.Operation.ADD_VALUE);
    }
}
