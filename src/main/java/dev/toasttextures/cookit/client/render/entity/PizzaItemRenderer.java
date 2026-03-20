package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.PizzaEntity;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class PizzaItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final PizzaEntity pizzaEntity = new PizzaEntity(BlockPos.ORIGIN, CookItBlocks.PIZZA.getDefaultState());
    private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntityRenderDispatcher == null) {
            blockEntityRenderDispatcher = MinecraftClient.getInstance().getBlockEntityRenderDispatcher();
        }

        pizzaEntity.readFromItemStack(stack);
        matrices.push();
        blockEntityRenderDispatcher.renderEntity(pizzaEntity, matrices, vertexConsumers, light, overlay);
        matrices.pop();
    }
}