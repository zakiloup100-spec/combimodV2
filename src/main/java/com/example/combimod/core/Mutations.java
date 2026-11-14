package com.example.combimod.core;

import java.util.List;
import java.util.Random;

/** Petits bonus « surprise » ajoutés aux fusions et aux objets légendaires. Pas de vie max, pour garder les malus lisibles. */
public final class Mutations {

    public static final List<Modifier> LIST = List.of(
            new Modifier(Affix.DAMAGE, 2),
            new Modifier(Affix.ATTACK_SPEED, 0.15),
            new Modifier(Affix.LIFESTEAL, 0.04),
            new Modifier(Affix.HEAL_PER_SECOND, 0.6),
            new Modifier(Affix.MANA_REGEN, 1),
            new Modifier(Affix.SPELL_POWER, 2),
            new Modifier(Affix.SUMMON_COUNT, 1),
            new Modifier(Affix.SUMMON_DAMAGE, 1.5),
            new Modifier(Affix.THORNS, 1.5),
            new Modifier(Affix.DEFENSE, 2),
            new Modifier(Affix.DAMAGE, 3),
            new Modifier(Affix.SPELL_POWER, 3),
            new Modifier(Affix.THORNS, 2.5),
            new Modifier(Affix.HEAL_PER_SECOND, 1),
            new Modifier(Affix.DEFENSE, 3),
            new Modifier(Affix.LIFESTEAL, 0.06));

    private Mutations() {}

    public static Modifier bySeed(long seed) {
        return LIST.get((int) Math.floorMod(seed >>> 8, (long) LIST.size()));
    }

    public static int randomIndex(Random rng) {
        return rng.nextInt(LIST.size());
    }
}
