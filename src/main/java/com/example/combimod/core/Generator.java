package com.example.combimod.core;

import java.util.*;

/**
 * Générateur d'objets : type de base x préfixe x suffixe x rareté, plus un bonus de signature
 * pour les légendaires. Des milliers de variantes, auxquelles s'ajoutent les fusions.
 */
public final class Generator {

    public record BaseType(String id, String name, boolean fem, Set<Tag> tags, List<Modifier> mods, long basePrice) {}
    public record Prefix(String masc, String fem, Set<Tag> tags, List<Modifier> mods) {}
    public record Suffix(String text, Set<Tag> tags, List<Modifier> mods) {}

    private static Set<Tag> tags(Tag first, Tag... rest) {
        return Collections.unmodifiableSet(EnumSet.of(first, rest));
    }

    private static Modifier m(Affix a, double v) {
        return new Modifier(a, v);
    }

    public static final List<BaseType> TYPES = List.of(
            new BaseType("epee", "Épée", true, tags(Tag.MELEE), List.of(m(Affix.DAMAGE, 4)), 40),
            new BaseType("hache", "Hache", true, tags(Tag.MELEE), List.of(m(Affix.DAMAGE, 5), m(Affix.ATTACK_SPEED, -0.1)), 45),
            new BaseType("lance", "Lance", true, tags(Tag.MELEE), List.of(m(Affix.DAMAGE, 3), m(Affix.THORNS, 1)), 40),
            new BaseType("dague", "Dague", true, tags(Tag.MELEE), List.of(m(Affix.DAMAGE, 2.5), m(Affix.ATTACK_SPEED, 0.2)), 40),
            new BaseType("baguette", "Baguette", true, tags(Tag.MAGIC), List.of(m(Affix.SPELL_POWER, 3)), 40),
            new BaseType("baton", "Bâton", false, tags(Tag.MAGIC), List.of(m(Affix.SPELL_POWER, 2), m(Affix.MANA_REGEN, 1)), 40),
            new BaseType("grimoire", "Grimoire", false, tags(Tag.MAGIC, Tag.SUPPORT), List.of(m(Affix.SPELL_POWER, 2), m(Affix.MANA_REGEN, 1)), 50),
            new BaseType("totem", "Totem", false, tags(Tag.SUMMON), List.of(m(Affix.SUMMON_COUNT, 1)), 45),
            new BaseType("cor", "Cor", false, tags(Tag.SUMMON, Tag.SUPPORT), List.of(m(Affix.SUMMON_DAMAGE, 1), m(Affix.SUMMON_COUNT, 1)), 50),
            new BaseType("bouclier", "Bouclier", false, tags(Tag.DEFENSE), List.of(m(Affix.DEFENSE, 2), m(Affix.MAX_HEALTH, 2)), 40),
            new BaseType("amulette", "Amulette", true, tags(Tag.SUPPORT, Tag.HEAL), List.of(m(Affix.HEAL_PER_SECOND, 0.5), m(Affix.MAX_HEALTH, 1)), 45),
            new BaseType("anneau", "Anneau", false, tags(Tag.SUPPORT), List.of(m(Affix.MAX_HEALTH, 2)), 35));

