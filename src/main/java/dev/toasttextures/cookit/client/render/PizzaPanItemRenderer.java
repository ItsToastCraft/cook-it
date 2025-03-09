package dev.toasttextures.cookit.client.render;

import dev.toasttextures.cookit.registry.CookItBlocks;
import dev.toasttextures.cookit.registry.CookItComponents;
import dev.toasttextures.cookit.registry.component.SingleCookingComponent;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class PizzaPanItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Renders the block first

        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(CookItBlocks.PIZZA_PAN.getDefaultState(), matrices, vertexConsumers, light, overlay);

        // Gets the NBT data of the item and checks if it is storing any other items
        ItemStack item = stack.getOrDefault(CookItComponents.SINGLE_COOKING_COMPONENT, SingleCookingComponent.DEFAULT).getItem();

        if (item.isEmpty()) return;

        matrices.push();
        //matrices.scale(0.5625f,0.5625f,0.5625f);
        matrices.translate(0.5f,0.5125f,0.5f);
        MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, MinecraftClient.getInstance().world, 0);
        matrices.pop();

    }
}
