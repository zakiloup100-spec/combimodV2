package com.example.combimod.core;

import java.util.*;

/** Catalogue de base. Chaque item a des tags et des modificateurs : les builds émergent des combinaisons. */
public final class Items {

    // ----- Items communs -----
    public static final ItemDef EPEE_BRAISE = item("epee_braise", "Épée de braise",
            Set.of(Tag.MELEE), List.of(new Modifier(Affix.DAMAGE, 4)), 100, Rarity.COMMON);

    public static final ItemDef SERPE_ROUILLEE = item("serpe_rouillee", "Serpe rouillée",
            Set.of(Tag.MELEE), List.of(new Modifier(Affix.DAMAGE, 2)), 40, Rarity.COMMON);

    public static final ItemDef BOUCLIER_CENDRE = item("bouclier_cendre", "Bouclier de cendre",
            Set.of(Tag.DEFENSE), List.of(new Modifier(Affix.MAX_HEALTH, 4)), 110, Rarity.COMMON);

    public static final ItemDef BATON_ARCANE = item("baton_arcane", "Bâton arcanique",
            Set.of(Tag.MAGIC), List.of(new Modifier(Affix.SPELL_POWER, 3)), 90, Rarity.COMMON);

    public static final ItemDef ANNEAU_VIE = item("anneau_vie", "Anneau de vie",
            Set.of(Tag.SUPPORT, Tag.HEAL), List.of(new Modifier(Affix.HEAL_PER_SECOND, 0.5)), 80, Rarity.COMMON);

    public static final ItemDef POTION_MANA = item("potion_mana", "Élixir de mana",
            Set.of(Tag.SUPPORT), List.of(new Modifier(Affix.MANA_REGEN, 1)), 60, Rarity.COMMON);

    // ----- Items rares -----
    public static final ItemDef GRIMOIRE_SOIN = item("grimoire_soin", "Grimoire de soin",
            Set.of(Tag.MAGIC, Tag.SUPPORT, Tag.HEAL),
            List.of(new Modifier(Affix.HEAL_PER_SECOND, 1), new Modifier(Affix.SPELL_POWER, 2)),
            150, Rarity.RARE);

    public static final ItemDef TOTEM_LOUP = item("totem_loup", "Totem du loup",
            Set.of(Tag.SUMMON), List.of(new Modifier(Affix.SUMMON_COUNT, 1)), 120, Rarity.RARE);

    public static final ItemDef ESPRIT_LOUP = item("esprit_loup", "Esprit du loup",
            Set.of(Tag.SUMMON), List.of(new Modifier(Affix.SUMMON_DAMAGE, 2)), 130, Rarity.RARE);

    public static final ItemDef ARMURE_EPINES = item("armure_epines", "Armure d'épines",
            Set.of(Tag.DEFENSE), List.of(new Modifier(Affix.THORNS, 2)), 140, Rarity.RARE);

    public static final ItemDef ARC_CHASSE = item("arc_chasse", "Arc de chasse",
            Set.of(Tag.MELEE), List.of(new Modifier(Affix.DAMAGE, 3), new Modifier(Affix.ATTACK_SPEED, 0.2)),
            130, Rarity.RARE);

    // ----- Items maudits (malus, qui deviennent bonus en fusion) -----
    public static final ItemDef PACTE_SANGLANT = item("pacte_sanglant", "Pacte sanglant",
            Set.of(Tag.CURSED, Tag.MELEE),
            List.of(new Modifier(Affix.MAX_HEALTH, -4), new Modifier(Affix.DAMAGE, 3)),
            80, Rarity.EPIC);

    public static final ItemDef LIVRE_OMBRES = item("livre_ombres", "Livre des ombres",
            Set.of(Tag.CURSED, Tag.MAGIC),
            List.of(new Modifier(Affix.MAX_HEALTH, -2), new Modifier(Affix.SPELL_POWER, 5)),
            120, Rarity.RARE);

    public static final ItemDef COEUR_CORROMPU = item("coeur_corrompu", "Cœur corrompu",
            Set.of(Tag.CURSED, Tag.HEAL),
            List.of(new Modifier(Affix.MAX_HEALTH, -6), new Modifier(Affix.LIFESTEAL, 0.2)),
            200, Rarity.EPIC);

    // ----- Items épiques et légendaires -----
    public static final ItemDef NECRO_ORBE = item("necro_orbe", "Orbe nécrotique",
            Set.of(Tag.SUMMON, Tag.MAGIC),
            List.of(new Modifier(Affix.SUMMON_COUNT, 1), new Modifier(Affix.SPELL_POWER, 2)),
            250, Rarity.EPIC);

    public static final ItemDef CRISTAL_TEMPS = item("cristal_temps", "Cristal du temps",
            Set.of(Tag.SUPPORT, Tag.MAGIC),
            List.of(new Modifier(Affix.ATTACK_SPEED, 0.3)),
            300, Rarity.EPIC);

