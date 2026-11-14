package com.example.combimod.core;

import java.util.*;

/**
 * Fusion de deux objets. Résultat stable (même paire = même résultat, dans les deux sens), avec :
 * synergies entre chaque paire de tags, bonus de trio, mutation surprise, malédictions retournées,
 * plafonds par affixe et chance d'améliorer la rareté.
 */
public final class Fusion {

    private record Triple(Set<Tag> required, List<Modifier> bonus) {}

    private static final Map<Set<Tag>, Modifier> SYNERGIES = new HashMap<>();
    private static final List<Triple> TRIPLES = new ArrayList<>();
    private static final Map<Set<String>, ItemDef> RECIPES = new HashMap<>();

    static {
        // Les 21 paires de tags ont toutes une synergie
        syn(Tag.MELEE, Tag.MAGIC, Affix.SPELL_POWER, 2);
        syn(Tag.MELEE, Tag.SUMMON, Affix.SUMMON_DAMAGE, 2);
        syn(Tag.MELEE, Tag.SUPPORT, Affix.ATTACK_SPEED, 0.1);
        syn(Tag.MELEE, Tag.HEAL, Affix.LIFESTEAL, 0.10);
        syn(Tag.MELEE, Tag.DEFENSE, Affix.THORNS, 2);
        syn(Tag.MELEE, Tag.CURSED, Affix.DAMAGE, 4);
        syn(Tag.MAGIC, Tag.SUMMON, Affix.SUMMON_DAMAGE, 4);
        syn(Tag.MAGIC, Tag.SUPPORT, Affix.MANA_REGEN, 2);
        syn(Tag.MAGIC, Tag.HEAL, Affix.HEAL_PER_SECOND, 1);
        syn(Tag.MAGIC, Tag.DEFENSE, Affix.DEFENSE, 2);
        syn(Tag.MAGIC, Tag.CURSED, Affix.SPELL_POWER, 3);
        syn(Tag.SUMMON, Tag.SUPPORT, Affix.SUMMON_COUNT, 1);
        syn(Tag.SUMMON, Tag.HEAL, Affix.HEAL_PER_SECOND, 0.5);
        syn(Tag.SUMMON, Tag.DEFENSE, Affix.MAX_HEALTH, 3);
        syn(Tag.SUMMON, Tag.CURSED, Affix.SUMMON_COUNT, 2);
        syn(Tag.SUPPORT, Tag.HEAL, Affix.HEAL_PER_SECOND, 1);
        syn(Tag.SUPPORT, Tag.DEFENSE, Affix.DEFENSE, 2);
        syn(Tag.SUPPORT, Tag.CURSED, Affix.MANA_REGEN, 1.5);
        syn(Tag.HEAL, Tag.DEFENSE, Affix.MAX_HEALTH, 3);
        syn(Tag.HEAL, Tag.CURSED, Affix.LIFESTEAL, 0.12);
        syn(Tag.DEFENSE, Tag.CURSED, Affix.THORNS, 4);

        // Bonus de trio : quand trois tags précis sont réunis
        triple(EnumSet.of(Tag.MELEE, Tag.MAGIC, Tag.DEFENSE), new Modifier(Affix.DEFENSE, 3), new Modifier(Affix.DAMAGE, 3));
        triple(EnumSet.of(Tag.MAGIC, Tag.SUMMON, Tag.CURSED), new Modifier(Affix.SUMMON_COUNT, 2), new Modifier(Affix.SPELL_POWER, 4));
        triple(EnumSet.of(Tag.HEAL, Tag.SUPPORT, Tag.DEFENSE), new Modifier(Affix.HEAL_PER_SECOND, 2), new Modifier(Affix.MAX_HEALTH, 4));
        triple(EnumSet.of(Tag.MELEE, Tag.HEAL, Tag.CURSED), new Modifier(Affix.LIFESTEAL, 0.15), new Modifier(Affix.DAMAGE, 5));
        triple(EnumSet.of(Tag.SUMMON, Tag.SUPPORT, Tag.MAGIC), new Modifier(Affix.SUMMON_DAMAGE, 4), new Modifier(Affix.MANA_REGEN, 2));
        triple(EnumSet.of(Tag.MELEE, Tag.SUMMON, Tag.DEFENSE), new Modifier(Affix.THORNS, 3), new Modifier(Affix.SUMMON_COUNT, 1));
        triple(EnumSet.of(Tag.MAGIC, Tag.HEAL, Tag.SUPPORT), new Modifier(Affix.SPELL_POWER, 3), new Modifier(Affix.HEAL_PER_SECOND, 2));
        triple(EnumSet.of(Tag.DEFENSE, Tag.CURSED, Tag.MELEE), new Modifier(Affix.DAMAGE, 4), new Modifier(Affix.THORNS, 4));
    }

