package toast.cook_it.block.appliances.oven;

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
public class OvenEntityRenderer implements BlockEntityRenderer<OvenEntity> {

    public OvenEntityRenderer(BlockEntityRendererFactory.Context ctx) { }
    @Override
    public void render(OvenEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();


        for (int i = 0; i < blockEntity.getItems().size(); i++) {
            ItemStack stack = blockEntity.getStack(i);

            if (!stack.isEmpty()) {
                matrices.push();
                matrices.scale(0.5625f,0.5625f,0.5625f);
                matrices.translate((double) (i % 2) / 2.325 + 0.6875f, 0.5625, (double) (i % 8) / 6 + 0.325f);
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
                matrices.pop();
            }
        }
    }


}
