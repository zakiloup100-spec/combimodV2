package com.example.combimod;

import com.example.combimod.core.ItemDef;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.LinkedHashMap;
import java.util.Map;

/** Matériaux d'armure custom. Défense bien au-dessus du vanilla. */
public final class ModArmor {

    private static final Map<String, Item> ITEMS = new LinkedHashMap<>();

    // Durabilité de base par matériau (valeur vanilla : cuir 5, fer 15, diamant 33)
    public static final int BASE_CUIR = 5;
    public static final int BASE_MAILLE = 15;
    public static final int BASE_ZEUS = 40;

    public static final ResourceKey<EquipmentAsset> CUIR_KEY = assetKey("cuir_runique");
    public static final ResourceKey<EquipmentAsset> MAILLE_KEY = assetKey("maille_runique");
    public static final ResourceKey<EquipmentAsset> ZEUS_KEY = assetKey("zeus");

    public static final TagKey<Item> REPAIR_CUIR = repairTag("repairs_cuir_runique");
    public static final TagKey<Item> REPAIR_MAILLE = repairTag("repairs_maille_runique");
    public static final TagKey<Item> REPAIR_ZEUS = repairTag("repairs_zeus");

    public static final ArmorMaterial CUIR = new ArmorMaterial(BASE_CUIR,
            Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 5, ArmorType.LEGGINGS, 4, ArmorType.BOOTS, 3),
            15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, REPAIR_CUIR, CUIR_KEY);

    public static final ArmorMaterial MAILLE = new ArmorMaterial(BASE_MAILLE,
            Map.of(ArmorType.HELMET, 6, ArmorType.CHESTPLATE, 10, ArmorType.LEGGINGS, 8, ArmorType.BOOTS, 5),
            12, SoundEvents.ARMOR_EQUIP_CHAIN, 1.0f, 0.0f, REPAIR_MAILLE, MAILLE_KEY);

    public static final ArmorMaterial ZEUS = new ArmorMaterial(BASE_ZEUS,
            Map.of(ArmorType.HELMET, 10, ArmorType.CHESTPLATE, 16, ArmorType.LEGGINGS, 14, ArmorType.BOOTS, 10),
            20, SoundEvents.ARMOR_EQUIP_GOLD, 3.0f, 0.1f, REPAIR_ZEUS, ZEUS_KEY);

    private ModArmor() {}

    public static void init() {
        for (ItemDef def : com.example.combimod.core.ArmorDefs.ALL) {
            String id = def.id();
            ArmorType type = typeOf(id);
            if (id.startsWith("cuir_")) piece(id, CUIR, type, BASE_CUIR);
            else if (id.startsWith("maille_")) piece(id, MAILLE, type, BASE_MAILLE);
            else if (id.startsWith("zeus_")) piece(id, ZEUS, type, BASE_ZEUS);
        }
    }

    private static void piece(String id, ArmorMaterial material, ArmorType type, int baseDurability) {
        Item item = ModItems.register(id, CombiItem::new,
                new Item.Properties()
                        .humanoidArmor(material, type)
                        .durability(type.getDurability(baseDurability))
                        .component(ModComponents.ITEM_DEF, id));
        ITEMS.put(id, item);
    }

    /** L'item physique d'une pièce d'armure, ou null si ce n'est pas une armure. */
    public static Item itemFor(String id) {
        return ITEMS.get(id);
    }

    private static ArmorType typeOf(String id) {
        if (id.endsWith("_casque")) return ArmorType.HELMET;
        if (id.endsWith("_plastron") || id.endsWith("_cuirasse")) return ArmorType.CHESTPLATE;
        if (id.endsWith("_pantalon")) return ArmorType.LEGGINGS;
        return ArmorType.BOOTS;
    }

    private static ResourceKey<EquipmentAsset> assetKey(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(CombiMod.MOD_ID, name));
    }

    private static TagKey<Item> repairTag(String name) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(CombiMod.MOD_ID, name));
    }
}
