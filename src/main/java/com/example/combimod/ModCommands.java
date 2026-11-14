package com.example.combimod;

import com.example.combimod.core.Chests;
import com.example.combimod.core.Codex;
import com.example.combimod.core.Fusion;
import com.example.combimod.core.Generator;
import com.example.combimod.core.ItemDef;
import com.example.combimod.core.Items;
import com.example.combimod.core.Rarity;
import com.example.combimod.core.Reforge;
import com.example.combimod.core.Tag;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class ModCommands {

    private static final Random RNG = new Random();

    private ModCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(Commands.literal("balance").executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                long bal = CombiMod.ECONOMY.balance(p.getUUID());
                ctx.getSource().sendSuccess(() -> Component.literal("Solde : " + bal + " pièces"), false);
                return 1;
            }));

            dispatcher.register(Commands.literal("shop")
                    .then(Commands.literal("list").executes(ctx -> {
                        List<ItemDef> list = new ArrayList<>(CombiMod.SHOP.catalog().values());
                        list.sort(Comparator.comparingLong(ItemDef::price));
                        for (ItemDef d : list) {
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    d.id() + " : " + d.name() + " (" + d.price() + ")")
                                    .withStyle(Descriptions.color(d.rarity())), false);
                        }
                        return 1;
                    }))
                    .then(Commands.literal("buy")
                            .then(Commands.argument("id", StringArgumentType.word()).executes(ctx -> {
                                ServerPlayer p = ctx.getSource().getPlayerOrException();
                                String id = StringArgumentType.getString(ctx, "id");
                                var bought = CombiMod.SHOP.buy(p.getUUID(), id, CombiMod.ECONOMY);
                                if (bought.isEmpty()) {
                                    ctx.getSource().sendFailure(Component.literal("Achat impossible (id inconnu ou solde insuffisant)"));
                                    return 0;
                                }
                                Rewards.give(p, bought.get());
                                return 1;
                            }))));

            dispatcher.register(Commands.literal("sell")
                    .executes(ctx -> {
                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                        ItemStack held = p.getMainHandItem();
                        ItemDef def = ItemUtil.defOf(held);
                        if (def == null) {
                            ctx.getSource().sendFailure(Component.literal("Tiens un objet du mod dans ta main."));
                            return 0;
                        }
                        long price = CombiMod.ECONOMY.sellPrice(def);
                        held.shrink(1);
                        CombiMod.ECONOMY.add(p.getUUID(), price);
                        ctx.getSource().sendSuccess(() -> Component.literal("Vendu " + def.name() + " pour " + price), false);
                        return 1;
                    })
                    .then(Commands.literal("junk").executes(ctx -> {
                        // Vend tous les objets communs du sac (hors armures portées et hors armures du mod)
                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                        Inventory inv = p.getInventory();
                        long total = 0;
                        int count = 0;
                        for (int i = 0; i < 36; i++) {
                            ItemStack s = inv.getItem(i);
                            ItemDef d = ItemUtil.defOf(s);
                            if (d != null && d.rarity() == Rarity.COMMON && ModArmor.itemFor(d.id()) == null) {
                                total += CombiMod.ECONOMY.sellPrice(d) * s.getCount();
                                count++;
                                inv.setItem(i, ItemStack.EMPTY);
                            }
                        }
                        final long gained = total;
                        final int sold = count;
                        if (sold == 0) {
                            ctx.getSource().sendFailure(Component.literal("Aucun objet commun à vendre."));
                            return 0;
                        }
                        CombiMod.ECONOMY.add(p.getUUID(), gained);
                        ctx.getSource().sendSuccess(() -> Component.literal(
                                sold + " objets communs vendus pour " + gained), false);
                        return 1;
                    })));

            dispatcher.register(Commands.literal("fuse").executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                ItemStack a = p.getMainHandItem();
                ItemStack b = p.getOffhandItem();
                ItemDef da = ItemUtil.defOf(a);
                ItemDef db = ItemUtil.defOf(b);
                if (da == null || db == null) {
                    ctx.getSource().sendFailure(Component.literal("Tiens un objet du mod dans chaque main."));
                    return 0;
                }
                ItemDef fused = Fusion.fuse(da, db);
                a.shrink(1);
                b.shrink(1);
                Rewards.give(p, fused);
                return 1;
            }));

            dispatcher.register(Commands.literal("stats").executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                ItemDef def = ItemUtil.defOf(p.getMainHandItem());
                if (def == null) {
                    ctx.getSource().sendFailure(Component.literal("Tiens un objet du mod dans ta main."));
                    return 0;
                }
                ctx.getSource().sendSuccess(() -> Component.literal(def.name())
                        .withStyle(Descriptions.color(def.rarity())), false);
                for (Component line : Descriptions.lines(def)) {
                    ctx.getSource().sendSuccess(() -> line, false);
                }
                return 1;
            }));

            dispatcher.register(Commands.literal("codex").executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                Codex.Counts c = CombiMod.CODEX.counts(p.getUUID());
                ctx.getSource().sendSuccess(() -> Component.literal("Ta collection").withStyle(ChatFormatting.GOLD), false);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        "Catalogue : " + c.base() + " / " + Items.BASE.size()), false);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        "Objets forgés : " + c.generated() + " / " + Generator.totalCombos()), false);
                ctx.getSource().sendSuccess(() -> Component.literal(
                        "Fusions uniques : " + c.fusions() + " (il y en a une infinité)"), false);
                return 1;
            }));

            dispatcher.register(Commands.literal("recipes").executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                List<Map.Entry<Set<String>, ItemDef>> list = new ArrayList<>(Fusion.recipes().entrySet());
                list.sort((x, y) -> Long.compare(x.getValue().price(), y.getValue().price()));
                int known = 0;
                for (Map.Entry<Set<String>, ItemDef> e : list) {
                    ItemDef r = e.getValue();
                    String text;
                    if (CombiMod.CODEX.has(p.getUUID(), r.id())) {
                        known++;
                        StringBuilder parents = new StringBuilder();
                        for (String id : e.getKey()) {
                            if (parents.length() > 0) parents.append(" + ");
                            parents.append(Items.BASE.get(id).name());
                        }
                        text = r.name() + " = " + parents;
                    } else {
                        StringBuilder tags = new StringBuilder();
                        for (Tag t : r.tags()) {
                            if (tags.length() > 0) tags.append("/");
                            tags.append(Descriptions.tagLabel(t));
                        }
                        text = "??? (" + Descriptions.rarityLabel(r.rarity()) + ", " + tags + ")";
                    }
                    final String line = text;
                    ctx.getSource().sendSuccess(() -> Component.literal(line)
                            .withStyle(Descriptions.color(r.rarity())), false);
                }
                final int found = known;
                final int total = list.size();
                ctx.getSource().sendSuccess(() -> Component.literal(
                        "Recettes découvertes : " + found + " / " + total
                                + " (fusionne des objets avec /fuse pour en trouver)")
                        .withStyle(ChatFormatting.GREEN), false);
                return 1;
            }));

            dispatcher.register(Commands.literal("reforge")
                    .executes(ctx -> {
                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                        ItemStack held = p.getMainHandItem();
                        ItemDef def = ItemUtil.defOf(held);
                        if (def == null || !Reforge.allowed(def)) {
                            ctx.getSource().sendFailure(Component.literal(
                                    "Tiens en main un objet relançable (pas une armure ni un objet de recette)."));
                            return 0;
                        }
                        long cost = Reforge.cost(def);
                        if (!CombiMod.ECONOMY.take(p.getUUID(), cost)) {
                            ctx.getSource().sendFailure(Component.literal("Il te faut " + cost + " pièces."));
                            return 0;
                        }
                        ItemDef fresh = Reforge.reroll(def, RNG);
                        held.shrink(1);
                        Rewards.give(p, fresh);
                        return 1;
                    })
                    .then(Commands.literal("price").executes(ctx -> {
                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                        ItemDef def = ItemUtil.defOf(p.getMainHandItem());
                        if (def == null || !Reforge.allowed(def)) {
                            ctx.getSource().sendFailure(Component.literal("Tiens en main un objet relançable."));
                            return 0;
                        }
                        long cost = Reforge.cost(def);
                        ctx.getSource().sendSuccess(() -> Component.literal(
                                "Relancer " + def.name() + " coûte " + cost + " pièces (même rareté, même orientation)."), false);
                        return 1;
                    })));

            dispatcher.register(Commands.literal("ally")
                    .then(Commands.literal("invite")
                            .then(Commands.argument("joueur", StringArgumentType.word()).executes(ctx -> {
                                ServerPlayer p = ctx.getSource().getPlayerOrException();
                                ServerPlayer target = ctx.getSource().getServer().getPlayerList()
                                        .getPlayerByName(StringArgumentType.getString(ctx, "joueur"));
                                if (target == null || target == p || !CombiMod.ALLIES.invite(p.getUUID(), target.getUUID())) {
                                    ctx.getSource().sendFailure(Component.literal("Invitation impossible (joueur absent, toi-même, ou déjà allié)."));
                                    return 0;
                                }
                                target.sendSystemMessage(Component.literal(p.getName().getString()
                                        + " t'invite comme allié. Tape /ally accept pour accepter.").withStyle(ChatFormatting.AQUA));
                                ctx.getSource().sendSuccess(() -> Component.literal("Invitation envoyée à " + target.getName().getString()), false);
                                return 1;
                            })))
                    .then(Commands.literal("accept").executes(ctx -> {
                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                        var from = CombiMod.ALLIES.accept(p.getUUID());
                        if (from.isEmpty()) {
                            ctx.getSource().sendFailure(Component.literal("Aucune invitation en attente."));
                            return 0;
                        }
                        ctx.getSource().sendSuccess(() -> Component.literal("Alliance créée."), false);
                        return 1;
                    }))
                    .then(Commands.literal("remove")
                            .then(Commands.argument("joueur", StringArgumentType.word()).executes(ctx -> {
                                ServerPlayer p = ctx.getSource().getPlayerOrException();
                                ServerPlayer target = ctx.getSource().getServer().getPlayerList()
                                        .getPlayerByName(StringArgumentType.getString(ctx, "joueur"));
                                if (target == null || !CombiMod.ALLIES.remove(p.getUUID(), target.getUUID())) {
                                    ctx.getSource().sendFailure(Component.literal("Ce joueur n'est pas (ou plus) ton allié connecté."));
                                    return 0;
                                }
                                ctx.getSource().sendSuccess(() -> Component.literal("Alliance rompue."), false);
                                return 1;
                            })))
                    .then(Commands.literal("list").executes(ctx -> {
                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                        var set = CombiMod.ALLIES.allyOf(p.getUUID());
                        if (set.isEmpty()) {
                            ctx.getSource().sendSuccess(() -> Component.literal("Aucun allié."), false);
                            return 1;
                        }
                        ctx.getSource().sendSuccess(() -> Component.literal("Alliés : " + set.size()
                                + " (les soins et buffs touchent ceux qui sont à moins de 8 blocs)"), false);
                        return 1;
                    })));

            dispatcher.register(Commands.literal("chest")
                    .then(Commands.literal("prices").executes(ctx -> {
                        for (int t = 0; t < Chests.tiers(); t++) {
                            final int tier = t;
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    (tier + 1) + " : " + Chests.NAMES[tier] + " (" + Chests.COST[tier] + " pièces)"), false);
                        }
                        return 1;
                    }))
                    .then(Commands.literal("buy")
                            .then(Commands.argument("niveau", IntegerArgumentType.integer(1, Chests.tiers()))
                                    .executes(ctx -> {
                                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                                        int tier = IntegerArgumentType.getInteger(ctx, "niveau") - 1;
                                        if (!CombiMod.ECONOMY.take(p.getUUID(), Chests.COST[tier])) {
                                            ctx.getSource().sendFailure(Component.literal("Solde insuffisant."));
                                            return 0;
                                        }
                                        ItemStack chest = new ItemStack(ModBlocks.chestFor(tier).asItem());
                                        if (!p.getInventory().add(chest)) p.drop(chest, false);
                                        ctx.getSource().sendSuccess(() -> Component.literal(
                                                Chests.NAMES[tier] + " acheté : pose-le et clique dessus."), false);
                                        return 1;
                                    }))));
        });
    }
}
