package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PlateItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();
        client.getBlockRenderManager().renderBlockAsEntity(((BlockItem) stack.getItem()).getBlock().getDefaultState(), matrices, vertexConsumers, light, overlay);

        CookIt.LOGGER.info("hi");
        ItemStack stored = ItemStorage.getStoredItem(stack);
        if (stored.isEmpty()) return;

        matrices.push();
        matrices.scale(0.5625f, 0.5625f, 0.5625f);
        //I'm fully aware of the 4th plate causing things to hover shut
        if (stored.isOf(CookItItems.PIZZA_SLICE)) {
            matrices.translate(0.234375f, 0.0f, -0.234375f);
        }
        matrices.translate(0.875f, 0.609375f + 0.125f * -0.125f, 0.875f);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        client.getItemRenderer().renderItem(stored, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, null, 0);
        matrices.pop();
    }
}