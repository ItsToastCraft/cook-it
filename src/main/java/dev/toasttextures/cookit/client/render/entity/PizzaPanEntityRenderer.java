package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.PizzaPanEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PizzaPanEntityRenderer implements BlockEntityRenderer<PizzaPanEntity> {
    public PizzaPanEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public int getRenderDistance() {
        return 16;
    }

    @Override
    public void render(PizzaPanEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        render(blockEntity.getStack(0), matrices, vertexConsumers, blockEntity.getWorld(), light, overlay);
    }

    public static void render(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int light, int overlay) {
        if (stack.isEmpty()) return;
        final MinecraftClient client = MinecraftClient.getInstance();

        matrices.push();
        matrices.translate(0.5f, 0.5125f, 0.5f);
        client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, world, 0);
        matrices.pop();
    }
}