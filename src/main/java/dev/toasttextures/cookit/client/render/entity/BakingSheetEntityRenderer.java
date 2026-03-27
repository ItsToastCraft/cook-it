package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.entity.BakingSheetEntity;
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
import net.minecraft.world.level.Level;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BakingSheetEntityRenderer implements BlockEntityRenderer<BakingSheetEntity> {

    public BakingSheetEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public void render(BakingSheetEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        render(blockEntity.getItems(), matrices, vertexConsumers, blockEntity.getLevel(), light, overlay);
    }

    public static void render(List<ItemStack> items, PoseStack matrices, MultiBufferSource vertexConsumers, Level world, int light, int overlay) {
        final Minecraft client = Minecraft.getInstance();
        if (items.isEmpty()) return;
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);

            if (stack.isEmpty()) continue;

            matrices.pushPose();
            if (stack.is(CookItItems.RAW_CINNAMON_ROLL) || stack.is(CookItItems.CINNAMON_ROLL) ) {
                matrices.scale(0.3125f,0.3125f,0.3125f);
                matrices.translate((double) (i % 2) / 1.25f + 1.25f, 0.5625f, (double) (i % 8) / 3.25f + 0.525f);
            } else {
                matrices.scale(0.5625f,0.5625f,0.5625f);
                matrices.translate((double) (i % 2) / 2.375 + 0.6875f, 0.5625f, (double) (i % 8) / 6 + 0.3125f);
            }
            client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, world, 0);
            matrices.popPose();
        }
    }
}