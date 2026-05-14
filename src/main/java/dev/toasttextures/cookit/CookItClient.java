package dev.toasttextures.cookit;

import dev.toasttextures.cookit.client.CookItVisuals;
import dev.toasttextures.cookit.client.render.entity.*;
import dev.toasttextures.cookit.client.CookItEntityModelLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import dev.toasttextures.cookit.registries.*;

@Environment(EnvType.CLIENT)
public class CookItClient implements ClientModInitializer {

    public static boolean isFiguraLoaded;

    public void onInitializeClient() {
        isFiguraLoaded = FabricLoader.getInstance().isModLoaded("figura");

        CookItBlockEntities.registerRenderers();
        CookItVisuals.registerItemRenderers();
        ChefOutfitRenderer.register();
        CookItEntityModelLayers.registerLayers();
        CookItVisuals.registerColorProviders();

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(),
                CookItBlocks.OVEN,
                CookItBlocks.MICROWAVE,
                CookItBlocks.FRYER);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                CookItBlocks.MUFFIN_TIN,
                CookItBlocks.PIZZA_CRUST,
                CookItBlocks.VANILLA_VINE,
                CookItBlocks.VANILLA_VINE_STEM);

        ItemProperties.register(CookItItems.FIRE_EXTINGUISHER, new ResourceLocation("extinguisher_fuel"), (stack, world, entity, seed) -> (float) Math.round(((float) stack.getMaxDamage() - stack.getDamageValue()) / 100) / 10);
        ParticleFactoryRegistry.getInstance().register(CookIt.OIL_PARTICLE, OilParticle.Factory::new);
    }
}