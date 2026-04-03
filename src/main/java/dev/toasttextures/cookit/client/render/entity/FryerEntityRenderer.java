package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.FryerEntity;
import dev.toasttextures.cookit.item.ItemStorage;
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
import net.minecraft.world.phys.Vec2;

import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@Environment(EnvType.CLIENT)
public class FryerEntityRenderer implements BlockEntityRenderer<FryerEntity> {

    public FryerEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(FryerEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemStack fryerBasket = entity.getFirst();
        if (!fryerBasket.is(CookItItems.FRYER_BASKET)) return; // Something went seriously wrong if there's something other than a basket here

        final Minecraft client = Minecraft.getInstance();
        ItemStack storedItem = ItemStorage.getStoredItem(fryerBasket);
        Direction facing = entity.getBlockState().getValue(HORIZONTAL_FACING);
        Vec2 pos = ITEM_POSITIONS.get(facing);

        matrices.mulPose(Axis.YN.rotationDegrees(facing.toYRot()));
        matrices.translate(pos.x, 0.625f, pos.y);

        client.getItemRenderer().renderStatic(fryerBasket, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, entity.getLevel(), 0);

        if (!storedItem.isEmpty()) {
            matrices.pushPose();
            matrices.scale(0.75f,0.75f,0.75f);
            matrices.translate(0.0,-0.25,0.0);

            client.getItemRenderer().renderStatic(storedItem, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, entity.getLevel(), 0);

            matrices.popPose();
        }
    }

    private static final EnumMap<Direction, Vec2> ITEM_POSITIONS = new EnumMap<>(Map.of(
            Direction.NORTH, new Vec2(-0.5f,-0.4375f),
            Direction.SOUTH, new Vec2(0.5f, 0.5625f),
            Direction.EAST, new Vec2(-0.5f,0.5625f),
            Direction.WEST, new Vec2(0.5f, -0.4375f)
    ));
}