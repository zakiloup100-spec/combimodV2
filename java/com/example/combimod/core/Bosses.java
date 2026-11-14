package com.example.combimod.core;

import java.util.*;

/** Tables de drop par boss : moitié objets du catalogue, moitié objets générés selon le thème du boss. */
public final class Bosses {

    private record Table(LootRoller roller, Set<Tag> theme, Random rng) {}

    private final Map<String, Table> tables = new HashMap<>();

    public void register(String bossId, Collection<ItemDef> pool, Set<Tag> theme, Random rng) {
        tables.put(bossId, new Table(new LootRoller(pool, rng), theme, rng));
    }

    public Optional<ItemDef> rollDrop(String bossId) {
        Table t = tables.get(bossId);
        if (t == null) return Optional.empty();
        if (t.rng().nextDouble() < 0.5) {
            Optional<ItemDef> fixed = t.roller().roll();
            if (fixed.isPresent()) return fixed;
        }
        return Optional.of(Generator.generate(t.rng(), Chests.bossRarity(t.rng()), t.theme()));
    }

    public static Bosses defaults(Random rng) {
        Bosses b = new Bosses();
        b.register("boss_soin", List.of(Items.ANNEAU_VIE, Items.GRIMOIRE_SOIN, Items.COEUR_CORROMPU, Items.RUNE_VIE, Items.BOUCLIER_SOLEIL),
                EnumSet.of(Tag.HEAL, Tag.SUPPORT), rng);
        b.register("boss_magie", List.of(Items.BATON_ARCANE, Items.LIVRE_OMBRES, Items.NECRO_ORBE, Items.CRISTAL_TEMPS, Items.COURONNE_ARCANE),
                EnumSet.of(Tag.MAGIC), rng);
        b.register("boss_melee", List.of(Items.EPEE_BRAISE, Items.ARC_CHASSE, Items.PACTE_SANGLANT, Items.SANG_DRAGON, Items.LAME_SPECTRALE),
                EnumSet.of(Tag.MELEE), rng);
        b.register("boss_ombre", List.of(Items.LIVRE_OMBRES, Items.CHARME_BRAISE, Items.COEUR_CORROMPU, Items.LAME_SPECTRALE, Items.PACTE_SANGLANT),
                EnumSet.of(Tag.CURSED), rng);
        b.register("boss_meute", List.of(Items.TOTEM_LOUP, Items.ESPRIT_LOUP, Items.COUR_LOUPS, Items.NECRO_ORBE, Items.COURONNE_ROI),
                EnumSet.of(Tag.SUMMON), rng);
        return b;
    }
}
