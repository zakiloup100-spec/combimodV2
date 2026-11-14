package com.example.combimod.core;

import java.util.Random;

/**
 * Pari pile ou face à 50/50, sans avantage pour la maison : chaque tirage a exactement une chance sur deux.
 * Sur le long terme, l'espérance de gain est nulle : on ne gagne pas, on ne perd pas en moyenne.
 * La variance reste réelle : on peut perdre beaucoup en peu de temps, d'où les limites de mise et le délai.
 */
public final class Gamble {

    public static final long MIN_BET = 1;
    public static final long MAX_BET = 1000;
    /** Délai entre deux paris (en ticks, 20 par seconde). */
    public static final long COOLDOWN_TICKS = 60;

    public record Outcome(boolean won, long net) {}

    private Gamble() {}

    /** Message d'erreur, ou null si la mise est acceptable. */
    public static String validate(long bet, long balance, long ticksSinceLast) {
        if (bet < MIN_BET) return "Mise minimum : " + MIN_BET + " pièce.";
        if (bet > MAX_BET) return "Mise maximum : " + MAX_BET + " pièces.";
        if (bet > balance) return "Il te faut " + bet + " pièces (tu en as " + balance + ").";
        if (ticksSinceLast < COOLDOWN_TICKS) {
            long wait = (COOLDOWN_TICKS - ticksSinceLast + 19) / 20;
            return "Attends encore " + wait + " s avant de rejouer.";
        }
        return null;
    }

    /** Un tirage. La mise est rendue doublée en cas de victoire, perdue sinon. */
    public static Outcome flip(Random rng, long bet) {
        boolean won = rng.nextBoolean();
        return new Outcome(won, won ? bet : -bet);
    }
}
