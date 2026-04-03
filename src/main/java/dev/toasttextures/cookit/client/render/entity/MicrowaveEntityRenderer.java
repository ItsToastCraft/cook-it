package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.MicrowaveEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@Environment(EnvType.CLIENT)
public class MicrowaveEntityRenderer implements BlockEntityRenderer<MicrowaveEntity> {

    private static final EnumMap<Direction, ItemRenderPosition> ITEM_POSITIONS = new EnumMap<>(Map.of(
            Direction.NORTH, new ItemRenderPosition(new Vec2(1.0625f, 1.0f), new Vec2(2.125f, 1.75f)),
            Direction.SOUTH, new ItemRenderPosition(new Vec2(0.9375f, 1.0f), new Vec2(2.125f, 1.75f)),
            Direction.EAST, new ItemRenderPosition(new Vec2(1.0f, 1.0625f), new Vec2(1.75f, 2.125f)),
            Direction.WEST, new ItemRenderPosition(new Vec2(1.0f, 0.9375f), new Vec2(1.75f, 2.125f))
    ));

    public MicrowaveEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(MicrowaveEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemStack stack = blockEntity.getItem(0);
        if (stack.isEmpty()) return;

        final Minecraft client = Minecraft.getInstance();
        Level world = blockEntity.getLevel();
        Direction facing = blockEntity.getBlockState().getValue(HORIZONTAL_FACING);
        ItemRenderPosition pair = ITEM_POSITIONS.get(facing);
        Vec2 pos;

        matrices.pushPose();
        if (stack.getItem() instanceof BlockItem) {
            pos = pair.blockItemPos;
            matrices.scale(0.25f, 0.25f, 0.25f);
            matrices.translate(pos.x, 1.0f, pos.y);
        } else {
            pos = pair.itemPos;
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(pos.x, 0.71875f, pos.y);
        }
        matrices.mulPose(Axis.YN.rotationDegrees(facing.toYRot()));

        // Rotate the item
        if (blockEntity.getProgress() > 0) {
            matrices.mulPose(Axis.YN.rotationDegrees((world.getGameTime() + tickDelta) * 4));
        }

        client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, world, 0);
        matrices.popPose();
    }

    private record ItemRenderPosition(Vec2 itemPos, Vec2 blockItemPos) {}
}