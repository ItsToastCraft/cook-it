package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Axis;

@Environment(EnvType.CLIENT)
public class PlateItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Minecraft client = Minecraft.getInstance();
        client.getBlockRenderer().renderSingleBlock(((BlockItem) stack.getItem()).getBlock().defaultBlockState(), matrices, vertexConsumers, light, overlay);

        CookIt.LOGGER.info("hi");
        ItemStack stored = ItemStorage.getStoredItem(stack);
        if (stored.isEmpty()) return;

        matrices.pushPose();
        matrices.scale(0.5625f, 0.5625f, 0.5625f);
        //I'm fully aware of the 4th plate causing things to hover shut
        if (stored.is(CookItItems.PIZZA_SLICE)) {
            matrices.translate(0.234375f, 0.0f, -0.234375f);
        }
        matrices.translate(0.875f, 0.609375f + 0.125f * -0.125f, 0.875f);

        matrices.mulPose(Axis.YP.rotationDegrees(90));
        client.getItemRenderer().renderStatic(stored, ItemDisplayContext.NONE, light, overlay, matrices, vertexConsumers, null, 0);
        matrices.popPose();
    }
}