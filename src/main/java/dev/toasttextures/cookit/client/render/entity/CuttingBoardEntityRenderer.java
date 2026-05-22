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
import net.minecraft.core.Direction;
import com.mojang.math.Axis;
import dev.toasttextures.cookit.CookIt;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@Environment(EnvType.CLIENT)
public class CuttingBoardEntityRenderer implements BlockEntityRenderer<CuttingBoardEntity> {

    public CuttingBoardEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(CuttingBoardEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemStack stack = blockEntity.getFirst();
        if (stack.isEmpty()) return;

        final Minecraft client = Minecraft.getInstance();
        Direction dir = blockEntity.getBlockState().getValue(HORIZONTAL_FACING);
        Level world = blockEntity.getLevel();

        if (CookIt.isVanilla(stack)) {
            Vec2 position = ITEM_POSITIONS.get(dir);
            matrices.mulPose(Axis.XP.rotationDegrees(90));
            matrices.translate(position.x, position.y, -0.125f);
        } else {
            matrices.translate(0.5f, 0.4375f, 0.5f);
        }
        matrices.mulPose(Axis.YN.rotationDegrees(dir.toYRot()));

        if (stack.is(CookItItems.RAW_DONUT) || stack.is(CookItItems.RAW_CINNAMON_ROLL)) {
            renderInPlace(client, stack, RAW_CINNAMON_ROLL_POS, matrices, vertexConsumers, light, overlay);
        } else if (stack.is(CookItItems.RAW_CROISSANT)) {
            renderInPlace(client, stack, RAW_CROISSANT_POS, matrices, vertexConsumers, light, overlay);
        } else {
            matrices.pushPose();
            matrices.scale(0.75f, 0.75f, 0.75f);
            client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, world, 0);
            matrices.popPose();
        }
    }

    private static void renderInPlace(Minecraft client, ItemStack stack, ArrayList<ItemRenderPosition> location, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        for (int i = 0; i < stack.getCount(); i++) {
            ItemRenderPosition pos = location.get(i);
            matrices.pushPose();
            matrices.scale(pos.extra(), pos.extra(), pos.extra());
            matrices.translate(pos.x(), pos.y(), pos.z());
            client.getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, null, 0);
            matrices.popPose();
        }
    }

    private static final ArrayList<ItemRenderPosition> RAW_CROISSANT_POS = ItemRenderPosition.createCache((i) -> new ItemRenderPosition(-i / 2.5f + 0.5625f, -0.25f, (i % 2) / 3.0f - 0.125f, 0.5f), 4);
    private static final ArrayList<ItemRenderPosition> RAW_CINNAMON_ROLL_POS = ItemRenderPosition.createCache((i) -> new ItemRenderPosition(0.375f - 0.625f * (i % 2), -0.25f, 0.375f - 0.675f * (i / 2.0f), 0.5f), 4);

    private static final EnumMap<Direction, Vec2> ITEM_POSITIONS = new EnumMap<>(Map.of(
        Direction.NORTH, new Vec2(0.5f, 0.625f),
        Direction.SOUTH, new Vec2(-0.5f, -0.375f),
        Direction.EAST, new Vec2(0.5f, -0.375f),
        Direction.WEST, new Vec2(-0.5f, 0.625f)
    ));
}