package com.example.combimod.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Economy {

    private final Map<UUID, Long> balances = new HashMap<>();

    public long balance(UUID player) {
        return balances.getOrDefault(player, 0L);
    }

    public void add(UUID player, long amount) {
        balances.merge(player, amount, Long::sum);
    }

    public boolean take(UUID player, long amount) {
        long current = balance(player);
        if (current < amount) return false;
        balances.put(player, current - amount);
        return true;
    }

    /** Le joueur reçoit la moitié de la valeur d'achat, minimum 1. */
    public long sellPrice(ItemDef def) {
        return Math.max(1, def.price() / 2);
    }

    public Map<UUID, Long> snapshot() {
        return new HashMap<>(balances);
    }

    public void restore(UUID player, long amount) {
        balances.put(player, amount);
    }
}
