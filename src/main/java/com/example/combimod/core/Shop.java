package com.example.combimod.core;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class Shop {

    private final Map<String, ItemDef> catalog;

    public Shop(Map<String, ItemDef> catalog) {
        this.catalog = catalog;
    }

    public Map<String, ItemDef> catalog() {
        return catalog;
    }

    public Optional<ItemDef> buy(UUID player, String id, Economy economy) {
        ItemDef def = catalog.get(id);
        if (def == null) return Optional.empty();
        return economy.take(player, def.price()) ? Optional.of(def) : Optional.empty();
    }
}
