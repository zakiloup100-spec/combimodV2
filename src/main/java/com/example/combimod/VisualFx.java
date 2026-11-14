package com.example.combimod;

import com.example.combimod.core.Spirits;
import com.example.combimod.core.Traits.Trait;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

/**
 * Effets visuels et sonores. Tout passe par le serveur (sendParticles / playSound) :
 * les joueurs voient et entendent les effets, même à distance.
 * Les couleurs sont choisies pour que chaque effet soit reconnaissable d'un coup d'œil.
 */
public final class VisualFx {

    public static final int HEAL = 0x66FF88;
    public static final int CHEST = 0xFFD700;
    public static final int SPELL_DEFAULT = 0x3399FF;

    private VisualFx() {}

    public static int colorOf(Trait t) {
        return switch (t) {
            case SLOW -> 0x55CCFF;
            case POISON -> 0x44DD44;
            case WEAKEN -> 0x9A9A9A;
            case BLIND -> 0x5555AA;
            case WITHER -> 0x8A2BE2;
            case GLOW -> 0xFFEE55;
            case LEVITATE -> 0xFFFFFF;
        };
    }

    public static int colorOf(Spirits.Kind kind) {
        return switch (kind) {
            case DARK -> 0x9933FF;
            case GUARD -> 0xFFAA00;
            case SPIRIT -> 0x33DDFF;
        };
    }

    /** Un trait de la combinaison donne la couleur de l'effet ; sans trait, bleu de sort. */
    public static int colorOf(java.util.Set<Trait> traits) {
        return traits.isEmpty() ? SPELL_DEFAULT : colorOf(traits.iterator().next());
    }

    /** Rayon de particules entre deux points, en pas réguliers. */
    public static void beam(ServerLevel level, Vec3 from, Vec3 to, int rgb) {
        double length = from.distanceTo(to);
        int steps = Math.max(4, (int) (length * 2));
        DustParticleOptions dust = new DustParticleOptions(rgb, 1.1f);
        for (int i = 0; i <= steps; i++) {
            Vec3 p = from.lerp(to, i / (double) steps);
            level.sendParticles(dust, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
    }

    /** Explosion de particules colorées autour d'un point. */
    public static void burst(ServerLevel level, Vec3 at, int rgb, int count, double spread) {
        level.sendParticles(new DustParticleOptions(rgb, 1.0f), at.x, at.y, at.z, count,
                spread, spread, spread, 0.02);
    }

    /** Anneau de particules horizontal, pour les pulsations des esprits. */
    public static void ring(ServerLevel level, Vec3 center, double radius, int rgb, int points) {
        DustParticleOptions dust = new DustParticleOptions(rgb, 0.9f);
        for (int i = 0; i < points; i++) {
            double a = 2 * Math.PI * i / points;
            level.sendParticles(dust, center.x + radius * Math.cos(a), center.y + 0.2, center.z + radius * Math.sin(a),
                    1, 0, 0, 0, 0);
        }
    }

    /** Fumée d'invocation : des particules d'âme qui montent. */
    public static void summonPuff(ServerLevel level, Vec3 at) {
        level.sendParticles(ParticleTypes.SOUL, at.x, at.y + 0.5, at.z, 12, 0.4, 0.5, 0.4, 0.05);
    }

    public static void sound(ServerLevel level, Vec3 at, SoundEvent sound, float pitch) {
        level.playSound(null, at.x, at.y, at.z, sound, SoundSource.PLAYERS, 0.8f, pitch);
    }
}
