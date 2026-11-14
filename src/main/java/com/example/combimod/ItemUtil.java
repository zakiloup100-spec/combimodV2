package com.example.combimod;

import com.example.combimod.core.Affix;
import com.example.combimod.core.ItemCodec;
import com.example.combimod.core.ItemDef;
import com.example.combimod.core.Items;
import com.example.combimod.core.Modifier;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ItemUtil {

    private static final Map<String, ItemDef> CACHE = new ConcurrentHashMap<>();

    private ItemUtil() {}

    /**
     * L'ItemDef d'un stack, ou null si ce n'est pas un objet du mod.
     * Le composant contient soit l'id d'un objet du catalogue, soit la définition complète
     * (objets générés et fusions) : elle voyage donc avec l'objet, jusqu'au client.
     */
    public static ItemDef defOf(ItemStack stack) {
        if (!(stack.getItem() instanceof CombiItem) || !stack.has(ModComponents.ITEM_DEF)) return null;
        String value = stack.get(ModComponents.ITEM_DEF);
        if (value == null || value.isEmpty()) return null;
        if (value.indexOf('|') < 0) return Items.ALL.get(value);

        ItemDef cached = CACHE.get(value);
        if (cached != null) return cached;
        ItemDef decoded = ItemCodec.decode(value);
        if (decoded != null) {
            if (CACHE.size() > 4000) CACHE.clear();
            CACHE.put(value, decoded);
        }
        return decoded;
    }

    /** Crée le stack : l'item physique (armure ou générique), le composant et les attributs. */
    public static ItemStack makeStack(ItemDef def) {
        ItemStack stack = new ItemStack(ModItems.itemFor(def.id()));
        stack.set(ModComponents.ITEM_DEF, Items.BASE.containsKey(def.id()) ? def.id() : ItemCodec.encode(def));
        AttributeStats.apply(stack, def);
        return stack;
    }

    /** Somme d'un modificateur, multipliée par la rareté de l'item. */
    public static double bonus(ItemStack stack, Affix affix) {
        ItemDef def = defOf(stack);
        if (def == null) return 0;
        double total = 0;
        for (Modifier m : def.modifiers()) {
            if (m.affix() == affix) total += m.value();
        }
        return total * def.rarity().power;
    }
}
