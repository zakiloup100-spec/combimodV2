package com.example.combimod.core;

import java.util.*;

/** Noms de fusion : un nom + un adjectif accordé + un aspect, choisis de façon stable. */
public final class Names {

    private record Noun(String word, boolean fem) {}
    private record Adj(String masc, String fem) {}

    private static final Map<Tag, List<Noun>> NOUNS = new EnumMap<>(Tag.class);
    private static final Map<Tag, String> ASPECT = new EnumMap<>(Tag.class);
    private static final List<Adj> ADJ = List.of(
            new Adj("Spectral", "Spectrale"), new Adj("Ardent", "Ardente"), new Adj("Givré", "Givrée"),
            new Adj("Tonnant", "Tonnante"), new Adj("Abyssal", "Abyssale"), new Adj("Céleste", "Céleste"),
            new Adj("Ancien", "Ancienne"), new Adj("Sacré", "Sacrée"), new Adj("Sauvage", "Sauvage"),
            new Adj("Éternel", "Éternelle"), new Adj("Brisé", "Brisée"), new Adj("Radieux", "Radieuse"),
            new Adj("Corrompu", "Corrompue"), new Adj("Primordial", "Primordiale"), new Adj("Oublié", "Oubliée"),
            new Adj("Furieux", "Furieuse"));

    static {
        NOUNS.put(Tag.MELEE, List.of(new Noun("Lame", true), new Noun("Faux", true), new Noun("Hache", true), new Noun("Marteau", false)));
        NOUNS.put(Tag.MAGIC, List.of(new Noun("Orbe", false), new Noun("Sceptre", false), new Noun("Grimoire", false), new Noun("Prisme", false)));
        NOUNS.put(Tag.SUMMON, List.of(new Noun("Totem", false), new Noun("Idole", true), new Noun("Cor", false), new Noun("Sceau", false)));
        NOUNS.put(Tag.SUPPORT, List.of(new Noun("Talisman", false), new Noun("Étendard", false), new Noun("Amulette", true), new Noun("Relique", true)));
        NOUNS.put(Tag.HEAL, List.of(new Noun("Calice", false), new Noun("Fiole", true), new Noun("Baume", false), new Noun("Source", true)));
        NOUNS.put(Tag.DEFENSE, List.of(new Noun("Égide", true), new Noun("Rempart", false), new Noun("Carapace", true), new Noun("Bastion", false)));
        NOUNS.put(Tag.CURSED, List.of(new Noun("Fléau", false), new Noun("Pacte", false), new Noun("Stigmate", false), new Noun("Malédiction", true)));

        ASPECT.put(Tag.MELEE, "du Combat");
        ASPECT.put(Tag.MAGIC, "des Arcanes");
        ASPECT.put(Tag.SUMMON, "de la Meute");
        ASPECT.put(Tag.SUPPORT, "de l'Alliance");
        ASPECT.put(Tag.HEAL, "de Vie");
        ASPECT.put(Tag.DEFENSE, "du Rempart");
        ASPECT.put(Tag.CURSED, "des Ténèbres");
    }

    private Names() {}

    static int idx(long v, int n) {
        return (int) Math.floorMod(v, (long) n);
    }

    public static String fusionName(Set<Tag> tags, long seed) {
        List<Tag> list = new ArrayList<>(tags);
        if (list.isEmpty()) return "Relique informe";

        Tag primary = list.get(idx(seed, list.size()));
        List<Noun> nouns = NOUNS.get(primary);
        Noun noun = nouns.get(idx(seed >>> 13, nouns.size()));
        Adj adj = ADJ.get(idx(seed >>> 21, ADJ.size()));

        StringBuilder sb = new StringBuilder(noun.word()).append(' ').append(noun.fem() ? adj.fem() : adj.masc());
        if (list.size() > 1) {
            List<Tag> others = new ArrayList<>(list);
            others.remove(primary);
            Tag secondary = others.get(idx(seed >>> 7, others.size()));
            sb.append(' ').append(ASPECT.get(secondary));
        }
        return sb.toString();
    }
}