    public static final ItemDef SANG_DRAGON = item("sang_dragon", "Sang de dragon",
            Set.of(Tag.HEAL, Tag.MELEE),
            List.of(new Modifier(Affix.LIFESTEAL, 0.1), new Modifier(Affix.DAMAGE, 5)),
            500, Rarity.LEGENDARY);

    public static final ItemDef COURONNE_ROI = item("couronne_roi", "Couronne du roi déchu",
            Set.of(Tag.SUPPORT, Tag.DEFENSE, Tag.SUMMON),
            List.of(new Modifier(Affix.MAX_HEALTH, 6), new Modifier(Affix.SUMMON_COUNT, 1)),
            800, Rarity.LEGENDARY);

    public static final ItemDef FLAMME_ETERNELLE = item("flamme_eternelle", "Flamme éternelle",
            Set.of(Tag.MAGIC, Tag.MELEE),
            List.of(new Modifier(Affix.DAMAGE, 3), new Modifier(Affix.SPELL_POWER, 3)),
            700, Rarity.LEGENDARY);

    // ----- Lot 2 : items supplémentaires -----
    public static final ItemDef DAGUE_OMBRE = item("dague_ombre", "Dague de l'ombre",
            Set.of(Tag.MELEE), List.of(new Modifier(Affix.DAMAGE, 3), new Modifier(Affix.ATTACK_SPEED, 0.2)), 110, Rarity.COMMON);

    public static final ItemDef MARTEAU_TONNERRE = item("marteau_tonnerre", "Marteau du tonnerre",
            Set.of(Tag.MELEE), List.of(new Modifier(Affix.DAMAGE, 7), new Modifier(Affix.ATTACK_SPEED, -0.2)), 260, Rarity.RARE);

    public static final ItemDef BAGUETTE_GLACE = item("baguette_glace", "Baguette de glace",
            Set.of(Tag.MAGIC), List.of(new Modifier(Affix.SPELL_POWER, 4), new Modifier(Affix.MANA_REGEN, 1)), 180, Rarity.RARE);

    public static final ItemDef SCEPTRE_FOUDRE = item("sceptre_foudre", "Sceptre de foudre",
            Set.of(Tag.MAGIC, Tag.MELEE), List.of(new Modifier(Affix.SPELL_POWER, 5), new Modifier(Affix.DAMAGE, 2)), 400, Rarity.EPIC);

    public static final ItemDef AMULETTE_VIGUEUR = item("amulette_vigueur", "Amulette de vigueur",
            Set.of(Tag.SUPPORT, Tag.DEFENSE), List.of(new Modifier(Affix.MAX_HEALTH, 3)), 150, Rarity.RARE);

    public static final ItemDef ECAILLE_DRAGON = item("ecaille_dragon", "Écaille de dragon",
            Set.of(Tag.DEFENSE, Tag.MELEE), List.of(new Modifier(Affix.DEFENSE, 4), new Modifier(Affix.THORNS, 3)), 420, Rarity.EPIC);

    public static final ItemDef CHARME_BRAISE = item("charme_braise", "Charme de braise",
            Set.of(Tag.MAGIC, Tag.CURSED), List.of(new Modifier(Affix.SPELL_POWER, 6), new Modifier(Affix.MAX_HEALTH, -3)), 220, Rarity.RARE);

    public static final ItemDef RUNE_VIE = item("rune_vie", "Rune de vie",
            Set.of(Tag.HEAL, Tag.SUPPORT), List.of(new Modifier(Affix.HEAL_PER_SECOND, 1.5)), 260, Rarity.RARE);

    public static final ItemDef COUR_LOUPS = item("cour_loups", "Cour des loups",
            Set.of(Tag.SUMMON, Tag.SUPPORT), List.of(new Modifier(Affix.SUMMON_COUNT, 2), new Modifier(Affix.SUMMON_DAMAGE, 1)), 360, Rarity.EPIC);

    public static final ItemDef LAME_SPECTRALE = item("lame_spectrale", "Lame spectrale",
            Set.of(Tag.MELEE, Tag.CURSED), List.of(new Modifier(Affix.DAMAGE, 8), new Modifier(Affix.LIFESTEAL, 0.08), new Modifier(Affix.MAX_HEALTH, -4)), 480, Rarity.LEGENDARY);

    public static final ItemDef COURONNE_ARCANE = item("couronne_arcane", "Couronne arcanique",
            Set.of(Tag.MAGIC, Tag.SUPPORT), List.of(new Modifier(Affix.SPELL_POWER, 8), new Modifier(Affix.MANA_REGEN, 2)), 750, Rarity.LEGENDARY);