    public static final List<Prefix> PREFIXES = List.of(
            new Prefix("Flamboyant", "Flamboyante", tags(Tag.MELEE), List.of(m(Affix.DAMAGE, 2))),
            new Prefix("Arcanique", "Arcanique", tags(Tag.MAGIC), List.of(m(Affix.SPELL_POWER, 2))),
            new Prefix("Vampirique", "Vampirique", tags(Tag.HEAL, Tag.MELEE), List.of(m(Affix.LIFESTEAL, 0.04))),
            new Prefix("Sauvage", "Sauvage", tags(Tag.SUMMON), List.of(m(Affix.SUMMON_COUNT, 1))),
            new Prefix("Blindé", "Blindée", tags(Tag.DEFENSE), List.of(m(Affix.DEFENSE, 2))),
            new Prefix("Vif", "Vive", tags(Tag.MELEE), List.of(m(Affix.ATTACK_SPEED, 0.15))),
            new Prefix("Béni", "Bénie", tags(Tag.HEAL, Tag.SUPPORT), List.of(m(Affix.HEAL_PER_SECOND, 0.5))),
            new Prefix("Épineux", "Épineuse", tags(Tag.DEFENSE), List.of(m(Affix.THORNS, 2))),
            new Prefix("Titanesque", "Titanesque", tags(Tag.DEFENSE), List.of(m(Affix.MAX_HEALTH, 4))),
            new Prefix("Mystique", "Mystique", tags(Tag.SUPPORT, Tag.MAGIC), List.of(m(Affix.MANA_REGEN, 1))),
            new Prefix("Maudit", "Maudite", tags(Tag.CURSED, Tag.MELEE), List.of(m(Affix.DAMAGE, 4), m(Affix.MAX_HEALTH, -3))),
            new Prefix("Corrompu", "Corrompue", tags(Tag.CURSED, Tag.MAGIC), List.of(m(Affix.SPELL_POWER, 4), m(Affix.MAX_HEALTH, -2))),
            new Prefix("Sanguinaire", "Sanguinaire", tags(Tag.CURSED, Tag.HEAL), List.of(m(Affix.LIFESTEAL, 0.08), m(Affix.MAX_HEALTH, -3))),
            new Prefix("Spectral", "Spectrale", tags(Tag.SUMMON, Tag.MAGIC), List.of(m(Affix.SUMMON_DAMAGE, 2))),
            new Prefix("Tonnant", "Tonnante", tags(Tag.MAGIC, Tag.MELEE), List.of(m(Affix.SPELL_POWER, 1), m(Affix.DAMAGE, 1))),
            new Prefix("Givré", "Givrée", tags(Tag.DEFENSE, Tag.MAGIC), List.of(m(Affix.DEFENSE, 1), m(Affix.SPELL_POWER, 1))),
            new Prefix("Radieux", "Radieuse", tags(Tag.HEAL, Tag.MAGIC), List.of(m(Affix.HEAL_PER_SECOND, 0.8), m(Affix.SPELL_POWER, 1))),
            new Prefix("Brisé", "Brisée", tags(Tag.CURSED, Tag.MELEE), List.of(m(Affix.DAMAGE, 3), m(Affix.DEFENSE, -2))),
            new Prefix("Fragile", "Fragile", tags(Tag.CURSED, Tag.SUPPORT), List.of(m(Affix.ATTACK_SPEED, 0.3), m(Affix.MAX_HEALTH, -4))),
            new Prefix("Éclatant", "Éclatante", tags(Tag.MAGIC), List.of(m(Affix.SPELL_POWER, 2), m(Affix.MANA_REGEN, 1))),
            new Prefix("Hanté", "Hantée", tags(Tag.SUMMON, Tag.CURSED), List.of(m(Affix.SUMMON_DAMAGE, 2), m(Affix.MAX_HEALTH, -2))),
            new Prefix("Forgé", "Forgée", tags(Tag.MELEE, Tag.DEFENSE), List.of(m(Affix.DAMAGE, 2), m(Affix.DEFENSE, 1))),
            new Prefix("Céleste", "Céleste", tags(Tag.HEAL, Tag.MAGIC), List.of(m(Affix.HEAL_PER_SECOND, 0.6), m(Affix.MANA_REGEN, 1))));

