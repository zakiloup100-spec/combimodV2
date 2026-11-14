package com.example.combimod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class ModBlocks {

    public static final Block MYSTIC_CHEST = register("mystic_chest", props -> new MysticChestBlock(props, 0));
    public static final Block EPIC_CHEST = register("epic_chest", props -> new MysticChestBlock(props, 1));
    public static final Block LEGENDARY_CHEST = register("legendary_chest", props -> new MysticChestBlock(props, 2));
    public static final Block SKY_CRATE = register("sky_crate", SkyCrateBlock::new);

    private ModBlocks() {}

    /** Le coffre du niveau demandé (0 = mystique, 1 = épique, 2 = légendaire). */
    public static Block chestFor(int tier) {
        return switch (tier) {
            case 0 -> MYSTIC_CHEST;
            case 1 -> EPIC_CHEST;
            default -> LEGENDARY_CHEST;
        };
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK,
                Identifier.fromNamespaceAndPath(CombiMod.MOD_ID, name));
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .strength(2.0f)
                .sound(SoundType.WOOD)
                .setId(blockKey);
        Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties));

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(CombiMod.MOD_ID, name));
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
        return block;
    }

    public static void init() {}
}