    public static final ItemDef BOUCLIER_SOLEIL = item("bouclier_soleil", "Bouclier du soleil",
            Set.of(Tag.DEFENSE, Tag.HEAL), List.of(new Modifier(Affix.DEFENSE, 6), new Modifier(Affix.HEAL_PER_SECOND, 1)), 650, Rarity.LEGENDARY);

    // ----- Lot 3 : objets de boutique -----
    public static final ItemDef BATON_NOUEUX = item("baton_noueux", "Bâton noueux",
            Set.of(Tag.MAGIC), List.of(new Modifier(Affix.SPELL_POWER, 2)), 50, Rarity.COMMON);

    public static final ItemDef BOUCLIER_BOIS = item("bouclier_bois", "Bouclier en bois",
            Set.of(Tag.DEFENSE), List.of(new Modifier(Affix.DEFENSE, 2), new Modifier(Affix.MAX_HEALTH, 2)), 60, Rarity.COMMON);

    public static final ItemDef AMULETTE_TREFLE = item("amulette_trefle", "Amulette de trèfle",
            Set.of(Tag.SUPPORT, Tag.HEAL), List.of(new Modifier(Affix.HEAL_PER_SECOND, 0.3), new Modifier(Affix.MAX_HEALTH, 1)), 65, Rarity.COMMON);

    public static final ItemDef SIFFLET_OS = item("sifflet_os", "Sifflet d'os",
            Set.of(Tag.SUMMON), List.of(new Modifier(Affix.SUMMON_COUNT, 1)), 70, Rarity.COMMON);

    public static final ItemDef GANT_RENFORCE = item("gant_renforce", "Gant renforcé",
            Set.of(Tag.MELEE), List.of(new Modifier(Affix.ATTACK_SPEED, 0.15), new Modifier(Affix.DAMAGE, 1)), 60, Rarity.COMMON);

    public static final ItemDef FIOLE_SOIN = item("fiole_soin", "Fiole de soin",
            Set.of(Tag.HEAL), List.of(new Modifier(Affix.HEAL_PER_SECOND, 0.7)), 70, Rarity.COMMON);

    public static final ItemDef EPEE_GIVRE = item("epee_givre", "Épée de givre",
            Set.of(Tag.MELEE, Tag.MAGIC), List.of(new Modifier(Affix.DAMAGE, 5), new Modifier(Affix.SPELL_POWER, 2)), 240, Rarity.RARE);

    public static final ItemDef HACHE_BERSERKER = item("hache_berserker", "Hache du berserker",
            Set.of(Tag.MELEE, Tag.CURSED), List.of(new Modifier(Affix.DAMAGE, 9), new Modifier(Affix.MAX_HEALTH, -3), new Modifier(Affix.ATTACK_SPEED, 0.2)), 260, Rarity.RARE);

    public static final ItemDef CAPE_OMBRE = item("cape_ombre", "Cape d'ombre",
            Set.of(Tag.DEFENSE, Tag.CURSED), List.of(new Modifier(Affix.DEFENSE, 4), new Modifier(Affix.THORNS, 3), new Modifier(Affix.MAX_HEALTH, -2)), 230, Rarity.RARE);

    public static final ItemDef COR_MEUTE = item("cor_meute", "Cor de meute",
            Set.of(Tag.SUMMON, Tag.SUPPORT), List.of(new Modifier(Affix.SUMMON_COUNT, 2), new Modifier(Affix.SUMMON_DAMAGE, 1)), 250, Rarity.RARE);

    public static final ItemDef ORBE_SOLEIL = item("orbe_soleil", "Orbe du soleil",
            Set.of(Tag.MAGIC, Tag.HEAL), List.of(new Modifier(Affix.SPELL_POWER, 4), new Modifier(Affix.HEAL_PER_SECOND, 1)), 270, Rarity.RARE);

    public static final ItemDef BAGUE_VAMPIRE = item("bague_vampire", "Bague vampire",
            Set.of(Tag.CURSED, Tag.HEAL), List.of(new Modifier(Affix.LIFESTEAL, 0.1), new Modifier(Affix.MAX_HEALTH, -2)), 240, Rarity.RARE);

    public static final ItemDef LANCE_TEMPETE = item("lance_tempete", "Lance de tempête",
            Set.of(Tag.MELEE, Tag.MAGIC), List.of(new Modifier(Affix.DAMAGE, 7), new Modifier(Affix.SPELL_POWER, 5), new Modifier(Affix.ATTACK_SPEED, 0.2)), 520, Rarity.EPIC);

    public static final ItemDef ARMURE_SPECTRALE = item("armure_spectrale", "Armure spectrale",
            Set.of(Tag.DEFENSE, Tag.SUMMON), List.of(new Modifier(Affix.DEFENSE, 5), new Modifier(Affix.SUMMON_DAMAGE, 3), new Modifier(Affix.MAX_HEALTH, 5)), 540, Rarity.EPIC);

