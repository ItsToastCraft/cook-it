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
        ItemStack stack = blockEntity.getStack(0);

            if (!stack.isEmpty()) {
                matrices.push();

                matrices.translate(0.5f, 1.125f, 0.5f);
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
                matrices.pop();
            }
        }
}
