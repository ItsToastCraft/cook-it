package dev.toasttextures.cookit.client.render.entity.item;

import dev.toasttextures.cookit.block.entity.PizzaEntity;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;

public class PizzaItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final PizzaEntity pizzaEntity = new PizzaEntity(BlockPos.ZERO, CookItBlocks.PIZZA.defaultBlockState());
    private BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (blockEntityRenderDispatcher == null) {
            blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        }

        pizzaEntity.readFromItemStack(stack);
        matrices.pushPose();
        blockEntityRenderDispatcher.renderItem(pizzaEntity, matrices, vertexConsumers, light, overlay);
        matrices.popPose();
    }
}