    public static final ItemDef CALICE_VIE = item("calice_vie", "Calice de vie",
            Set.of(Tag.HEAL, Tag.SUPPORT), List.of(new Modifier(Affix.HEAL_PER_SECOND, 3), new Modifier(Affix.MAX_HEALTH, 4)), 500, Rarity.EPIC);

    public static final ItemDef MASQUE_CHAOS = item("masque_chaos", "Masque du chaos",
            Set.of(Tag.CURSED, Tag.MAGIC, Tag.MELEE), List.of(new Modifier(Affix.SPELL_POWER, 8), new Modifier(Affix.DAMAGE, 5), new Modifier(Affix.MAX_HEALTH, -6)), 450, Rarity.EPIC);

    public static final ItemDef EPEE_ZEUS = item("epee_zeus", "Épée de Zeus",
            Set.of(Tag.MELEE, Tag.MAGIC), List.of(new Modifier(Affix.DAMAGE, 12), new Modifier(Affix.SPELL_POWER, 8), new Modifier(Affix.ATTACK_SPEED, 0.3)), 1100, Rarity.LEGENDARY);

    public static final ItemDef TRIDENT_POSEIDON = item("trident_poseidon", "Trident de Poséidon",
            Set.of(Tag.MELEE, Tag.SUMMON), List.of(new Modifier(Affix.DAMAGE, 10), new Modifier(Affix.SUMMON_COUNT, 2), new Modifier(Affix.DEFENSE, 3)), 1000, Rarity.LEGENDARY);

    public static final ItemDef SCEPTRE_HADES = item("sceptre_hades", "Sceptre d'Hadès",
            Set.of(Tag.CURSED, Tag.SUMMON, Tag.MAGIC), List.of(new Modifier(Affix.SUMMON_COUNT, 3), new Modifier(Affix.SPELL_POWER, 8), new Modifier(Affix.MAX_HEALTH, -4)), 1150, Rarity.LEGENDARY);

    public static final ItemDef EGIDE_ATHENA = item("egide_athena", "Égide d'Athéna",
            Set.of(Tag.DEFENSE, Tag.SUPPORT, Tag.HEAL), List.of(new Modifier(Affix.DEFENSE, 8), new Modifier(Affix.HEAL_PER_SECOND, 2), new Modifier(Affix.MAX_HEALTH, 6)), 1050, Rarity.LEGENDARY);

    public static final ItemDef LARME_DIEUX = item("larme_dieux", "Larme des dieux",
            Set.of(Tag.HEAL, Tag.SUPPORT, Tag.MAGIC), List.of(new Modifier(Affix.HEAL_PER_SECOND, 5), new Modifier(Affix.SPELL_POWER, 10), new Modifier(Affix.MANA_REGEN, 4)), 3000, Rarity.MYTHIC);

    public static final ItemDef FOUDRE_ZEUS = item("foudre_zeus", "Foudre de Zeus",
            Set.of(Tag.MELEE, Tag.MAGIC), List.of(new Modifier(Affix.DAMAGE, 14), new Modifier(Affix.SPELL_POWER, 12), new Modifier(Affix.ATTACK_SPEED, 0.4)), 2500, Rarity.MYTHIC);

    public static final ItemDef VOILE_NUIT = item("voile_nuit", "Voile de la nuit éternelle",
            Set.of(Tag.CURSED, Tag.SUMMON, Tag.DEFENSE), List.of(new Modifier(Affix.SUMMON_COUNT, 4), new Modifier(Affix.DEFENSE, 8), new Modifier(Affix.MAX_HEALTH, -6), new Modifier(Affix.THORNS, 6)), 2800, Rarity.MYTHIC);

    // ----- Lot 4 : invocation et mana -----
    public static final ItemDef SIFFLET_MEUTE = item("sifflet_meute", "Sifflet de meute",
            Set.of(Tag.SUMMON), List.of(new Modifier(Affix.SUMMON_COUNT, 1)), 90, Rarity.COMMON);

    public static final ItemDef COLLIER_MEUTE = item("collier_meute", "Collier de meute",
            Set.of(Tag.SUMMON, Tag.SUPPORT), List.of(new Modifier(Affix.SUMMON_COUNT, 1), new Modifier(Affix.MAX_HEALTH, 3)), 170, Rarity.RARE);

    public static final ItemDef FLUTE_INVOCATION = item("flute_invocation", "Flûte d'invocation",
            Set.of(Tag.SUMMON, Tag.MAGIC), List.of(new Modifier(Affix.SUMMON_COUNT, 2), new Modifier(Affix.MANA_REGEN, 1)), 260, Rarity.RARE);

    public static final ItemDef OS_DE_LUNE = item("os_de_lune", "Os de lune",
            Set.of(Tag.SUMMON, Tag.DEFENSE), List.of(new Modifier(Affix.SUMMON_DAMAGE, 3), new Modifier(Affix.DEFENSE, 2)), 360, Rarity.EPIC);

