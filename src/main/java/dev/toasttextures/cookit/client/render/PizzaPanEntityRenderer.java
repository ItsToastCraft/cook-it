package dev.toasttextures.cookit.client.render;

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
        final MinecraftClient client = MinecraftClient.getInstance();
        ItemStack stack = blockEntity.getStack(0);
        if (stack.isEmpty()) return;

        matrices.push();
        matrices.translate(0.5f, 0.5125f, 0.5f);
        client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, blockEntity.getWorld(), 0);
        matrices.pop();
    }
}