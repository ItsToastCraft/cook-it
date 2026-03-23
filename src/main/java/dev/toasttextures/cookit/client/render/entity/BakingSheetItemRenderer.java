package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.Container;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import dev.toasttextures.cookit.registries.CookItBlocks;

public class BakingSheetItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Renders the block first
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(CookItBlocks.BAKING_SHEET.getDefaultState(), matrices, vertexConsumers, light, overlay);

        BakingSheetEntityRenderer.render(Container.getItems(stack), matrices, vertexConsumers, null, light, overlay);
    }
}