    public static final ItemDef TOTEM_ANCESTRAL = item("totem_ancestral", "Totem ancestral",
            Set.of(Tag.SUMMON, Tag.MAGIC), List.of(new Modifier(Affix.SUMMON_COUNT, 3), new Modifier(Affix.SPELL_POWER, 3), new Modifier(Affix.MANA_REGEN, 2)), 480, Rarity.EPIC);

    public static final ItemDef SCEPTRE_ASTRAL = item("sceptre_astral", "Sceptre astral",
            Set.of(Tag.SUMMON, Tag.MAGIC), List.of(new Modifier(Affix.SUMMON_COUNT, 4), new Modifier(Affix.MANA_REGEN, 8), new Modifier(Affix.SPELL_POWER, 8)), 1100, Rarity.LEGENDARY);

    public static final ItemDef CRISTAL_MANA = item("cristal_mana", "Cristal de mana",
            Set.of(Tag.MAGIC), List.of(new Modifier(Affix.MANA_REGEN, 3), new Modifier(Affix.SPELL_POWER, 1)), 70, Rarity.COMMON);

    public static final ItemDef PIERRE_ARCANE = item("pierre_arcane", "Pierre arcane",
            Set.of(Tag.MAGIC), List.of(new Modifier(Affix.MANA_REGEN, 4), new Modifier(Affix.SPELL_POWER, 3)), 200, Rarity.RARE);

    public static final ItemDef ORBE_MANA_PUR = item("orbe_mana_pur", "Orbe de mana pur",
            Set.of(Tag.MAGIC, Tag.SUPPORT), List.of(new Modifier(Affix.MANA_REGEN, 6), new Modifier(Affix.SPELL_POWER, 4)), 420, Rarity.EPIC);

    // ----- Lot 5 : résultats de recettes secrètes -----
    public static final ItemDef LANTERNE_OSSEMENTS = item("lanterne_ossements", "Lanterne des ossements",
            Set.of(Tag.SUMMON, Tag.MAGIC, Tag.DEFENSE), List.of(new Modifier(Affix.SUMMON_COUNT, 1), new Modifier(Affix.MANA_REGEN, 4), new Modifier(Affix.DEFENSE, 2)), 250, Rarity.EPIC);

    public static final ItemDef LAME_CREPUSCULE = item("lame_crepuscule", "Lame du crépuscule",
            Set.of(Tag.MELEE, Tag.MAGIC), List.of(new Modifier(Affix.DAMAGE, 6), new Modifier(Affix.SPELL_POWER, 3), new Modifier(Affix.ATTACK_SPEED, 0.25)), 300, Rarity.EPIC);

    public static final ItemDef ARMURE_RUNIQUE = item("armure_runique", "Armure runique",
            Set.of(Tag.DEFENSE, Tag.HEAL), List.of(new Modifier(Affix.DEFENSE, 6), new Modifier(Affix.HEAL_PER_SECOND, 2), new Modifier(Affix.THORNS, 3)), 500, Rarity.LEGENDARY);

    public static final ItemDef CHEF_MEUTE = item("chef_meute", "Chef de meute",
            Set.of(Tag.SUMMON, Tag.SUPPORT), List.of(new Modifier(Affix.SUMMON_COUNT, 3), new Modifier(Affix.SUMMON_DAMAGE, 2)), 300, Rarity.EPIC);

    public static final ItemDef HYMNE_ETHER = item("hymne_ether", "Hymne de l'éther",
            Set.of(Tag.MAGIC, Tag.SUMMON, Tag.SUPPORT), List.of(new Modifier(Affix.MANA_REGEN, 8), new Modifier(Affix.SPELL_POWER, 4), new Modifier(Affix.SUMMON_COUNT, 1)), 700, Rarity.LEGENDARY);

    public static final ItemDef PANTHEON_ANIMAL = item("pantheon_animal", "Panthéon animal",
            Set.of(Tag.SUMMON, Tag.MAGIC), List.of(new Modifier(Affix.SUMMON_COUNT, 5), new Modifier(Affix.SUMMON_DAMAGE, 4), new Modifier(Affix.SPELL_POWER, 4)), 1500, Rarity.MYTHIC);

    public static final ItemDef ECLAT_TONNERRE = item("eclat_tonnerre", "Éclat du tonnerre",
            Set.of(Tag.MELEE), List.of(new Modifier(Affix.DAMAGE, 11), new Modifier(Affix.ATTACK_SPEED, 0.1)), 350, Rarity.EPIC);

    public static final ItemDef SCEPTRE_NEANT = item("sceptre_neant_pur", "Sceptre du néant pur",
            Set.of(Tag.MAGIC, Tag.CURSED), List.of(new Modifier(Affix.SPELL_POWER, 9), new Modifier(Affix.MAX_HEALTH, -3), new Modifier(Affix.MANA_REGEN, 5)), 280, Rarity.EPIC);

