package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MuffinTinEntityRenderer implements BlockEntityRenderer<MuffinTinEntity> {

    public MuffinTinEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(MuffinTinEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        render(blockEntity.getItems(), matrices, vertexConsumers, blockEntity.getLevel(), light, overlay);
    }

    public static void render(NonNullList<ItemStack> stacks, PoseStack matrices, MultiBufferSource vertexConsumers, @Nullable Level world, int light, int overlay) {
        final Minecraft client = Minecraft.getInstance();

        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (stack.isEmpty()) continue;

            matrices.pushPose();
            matrices.scale(0.359375f,0.359375f,0.359375f);
            //CookIt.LOGGER.info("{} {}", i, stack);
            matrices.translate((float) ( i / 3) * 0.8675 + 0.95625, 0.5625, (i % 3) * 0.69125 + 0.6125);
            client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, world, 0);
            matrices.popPose();
        }
    }
}