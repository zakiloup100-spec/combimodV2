package com.example.combimod;

import com.example.combimod.core.Generator;
import com.example.combimod.core.Rarity;
import com.example.combimod.core.SkyDrops;
import com.example.combimod.core.SkyDrops.Segment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Apparition des colis, ouverture, et animation de la roue de rareté. */
public final class SkyEvents {

    private static final Random RNG = new Random();
    private static final long CRATE_LIFETIME = 20 * 60 * 3; // 3 minutes avant de disparaître
    private static final int SPIN_TICKS = 60;               // 3 secondes de roue
    private static final int SPIN_STEP = 3;                 // une case toutes les 3 ticks

    private static int countdown = SkyDrops.nextIntervalTicks(RNG);
    private static final Map<BlockPos, Long> CRATES = new HashMap<>(); // position -> tick d'expiration
    private static final Map<java.util.UUID, Spin> SPINS = new HashMap<>();

    private record Spin(ServerPlayer player, int ticksLeft, Segment result) {}

    private SkyEvents() {}

    /** Appelée à chaque tick serveur. */
    public static void tick(MinecraftServer server) {
        tickSpins(server);
        tickCrates(server);
        if (--countdown <= 0) {
            countdown = SkyDrops.nextIntervalTicks(RNG);
            dropCrate(server);
        }
    }

    private static void dropCrate(MinecraftServer server) {
        List<ServerPlayer> players = new ArrayList<>(server.getPlayerList().getPlayers());
        if (players.isEmpty()) return;
        ServerPlayer target = players.get(RNG.nextInt(players.size()));
        ServerLevel level = server.overworld();

        double angle = RNG.nextDouble() * 2 * Math.PI;
        int dist = 30 + RNG.nextInt(31);
        int x = (int) Math.floor(target.getX() + dist * Math.cos(angle));
        int z = (int) Math.floor(target.getZ() + dist * Math.sin(angle));
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        BlockPos pos = new BlockPos(x, y, z);
        if (!level.getBlockState(pos).isAir()) return; // on ne fait pas tomber un colis dans un mur

        level.setBlock(pos, ModBlocks.SKY_CRATE.defaultBlockState(), 3);
        CRATES.put(pos, server.getTickCount() + CRATE_LIFETIME);

        Vec3 top = new Vec3(x + 0.5, y + 40, z + 0.5);
        Vec3 ground = new Vec3(x + 0.5, y + 1, z + 0.5);
        VisualFx.beam(level, top, ground, VisualFx.CHEST);
        VisualFx.sound(level, ground, SoundEvents.BELL_RESONATE, 1.0f);
        GuildCommands.broadcast(server, "§6Un colis tombe du ciel vers " + x + ", " + z + " !");
    }

    private static void tickCrates(MinecraftServer server) {
        long now = server.getTickCount();
        Iterator<Map.Entry<BlockPos, Long>> it = CRATES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Long> e = it.next();
            if (now < e.getValue()) continue;
            ServerLevel level = server.overworld();
            if (level.getBlockState(e.getKey()).is(ModBlocks.SKY_CRATE)) {
                level.removeBlock(e.getKey(), false);
            }
            it.remove();
        }
    }

    /** Ouverture : toujours quelques pièces, et une chance de lancer la roue. */
    public static void openCrate(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!level.getBlockState(pos).is(ModBlocks.SKY_CRATE)) return;
        level.removeBlock(pos, false);
        CRATES.remove(pos);

        long money = SkyDrops.moneyReward(RNG);
        CombiMod.ECONOMY.add(player.getUUID(), money);
        player.displayClientMessage(Component.literal("+" + money + " pièces du colis").withStyle(ChatFormatting.YELLOW), false);
        VisualFx.burst(level, Vec3.atCenterOf(pos), VisualFx.CHEST, 30, 0.6);
        VisualFx.sound(level, Vec3.atCenterOf(pos), SoundEvents.CHEST_OPEN, 1.0f);

        if (SkyDrops.wheelTriggered(RNG) && !SPINS.containsKey(player.getUUID())) {
            SPINS.put(player.getUUID(), new Spin(player, SPIN_TICKS, SkyDrops.spin(RNG)));
            player.displayClientMessage(Component.literal("Roue de rareté : tente ta chance !")
                    .withStyle(ChatFormatting.GOLD), false);
        }
    }

    /** Animation : les segments défilent, ralentissent (ici : plus lentement à la fin), puis s'arrêtent. */
    private static void tickSpins(MinecraftServer server) {
        Iterator<Map.Entry<java.util.UUID, Spin>> it = SPINS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<java.util.UUID, Spin> e = it.next();
            Spin s = e.getValue();
            if (s.player().isRemoved()) {
                it.remove();
                continue;
            }
            int left = s.ticksLeft() - 1;
            if (left > 0) {
                if (left % SPIN_STEP == 0) {
                    Segment shown = Segment.values()[RNG.nextInt(Segment.values().length)];
                    s.player().displayClientMessage(wheelLine(shown, false), true);
                }
                e.setValue(new Spin(s.player(), left, s.result()));
            } else {
                finish(s);
                it.remove();
            }
        }
    }

    private static void finish(Spin s) {
        ServerPlayer p = s.player();
        Segment r = s.result();
        p.displayClientMessage(wheelLine(r, true), true);
        if (r.rarity == null) {
            p.sendSystemMessage(Component.literal("La roue s'arrête sur « Rien ». Pas de chance cette fois.")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        var item = Generator.generate(RNG, r.rarity, Set.of());
        Rewards.give(p, item);
    }

    private static Component wheelLine(Segment seg, boolean done) {
        ChatFormatting color = seg.rarity == null ? ChatFormatting.GRAY : Descriptions.color(seg.rarity);
        String text = done ? "» " + seg.label + " «" : "~ " + seg.label + " ~";
        return Component.literal(text).withStyle(color);
    }

    public static void reset() {
        CRATES.clear();
        SPINS.clear();
        countdown = SkyDrops.nextIntervalTicks(RNG);
    }
}