    // ----- Résultats de recettes spéciales -----
    public static final ItemDef FAUX_ALCHIMISTE = item("faux_alchimiste", "Faux de l'alchimiste",
            Set.of(Tag.MELEE, Tag.SUPPORT), List.of(new Modifier(Affix.DAMAGE, 6), new Modifier(Affix.MANA_REGEN, 3), new Modifier(Affix.ATTACK_SPEED, 0.3)), 80, Rarity.RARE);

    public static final ItemDef MOISSON_NOIRE = item("moisson_noire", "Moisson noire",
            Set.of(Tag.MELEE, Tag.CURSED), List.of(new Modifier(Affix.DAMAGE, 9), new Modifier(Affix.LIFESTEAL, 0.12), new Modifier(Affix.MAX_HEALTH, -4)), 90, Rarity.EPIC);

    public static final ItemDef SOURCE_ETERNELLE = item("source_eternelle", "Source éternelle",
            Set.of(Tag.SUPPORT, Tag.HEAL, Tag.MAGIC), List.of(new Modifier(Affix.HEAL_PER_SECOND, 2), new Modifier(Affix.MANA_REGEN, 3), new Modifier(Affix.MAX_HEALTH, 3)), 100, Rarity.RARE);

    public static final ItemDef REMPART_TRANCHANT = item("rempart_tranchant", "Rempart tranchant",
            Set.of(Tag.DEFENSE, Tag.MELEE), List.of(new Modifier(Affix.DEFENSE, 4), new Modifier(Affix.THORNS, 5), new Modifier(Affix.DAMAGE, 3)), 100, Rarity.RARE);

    public static final ItemDef BATON_CHAMAN = item("baton_chaman", "Bâton du chaman",
            Set.of(Tag.MAGIC, Tag.SUMMON), List.of(new Modifier(Affix.SPELL_POWER, 6), new Modifier(Affix.SUMMON_COUNT, 2), new Modifier(Affix.SUMMON_DAMAGE, 3)), 150, Rarity.EPIC);

    public static final ItemDef COEUR_SACRE = item("coeur_sacre", "Cœur sacré",
            Set.of(Tag.HEAL, Tag.SUPPORT, Tag.DEFENSE), List.of(new Modifier(Affix.HEAL_PER_SECOND, 3), new Modifier(Affix.MAX_HEALTH, 8), new Modifier(Affix.LIFESTEAL, 0.1)), 220, Rarity.EPIC);

    public static final ItemDef GRIMOIRE_GIVRE_NOIR = item("grimoire_givre_noir", "Grimoire du givre noir",
            Set.of(Tag.MAGIC, Tag.CURSED), List.of(new Modifier(Affix.SPELL_POWER, 12), new Modifier(Affix.DEFENSE, 3), new Modifier(Affix.MANA_REGEN, 2)), 260, Rarity.EPIC);

    public static final ItemDef MEUTE_EPINEUSE = item("meute_epineuse", "Meute épineuse",
            Set.of(Tag.SUMMON, Tag.DEFENSE), List.of(new Modifier(Affix.SUMMON_COUNT, 3), new Modifier(Affix.SUMMON_DAMAGE, 4), new Modifier(Affix.THORNS, 5), new Modifier(Affix.MAX_HEALTH, 4)), 300, Rarity.EPIC);

    public static final ItemDef LAME_VAMPIRE_SUPREME = item("lame_vampire_supreme", "Lame du vampire suprême",
            Set.of(Tag.MELEE, Tag.CURSED, Tag.HEAL), List.of(new Modifier(Affix.DAMAGE, 14), new Modifier(Affix.LIFESTEAL, 0.25), new Modifier(Affix.ATTACK_SPEED, 0.3), new Modifier(Affix.MAX_HEALTH, -5)), 700, Rarity.LEGENDARY);

    public static final ItemDef COURONNE_DEUX_ROYAUMES = item("couronne_deux_royaumes", "Couronne des deux royaumes",
            Set.of(Tag.MAGIC, Tag.SUPPORT, Tag.DEFENSE, Tag.SUMMON), List.of(new Modifier(Affix.SPELL_POWER, 14), new Modifier(Affix.SUMMON_COUNT, 3), new Modifier(Affix.MAX_HEALTH, 8), new Modifier(Affix.MANA_REGEN, 4)), 1500, Rarity.MYTHIC);

    public static final ItemDef SOUFFLE_DRAGON_ANCIEN = item("souffle_dragon_ancien", "Souffle du dragon ancien",
            Set.of(Tag.MELEE, Tag.MAGIC, Tag.HEAL), List.of(new Modifier(Affix.DAMAGE, 12), new Modifier(Affix.SPELL_POWER, 10), new Modifier(Affix.LIFESTEAL, 0.15)), 1200, Rarity.MYTHIC);

