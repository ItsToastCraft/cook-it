package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class PizzaPanItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Renders the block first
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(CookItBlocks.PIZZA_PAN.getDefaultState(), matrices, vertexConsumers, light, overlay);

        PizzaPanEntityRenderer.render(ItemStorage.getStoredItem(stack), matrices, vertexConsumers, null, light, overlay);
    }
}