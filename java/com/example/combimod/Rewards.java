package com.example.combimod;

import com.example.combimod.core.ItemDef;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Donne des objets et des pièces au joueur, avec messages et suivi du codex. */
public final class Rewards {

    private Rewards() {}

    public static void give(ServerPlayer player, ItemDef def) {
        ItemStack stack = ItemUtil.makeStack(def);
        if (!player.getInventory().add(stack)) player.drop(stack, false);

        player.sendSystemMessage(Component.literal("Obtenu : " + def.name())
                .withStyle(Descriptions.color(def.rarity())));
        if (CombiMod.CODEX.discover(player.getUUID(), def)) {
            player.sendSystemMessage(Component.literal("Nouvelle découverte ! /codex pour voir ta collection")
                    .withStyle(ChatFormatting.GREEN));
        }
    }

    public static void coins(ServerPlayer player, long amount) {
        CombiMod.ECONOMY.add(player.getUUID(), amount);
        player.displayClientMessage(Component.literal("+" + amount + " pièces").withStyle(ChatFormatting.YELLOW), true);
    }
}
