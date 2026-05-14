package dev.toasttextures.cookit.client;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.containers.MixingBowl;
import dev.toasttextures.cookit.block.containers.Plate;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.client.render.entity.item.*;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.nbt.CompoundTag;

public class CookItVisuals {
    public static void registerItemRenderers() {
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.BAKING_SHEET.asItem(), new BakingSheetItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.MUFFIN_TIN.asItem(), new MuffinTinItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.PIZZA_PAN.asItem(), new PizzaPanItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.PIZZA.asItem(), new PizzaItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItBlocks.UNCOOKED_PIZZA.asItem(), new PizzaItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(CookItItems.PIZZA_SLICE, new PizzaItemRenderer());

        for (Plate plate: CookItBlocks.PLATES) {
            BuiltinItemRendererRegistry.INSTANCE.register(plate, new PlateItemRenderer());
        }
    }

    public static void registerColorProviders() {
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            if (view == null || pos == null) return 0xFFFFFF;
            return BiomeColors.getAverageFoliageColor(view, pos);
        }, CookItBlocks.VANILLA_VINE, CookItBlocks.VANILLA_VINE_STEM);

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            if (view != null && view.getBlockEntity(pos) instanceof MixingBowlEntity blockEntity) {
                if (state.getValue(MixingBowl.CONTAINS_GOOP)) return blockEntity.getGoopColor();
                CookIt.LOGGER.info("{} {}", state.getValue(MixingBowl.LIQUID_LAYER), blockEntity.getLiquid());
                if (state.getValue(MixingBowl.LIQUID_LAYER)) {
                    CookIt.LOGGER.info(String.valueOf(blockEntity.getLiquid().getColor()));
                    return blockEntity.getLiquid().getColor();
                }
            }
            return 0xF8D478;
        }, CookItBlocks.MIXING_BOWL);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            CompoundTag nbt = stack.getTag();
            if (nbt == null) return 0xF8D478;
            return nbt.getInt(MixingBowlEntity.COLOR_KEY);
        }, CookItItems.GOOP);
    }
}