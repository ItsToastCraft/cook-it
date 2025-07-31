package dev.toasttextures.cookit.client.render;

import dev.toasttextures.cookit.block.entity.CuttingBoardEntity;
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
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import dev.toasttextures.cookit.CookIt;

@Environment(EnvType.CLIENT)
public class CuttingBoardEntityRenderer implements BlockEntityRenderer<CuttingBoardEntity> {

    public CuttingBoardEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(CuttingBoardEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();

        ItemStack stack = blockEntity.getStack(0);

        if (!stack.isEmpty()) {
            Direction facing = blockEntity.getCachedState().get(Properties.FACING);
            float x, y;
            int dir = 0;
            switch (facing) {
                case NORTH -> {
                    x = 0.5f;
                    y = 0.625f;
                }
                case SOUTH -> {
                    x = -0.5f;
                    y = -0.375f;
                    dir = 2;
                }
                case EAST -> {
                    x = 0.5f;
                    y = -0.375f;
                    dir = 3;
                }
                case WEST -> {
                    x = -0.5f;
                    y = 0.625f;
                    dir = 1;
                }
                default -> {
                    CookIt.LOGGER.error("Cutting board is confused");
                    x = 0.0f;
                    y = 0.0f;
                }
            }
            if (Registries.ITEM.getId(stack.getItem()).getNamespace().equals("minecraft")) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * (dir)));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
                matrices.translate(x, y, -0.125f);
            } else {
                matrices.translate(0.5f, 0.4375f, 0.5f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * dir));
            }
            if (stack.isOf(CookItItems.RAW_DONUT) || stack.isOf(CookItItems.RAW_CINNAMON_ROLL)) {
                for (int i = 0; i < stack.getCount(); i++) {
                    matrices.push();
                    matrices.scale(0.5f, 0.5f, 0.5f);
                    matrices.translate(0.375f - 0.675f * (i % 2), -0.25f, 0.375 - 0.675f * (double) (i / 2));
                    client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
                    matrices.pop();
                }
            } else if (stack.isOf(CookItItems.RAW_CROISSANT)) {
                for (int i = 0; i < stack.getCount(); i++) {
                    matrices.push();
                    matrices.scale(0.5f, 0.5f, 0.5f);
                    matrices.translate(-i/2.5f + 0.5625f, -0.25f, (i%2)/ 3.0f - 0.125f);
                    client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
                    matrices.pop();
                }
            } else {
                matrices.push();
                matrices.scale(0.75f, 0.75f, 0.75f);
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
                matrices.pop();
            }
        }
    }
}