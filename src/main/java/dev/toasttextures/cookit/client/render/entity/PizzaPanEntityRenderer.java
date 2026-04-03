package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.PizzaPanEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Environment(EnvType.CLIENT)
public class PizzaPanEntityRenderer implements BlockEntityRenderer<PizzaPanEntity> {
    public PizzaPanEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public void render(PizzaPanEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        render(blockEntity.getFirst(), matrices, vertexConsumers, blockEntity.getLevel(), light, overlay);
    }

    public static void render(ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers, Level world, int light, int overlay) {
        if (stack.isEmpty()) return;
        final Minecraft client = Minecraft.getInstance();

        matrices.pushPose();
        matrices.translate(0.5f, 0.5125f, 0.5f);
        client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, world, 0);
        matrices.popPose();
    }
}