    private Fusion() {}

    private static void syn(Tag a, Tag b, Affix affix, double value) {
        SYNERGIES.put(EnumSet.of(a, b), new Modifier(affix, value));
    }

    private static void triple(Set<Tag> required, Modifier... bonus) {
        TRIPLES.add(new Triple(required, List.of(bonus)));
    }

    private static Set<String> pair(String a, String b) {
        return new HashSet<>(Arrays.asList(a, b));
    }

    public static void registerRecipe(ItemDef a, ItemDef b, ItemDef result) {
        RECIPES.put(pair(a.id(), b.id()), result);
    }

    public static Map<Set<String>, ItemDef> recipes() {
        return Collections.unmodifiableMap(RECIPES);
    }

    public static int synergyCount() {
        return SYNERGIES.size();
    }

    public static int tripleCount() {
        return TRIPLES.size();
    }

    public static ItemDef fuse(ItemDef a, ItemDef b) {
        ItemDef special = RECIPES.get(pair(a.id(), b.id()));
        if (special != null) return special;

        // Ordre stable : fuse(a, b) donne exactement le même résultat que fuse(b, a)
        ItemDef x = a.id().compareTo(b.id()) <= 0 ? a : b;
        ItemDef y = (x == a) ? b : a;
        String key = x.id() + "+" + y.id();
        long seed = Ids.seed(key);

        Set<Tag> tags = EnumSet.noneOf(Tag.class);
        tags.addAll(x.tags());
        tags.addAll(y.tags());

        Map<Affix, Double> sum = new EnumMap<>(Affix.class);
        for (Modifier m : x.modifiers()) sum.merge(m.affix(), m.value(), Double::sum);
        for (Modifier m : y.modifiers()) sum.merge(m.affix(), m.value(), Double::sum);

        // Malédiction retournée : un item maudit combiné à autre chose transforme ses malus en bonus réduits
        if (tags.contains(Tag.CURSED) && tags.size() > 1) {
            for (Map.Entry<Affix, Double> e : sum.entrySet()) {
                if (e.getValue() < 0) e.setValue(-e.getValue() * 0.5);
            }
        }

        List<Tag> list = new ArrayList<>(tags);
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                Modifier bonus = SYNERGIES.get(EnumSet.of(list.get(i), list.get(j)));
                if (bonus != null) sum.merge(bonus.affix(), bonus.value(), Double::sum);
            }
        }
        for (Triple t : TRIPLES) {
            if (tags.containsAll(t.required())) {
                for (Modifier m : t.bonus()) sum.merge(m.affix(), m.value(), Double::sum);
            }
        }

        // Mutation surprise, doublée si la fusion est maudite
        Modifier mutation = Mutations.bySeed(seed);
        double mv = tags.contains(Tag.CURSED) ? mutation.value() * 2 : mutation.value();
        sum.merge(mutation.affix(), mv, Double::sum);

        List<Modifier> mods = new ArrayList<>();
        for (Map.Entry<Affix, Double> e : sum.entrySet()) {
            double v = Math.round(e.getKey().clamp(e.getValue()) * 100.0) / 100.0;
            if (Math.abs(v) > 1e-9) mods.add(new Modifier(e.getKey(), v));
        }

        Rarity[] rs = Rarity.values();
        int ord = Math.max(x.rarity().ordinal(), y.rarity().ordinal());
        if (x.rarity() == y.rarity() && ord < rs.length - 1 && (seed & 7L) == 0) ord++; // 1 chance sur 8
        if (tags.size() >= 6) ord = rs.length - 1; // jackpot : presque tous les types réunis

        return new ItemDef(
                "f:" + Ids.hash(key),
                Names.fusionName(tags, seed),
                Collections.unmodifiableSet(tags),
                mods,
                (long) ((x.price() + y.price()) * 1.5),
                rs[ord]);
    }
}
