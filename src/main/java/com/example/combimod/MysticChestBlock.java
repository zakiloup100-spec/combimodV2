package com.example.combimod;

import com.example.combimod.core.ItemDef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvents;

/** Coffre à usage unique : un clic donne un objet selon son niveau, puis le coffre disparaît. */
public class MysticChestBlock extends Block {

    private final int tier;

    public MysticChestBlock(BlockBehaviour.Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        ItemDef def = CombiMod.rollChest(tier);
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            Vec3 at = Vec3.atCenterOf(pos);
            VisualFx.burst(serverLevel, at, VisualFx.CHEST, 30, 0.6);
            VisualFx.sound(serverLevel, at, SoundEvents.CHEST_OPEN, 1.0f);
        }
        level.removeBlock(pos, false);
        Rewards.give(serverPlayer, def);
        return InteractionResult.SUCCESS;
    }
}
