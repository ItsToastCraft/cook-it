package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.CuttingBoardEntity;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Direction;
import com.mojang.math.Axis;
import dev.toasttextures.cookit.CookIt;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@Environment(EnvType.CLIENT)
public class CuttingBoardEntityRenderer implements BlockEntityRenderer<CuttingBoardEntity> {

    public CuttingBoardEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(CuttingBoardEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Minecraft client = Minecraft.getInstance();

        ItemStack stack = blockEntity.getItem(0);
        if (stack.isEmpty()) return;

        Direction dir = blockEntity.getBlockState().getValue(HORIZONTAL_FACING);
        Level world = blockEntity.getLevel();
        float facing = CookIt.DIRECTION_TO_FLOAT.getOrDefault(dir, 0.0f);

        if (BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals("minecraft")) {
            Vec2 position = ITEM_POSITIONS.get(dir);
            matrices.mulPose(Axis.XP.rotationDegrees(90));
            matrices.translate(position.x, position.y, -0.125f);
        } else {
            matrices.translate(0.5f, 0.4375f, 0.5f);
        }
        matrices.mulPose(Axis.YP.rotationDegrees(facing));

        if (stack.is(CookItItems.RAW_DONUT) || stack.is(CookItItems.RAW_CINNAMON_ROLL)) {
            for (int i = 0; i < stack.getCount(); i++) {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(0.375f - 0.675f * (i % 2), -0.25f, 0.375 - 0.675f * (double) (i / 2));
                client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, world, 0);
                matrices.popPose();
            }
        } else if (stack.is(CookItItems.RAW_CROISSANT)) {
            for (int i = 0; i < stack.getCount(); i++) {
                matrices.pushPose();
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-i / 2.5f + 0.5625f, -0.25f, (i % 2) / 3.0f - 0.125f);
                client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, world, 0);
                matrices.popPose();
            }
        } else {
            matrices.pushPose();
            matrices.scale(0.75f, 0.75f, 0.75f);
            client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, world, 0);
            matrices.popPose();
        }
    }

    private static final EnumMap<Direction, Vec2> ITEM_POSITIONS = new EnumMap<>(Map.of(
        Direction.NORTH, new Vec2(0.5f, 0.625f),
        Direction.SOUTH, new Vec2(-0.5f, -0.375f),
        Direction.EAST, new Vec2(0.5f, -0.375f),
        Direction.WEST, new Vec2(-0.5f, 0.625f)
    ));
}