package com.example.combimod;

import com.example.combimod.core.ItemDef;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** Item du mod : nom coloré selon la rareté, description au survol, sorts à l'utilisation. */
public class CombiItem extends Item {

    public CombiItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        ItemDef def = ItemUtil.defOf(stack);
        if (def == null) return Component.literal("Objet inconnu");
        return Component.literal(def.name()).withStyle(Descriptions.color(def.rarity()));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        ItemDef def = ItemUtil.defOf(stack);
        if (def == null) return;
        for (Component line : Descriptions.lines(def)) {
            tooltip.accept(line);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.SUCCESS;

        // Un objet peut cumuler plusieurs types (ex. Orbe nécrotique : invocation + magie) : tout s'exécute
        InteractionResult summon = InteractionResult.PASS;
        InteractionResult ability = InteractionResult.PASS;
        if (player instanceof ServerPlayer sp) {
            summon = Summons.summon(serverLevel, sp, stack);
            ability = Abilities.use(serverLevel, sp, stack);
        }
        InteractionResult spell = Spells.cast(serverLevel, player, stack);

        if (summon == InteractionResult.SUCCESS || spell == InteractionResult.SUCCESS
                || ability == InteractionResult.SUCCESS) {
            return InteractionResult.SUCCESS;
        }
        if (summon == InteractionResult.FAIL || spell == InteractionResult.FAIL
                || ability == InteractionResult.FAIL) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }
}
