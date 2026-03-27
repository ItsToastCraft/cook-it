package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.containers.MixingBowl;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.math.Axis;

@Environment(EnvType.CLIENT)
public class MixingBowlEntityRenderer implements BlockEntityRenderer<MixingBowlEntity> {

    public MixingBowlEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(MixingBowlEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Minecraft client = Minecraft.getInstance();
        BlockState state = blockEntity.getBlockState();
        if (state.getValue(MixingBowl.CONTAINS_LIQUID)) return;

        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            ItemStack stack = blockEntity.getItem(i);
            if (stack.isEmpty()) continue;
            ItemRenderPosition pos = ITEM_RENDER_POSITIONS[i];
            float height = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals("minecraft") ? 0.25f : 0.5f;

            matrices.pushPose();
            matrices.scale(0.3125f,0.3125f,0.3125f);
            matrices.translate(pos.x, height + pos.y, pos.z);
            matrices.mulPose(Axis.XN.rotationDegrees(90));
            matrices.mulPose(Axis.ZN.rotationDegrees(pos.angle));
            client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
            matrices.popPose();
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