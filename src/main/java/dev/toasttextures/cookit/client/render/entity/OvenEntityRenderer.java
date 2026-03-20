package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.OvenEntity;
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

import static dev.toasttextures.cookit.block.appliances.Oven.FACING;

@Environment(EnvType.CLIENT)
public class OvenEntityRenderer implements BlockEntityRenderer<OvenEntity> {

    public OvenEntityRenderer(BlockEntityRendererFactory.Context ctx) { }
    @Override
    public void render(OvenEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();
        for (int i = 0; i < 2; i++) {
            ItemStack stack = blockEntity.getStack(i);

            if (!stack.isEmpty()) {
                matrices.push();
                matrices.scale(0.875f,0.875f,0.875f);
                Direction facing = blockEntity.getCachedState().get(FACING);

                matrices.translate(0.5625f, 0.3f * i + 0.9125f, 0.5625f);
                if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                }

//                if (stack.getItem().equals(CookItBlocks.PIZZA_PAN.asItem())) {
//                    NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
//                    if (nbt == null || !nbt.contains("Items")) return;
//
//                    NbtList itemsTag = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
//                    NbtCompound itemTag = itemsTag.getCompound(0);
//
//                    ItemStack pizza = ItemStack.fromNbt(itemTag);
//                    if (pizza.isEmpty()) { return; }
//                    matrices.push();
//                    matrices.translate(0.0f,0.0625f, 0.0f);
//                    client.getItemRenderer().renderItem(pizza, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
//                    matrices.pop();
//                }
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
                matrices.pop();
            }
        }
        }
}
