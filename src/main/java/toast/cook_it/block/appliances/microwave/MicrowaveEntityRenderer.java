package toast.cook_it.block.appliances.microwave;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import toast.cook_it.CookIt;

import static toast.cook_it.block.appliances.microwave.Microwave.FACING;

@Environment(EnvType.CLIENT)
public class MicrowaveEntityRenderer implements BlockEntityRenderer<MicrowaveEntity> {

    public MicrowaveEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(MicrowaveEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();

        ItemStack stack = blockEntity.getStack(0);

        Direction facing = blockEntity.getCachedState().get(FACING);
        float x, y, z, x2, y2, z2;
        int dir = 0;
        switch (facing) {
            case NORTH -> {
                x = 1.0625f;
                y = 0.71875f;
                z = 1.0f;
                x2 = 2.125f;
                y2 = 1.0f;
                z2 = 1.75f;
                dir = 0;
            }
            case SOUTH -> {
                x = 0.9375f;
                y = 0.71875f;
                z = 1.0f;
                x2 = 2.125f;
                y2 = 1.0f;
                z2 = 1.75f;
                dir = 2;
            }
            case EAST -> {

                x = 1.0f;
                y = 0.71875f;
                z = 1.0625f;
                x2 = 1.75f;
                y2 = 1.0f;
                z2 = 2.125f;
                dir = 3;
            }
            case WEST -> {
                x = 1.0f;
                y = 0.71875f;
                z = 0.9375f;
                x2 = 1.75f;
                y2 = 1.0f;
                z2 = 2.125f;
                dir = 1;
            }
            default -> {
                CookIt.LOGGER.error("Microwave combustion");
                x = 0.0f;
                y = 0.0f;
                z = 0.0f;
                x2 = 0.0f;
                y2 = 0.0f;
                z2 = 0.0f;
            }
        }
        if (!stack.isEmpty()) {
            matrices.push();
            if (stack.getItem() instanceof BlockItem) {
                matrices.scale(0.25f, 0.25f, 0.25f);
                matrices.translate(x2, y2, z2);
            } else {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(x, y, z);
            }
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(dir * 90));

            // Rotate the item
            if (blockEntity.progress > 0) {
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((blockEntity.getWorld().getTime() + tickDelta) * 4));

            }

            client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
            matrices.pop();
        }
    }

}
