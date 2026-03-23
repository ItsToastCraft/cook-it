package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.entity.MicrowaveEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;

import java.util.Map;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

@Environment(EnvType.CLIENT)
public class MicrowaveEntityRenderer implements BlockEntityRenderer<MicrowaveEntity> {

    private static final Map<Direction, ItemRenderPosition> ITEM_POSITIONS = Map.of(
            Direction.NORTH, new ItemRenderPosition(new Vec2f(1.0625f, 1.0f), new Vec2f(2.125f, 1.75f)),
            Direction.SOUTH, new ItemRenderPosition(new Vec2f(0.9375f, 1.0f), new Vec2f(2.125f, 1.75f)),
            Direction.EAST, new ItemRenderPosition(new Vec2f(1.0f, 1.0625f), new Vec2f(1.75f, 2.125f)),
            Direction.WEST, new ItemRenderPosition(new Vec2f(1.0f, 0.9375f), new Vec2f(1.75f, 2.125f)));

    public MicrowaveEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(MicrowaveEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();

        ItemStack stack = blockEntity.getStack(0);
        if (stack.isEmpty()) return;
        World world = blockEntity.getWorld();
        if (world == null) return;

        Direction facing = blockEntity.getCachedState().get(HORIZONTAL_FACING);
        ItemRenderPosition pair = ITEM_POSITIONS.get(facing);
        Vec2f pos;
        matrices.push();
        if (stack.getItem() instanceof BlockItem) {
            pos = pair.blockItemPos;
            matrices.scale(0.25f, 0.25f, 0.25f);
            matrices.translate(pos.x, 1.0f, pos.y);
        } else {
            pos = pair.itemPos;
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.translate(pos.x, 0.71875f, pos.y);
        }
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(CookIt.DIRECTION_TO_FLOAT.getOrDefault(facing, 0.0f)));

        // Rotate the item
        if (blockEntity.getProgress() > 0) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((world.getTime() + tickDelta) * 4));
        }

        client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, world, 0);
        matrices.pop();
    }

    private record ItemRenderPosition(Vec2f itemPos, Vec2f blockItemPos) {}
}