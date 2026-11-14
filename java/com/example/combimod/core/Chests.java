package com.example.combimod.core;

import java.util.*;

/** Coffres à trois niveaux, à usage unique. Le prix est calibré pour que l'ouverture ne rapporte pas d'argent à la revente. */
public final class Chests {

    public static final String[] NAMES = {"Coffre mystique", "Coffre épique", "Coffre légendaire"};
    /** Poids par rareté : COMMON, RARE, EPIC, LEGENDARY, MYTHIC. */
    private static final int[][] WEIGHTS = {
            {60, 28, 10, 2, 0},
            {0, 55, 33, 11, 1},
            {0, 0, 50, 40, 10}};
    private static final int[] MOB_WEIGHTS = {70, 22, 7, 1, 0};
    private static final int[] BOSS_WEIGHTS = {0, 50, 35, 13, 2};

    public static final long[] COST = {200, 450, 900};

    private Chests() {}

    public static int tiers() {
        return NAMES.length;
    }

    public static Rarity pickRarity(int[] weights, Random rng) {
        int total = 0;
        for (int w : weights) total += w;
        int pick = rng.nextInt(total);
        for (int i = 0; i < weights.length; i++) {
            pick -= weights[i];
            if (pick < 0) return Rarity.values()[i];
        }
        return Rarity.COMMON;
    }

    public static Rarity bossRarity(Random rng) {
        return pickRarity(BOSS_WEIGHTS, rng);
    }

    public static ItemDef roll(int tier, Random rng) {
        return rollWith(WEIGHTS[tier], rng);
    }

    /** Petit drop sur un monstre ordinaire. */
    public static ItemDef rollMob(Random rng) {
        return rollWith(MOB_WEIGHTS, rng);
    }

    private static ItemDef rollWith(int[] weights, Random rng) {
        Rarity r = pickRarity(weights, rng);
        List<ItemDef> pool = new ArrayList<>();
        for (ItemDef d : Items.SHOP.values()) {
            if (d.rarity() == r) pool.add(d);
        }
        // 40 % d'objet du catalogue si le choix est assez large, sinon objet généré
        if (pool.size() >= 3 && rng.nextDouble() < 0.4) return pool.get(rng.nextInt(pool.size()));
        return Generator.generate(rng, r, Set.of());
    }
}
