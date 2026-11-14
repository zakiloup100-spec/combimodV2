package com.example.combimod.core;

import java.util.*;

/**
 * Chaque arme et baguette porte un trio d'effets appliqué à la cible touchée.
 * Il y a 91 combinaisons possibles (2, 3 ou 4 effets parmi 7). Les objets du catalogue reçoivent chacun une
 * combinaison unique ; les objets générés en reçoivent une selon leur identifiant.
 */
public final class Traits {

    public enum Trait {
        SLOW("Lenteur"), POISON("Poison"), WEAKEN("Faiblesse"), BLIND("Cécité"),
        WITHER("Flétrissement"), GLOW("Lueur"), LEVITATE("Lévitation");

        public final String label;

        Trait(String label) {
            this.label = label;
        }
    }

    private static final List<Set<Trait>> TRIOS = buildTrios();
    private static Map<String, Integer> catalogIndex;

    private Traits() {}

    /** Toutes les combinaisons de 2, 3 ou 4 effets : 21 + 35 + 35 = 91. */
    private static List<Set<Trait>> buildTrios() {
        List<Set<Trait>> out = new ArrayList<>();
        Trait[] all = Trait.values();
        for (int mask = 0; mask < (1 << all.length); mask++) {
            int bits = Integer.bitCount(mask);
            if (bits < 2 || bits > 4) continue;
            EnumSet<Trait> set = EnumSet.noneOf(Trait.class);
            for (int i = 0; i < all.length; i++) if ((mask & (1 << i)) != 0) set.add(all[i]);
            out.add(Collections.unmodifiableSet(set));
        }
        return Collections.unmodifiableList(out);
    }

    public static int trioCount() {
        return TRIOS.size();
    }

    public static boolean isWeapon(ItemDef d) {
        return d.tags().contains(Tag.MELEE) || d.tags().contains(Tag.MAGIC);
    }

    /** Index stable des armes du catalogue, triées par identifiant : chacune a son propre trio. */
    private static synchronized Map<String, Integer> catalogIndex() {
        if (catalogIndex == null) {
            List<String> ids = new ArrayList<>();
            for (ItemDef d : Items.BASE.values()) if (isWeapon(d)) ids.add(d.id());
            Collections.sort(ids);
            Map<String, Integer> m = new HashMap<>();
            for (int i = 0; i < ids.size(); i++) m.put(ids.get(i), i);
            catalogIndex = m;
        }
        return catalogIndex;
    }

    public static int catalogWeaponCount() {
        return catalogIndex().size();
    }

    /** Combinaison d'effets de l'objet. Vide si ce n'est pas une arme ni une baguette. */
    public static Set<Trait> of(ItemDef d) {
        if (!isWeapon(d)) return Set.of();
        Integer idx = catalogIndex().get(d.id());
        int i = idx != null ? idx : (int) Math.floorMod(Ids.seed(d.id()) >>> 1, (long) TRIOS.size());
        return TRIOS.get(i % TRIOS.size());
    }
}
