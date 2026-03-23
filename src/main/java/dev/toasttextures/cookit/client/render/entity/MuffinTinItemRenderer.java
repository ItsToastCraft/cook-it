package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.Container;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MuffinTinItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(CookItBlocks.MUFFIN_TIN.getDefaultState(), matrices, vertexConsumers, light, overlay);

        List<ItemStack> items = Container.getItems(stack);

        MuffinTinEntityRenderer.render(items, matrices, vertexConsumers, null, light, overlay);
    }
}