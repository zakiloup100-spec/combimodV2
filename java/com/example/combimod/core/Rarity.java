package com.example.combimod.core;

/** weight : fréquence dans le loot. power : multiplicateur appliqué aux stats et aux sorts. */
public enum Rarity {
    COMMON(60, 1.0),
    RARE(25, 1.35),
    EPIC(10, 1.8),
    LEGENDARY(4, 2.5),
    MYTHIC(1, 3.5);

    public final int weight;
    public final double power;

    Rarity(int weight, double power) {
        this.weight = weight;
        this.power = power;
    }
}
