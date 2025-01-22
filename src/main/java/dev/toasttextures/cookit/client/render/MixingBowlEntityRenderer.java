package dev.toasttextures.cookit.client.render;

import dev.toasttextures.cookit.block.containers.MixingBowl;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.RotationAxis;


@Environment(EnvType.CLIENT)
public class MixingBowlEntityRenderer implements BlockEntityRenderer<MixingBowlEntity> {

    public MixingBowlEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }
    private final float[][] modifications = {
            {1f, 0.0675f, 1.5f, 30f }, {2.125f, 0.125f, 0.875f, 18.7f },
            {1.75f, 0.0675f, 1.75f, -30f }, {1.125f, 0.0f, 1.0f, 120f },
            {1.0f, 0.0f, 2.25f, 60f }, {2.25f, 0.0f, 2.125f, 72f }
    };
    @Override
    public void render(MixingBowlEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();
        BlockState state = blockEntity.getCachedState();
        if (state.get(MixingBowl.HAS_GOOP)) return;
        for (int i = 0; i < blockEntity.size() - 1; i++) {
            ItemStack stack = blockEntity.getStack(i);
            if (!stack.isEmpty()) {
                matrices.push();
                float height = Registries.ITEM.getId(stack.getItem()).getNamespace().equals("minecraft") ? 0.25f : 0.5f ;
                matrices.scale(0.3125f,0.3125f,0.3125f);
                matrices.translate(modifications[i][0], height + modifications[i][1], modifications[i][2]);
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
                matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(modifications[i][3]));
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
                matrices.pop();
            }
        }
    }
}