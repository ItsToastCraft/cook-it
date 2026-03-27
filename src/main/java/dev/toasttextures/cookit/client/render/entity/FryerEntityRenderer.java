package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.FryerEntity;
import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;

import java.util.EnumMap;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

@Environment(EnvType.CLIENT)
public class FryerEntityRenderer implements BlockEntityRenderer<FryerEntity> {

    public FryerEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(FryerEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();

        ItemStack fryerBasket = entity.getFirst();
        if (!fryerBasket.isOf(CookItItems.FRYER_BASKET)) return; // Something went seriously wrong if there's something other than a basket here

        ItemStack storedItem = ItemStorage.getStoredItem(fryerBasket);
        Direction facing = entity.getCachedState().get(HORIZONTAL_FACING);
        Vec2f pos = ITEM_POSITIONS.get(facing);

        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(facing.asRotation()));
        matrices.translate(pos.x, 0.625, pos.y);

        client.getItemRenderer().renderItem(fryerBasket, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);

        if (!storedItem.isEmpty()) {
            matrices.push();
            matrices.scale(0.75f,0.75f,0.75f);
            matrices.translate(0.0,-0.25,0.0);

            client.getItemRenderer().renderItem(storedItem, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);

            matrices.pop();
        }
    }

    private static final EnumMap<Direction, Vec2f> ITEM_POSITIONS = new EnumMap<>(Direction.class);

    static  {
        ITEM_POSITIONS.put(Direction.NORTH, new Vec2f(-0.5f,-0.4375f));
        ITEM_POSITIONS.put(Direction.SOUTH, new Vec2f(0.5f, 0.5625f));
        ITEM_POSITIONS.put(Direction.EAST, new Vec2f(-0.5f,0.5625f));
        ITEM_POSITIONS.put(Direction.WEST, new Vec2f(0.5f, -0.4375f));
    }
}