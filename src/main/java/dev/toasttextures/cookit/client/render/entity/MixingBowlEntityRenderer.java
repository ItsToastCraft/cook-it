package dev.toasttextures.cookit.client.render.entity;

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

    @Override
    public void render(MixingBowlEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();
        BlockState state = blockEntity.getCachedState();
        if (state.get(MixingBowl.CONTAINS_LIQUID)) return;

        for (int i = 0; i < blockEntity.size(); i++) {
            ItemStack stack = blockEntity.getStack(i);
            if (stack.isEmpty()) continue;
            ItemRenderPosition pos = ITEM_RENDER_POSITIONS[i];
            float height = Registries.ITEM.getId(stack.getItem()).getNamespace().equals("minecraft") ? 0.25f : 0.5f;

            matrices.push();
            matrices.scale(0.3125f,0.3125f,0.3125f);
            matrices.translate(pos.x, height + pos.y, pos.z);
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(pos.angle));
            client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
            matrices.pop();
        }
    }

    private static final ItemRenderPosition[] ITEM_RENDER_POSITIONS = new ItemRenderPosition[] {
            new ItemRenderPosition(1.0f, 0.0675f, 1.5f, 30.0f),
            new ItemRenderPosition(2.125f, 0.125f, 0.875f, 18.7f),
            new ItemRenderPosition(1.75f, 0.0675f, 1.75f, -30.0f),
            new ItemRenderPosition(1.125f, 0.0f, 1.0f, 120.0f),
            new ItemRenderPosition(1.0f, 0.0f, 2.25f, 60f),
            new ItemRenderPosition(2.25f, 0.0f, 2.125f, 72.0f)
    };

    private record ItemRenderPosition(float x, float y, float z, float angle) {}
}