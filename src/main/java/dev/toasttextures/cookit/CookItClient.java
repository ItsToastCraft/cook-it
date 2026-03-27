package dev.toasttextures.cookit;

import dev.toasttextures.cookit.block.containers.MixingBowl;
import dev.toasttextures.cookit.block.containers.Plate;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.client.render.entity.*;
import dev.toasttextures.cookit.client.CookItEntityModelLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import dev.toasttextures.cookit.registries.*;

@Environment(EnvType.CLIENT)
public class CookItClient implements ClientModInitializer {

    public static boolean isFiguraLoaded;


    public void onInitializeClient() {
        isFiguraLoaded = (FabricLoader.getInstance().isModLoaded("figura"));

        CookItBlockEntities.registerRenderers();
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(),
                CookItBlocks.OVEN,
                CookItBlocks.MICROWAVE,
                CookItBlocks.FRYER);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                CookItBlocks.MUFFIN_TIN,
                CookItBlocks.PIZZA_CRUST,
                CookItBlocks.VANILLA_VINE,
                CookItBlocks.VANILLA_VINE_STEM);

        ChefOutfitRenderer.register();

        CookItEntityModelLayers.registerLayers();
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.BAKING_SHEET.asItem(), new BakingSheetItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.MUFFIN_TIN.asItem(), new MuffinTinItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.PIZZA_PAN.asItem(), new PizzaPanItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.PIZZA.asItem(), new PizzaItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.UNCOOKED_PIZZA.asItem(), new PizzaItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItItems.PIZZA_SLICE, new PizzaItemRenderer());

        for (Plate plate: CookItBlocks.PLATES) {
            CookIt.LOGGER.info("registering {}", plate.toString());
            BuiltinItemRendererRegistry.INSTANCE.register(plate, new PlateItemRenderer());
        }

        ItemProperties.register(CookItItems.FIRE_EXTINGUISHER, new ResourceLocation("extinguisher_fuel"), (stack, world, entity, seed) -> (float) Math.round(((float) stack.getMaxDamage() - stack.getDamageValue()) / 100) / 10);
        ParticleFactoryRegistry.getInstance().register(CookIt.OIL_PARTICLE, OilParticle.Factory::new);

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            if (view != null && pos != null) {
                // Use the biome's grass color
                return BiomeColors.getAverageFoliageColor(view, pos);
            }
            return 0xFFFFFF;
        }, CookItBlocks.VANILLA_VINE, CookItBlocks.VANILLA_VINE_STEM);

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {

            if (view != null && view.getBlockEntity(pos) instanceof MixingBowlEntity entity && state.getValue(MixingBowl.CONTAINS_LIQUID)) {
                return entity.getGoopColor();
            }
            return 0xF8D478;
        }, CookItBlocks.MIXING_BOWL);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            assert stack.getTag() != null;
            return stack.getTag().getInt("color");
        }, CookItItems.GOOP);
    }
}