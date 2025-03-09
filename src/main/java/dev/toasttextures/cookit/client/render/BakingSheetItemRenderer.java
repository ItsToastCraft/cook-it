package dev.toasttextures.cookit.client.render;

import dev.toasttextures.cookit.registry.CookItComponents;
import dev.toasttextures.cookit.registry.component.CookingComponent;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import dev.toasttextures.cookit.registry.CookItBlocks;

import java.util.List;

public class BakingSheetItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Renders the block first
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(CookItBlocks.BAKING_SHEET.getDefaultState(), matrices, vertexConsumers, light, overlay);
        // Gets the cooking component of the item and checks if it is storing any other items
        List<ItemStack> items = stack.getOrDefault(CookItComponents.COOKING_COMPONENT, CookingComponent.DEFAULT).stacks();
        if (items.isEmpty()) return;

        // Render any items that the baking sheet is storing
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty()) {
                BakingSheetEntityRenderer.renderItem(matrices, i, itemStack);
                MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(itemStack.getItem()), ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, MinecraftClient.getInstance().world, 0);
                matrices.pop();
            }
        }
    }
}
