package com.example.combimod.core;

import java.util.*;

/**
 * Relance les stats d'un objet : même rareté, même orientation (tags), nouvelles stats.
 * Le prix dépend de la rareté et non de l'objet, pour qu'on ne puisse pas gagner d'argent en relançant des objets bon marché.
 */
public final class Reforge {

    private static final long[] AVERAGE = new long[Rarity.values().length];
    private static final Set<String> ARMOR_IDS = new HashSet<>();

    static {
        Random rng = new Random(12345);
        int n = 4000;
        for (Rarity r : Rarity.values()) {
            long sum = 0;
            for (int i = 0; i < n; i++) sum += Generator.generate(rng, r, Set.of()).price();
            AVERAGE[r.ordinal()] = sum / n;
        }
        for (ItemDef d : ArmorDefs.ALL) ARMOR_IDS.add(d.id());
    }

    private Reforge() {}

    /** Les armures (stats du matériau) et les objets de recette ne se relancent pas. */
    public static boolean allowed(ItemDef d) {
        return !ARMOR_IDS.contains(d.id()) && !Items.RECIPE_ONLY.contains(d.id());
    }

    public static long cost(ItemDef d) {
        return Math.max(10, Math.round(AVERAGE[d.rarity().ordinal()] * 0.9));
    }

    public static ItemDef reroll(ItemDef d, Random rng) {
        return Generator.generate(rng, d.rarity(), d.tags());
    }
}
