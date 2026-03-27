package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.OvenEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import com.mojang.math.Axis;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@Environment(EnvType.CLIENT)
public class OvenEntityRenderer implements BlockEntityRenderer<OvenEntity> {

    public OvenEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(OvenEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Minecraft client = Minecraft.getInstance();
        for (int i = 0; i < 2; i++) {
            ItemStack stack = blockEntity.getItem(i);
            if (stack.isEmpty()) continue;
            matrices.pushPose();
            matrices.scale(0.875f, 0.875f, 0.875f);

            matrices.translate(0.5625f, 0.3f * i + 0.9125f, 0.5625f);
            if (blockEntity.getBlockState().getValue(HORIZONTAL_FACING).getAxis() == net.minecraft.core.Direction.Axis.Z) {
                matrices.mulPose(Axis.YP.rotationDegrees(90));
            }

            client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
            matrices.popPose();
        }
    }
}