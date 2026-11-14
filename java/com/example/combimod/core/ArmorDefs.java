package com.example.combimod.core;

import java.util.List;
import java.util.Set;

/** Définitions des armures custom. Les valeurs de défense doivent correspondre à ModArmor. */
public final class ArmorDefs {

    private ArmorDefs() {}

    public static final List<ItemDef> ALL = List.of(
            piece("cuir_runique_casque", "Casque en cuir runique", Rarity.COMMON, 60, 3),
            piece("cuir_runique_plastron", "Plastron en cuir runique", Rarity.COMMON, 60, 5),
            piece("cuir_runique_pantalon", "Pantalon en cuir runique", Rarity.COMMON, 60, 4),
            piece("cuir_runique_bottes", "Bottes en cuir runique", Rarity.COMMON, 60, 3),

            piece("maille_runique_casque", "Heaume de maille runique", Rarity.RARE, 180, 6),
            piece("maille_runique_plastron", "Haubert de maille runique", Rarity.RARE, 180, 10),
            piece("maille_runique_pantalon", "Chausses de maille runique", Rarity.RARE, 180, 8),
            piece("maille_runique_bottes", "Solerets de maille runique", Rarity.RARE, 180, 5),

            piece("zeus_casque", "Casque du dieu Zeus", Rarity.LEGENDARY, 900, 10),
            piece("zeus_cuirasse", "Cuirasse du dieu Zeus", Rarity.LEGENDARY, 900, 16),
            piece("zeus_pantalon", "Pantalon du dieu Zeus", Rarity.LEGENDARY, 900, 14),
            piece("zeus_bottes", "Bottes du dieu Zeus", Rarity.LEGENDARY, 900, 10)
    );

    /** Sets qui donnent un bonus quand les 4 pièces sont portées. */
    public static final Set<String> ZEUS_SET = Set.of("zeus_casque", "zeus_cuirasse", "zeus_pantalon", "zeus_bottes");
    public static final Set<String> MAILLE_SET = Set.of("maille_runique_casque", "maille_runique_plastron",
            "maille_runique_pantalon", "maille_runique_bottes");

    private static ItemDef piece(String id, String name, Rarity rarity, long price, int defense) {
        return new ItemDef(id, name, Set.of(Tag.DEFENSE),
                List.of(new Modifier(Affix.DEFENSE, defense)), price, rarity);
    }
}
