package com.example.combimod.client;

import com.example.combimod.entity.BossZombie;
import com.example.combimod.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.EntityType;

public class CombiModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Apparence de zombie vanille pour l'instant (boss et esprits).
        // Le cast brut est nécessaire : ZombieRenderer est typé Zombie, les entités du mod en héritent.
        EntityRendererProvider<?> zombie = ZombieRenderer::new;
        for (EntityType<BossZombie> type : ModEntities.all()) {
            EntityRendererRegistry.register(type, (EntityRendererProvider) zombie);
        }
        EntityRendererRegistry.register(ModEntities.SPIRIT, (EntityRendererProvider) zombie);
    }
}