    public static final ItemDef SCEPTRE_TEMPS_ARRETE = item("sceptre_temps_arrete", "Sceptre du temps arrêté",
            Set.of(Tag.MAGIC, Tag.SUPPORT, Tag.MELEE), List.of(new Modifier(Affix.SPELL_POWER, 12), new Modifier(Affix.ATTACK_SPEED, 0.8), new Modifier(Affix.MANA_REGEN, 4)), 650, Rarity.LEGENDARY);

    public static final ItemDef MARTEAU_SOLEIL_LEVANT = item("marteau_soleil_levant", "Marteau du soleil levant",
            Set.of(Tag.MELEE, Tag.DEFENSE, Tag.HEAL), List.of(new Modifier(Affix.DAMAGE, 12), new Modifier(Affix.DEFENSE, 6), new Modifier(Affix.HEAL_PER_SECOND, 2)), 750, Rarity.LEGENDARY);

    public static final ItemDef DOUBLE_MALEDICTION = item("double_malediction", "Double malédiction",
            Set.of(Tag.CURSED, Tag.MAGIC, Tag.MELEE), List.of(new Modifier(Affix.SPELL_POWER, 14), new Modifier(Affix.DAMAGE, 8), new Modifier(Affix.MAX_HEALTH, -2)), 400, Rarity.LEGENDARY);

    public static final ItemDef GRIMOIRE_DU_PACTE = item("grimoire_du_pacte", "Grimoire du pacte",
            Set.of(Tag.MAGIC, Tag.CURSED),
            List.of(new Modifier(Affix.SPELL_POWER, 10), new Modifier(Affix.LIFESTEAL, 0.05)),
            440, Rarity.MYTHIC);

    /** Tous les items, par id. Sert au loot, à la boutique et aux commandes. */
    public static final Map<String, ItemDef> ALL = new LinkedHashMap<>();

    /** Objets qu'on ne peut obtenir que par fusion : absents de la boutique. */
    public static final Set<String> RECIPE_ONLY;

    /** Catalogue de base figé (pas de fusions ni d'objets générés). */
    public static final Map<String, ItemDef> BASE;

    /** Ce qui est vendu en boutique : le catalogue sans les objets de recette. */
    public static final Map<String, ItemDef> SHOP;

