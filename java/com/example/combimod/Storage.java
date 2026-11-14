package com.example.combimod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Sauvegarde dans config/combimod-data.json : soldes et codex.
 * Les objets générés et fusionnés n'ont pas besoin de sauvegarde : leur définition est dans l'objet.
 */
public final class Storage {

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("combimod-data.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Storage() {}

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> load());
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> save());
        // Sauvegarde à chaque déconnexion : les pièces suivent le joueur même après une coupure
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> save());
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 1200 == 0) save(); // toutes les minutes
        });
    }

    static void save() {
        JsonObject root = new JsonObject();

        JsonObject balances = new JsonObject();
        CombiMod.ECONOMY.snapshot().forEach((uuid, amount) -> balances.addProperty(uuid.toString(), amount));
        root.add("balances", balances);

        JsonObject discovered = new JsonObject();
        for (Map.Entry<UUID, Set<String>> e : CombiMod.CODEX.snapshot().entrySet()) {
            JsonArray keys = new JsonArray();
            for (String key : e.getValue()) keys.add(key);
            discovered.add(e.getKey().toString(), keys);
        }
        root.add("discovered", discovered);

        JsonObject allies = new JsonObject();
        for (Map.Entry<UUID, Set<UUID>> e : CombiMod.ALLIES.snapshot().entrySet()) {
            JsonArray others = new JsonArray();
            for (UUID other : e.getValue()) others.add(other.toString());
            allies.add(e.getKey().toString(), others);
        }
        root.add("allies", allies);

        JsonArray guilds = new JsonArray();
        for (var g : CombiMod.GUILDS.export()) {
            JsonObject o = new JsonObject();
            o.addProperty("name", g.name());
            o.addProperty("leader", g.leader().toString());
            o.addProperty("points", g.points());
            JsonArray members = new JsonArray();
            for (UUID m : g.members()) members.add(m.toString());
            o.add("members", members);
            guilds.add(o);
        }
        root.add("guilds", guilds);

        JsonObject owners = new JsonObject();
        CombiMod.CAPTURE.owners().forEach(owners::addProperty);
        root.add("capture_owners", owners);

        // Écriture atomique : un fichier temporaire, puis remplacement. Un crash ne corrompt jamais la sauvegarde.
        Path tmp = FILE.resolveSibling(FILE.getFileName() + ".tmp");
        try (Writer w = Files.newBufferedWriter(tmp)) {
            GSON.toJson(root, w);
        } catch (Exception e) {
            CombiMod.LOGGER.error("Impossible d'écrire la sauvegarde temporaire", e);
            return;
        }
        try {
            rotateBackups();
            Files.move(tmp, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            CombiMod.LOGGER.error("Impossible de sauvegarder les données", e);
        }
    }

    /** Garde les 5 dernières sauvegardes : combimod-data.json.1 (la plus récente) à .5 (la plus ancienne). */
    static void rotateBackups() throws Exception {
        if (!Files.exists(FILE)) return;
        Path oldest = FILE.resolveSibling(FILE.getFileName() + ".5");
        Files.deleteIfExists(oldest);
        for (int i = 4; i >= 1; i--) {
            Path from = FILE.resolveSibling(FILE.getFileName() + "." + i);
            if (Files.exists(from)) {
                Files.move(from, FILE.resolveSibling(FILE.getFileName() + "." + (i + 1)), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        Files.copy(FILE, FILE.resolveSibling(FILE.getFileName() + ".1"), StandardCopyOption.REPLACE_EXISTING);
    }

    static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader r = Files.newBufferedReader(FILE)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();

            if (root.has("balances")) {
                for (Map.Entry<String, JsonElement> e : root.getAsJsonObject("balances").entrySet()) {
                    CombiMod.ECONOMY.restore(UUID.fromString(e.getKey()), e.getValue().getAsLong());
                }
            }
            if (root.has("discovered")) {
                for (Map.Entry<String, JsonElement> e : root.getAsJsonObject("discovered").entrySet()) {
                    List<String> keys = new ArrayList<>();
                    for (JsonElement k : e.getValue().getAsJsonArray()) keys.add(k.getAsString());
                    CombiMod.CODEX.restore(UUID.fromString(e.getKey()), keys);
                }
            }
            if (root.has("guilds")) {
                for (JsonElement el : root.getAsJsonArray("guilds")) {
                    JsonObject o = el.getAsJsonObject();
                    List<UUID> members = new ArrayList<>();
                    for (JsonElement m : o.getAsJsonArray("members")) members.add(UUID.fromString(m.getAsString()));
                    CombiMod.GUILDS.restore(new com.example.combimod.core.Guilds.Saved(
                            o.get("name").getAsString(), UUID.fromString(o.get("leader").getAsString()),
                            members, o.get("points").getAsLong()));
                }
            }
            if (root.has("capture_owners")) {
                for (Map.Entry<String, JsonElement> e : root.getAsJsonObject("capture_owners").entrySet()) {
                    CombiMod.CAPTURE.restoreOwner(e.getKey(), e.getValue().getAsString());
                }
            }
            if (root.has("allies")) {
                for (Map.Entry<String, JsonElement> e : root.getAsJsonObject("allies").entrySet()) {
                    List<UUID> others = new ArrayList<>();
                    for (JsonElement o : e.getValue().getAsJsonArray()) others.add(UUID.fromString(o.getAsString()));
                    CombiMod.ALLIES.restore(UUID.fromString(e.getKey()), others);
                }
            }
        } catch (Exception e) {
            CombiMod.LOGGER.error("Impossible de charger les données", e);
        }
    }
}
