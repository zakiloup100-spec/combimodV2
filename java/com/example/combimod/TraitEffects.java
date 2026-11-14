package com.example.combimod;

import com.example.combimod.core.Traits.Trait;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Set;
import java.util.UUID;

/** Effets appliqués aux cibles : par les armes (effets de l'objet) et par les esprits (attaques périodiques). */
public final class TraitEffects {

    private static final int DURATION = 80; // 4 secondes

    private TraitEffects() {}

    public static void applyAll(LivingEntity target, Set<Trait> traits) {
        for (Trait t : traits) apply(target, t);
    }

    public static void apply(LivingEntity target, Trait trait) {
        int duration = trait == Trait.LEVITATE ? 20 : DURATION;
        target.addEffect(new MobEffectInstance(effectHolder(trait), duration, 0));
    }

    private static Holder<MobEffect> effectHolder(Trait trait) {
        return switch (trait) {
            case SLOW -> MobEffects.SLOWNESS;
            case POISON -> MobEffects.POISON;
            case WEAKEN -> MobEffects.WEAKNESS;
            case BLIND -> MobEffects.BLINDNESS;
            case WITHER -> MobEffects.WITHER;
            case GLOW -> MobEffects.GLOWING;
            case LEVITATE -> MobEffects.LEVITATION;
        };
    }

    /** Vrai si la cible est un allié du joueur (ou le joueur lui-même) : aucun effet d'arme ne la touche. */
    public static boolean isFriend(UUID owner, LivingEntity target) {
        if (owner == null || !(target instanceof Player p)) return false;
        return p.getUUID().equals(owner) || CombiMod.ALLIES.isAlly(owner, p.getUUID());
    }
}