    public static final List<Suffix> SUFFIXES = List.of(
            new Suffix("du Loup", tags(Tag.SUMMON), List.of(m(Affix.SUMMON_COUNT, 1))),
            new Suffix("de l'Aube", tags(Tag.HEAL), List.of(m(Affix.HEAL_PER_SECOND, 0.7))),
            new Suffix("de la Tempête", tags(Tag.MAGIC), List.of(m(Affix.ATTACK_SPEED, 0.2), m(Affix.SPELL_POWER, 1))),
            new Suffix("du Titan", tags(Tag.DEFENSE), List.of(m(Affix.MAX_HEALTH, 5))),
            new Suffix("des Ombres", tags(Tag.CURSED, Tag.MAGIC), List.of(m(Affix.SPELL_POWER, 3), m(Affix.MAX_HEALTH, -2))),
            new Suffix("du Dragon", tags(Tag.MELEE), List.of(m(Affix.DAMAGE, 3), m(Affix.THORNS, 1))),
            new Suffix("de la Garde", tags(Tag.DEFENSE), List.of(m(Affix.DEFENSE, 3))),
            new Suffix("du Sage", tags(Tag.MAGIC, Tag.SUPPORT), List.of(m(Affix.MANA_REGEN, 1.5), m(Affix.SPELL_POWER, 1))),
            new Suffix("du Phénix", tags(Tag.HEAL), List.of(m(Affix.HEAL_PER_SECOND, 1), m(Affix.LIFESTEAL, 0.03))),
            new Suffix("de la Meute", tags(Tag.SUMMON, Tag.SUPPORT), List.of(m(Affix.SUMMON_DAMAGE, 2), m(Affix.SUMMON_COUNT, 1))),
            new Suffix("du Bourreau", tags(Tag.CURSED, Tag.MELEE), List.of(m(Affix.DAMAGE, 5), m(Affix.MAX_HEALTH, -4))),
            new Suffix("de l'Abîme", tags(Tag.CURSED), List.of(m(Affix.SPELL_POWER, 4), m(Affix.DEFENSE, -2))),
            new Suffix("du Veilleur", tags(Tag.DEFENSE), List.of(m(Affix.THORNS, 3), m(Affix.DEFENSE, 1))),
            new Suffix("de la Lune", tags(Tag.SUPPORT, Tag.HEAL), List.of(m(Affix.MANA_REGEN, 1), m(Affix.HEAL_PER_SECOND, 0.4))),
            new Suffix("du Chaos", tags(Tag.MELEE, Tag.MAGIC), List.of(m(Affix.DAMAGE, 2), m(Affix.SPELL_POWER, 2), m(Affix.ATTACK_SPEED, -0.1))),
            new Suffix("des Anciens", tags(Tag.SUPPORT, Tag.SUMMON), List.of(m(Affix.MAX_HEALTH, 3), m(Affix.SUMMON_COUNT, 1))),
            new Suffix("de Zeus", tags(Tag.MAGIC, Tag.MELEE), List.of(m(Affix.SPELL_POWER, 2), m(Affix.ATTACK_SPEED, 0.15), m(Affix.DAMAGE, 2))),
            new Suffix("du Néant", tags(Tag.CURSED, Tag.MAGIC, Tag.MELEE), List.of(m(Affix.DAMAGE, 3), m(Affix.SPELL_POWER, 3), m(Affix.MAX_HEALTH, -5))),
            new Suffix("de l'Orage", tags(Tag.MAGIC, Tag.MELEE), List.of(m(Affix.ATTACK_SPEED, 0.2), m(Affix.SPELL_POWER, 1))),
            new Suffix("des Cendres", tags(Tag.CURSED, Tag.HEAL), List.of(m(Affix.LIFESTEAL, 0.05), m(Affix.MAX_HEALTH, -2))),
            new Suffix("de l'Aurore", tags(Tag.SUPPORT, Tag.HEAL), List.of(m(Affix.HEAL_PER_SECOND, 0.8), m(Affix.MANA_REGEN, 1))),
            new Suffix("du Spectre", tags(Tag.SUMMON, Tag.CURSED), List.of(m(Affix.SUMMON_COUNT, 1), m(Affix.MAX_HEALTH, -2))));

    private static final Map<Affix, Double> PRICE_WEIGHT = new EnumMap<>(Affix.class);
    private static final double[] RARITY_PRICE = {1.0, 1.8, 3.2, 6.0, 12.0};

    static {
        PRICE_WEIGHT.put(Affix.DAMAGE, 14.0);
        PRICE_WEIGHT.put(Affix.ATTACK_SPEED, 120.0);
        PRICE_WEIGHT.put(Affix.MAX_HEALTH, 8.0);
        PRICE_WEIGHT.put(Affix.HEAL_PER_SECOND, 40.0);
        PRICE_WEIGHT.put(Affix.LIFESTEAL, 300.0);
        PRICE_WEIGHT.put(Affix.MANA_REGEN, 12.0);
        PRICE_WEIGHT.put(Affix.SPELL_POWER, 14.0);
        PRICE_WEIGHT.put(Affix.SUMMON_COUNT, 40.0);
        PRICE_WEIGHT.put(Affix.SUMMON_DAMAGE, 20.0);
        PRICE_WEIGHT.put(Affix.THORNS, 12.0);
        PRICE_WEIGHT.put(Affix.DEFENSE, 10.0);
    }

    private Generator() {}

    /** Nombre de combinaisons type x préfixe x suffixe (hors rareté, hors fusions). */
    public static int totalCombos() {
        return TYPES.size() * ((PREFIXES.size() + 1) * (SUFFIXES.size() + 1) - 1);
    }

