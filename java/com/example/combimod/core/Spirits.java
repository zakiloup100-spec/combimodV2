package com.example.combimod.core;

/**
 * Types d'esprits invoqués. Selon l'objet qui les invoque :
 * magie + invocation = spectre sombre, défense + invocation = gardien, sinon esprit.
 * Chaque type inflige ses effets aux ennemis proches, à intervalle régulier.
 */
public final class Spirits {

    public enum Kind {
        DARK("Spectre sombre", Traits.Trait.BLIND, Traits.Trait.WEAKEN),
        GUARD("Gardien", Traits.Trait.SLOW),
        SPIRIT("Esprit", Traits.Trait.POISON);

        public final String label;
        public final Traits.Trait[] pulse;

        Kind(String label, Traits.Trait... pulse) {
            this.label = label;
            this.pulse = pulse;
        }
    }

    private Spirits() {}

    public static Kind kindOf(ItemDef d) {
        if (d.tags().contains(Tag.MAGIC)) return Kind.DARK;
        if (d.tags().contains(Tag.DEFENSE)) return Kind.GUARD;
        return Kind.SPIRIT;
    }
}
