package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.FryerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import dev.toasttextures.cookit.CookIt;

import static dev.toasttextures.cookit.block.appliances.Microwave.FACING;

@Environment(EnvType.CLIENT)
public class FryerEntityRenderer implements BlockEntityRenderer<FryerEntity> {

    public FryerEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }


    @Override
    public void render(FryerEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();
        ItemStack fryerBasket = entity.getStack(0);
        ItemStack item = !FryerEntity.getContainerItems(entity.getStack(0)).isEmpty() ? FryerEntity.getContainerItems(entity.getStack(0)).get(0) : ItemStack.EMPTY;
        Direction facing = entity.getCachedState().get(FACING);
        int dir = 0;
        float x = 0, y = 0, z = 0;
        switch (facing) {
            case NORTH -> {
                x = -0.5f;
                y = 0.625f;
                z = -0.4375f;
                dir = 2;
            }
            case SOUTH -> {
                x = 0.5f;
                y = 0.625f;
                z = 0.5625f;
                dir = 4;
            }
            case EAST -> {
                x = -0.5f;
                y = 0.625f;
                z = 0.5625f;
                dir = 1;
            }
            case WEST -> {
                x = 0.5f;
                y = 0.625f;
                z = -0.4375f;
                dir = 3;
            }
            default -> CookIt.LOGGER.error("Fryer fried its braincells > {}", facing);
        }
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * dir));
        matrices.translate(x, y, z);
        if (!item.isEmpty()) {

            matrices.push();
            matrices.scale(0.75f,0.75f,0.75f);
            matrices.translate(0,-0.25,0);

            client.getItemRenderer().renderItem(item, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);

            matrices.pop();
        }

        client.getItemRenderer().renderItem(fryerBasket, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);
    }
}
