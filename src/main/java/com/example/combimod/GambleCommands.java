package com.example.combimod;

import com.example.combimod.core.Gamble;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/** /gamble <mise> : pile ou face 50/50. Gagner double la mise, perdre la fait disparaître. */
public final class GambleCommands {

    private static final Random RNG = new Random();
    private static final Map<UUID, Long> LAST_PLAY = new HashMap<>();

    private GambleCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(Commands.literal("gamble")
                        .then(Commands.argument("mise", LongArgumentType.longArg(Gamble.MIN_BET, Gamble.MAX_BET))
                                .executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    long bet = LongArgumentType.getLong(ctx, "mise");
                                    long now = ctx.getSource().getServer().getTickCount();
                                    Long last = LAST_PLAY.get(p.getUUID());
                                    long since = last == null ? Long.MAX_VALUE : now - last;
                                    long balance = CombiMod.ECONOMY.balance(p.getUUID());

                                    String error = Gamble.validate(bet, balance, since);
                                    if (error != null) {
                                        ctx.getSource().sendFailure(Component.literal(error));
                                        return 0;
                                    }

                                    CombiMod.ECONOMY.take(p.getUUID(), bet);
                                    LAST_PLAY.put(p.getUUID(), now);
                                    Gamble.Outcome out = Gamble.flip(RNG, bet);
                                    Vec3 at = p.position();

                                    if (out.won()) {
                                        CombiMod.ECONOMY.add(p.getUUID(), bet * 2);
                                        p.sendSystemMessage(Component.literal("Pile ! Tu gagnes " + out.net()
                                                + " pièces. Solde : " + CombiMod.ECONOMY.balance(p.getUUID()))
                                                .withStyle(ChatFormatting.GREEN));
                                        VisualFx.sound((net.minecraft.server.level.ServerLevel) p.level(),
                                                at, SoundEvents.EXPERIENCE_ORB_PICKUP, 1.3f);
                                    } else {
                                        p.sendSystemMessage(Component.literal("Face ! Tu perds " + bet
                                                + " pièces. Solde : " + CombiMod.ECONOMY.balance(p.getUUID()))
                                                .withStyle(ChatFormatting.RED));
                                        VisualFx.sound((net.minecraft.server.level.ServerLevel) p.level(),
                                                at, SoundEvents.VILLAGER_NO, 0.9f);
                                    }
                                    return 1;
                                }))));
    }

    public static void reset() {
        LAST_PLAY.clear();
    }
}
