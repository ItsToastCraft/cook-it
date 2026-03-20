package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.PlateEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

import static dev.toasttextures.cookit.block.containers.Plate.COUNT;

@Environment(EnvType.CLIENT)
public class PlateEntityRenderer<T extends PlateEntity> implements BlockEntityRenderer<T> {
    public PlateEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();
        ItemStack stack = blockEntity.getStack(0);
        if (stack.isEmpty()) return;

        matrices.push();
        matrices.scale(0.5625f, 0.5625f, 0.5625f);
        //I'm fully aware of the 4th plate causing things to hover shut
        if (stack.isOf(CookItItems.PIZZA_SLICE)) {
            matrices.translate(0.234375f, 0.0f, -0.234375f);
        }
        matrices.translate(0.875f, 0.609375f + 0.125f * Math.max(0, blockEntity.getCachedState().get(COUNT) - 1.125f), 0.875f);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
        matrices.pop();
    }
}