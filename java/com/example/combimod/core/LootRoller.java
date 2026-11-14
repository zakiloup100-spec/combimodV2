package com.example.combimod.core;

import java.util.*;

public final class LootRoller {

    private final Map<Rarity, List<ItemDef>> byRarity = new EnumMap<>(Rarity.class);
    private final Random rng;

    public LootRoller(Collection<ItemDef> items, Random rng) {
        this.rng = rng;
        for (ItemDef d : items) {
            byRarity.computeIfAbsent(d.rarity(), k -> new ArrayList<>()).add(d);
        }
    }

    /** Tire d'abord une rareté pondérée, puis un item de cette rareté. */
    public Optional<ItemDef> roll() {
        int total = 0;
        for (Rarity r : byRarity.keySet()) total += r.weight;
        if (total == 0) return Optional.empty();

        int pick = rng.nextInt(total);
        for (Map.Entry<Rarity, List<ItemDef>> e : byRarity.entrySet()) {
            pick -= e.getKey().weight;
            if (pick < 0) {
                List<ItemDef> list = e.getValue();
                return Optional.of(list.get(rng.nextInt(list.size())));
            }
        }
        return Optional.empty();
    }
}
