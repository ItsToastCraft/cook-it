package toast.cook_it.block.containers.plate;

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

@Environment(EnvType.CLIENT)
public class PlateEntityRenderer implements BlockEntityRenderer<PlateEntity> {

    public PlateEntityRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(PlateEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();

        ItemStack stack = blockEntity.getStack(0);

        if (!stack.isEmpty()) {
            matrices.push();
            matrices.scale(0.5625f,0.5625f,0.5625f);
            matrices.translate(0.875f, 0.625f, 0.875f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
            matrices.pop();
        }
    }
}
