package dev.toasttextures.cookit.client;

import dev.toasttextures.cookit.block.containers.Plate;
import dev.toasttextures.cookit.client.render.entity.item.*;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

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
}