    static {
        ArmorDefs.ALL.forEach(d -> ALL.put(d.id(), d));
        for (ItemDef d : List.of(EPEE_BRAISE, SERPE_ROUILLEE, BOUCLIER_CENDRE, BATON_ARCANE,
                ANNEAU_VIE, POTION_MANA, GRIMOIRE_SOIN, TOTEM_LOUP, ESPRIT_LOUP, ARMURE_EPINES,
                ARC_CHASSE, PACTE_SANGLANT, LIVRE_OMBRES, COEUR_CORROMPU, NECRO_ORBE,
                CRISTAL_TEMPS, SANG_DRAGON, COURONNE_ROI, FLAMME_ETERNELLE, GRIMOIRE_DU_PACTE,
                DAGUE_OMBRE, MARTEAU_TONNERRE, BAGUETTE_GLACE, SCEPTRE_FOUDRE, AMULETTE_VIGUEUR,
                ECAILLE_DRAGON, CHARME_BRAISE, RUNE_VIE, COUR_LOUPS, LAME_SPECTRALE,
                COURONNE_ARCANE, BOUCLIER_SOLEIL,
                LANTERNE_OSSEMENTS, LAME_CREPUSCULE, ARMURE_RUNIQUE, CHEF_MEUTE, HYMNE_ETHER, PANTHEON_ANIMAL, ECLAT_TONNERRE, SCEPTRE_NEANT,
                SIFFLET_MEUTE, COLLIER_MEUTE, FLUTE_INVOCATION, OS_DE_LUNE, TOTEM_ANCESTRAL, SCEPTRE_ASTRAL, CRISTAL_MANA, PIERRE_ARCANE, ORBE_MANA_PUR,
                BATON_NOUEUX, BOUCLIER_BOIS, AMULETTE_TREFLE, SIFFLET_OS, GANT_RENFORCE, FIOLE_SOIN, EPEE_GIVRE, HACHE_BERSERKER, CAPE_OMBRE, COR_MEUTE, ORBE_SOLEIL, BAGUE_VAMPIRE, LANCE_TEMPETE, ARMURE_SPECTRALE, CALICE_VIE, MASQUE_CHAOS, EPEE_ZEUS, TRIDENT_POSEIDON, SCEPTRE_HADES, EGIDE_ATHENA, LARME_DIEUX, FOUDRE_ZEUS, VOILE_NUIT, FAUX_ALCHIMISTE, MOISSON_NOIRE, SOURCE_ETERNELLE, REMPART_TRANCHANT, BATON_CHAMAN, COEUR_SACRE, GRIMOIRE_GIVRE_NOIR, MEUTE_EPINEUSE, LAME_VAMPIRE_SUPREME, COURONNE_DEUX_ROYAUMES, SOUFFLE_DRAGON_ANCIEN, SCEPTRE_TEMPS_ARRETE, MARTEAU_SOLEIL_LEVANT, DOUBLE_MALEDICTION)) {
            ALL.put(d.id(), d);
        }

        // Recettes spéciales : résultat précis pour une paire donnée
        Fusion.registerRecipe(PACTE_SANGLANT, GRIMOIRE_SOIN, GRIMOIRE_DU_PACTE);
        Fusion.registerRecipe(LIVRE_OMBRES, NECRO_ORBE, GRIMOIRE_DU_PACTE);
        Fusion.registerRecipe(EPEE_BRAISE, CRISTAL_TEMPS, FLAMME_ETERNELLE);
        Fusion.registerRecipe(SERPE_ROUILLEE, POTION_MANA, FAUX_ALCHIMISTE);
        Fusion.registerRecipe(SERPE_ROUILLEE, PACTE_SANGLANT, MOISSON_NOIRE);
        Fusion.registerRecipe(ANNEAU_VIE, POTION_MANA, SOURCE_ETERNELLE);
        Fusion.registerRecipe(BOUCLIER_CENDRE, SERPE_ROUILLEE, REMPART_TRANCHANT);
        Fusion.registerRecipe(BATON_ARCANE, TOTEM_LOUP, BATON_CHAMAN);
        Fusion.registerRecipe(COEUR_CORROMPU, ANNEAU_VIE, COEUR_SACRE);
        Fusion.registerRecipe(LIVRE_OMBRES, BAGUETTE_GLACE, GRIMOIRE_GIVRE_NOIR);
        Fusion.registerRecipe(ESPRIT_LOUP, ARMURE_EPINES, MEUTE_EPINEUSE);
        Fusion.registerRecipe(LAME_SPECTRALE, COEUR_CORROMPU, LAME_VAMPIRE_SUPREME);
        Fusion.registerRecipe(COURONNE_ARCANE, COURONNE_ROI, COURONNE_DEUX_ROYAUMES);
        Fusion.registerRecipe(SANG_DRAGON, FLAMME_ETERNELLE, SOUFFLE_DRAGON_ANCIEN);
        Fusion.registerRecipe(CRISTAL_TEMPS, SCEPTRE_FOUDRE, SCEPTRE_TEMPS_ARRETE);
        Fusion.registerRecipe(MARTEAU_TONNERRE, BOUCLIER_SOLEIL, MARTEAU_SOLEIL_LEVANT);
        Fusion.registerRecipe(OS_DE_LUNE, CRISTAL_MANA, LANTERNE_OSSEMENTS);
        Fusion.registerRecipe(DAGUE_OMBRE, PIERRE_ARCANE, LAME_CREPUSCULE);
        Fusion.registerRecipe(RUNE_VIE, ECAILLE_DRAGON, ARMURE_RUNIQUE);
        Fusion.registerRecipe(SIFFLET_MEUTE, COLLIER_MEUTE, CHEF_MEUTE);
        Fusion.registerRecipe(FLUTE_INVOCATION, ORBE_MANA_PUR, HYMNE_ETHER);
        Fusion.registerRecipe(COUR_LOUPS, TOTEM_ANCESTRAL, PANTHEON_ANIMAL);
        Fusion.registerRecipe(MARTEAU_TONNERRE, DAGUE_OMBRE, ECLAT_TONNERRE);
        Fusion.registerRecipe(BATON_ARCANE, CRISTAL_MANA, SCEPTRE_NEANT);
        Fusion.registerRecipe(CHARME_BRAISE, PACTE_SANGLANT, DOUBLE_MALEDICTION);

        RECIPE_ONLY = Set.of("grimoire_du_pacte", "lanterne_ossements", "lame_crepuscule", "armure_runique", "chef_meute", "hymne_ether", "pantheon_animal", "eclat_tonnerre", "sceptre_neant_pur", "faux_alchimiste", "moisson_noire", "source_eternelle", "rempart_tranchant", "baton_chaman", "coeur_sacre", "grimoire_givre_noir", "meute_epineuse", "lame_vampire_supreme", "couronne_deux_royaumes", "souffle_dragon_ancien", "sceptre_temps_arrete", "marteau_soleil_levant", "double_malediction");
        BASE = Collections.unmodifiableMap(new LinkedHashMap<>(ALL));
        Map<String, ItemDef> shop = new LinkedHashMap<>(ALL);
        for (String id : RECIPE_ONLY) shop.remove(id);
        SHOP = Collections.unmodifiableMap(shop);
    }

    private Items() {}

    private static ItemDef item(String id, String name, Set<Tag> tags, List<Modifier> mods, long price, Rarity rarity) {
        return new ItemDef(id, name, tags, mods, price, rarity);
    }
}
