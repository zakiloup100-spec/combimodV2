package com.example.combimod;

import com.example.combimod.core.Allies;
import com.example.combimod.core.Bosses;
import com.example.combimod.core.Chests;
import com.example.combimod.core.CapturePoints;
import com.example.combimod.core.Codex;
import com.example.combimod.core.Guilds;
import com.example.combimod.core.Economy;
import com.example.combimod.core.ItemDef;
import com.example.combimod.core.Items;
import com.example.combimod.core.Shop;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class CombiMod implements ModInitializer {

    public static final String MOD_ID = "combimod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Economy ECONOMY = new Economy();
    public static final Codex CODEX = new Codex();
    public static final Allies ALLIES = new Allies();
    public static final Guilds GUILDS = new Guilds();
    public static final CapturePoints CAPTURE = new CapturePoints();
    public static final com.example.combimod.core.ManaPool MANA = new com.example.combimod.core.ManaPool();
    public static final Shop SHOP = new Shop(Items.SHOP);
    public static final Bosses BOSSES = Bosses.defaults(new Random());
    private static final Random RNG = new Random();

    @Override
    public void onInitialize() {
        ModComponents.init();
        ModArmor.init();
        ModItems.init();
        ModBlocks.init();
        com.example.combimod.entity.ModEntities.init();
        Storage.register();
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> resetRuntime());
        MobDrops.register();
        Effects.register();
        CombatEffects.register();
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK
                .register(server -> {
                    Summons.tick(server.getTickCount());
                    if (server.getTickCount() % 10 == 0) com.example.combimod.entity.BossZombie.cleanup();
                });
        ModCommands.register();
        GuildCommands.register();
        GuildEvents.register();
        GambleCommands.register();
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(SkyEvents::tick);
        LOGGER.info("Combi Mod chargé : {} items.", Items.ALL.size());
    }

    /** État purement en mémoire (cooldowns, loups, barres) : vidé à chaque arrêt du serveur. */
    static void resetRuntime() {
        Summons.reset();
        Spells.reset();
        Abilities.reset();
        CAPTURE.stop();
        SkyEvents.reset();
        GambleCommands.reset();
        MANA.reset();
        com.example.combimod.entity.BossZombie.reset();
    }

    /** Tirage pour un coffre : niveau 0 (mystique), 1 (épique) ou 2 (légendaire). */
    public static ItemDef rollChest(int tier) {
        return Chests.roll(tier, RNG);
    }
}
