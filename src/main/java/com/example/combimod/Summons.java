package com.example.combimod;

import com.example.combimod.core.Affix;
import com.example.combimod.core.ItemDef;
import com.example.combimod.core.Spirits;
import com.example.combimod.core.Tag;
import com.example.combimod.entity.ModEntities;
import com.example.combimod.entity.SpiritEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Invocations : des esprits temporaires, passifs avec tes alliés, hostiles aux ennemis. */
public final class Summons {

    private static final long COOLDOWN = 600;   // 30 s
    private static final long LIFETIME = 1200;  // 60 s
    private static final int MAX_SPIRITS = 8;

    private static final Map<UUID, Long> LAST_SUMMON = new HashMap<>();
    private static final List<Ally> ALLIES = new ArrayList<>();

    private record Ally(SpiritEntity spirit, long expireTick) {}

    private Summons() {}

    public static InteractionResult summon(ServerLevel level, ServerPlayer player, ItemStack stack) {
        ItemDef def = ItemUtil.defOf(stack);
        if (def == null || !def.tags().contains(Tag.SUMMON)) return InteractionResult.PASS;

        long now = level.getGameTime();
        Long last = LAST_SUMMON.get(player.getUUID());
        if (last != null && now - last < COOLDOWN) return InteractionResult.FAIL;
        if (!Mana.spend(player, com.example.combimod.core.ManaPool.SUMMON_COST)) return InteractionResult.FAIL;
        LAST_SUMMON.put(player.getUUID(), now);

        int count = Math.min(MAX_SPIRITS, 2 + (int) Math.round(ItemUtil.bonus(stack, Affix.SUMMON_COUNT)));
        int strength = Math.max(0, (int) Math.round(ItemUtil.bonus(stack, Affix.SUMMON_DAMAGE)) - 1);
        boolean armored = ItemUtil.bonus(stack, Affix.DEFENSE) > 0;
        Spirits.Kind kind = Spirits.kindOf(def);

        for (int i = 0; i < count; i++) {
            SpiritEntity spirit = new SpiritEntity(ModEntities.SPIRIT, level, kind, player.getUUID());
            double angle = (2 * Math.PI * i) / count;
            spirit.setPos(player.getX() + 2.0 * Math.cos(angle), player.getY(), player.getZ() + 2.0 * Math.sin(angle));
            spirit.addEffect(new MobEffectInstance(MobEffects.STRENGTH, (int) LIFETIME, strength));
            if (armored) spirit.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, (int) LIFETIME, 0));
            level.addFreshEntity(spirit);
            VisualFx.summonPuff(level, spirit.position());
            VisualFx.sound(level, spirit.position(), SoundEvents.EVOKER_PREPARE_SUMMON, 1.0f);
            ALLIES.add(new Ally(spirit, now + LIFETIME));
        }
        return InteractionResult.SUCCESS;
    }

    /** À l'arrêt du serveur : retire tous les esprits invoqués et vide l'état. */
    public static void reset() {
        for (Ally ally : ALLIES) ally.spirit().discard();
        ALLIES.clear();
        LAST_SUMMON.clear();
    }

    /** Retire les esprits expirés ou morts. Appelé à chaque tick serveur. */
    public static void tick(long now) {
        Iterator<Ally> it = ALLIES.iterator();
        while (it.hasNext()) {
            Ally ally = it.next();
            if (now >= ally.expireTick() || !ally.spirit().isAlive()) {
                ally.spirit().discard();
                it.remove();
            }
        }
    }
}
