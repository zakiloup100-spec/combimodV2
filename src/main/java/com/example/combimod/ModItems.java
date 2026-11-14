package com.example.combimod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public final class ModItems {

    /** Item physique des objets sans modèle propre. Nom et stats viennent du composant. */
    public static final Item COMBI_ITEM = register("combi_item", CombiItem::new,
            new Item.Properties().stacksTo(1).component(ModComponents.ITEM_DEF, ""));

    private ModItems() {}

    public static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties settings) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(CombiMod.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(settings.setId(key)));
    }

    /** Item physique à donner pour un id : l'armure si c'en est une, sinon l'item générique. */
    public static Item itemFor(String id) {
        Item armor = ModArmor.itemFor(id);
        return armor != null ? armor : COMBI_ITEM;
    }

    public static void init() {}
}
