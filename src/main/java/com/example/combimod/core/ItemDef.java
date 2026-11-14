package com.example.combimod.core;

import java.util.List;
import java.util.Set;

public record ItemDef(
        String id,
        String name,
        Set<Tag> tags,
        List<Modifier> modifiers,
        long price,
        Rarity rarity
) {}
