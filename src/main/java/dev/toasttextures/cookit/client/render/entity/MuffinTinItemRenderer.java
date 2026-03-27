package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.Container;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MuffinTinItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(CookItBlocks.MUFFIN_TIN.defaultBlockState(), matrices, vertexConsumers, light, overlay);

        List<ItemStack> items = Container.getItems(stack);

        MuffinTinEntityRenderer.render(items, matrices, vertexConsumers, null, light, overlay);
    }
}