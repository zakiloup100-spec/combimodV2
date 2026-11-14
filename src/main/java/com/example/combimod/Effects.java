package com.example.combimod;

import com.example.combimod.core.Affix;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

/** Applique les modificateurs passifs. Les autres effets (dégâts, invocations) restent à faire. */
public final class Effects {

    private Effects() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 != 0) return;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                Mana.tick(player);
                double heal = totalBonus(player.getInventory(), Affix.HEAL_PER_SECOND);
                if (heal > 0) player.heal((float) heal);
                ArmorSets.apply(player);
            }
        });
    }

    static double totalBonus(Inventory inv, Affix affix) {
        double total = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            total += ItemUtil.bonus(inv.getItem(i), affix);
        }
        return total;
    }
}
