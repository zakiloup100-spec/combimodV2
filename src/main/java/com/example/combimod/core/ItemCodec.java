package com.example.combimod.core;

import java.util.*;

/**
 * Texte compact d'un ItemDef, stocké dans l'objet lui-même. Ainsi les objets générés et fusionnés
 * gardent leurs stats sans fichier de sauvegarde, et le client peut les afficher.
 * Format : id|nom|RARETÉ|TAG,TAG|AFFIXE:valeur,AFFIXE:valeur|prix
 */
public final class ItemCodec {

    private ItemCodec() {}

    public static String encode(ItemDef d) {
        StringJoiner tags = new StringJoiner(",");
        for (Tag t : d.tags()) tags.add(t.name());
        StringJoiner mods = new StringJoiner(",");
        for (Modifier m : d.modifiers()) mods.add(m.affix().name() + ":" + m.value());
        return String.join("|", d.id(), d.name().replace('|', '/'), d.rarity().name(),
                tags.toString(), mods.toString(), Long.toString(d.price()));
    }

    /** Renvoie null si le texte est invalide. */
    public static ItemDef decode(String s) {
        try {
            String[] p = s.split("\\|", -1);
            if (p.length != 6) return null;
            Set<Tag> tags = EnumSet.noneOf(Tag.class);
            if (!p[3].isEmpty()) for (String t : p[3].split(",")) tags.add(Tag.valueOf(t));
            List<Modifier> mods = new ArrayList<>();
            if (!p[4].isEmpty()) {
                for (String m : p[4].split(",")) {
                    int c = m.indexOf(':');
                    mods.add(new Modifier(Affix.valueOf(m.substring(0, c)), Double.parseDouble(m.substring(c + 1))));
                }
            }
            return new ItemDef(p[0], p[1], Collections.unmodifiableSet(tags), List.copyOf(mods),
                    Long.parseLong(p[5]), Rarity.valueOf(p[2]));
        } catch (Exception e) {
            return null;
        }
    }
}
