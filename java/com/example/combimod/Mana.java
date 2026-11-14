package com.example.combimod;

import com.example.combimod.core.Affix;
import com.example.combimod.core.ManaPool;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/** Mana d'un joueur : maximum et régénération viennent des objets de son inventaire (affixe MANA_REGEN). */
public final class Mana {

    private Mana() {}

    public static double max(ServerPlayer player) {
        return ManaPool.maxFor(Effects.totalBonus(player.getInventory(), Affix.MANA_REGEN));
    }

    /** Dépense le coût. Si ce n'est pas possible, prévient le joueur et renvoie false. */
    public static boolean spend(ServerPlayer player, double cost) {
        if (CombiMod.MANA.consume(player.getUUID(), max(player), cost)) return true;
        player.displayClientMessage(Component.literal("Pas assez de mana (" + (int) cost + " requis)")
                .withStyle(ChatFormatting.RED), true);
        return false;
    }

    /** Appelée chaque seconde : régénère et affiche la réserve dans la barre d'action. */
    public static void tick(ServerPlayer player) {
        double max = max(player);
        double regen = ManaPool.regenFor(Effects.totalBonus(player.getInventory(), Affix.MANA_REGEN));
        CombiMod.MANA.regen(player.getUUID(), max, regen);
        double now = CombiMod.MANA.get(player.getUUID(), max);
        player.displayClientMessage(Component.literal("Mana : " + (int) now + " / " + (int) max)
                .withStyle(ChatFormatting.BLUE), true);
    }
}
