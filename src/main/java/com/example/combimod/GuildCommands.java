package com.example.combimod;

import com.example.combimod.core.CapturePoints;
import com.example.combimod.core.Guilds;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

/** /guild (tout le monde) et /event (opérateurs uniquement). */
public final class GuildCommands {

    private GuildCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(Commands.literal("guild")
                    .then(Commands.literal("create").then(Commands.argument("nom", StringArgumentType.word())
                            .executes(ctx -> reply(ctx, CombiMod.GUILDS.create(player(ctx).getUUID(),
                                    StringArgumentType.getString(ctx, "nom"))))))
                    .then(Commands.literal("invite").then(Commands.argument("joueur", StringArgumentType.word())
                            .executes(ctx -> {
                                ServerPlayer me = player(ctx);
                                ServerPlayer target = online(ctx, StringArgumentType.getString(ctx, "joueur"));
                                if (target == null) return fail(ctx, "Joueur introuvable en ligne.");
                                Guilds.Result r = CombiMod.GUILDS.invite(me.getUUID(), target.getUUID());
                                if (r.ok()) {
                                    String guild = CombiMod.GUILDS.guildOf(me.getUUID()).orElse("?");
                                    target.sendSystemMessage(Component.literal(me.getName().getString()
                                            + " t'invite dans « " + guild + " ». Tape /guild accept.")
                                            .withStyle(ChatFormatting.AQUA));
                                }
                                return reply(ctx, r);
                            }))))
                    .then(Commands.literal("accept").executes(ctx -> reply(ctx, CombiMod.GUILDS.accept(player(ctx).getUUID()))))
                    .then(Commands.literal("leave").executes(ctx -> reply(ctx, CombiMod.GUILDS.leave(player(ctx).getUUID()))))
                    .then(Commands.literal("disband").executes(ctx -> reply(ctx, CombiMod.GUILDS.disband(player(ctx).getUUID()))))
                    .then(Commands.literal("info").executes(ctx -> {
                        Optional<String> g = CombiMod.GUILDS.guildOf(player(ctx).getUUID());
                        if (g.isEmpty()) return fail(ctx, "Tu n'es dans aucune guilde.");
                        ctx.getSource().sendSuccess(() -> Component.literal("Guilde : " + g.get()
                                + " | membres : " + CombiMod.GUILDS.members(g.get()).size()
                                + " | points : " + CombiMod.GUILDS.points(g.get())), false);
                        return 1;
                    }))
                    .then(Commands.literal("top").executes(ctx -> {
                        var board = CombiMod.GUILDS.leaderboard(10);
                        if (board.isEmpty()) return fail(ctx, "Aucune guilde pour l'instant.");
                        int rank = 1;
                        for (Guilds.Entry e : board) {
                            final int r = rank++;
                            ctx.getSource().sendSuccess(() -> Component.literal("#" + r + " " + e.name()
                                    + " : " + e.points() + " points (" + e.members() + " membres)")
                                    .withStyle(r == 1 ? ChatFormatting.GOLD : ChatFormatting.WHITE), false);
                        }
                        return 1;
                    }));

            // Admin : start et stop réservés aux opérateurs (niveau 2). status reste ouvert à tous.
            dispatcher.register(Commands.literal("event")
                    .then(Commands.literal("start").requires(src -> src.hasPermission(2)).executes(ctx -> {
                        if (CombiMod.CAPTURE.isActive()) return fail(ctx, "L'événement est déjà en cours.");
                        CombiMod.CAPTURE.start();
                        broadcast(ctx.getSource().getServer(), "§6Événement de guerre commencé ! Capture les points à 300-500 blocs du spawn.");
                        return 1;
                    }))
                    .then(Commands.literal("stop").requires(src -> src.hasPermission(2)).executes(ctx -> {
                        if (!CombiMod.CAPTURE.isActive()) return fail(ctx, "Aucun événement en cours.");
                        GuildEvents.finish(ctx.getSource().getServer());
                        return 1;
                    }))
                    .then(Commands.literal("status").executes(ctx -> {
                        boolean on = CombiMod.CAPTURE.isActive();
                        ctx.getSource().sendSuccess(() -> Component.literal("Événement : " + (on ? "en cours" : "arrêté")), false);
                        for (CapturePoints.Point p : CapturePoints.LAYOUT) {
                            String owner = CombiMod.CAPTURE.ownerOf(p.id()).orElse("libre");
                            ctx.getSource().sendSuccess(() -> Component.literal(p.id() + " (" + p.x() + ", " + p.z() + ") : " + owner), false);
                        }
                        return 1;
                    })));
        });
    }

    private static ServerPlayer player(com.mojang.brigadier.context.CommandContext<net.minecraft.commands.CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return ctx.getSource().getPlayerOrException();
    }

    private static ServerPlayer online(com.mojang.brigadier.context.CommandContext<net.minecraft.commands.CommandSourceStack> ctx, String name) {
        return ctx.getSource().getServer().getPlayerList().getPlayerByName(name);
    }

    private static int reply(com.mojang.brigadier.context.CommandContext<net.minecraft.commands.CommandSourceStack> ctx, Guilds.Result r) {
        if (r.ok()) ctx.getSource().sendSuccess(() -> Component.literal(r.message()).withStyle(ChatFormatting.GREEN), false);
        else ctx.getSource().sendFailure(Component.literal(r.message()));
        return r.ok() ? 1 : 0;
    }

    private static int fail(com.mojang.brigadier.context.CommandContext<net.minecraft.commands.CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendFailure(Component.literal(msg));
        return 0;
    }

    static void broadcast(MinecraftServer server, String text) {
        server.getPlayerList().broadcastSystemMessage(Component.literal(text), false);
    }
}
