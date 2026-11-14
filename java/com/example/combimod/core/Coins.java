package com.example.combimod.core;

import java.util.Random;

/** Pièces gagnées en tuant. */
public final class Coins {

    private Coins() {}

    public static long mob(Random rng) {
        return 2 + rng.nextInt(5);
    }

    public static long boss(Random rng) {
        return 150 + rng.nextInt(151);
    }
}
