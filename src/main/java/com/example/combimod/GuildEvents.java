package com.example.combimod;

import com.example.combimod.core.CapturePoints;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Boucle de l'événement : détection des guildes sur les points, captures, revenus, fin de guerre. */
public final class GuildEvents {

    /** Rayon autour du centre d'un point. */
    private static final double NEAR = CapturePoints.CAPTURE_RADIUS;

    private GuildEvents() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 20 == 0) tick(server);
        });
    }

    static void tick(MinecraftServer server) {
        CapturePoints cp = CombiMod.CAPTURE;
        if (!cp.isActive()) return;

        Map<String, Set<String>> presence = new HashMap<>();
        for (CapturePoints.Point p : CapturePoints.LAYOUT) presence.put(p.id(), new HashSet<>());

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Optional<String> guild = CombiMod.GUILDS.guildOf(player.getUUID());
            if (guild.isEmpty()) continue;
            if (!player.level().dimension().equals(Level.OVERWORLD)) continue;
            for (CapturePoints.Point p : CapturePoints.LAYOUT) {
                double dx = player.getX() - (p.x() + 0.5);
                double dz = player.getZ() - (p.z() + 0.5);
                if (dx * dx + dz * dz <= NEAR * NEAR) presence.get(p.id()).add(guild.get());
            }
        }

        CapturePoints.Step step = cp.step(presence);
        for (String c : step.captured()) {
            String[] parts = c.split(":", 2); // "p1:Dragons"
            CombiMod.LOGGER.info("Point {} capturé par {}", parts[0], parts[1]);
            GuildCommands.broadcast(server, "§6" + parts[1] + " §fcapture le point " + parts[0] + " !");
        }
        step.income().forEach(CombiMod.GUILDS::addPoints);
    }

    /** Fin de guerre : la guilde en tête reçoit le bonus, les points de l'événement sont annoncés. */
    static void finish(MinecraftServer server) {
        Optional<String> winner = CombiMod.CAPTURE.stop();
        if (winner.isPresent()) {
            String w = winner.get();
            CombiMod.GUILDS.addPoints(w, CapturePoints.EVENT_WINNER_BONUS);
            GuildCommands.broadcast(server, "§6Fin de la guerre ! §f« " + w + " » remporte "
                    + CapturePoints.EVENT_WINNER_BONUS + " points de guilde.");
        } else {
            GuildCommands.broadcast(server, "§7Fin de la guerre : aucun point capturé, pas de vainqueur.");
        }
    }
}
