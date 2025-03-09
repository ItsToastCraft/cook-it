package dev.toasttextures.cookit;

import dev.toasttextures.cookit.block.containers.MixingBowl;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.client.render.MuffinTinItemRenderer;
import dev.toasttextures.cookit.client.render.PizzaPanItemRenderer;
import dev.toasttextures.cookit.client.CookItEntityModelLayers;
import dev.toasttextures.cookit.client.render.PizzaItemRenderer;
import dev.toasttextures.cookit.item.armor.ChefOutfitRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.client.render.BakingSheetItemRenderer;
import dev.toasttextures.cookit.registry.*;

@Environment(EnvType.CLIENT)
public class CookItClient implements ClientModInitializer {

    public static boolean isFiguraLoaded;

    public void onInitializeClient() {
        isFiguraLoaded = (FabricLoader.getInstance().isModLoaded("figura"));

        CookItBlockEntities.registerRenderers();
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
                CookItBlocks.OVEN,
                CookItBlocks.MICROWAVE,
                CookItBlocks.FRYER);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
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


        ModelPredicateProviderRegistry.register(CookItItems.FIRE_EXTINGUISHER, Identifier.of("extinguisher_fuel"), (stack, world, entity, seed) -> (float) Math.round(((float) stack.getMaxDamage() - stack.getDamage()) / 100) / 10);
        ParticleFactoryRegistry.getInstance().register(CookIt.OIL_PARTICLE, OilParticle.Factory::new);

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            if (view != null && pos != null) {
                // Use the biome's grass color
                return BiomeColors.getFoliageColor(view, pos);
            }
            return 0xFFFFFFFF;
        }, CookItBlocks.VANILLA_VINE, CookItBlocks.VANILLA_VINE_STEM);

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {

            if (view != null && view.getBlockEntity(pos) instanceof MixingBowlEntity entity && state.get(MixingBowl.HAS_GOOP)) {
                return entity.getGoopColor();
            }
            return 0xF8D478FF;
        }, CookItBlocks.MIXING_BOWL);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> stack.getOrDefault(CookItComponents.COLOR_COMPONENT, 0xF8D478FF), CookItItems.GOOP);
    }
}