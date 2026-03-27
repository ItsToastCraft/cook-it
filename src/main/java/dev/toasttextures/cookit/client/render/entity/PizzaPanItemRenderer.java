package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;

public class PizzaPanItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        // Renders the block first
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(CookItBlocks.PIZZA_PAN.defaultBlockState(), matrices, vertexConsumers, light, overlay);

        PizzaPanEntityRenderer.render(ItemStorage.getStoredItem(stack), matrices, vertexConsumers, null, light, overlay);
    }
}