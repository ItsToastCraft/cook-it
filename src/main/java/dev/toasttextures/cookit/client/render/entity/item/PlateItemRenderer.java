package dev.toasttextures.cookit.client.render.entity.item;

import dev.toasttextures.cookit.client.render.entity.PlateEntityRenderer;
import dev.toasttextures.cookit.item.ItemStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public class PlateItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (stack.isEmpty()) return;
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Block.byItem(stack.getItem()).defaultBlockState(), matrices, vertexConsumers, light, overlay);

        PlateEntityRenderer.render(ItemStorage.getStoredItem(stack), matrices, vertexConsumers, null, light, overlay, 1);
    }
}