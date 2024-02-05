package toast.cook_it;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import toast.cook_it.block.CookItBlocks;
import toast.cook_it.block.baking_sheet.BakingSheetEntityRenderer;
import toast.cook_it.item.CookItItems;

@Environment(EnvType.CLIENT)
public class CookItClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Here we will put client-only registration code
        BlockEntityRendererRegistry.register(CookItBlocks.BAKING_SHEET_ENTITY, BakingSheetEntityRenderer::new);
        ModelPredicateProviderRegistry.register(CookItItems.FIRE_EXTINGUISHER, new Identifier("extinguisher_fuel"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return (float) (stack.getDamage() / 100);
            }
        });
    }
}