package com.example.combimod.core;

import java.util.*;

/** Suivi des objets découverts par joueur : catalogue, objets générés (par combinaison) et fusions. */
public final class Codex {

    public record Counts(int base, int generated, int fusions) {}

    private final Map<UUID, Set<String>> found = new HashMap<>();

    /** Clé de découverte : la rareté est ignorée pour les objets générés. */
    public static String keyOf(ItemDef d) {
        String id = d.id();
        if (id.startsWith("g:")) {
            String[] p = id.split(":");
            if (p.length >= 4) return p[0] + ":" + p[1] + ":" + p[2] + ":" + p[3];
        }
        return id;
    }

    /** Renvoie true si c'est une nouvelle découverte. */
    public boolean discover(UUID player, ItemDef def) {
        return found.computeIfAbsent(player, k -> new HashSet<>()).add(keyOf(def));
    }

    public boolean has(UUID player, String key) {
        return found.getOrDefault(player, Set.of()).contains(key);
    }

    public Counts counts(UUID player) {
        int base = 0;
        int gen = 0;
        int fus = 0;
        for (String k : found.getOrDefault(player, Set.of())) {
            if (Items.BASE.containsKey(k)) base++;
            else if (k.startsWith("g:")) gen++;
            else if (k.startsWith("f:")) fus++;
        }
        return new Counts(base, gen, fus);
    }

    public Map<UUID, Set<String>> snapshot() {
        Map<UUID, Set<String>> copy = new HashMap<>();
        found.forEach((k, v) -> copy.put(k, new HashSet<>(v)));
        return copy;
    }

    public void restore(UUID player, Collection<String> keys) {
        found.computeIfAbsent(player, k -> new HashSet<>()).addAll(keys);
    }
}
