package toast.cook_it.block.containers.muffin_tin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class MuffinTinEntityRenderer implements BlockEntityRenderer<MuffinTinEntity> {

    public MuffinTinEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(MuffinTinEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();


        for (int i = 0; i < 2; i++) {

            for (int j = 0; j < 3; j++) {
                //CookIt.LOGGER.info(i + " " + j);
                ItemStack stack = blockEntity.getStack(i * 3 + j);
                if (!stack.isEmpty()) {
                    matrices.push();
                    matrices.scale(0.359375f,0.359375f,0.359375f);
                    matrices.translate(i * 0.8675 + 0.95625, 0.5625, j * 0.69125 + 0.6125);
                    client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
                    matrices.pop();
                }
            }

        }
    }
}
