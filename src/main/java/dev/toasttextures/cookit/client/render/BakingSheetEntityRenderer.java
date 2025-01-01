package dev.toasttextures.cookit.client.render;

import dev.toasttextures.cookit.block.entity.BakingSheetEntity;
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

@Environment(EnvType.CLIENT)
public class BakingSheetEntityRenderer implements BlockEntityRenderer<BakingSheetEntity> {

    public BakingSheetEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public int getRenderDistance() {
        return 32;
    }

    @Override
    public void render(BakingSheetEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();


        for (int i = 0; i < blockEntity.getItems().size(); i++) {
            ItemStack stack = blockEntity.getStack(i);
            if (!stack.isEmpty()) {
                matrices.push();
                if (stack.isOf(CookItItems.RAW_CINNAMON_ROLL) || stack.isOf(CookItItems.CINNAMON_ROLL) ) {
                    matrices.scale(0.3125f,0.3125f,0.3125f);
                    matrices.translate((double) (i % 2) / 1.25f + 1.25f, 0.5625f, (double) (i % 8) / 3.25f + 0.525f);//(double) (i % 8) / 3.375f + 0.525f);
                } else {
                    matrices.scale(0.5625f,0.5625f,0.5625f);
                    matrices.translate((double) (i % 2) / 2.375 + 0.6875f, 0.5625f, (double) (i % 8) / 6 + 0.3125f);
                }
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
                matrices.pop();
            }
        }
    }
}
