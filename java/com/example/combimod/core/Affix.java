package com.example.combimod.core;

public enum Affix {
    DAMAGE(1.0, 40),
    ATTACK_SPEED(0.1, 1.5),
    MAX_HEALTH(2.0, 16),
    HEAL_PER_SECOND(0.5, 6),
    LIFESTEAL(0.05, 0.5),
    MANA_REGEN(0.5, 10),
    SPELL_POWER(1.0, 40),
    SUMMON_COUNT(1.0, 6),
    SUMMON_DAMAGE(1.0, 8),
    THORNS(1.0, 15),
    DEFENSE(1.0, 20);

    public final double baseValue;
    /** Valeur maximale (en positif ou en négatif) : évite que les fusions explosent. */
    public final double cap;

    Affix(double baseValue, double cap) {
        this.baseValue = baseValue;
        this.cap = cap;
    }

    public double clamp(double v) {
        return Math.max(-cap, Math.min(cap, v));
    }
}
