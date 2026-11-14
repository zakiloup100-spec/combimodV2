package com.example.combimod.core;

import java.util.Random;

/**
 * Colis qui tombent du ciel. Chaque colis donne une petite somme et, avec une faible chance,
 * déclenche la roue de rareté : un tirage pondéré qui peut ne rien donner.
 */
public final class SkyDrops {

    /** Intervalle entre deux colis : entre 8 et 15 minutes (en ticks, 20 par seconde). */
    public static final int MIN_INTERVAL_TICKS = 20 * 60 * 8;
    public static final int MAX_INTERVAL_TICKS = 20 * 60 * 15;
    public static final long MONEY_MIN = 5;
    public static final long MONEY_MAX = 25;
    /** Chance qu'un colis déclenche la roue. */
    public static final double WHEEL_CHANCE = 0.10;

    /** Segments de la roue. NOTHING = la roue ne donne rien (le pari est perdu). */
    public enum Segment {
        NOTHING("Rien", null, 30),
        COMMON("Commun", Rarity.COMMON, 35),
        RARE("Rare", Rarity.RARE, 20),
        EPIC("Épique", Rarity.EPIC, 10),
        LEGENDARY("Légendaire", Rarity.LEGENDARY, 4),
        MYTHIC("Mythique", Rarity.MYTHIC, 1);

        public final String label;
        public final Rarity rarity;
        public final int weight;

        Segment(String label, Rarity rarity, int weight) {
            this.label = label;
            this.rarity = rarity;
            this.weight = weight;
        }
    }

    private SkyDrops() {}

    public static int totalWeight() {
        int t = 0;
        for (Segment s : Segment.values()) t += s.weight;
        return t;
    }

    public static int nextIntervalTicks(Random rng) {
        return MIN_INTERVAL_TICKS + rng.nextInt(MAX_INTERVAL_TICKS - MIN_INTERVAL_TICKS + 1);
    }

    public static long moneyReward(Random rng) {
        return MONEY_MIN + rng.nextInt((int) (MONEY_MAX - MONEY_MIN + 1));
    }

    public static boolean wheelTriggered(Random rng) {
        return rng.nextDouble() < WHEEL_CHANCE;
    }

    /** Tirage pondéré d'un segment de la roue. */
    public static Segment spin(Random rng) {
        int pick = rng.nextInt(totalWeight());
        for (Segment s : Segment.values()) {
            pick -= s.weight;
            if (pick < 0) return s;
        }
        return Segment.NOTHING;
    }
}
