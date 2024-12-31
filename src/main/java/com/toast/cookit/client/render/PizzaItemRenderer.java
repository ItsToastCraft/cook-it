package com.toast.cookit.client.render;

import com.toast.cookit.block.entity.PizzaEntity;
import com.toast.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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
