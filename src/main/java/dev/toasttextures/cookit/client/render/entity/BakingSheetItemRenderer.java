package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.Container;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import dev.toasttextures.cookit.registries.CookItBlocks;

public class BakingSheetItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        // Renders the block first
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(CookItBlocks.BAKING_SHEET.defaultBlockState(), matrices, vertexConsumers, light, overlay);

        BakingSheetEntityRenderer.render(Container.getItems(stack), matrices, vertexConsumers, null, light, overlay);
    }
}