package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.PlateEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Axis;

import static dev.toasttextures.cookit.block.containers.Plate.COUNT;

@Environment(EnvType.CLIENT)
public class PlateEntityRenderer<T extends PlateEntity> implements BlockEntityRenderer<T> {
    public PlateEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(T blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Minecraft client = Minecraft.getInstance();
        ItemStack stack = blockEntity.getItem(0);
        if (stack.isEmpty()) return;

        matrices.pushPose();
        matrices.scale(0.5625f, 0.5625f, 0.5625f);
        //I'm fully aware of the 4th plate causing things to hover shut
        if (stack.is(CookItItems.PIZZA_SLICE)) {
            matrices.translate(0.234375f, 0.0f, -0.234375f);
        }
        matrices.translate(0.875f, 0.609375f + 0.125f * Math.max(0, blockEntity.getBlockState().getValue(COUNT) - 1.125f), 0.875f);

        matrices.mulPose(Axis.YP.rotationDegrees(90));
        client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
        matrices.popPose();
    }
}