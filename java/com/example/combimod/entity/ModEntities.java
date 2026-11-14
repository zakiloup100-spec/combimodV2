package com.example.combimod.entity;

import com.example.combimod.CombiMod;
import com.example.combimod.core.Spirits;
import com.example.combimod.entity.BossZombie.BossPower;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModEntities {

    // Déclarés avant les constantes : register() les remplit
    private static final Map<EntityType<BossZombie>, BossPower> POWERS = new LinkedHashMap<>();
    private static final List<EntityType<BossZombie>> ALL = new ArrayList<>();

    public static final EntityType<BossZombie> BOSS_BLAST = register("boss_blast", BossPower.BLAST);
    public static final EntityType<BossZombie> BOSS_FREEZE = register("boss_freeze", BossPower.FREEZE);
    public static final EntityType<BossZombie> BOSS_LEECH = register("boss_leech", BossPower.LEECH);
    public static final EntityType<BossZombie> BOSS_POISON = register("boss_poison", BossPower.POISON);
    public static final EntityType<BossZombie> BOSS_BLIND = register("boss_blind", BossPower.BLIND);
    public static final EntityType<BossZombie> BOSS_WITHER = register("boss_wither", BossPower.WITHER);
    public static final EntityType<BossZombie> BOSS_WEAKEN = register("boss_weaken", BossPower.WEAKEN);
    public static final EntityType<BossZombie> BOSS_LEVITATE = register("boss_levitate", BossPower.LEVITATE);

    /** Esprit invoqué : un seul type, le comportement vient de son Kind. */
    public static final EntityType<SpiritEntity> SPIRIT = registerSpirit();

    private ModEntities() {}

    private static EntityType<SpiritEntity> registerSpirit() {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(CombiMod.MOD_ID, "spirit"));
        EntityType<SpiritEntity> type = EntityType.Builder
                .of((t, level) -> new SpiritEntity(t, level, Spirits.Kind.SPIRIT, null), MobCategory.MONSTER)
                .sized(0.6f, 1.95f)
                .clientTrackingRange(8)
                .build(key);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
    }

    public static List<EntityType<BossZombie>> all() {
        return ALL;
    }

    private static EntityType<BossZombie> register(String name, BossPower power) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(CombiMod.MOD_ID, name));
        EntityType<BossZombie> type = EntityType.Builder
                .of((t, level) -> new BossZombie(t, level, power), MobCategory.MONSTER)
                .sized(0.6f, 1.95f)
                .fireImmune() // ne brûle pas au soleil (ni dans la lave)
                .clientTrackingRange(8)
                .build(key);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type);
        POWERS.put(type, power);
        ALL.add(type);
        return type;
    }

    public static void init() {
        FabricDefaultAttributeRegistry.register(SPIRIT, SpiritEntity.createAttributes());

        for (EntityType<BossZombie> type : ALL) {
            FabricDefaultAttributeRegistry.register(type, BossZombie.createAttributes(POWERS.get(type)));
            // Spawn naturel dans l'Overworld. 8 boss x poids 5 = 40 au total (un zombie vanille pèse 100).
            // Pas de règle de placement custom : l'API de placement est privée en 1.21.11.
            BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), MobCategory.MONSTER, type, 5, 1, 1);
        }
    }
}