    /** Génère un objet de la rareté demandée. theme : tags préférés (vide ou null = aucun thème). */
    public static ItemDef generate(Random rng, Rarity rarity, Set<Tag> theme) {
        List<BaseType> pool = TYPES;
        if (theme != null && !theme.isEmpty()) {
            List<BaseType> filtered = new ArrayList<>();
            for (BaseType t : TYPES) {
                if (!Collections.disjoint(t.tags(), theme)) filtered.add(t);
            }
            if (!filtered.isEmpty()) pool = filtered;
        }
        BaseType type = pool.get(rng.nextInt(pool.size()));

        int pi = -1;
        int si = -1;
        boolean both = rarity != Rarity.COMMON;
        if (both || rng.nextBoolean()) pi = pickPrefix(rng, theme);
        if (both || pi < 0) si = pickSuffix(rng, theme);
        int extra = rarity.ordinal() >= Rarity.LEGENDARY.ordinal() ? Mutations.randomIndex(rng) : -1;
        return build(type, pi, si, rarity, extra);
    }

    private static int pickPrefix(Random rng, Set<Tag> theme) {
        List<Integer> preferred = new ArrayList<>();
        if (theme != null && !theme.isEmpty() && rng.nextDouble() < 0.7) {
            for (int i = 0; i < PREFIXES.size(); i++) {
                if (!Collections.disjoint(PREFIXES.get(i).tags(), theme)) preferred.add(i);
            }
        }
        return preferred.isEmpty() ? rng.nextInt(PREFIXES.size()) : preferred.get(rng.nextInt(preferred.size()));
    }

    private static int pickSuffix(Random rng, Set<Tag> theme) {
        List<Integer> preferred = new ArrayList<>();
        if (theme != null && !theme.isEmpty() && rng.nextDouble() < 0.7) {
            for (int i = 0; i < SUFFIXES.size(); i++) {
                if (!Collections.disjoint(SUFFIXES.get(i).tags(), theme)) preferred.add(i);
            }
        }
        return preferred.isEmpty() ? rng.nextInt(SUFFIXES.size()) : preferred.get(rng.nextInt(preferred.size()));
    }

    /** Construit un objet précis. pi, si, extra : -1 = absent. */
    public static ItemDef build(BaseType type, int pi, int si, Rarity rarity, int extra) {
        Set<Tag> tags = EnumSet.noneOf(Tag.class);
        tags.addAll(type.tags());
        Map<Affix, Double> sum = new EnumMap<>(Affix.class);
        add(sum, type.mods());
        StringBuilder name = new StringBuilder(type.name());

        if (pi >= 0) {
            Prefix p = PREFIXES.get(pi);
            tags.addAll(p.tags());
            add(sum, p.mods());
            name.append(' ').append(type.fem() ? p.fem() : p.masc());
        }
        if (si >= 0) {
            Suffix s = SUFFIXES.get(si);
            tags.addAll(s.tags());
            add(sum, s.mods());
            name.append(' ').append(s.text());
        }
        if (extra >= 0) {
            Modifier mut = Mutations.LIST.get(extra);
            sum.merge(mut.affix(), mut.value(), Double::sum);
        }

        List<Modifier> mods = new ArrayList<>();
        for (Map.Entry<Affix, Double> e : sum.entrySet()) {
            double v = Math.round(e.getKey().clamp(e.getValue()) * 100.0) / 100.0;
            if (Math.abs(v) > 1e-9) mods.add(new Modifier(e.getKey(), v));
        }

        String id = "g:" + type.id() + ":" + pi + ":" + si + ":" + rarity.ordinal() + (extra >= 0 ? ":" + extra : "");
        return new ItemDef(id, name.toString(), Collections.unmodifiableSet(tags), mods,
                price(mods, type.basePrice(), rarity), rarity);
    }

    private static void add(Map<Affix, Double> sum, List<Modifier> mods) {
        for (Modifier m : mods) sum.merge(m.affix(), m.value(), Double::sum);
    }

    static long price(List<Modifier> mods, long base, Rarity rarity) {
        double score = 0;
        for (Modifier m : mods) {
            double w = PRICE_WEIGHT.get(m.affix());
            score += m.value() > 0 ? m.value() * w : m.value() * w * 0.3;
        }
        return Math.max(10, Math.round((base + score) * RARITY_PRICE[rarity.ordinal()]));
    }
}
