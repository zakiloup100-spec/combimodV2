package com.example.combimod.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Réserve de mana par joueur. Le maximum et la régénération viennent des objets équipés
 * (affixe MANA_REGEN). Un joueur commence plein. Le mana est en mémoire : il est vidé à l'arrêt du serveur.
 */
public final class ManaPool {

    public static final double BASE_MAX = 60;
    public static final double MAX_PER_REGEN = 10;
    public static final double BASE_REGEN = 2;
    public static final double SPELL_COST = 8;
    public static final double SUMMON_COST = 15;

    private final Map<UUID, Double> stored = new HashMap<>();

    public static double maxFor(double regenBonus) {
        return BASE_MAX + MAX_PER_REGEN * Math.max(0, regenBonus);
    }

    public static double regenFor(double regenBonus) {
        return BASE_REGEN + Math.max(0, regenBonus);
    }

    /** Mana actuel, borné au maximum courant (si on a retiré un objet, la réserve baisse). */
    public double get(UUID player, double max) {
        return Math.min(max, stored.getOrDefault(player, max));
    }

    /** Dépense le coût si possible. Renvoie false sans rien dépenser sinon. */
    public boolean consume(UUID player, double max, double cost) {
        double current = get(player, max);
        if (current < cost) return false;
        stored.put(player, current - cost);
        return true;
    }

    public void regen(UUID player, double max, double amount) {
        stored.put(player, Math.min(max, get(player, max) + amount));
    }

    public void reset() {
        stored.clear();
    }
}
