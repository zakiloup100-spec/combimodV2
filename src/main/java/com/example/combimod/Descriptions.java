package com.example.combimod;

import com.example.combimod.core.Affix;
import com.example.combimod.core.ItemDef;
import com.example.combimod.core.Items;
import com.example.combimod.core.Modifier;
import com.example.combimod.core.Rarity;
import com.example.combimod.core.Tag;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Textes affichés au survol : rareté, tags, stats (valeurs réelles selon la rareté) et prix. */
public final class Descriptions {

    private Descriptions() {}

    public static ChatFormatting color(Rarity rarity) {
        return switch (rarity) {
            case COMMON -> ChatFormatting.WHITE;
            case RARE -> ChatFormatting.AQUA;
            case EPIC -> ChatFormatting.LIGHT_PURPLE;
            case LEGENDARY -> ChatFormatting.GOLD;
            case MYTHIC -> ChatFormatting.RED;
        };
    }

    public static List<Component> lines(ItemDef def) {
        Rarity r = def.rarity();
        List<Component> out = new ArrayList<>();
        out.add(Component.literal(rarityLabel(r)).withStyle(color(r)));
        out.add(Component.literal("Puissance : x" + trim(r.power)).withStyle(ChatFormatting.GRAY));

        if (!def.tags().isEmpty()) {
            StringBuilder tags = new StringBuilder("Type : ");
            for (Tag t : def.tags()) tags.append(tagLabel(t)).append(' ');
            out.add(Component.literal(tags.toString().trim()).withStyle(ChatFormatting.GRAY));
        }

        out.add(Component.literal(" "));
        for (Modifier m : def.modifiers()) {
            double value = m.value() * r.power;
            ChatFormatting c = value >= 0 ? ChatFormatting.BLUE : ChatFormatting.RED;
            out.add(Component.literal(format(m.affix(), value)).withStyle(c));
        }

        if (com.example.combimod.core.Traits.isWeapon(def)) {
            StringBuilder effects = new StringBuilder("Effets : ");
            for (var t : com.example.combimod.core.Traits.of(def)) effects.append(t.label).append(' ');
            out.add(Component.literal(effects.toString().trim()).withStyle(ChatFormatting.DARK_GREEN));
        }
        if (def.tags().contains(Tag.CURSED)) {
            out.add(Component.literal("Maudit : les malus se retournent en fusion").withStyle(ChatFormatting.DARK_PURPLE));
        }
        if (Items.RECIPE_ONLY.contains(def.id())) {
            out.add(Component.literal("Recette secrète : ne s'obtient que par fusion").withStyle(ChatFormatting.DARK_AQUA));
        }
        if (def.id().startsWith("f:")) {
            out.add(Component.literal("Fusion unique").withStyle(ChatFormatting.DARK_AQUA));
        }
        out.add(Component.literal("Valeur : " + def.price()).withStyle(ChatFormatting.YELLOW));
        return out;
    }

    static String format(Affix affix, double value) {
        String sign = value >= 0 ? "+" : "";
        if (affix == Affix.LIFESTEAL) {
            return sign + trim(value * 100) + "% " + affixLabel(affix);
        }
        return sign + trim(value) + " " + affixLabel(affix);
    }

    /** Nombre entier si possible, sinon 2 décimales. */
    static String trim(double v) {
        if (v == Math.rint(v)) return String.valueOf((long) v);
        return String.format(Locale.ROOT, "%.2f", v);
    }

    static String rarityLabel(Rarity r) {
        return switch (r) {
            case COMMON -> "Commun";
            case RARE -> "Rare";
            case EPIC -> "Épique";
            case LEGENDARY -> "Légendaire";
            case MYTHIC -> "Mythique";
        };
    }

    static String tagLabel(Tag t) {
        return switch (t) {
            case MELEE -> "Mêlée";
            case MAGIC -> "Magie";
            case SUMMON -> "Invocation";
            case SUPPORT -> "Soutien";
            case HEAL -> "Soin";
            case DEFENSE -> "Défense";
            case CURSED -> "Maudit";
        };
    }

    static String affixLabel(Affix a) {
        return switch (a) {
            case DAMAGE -> "dégâts";
            case ATTACK_SPEED -> "vitesse d'attaque";
            case MAX_HEALTH -> "vie max";
            case HEAL_PER_SECOND -> "soin/s";
            case LIFESTEAL -> "vol de vie";
            case MANA_REGEN -> "régén. mana";
            case SPELL_POWER -> "puissance des sorts";
            case SUMMON_COUNT -> "invocations";
            case SUMMON_DAMAGE -> "dégâts d'invocation";
            case THORNS -> "épines";
            case DEFENSE -> "défense";
        };
    }
}
