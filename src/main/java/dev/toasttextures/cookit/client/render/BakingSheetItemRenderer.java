package dev.toasttextures.cookit.client.render;

import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import dev.toasttextures.cookit.registries.CookItBlocks;

public class BakingSheetItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Renders the block first
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(CookItBlocks.BAKING_SHEET.getDefaultState(), matrices, vertexConsumers, light, overlay);

            // Gets the NBT data of the item and checks if it is storing any other items
        NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
        if (nbt == null || !nbt.contains("Items")) return;

        NbtList itemsTag = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

        // Render any items that the baking sheet is storing
        for (int i = 0; i < itemsTag.size(); i++) {
            NbtCompound itemTag = itemsTag.getCompound(i);
            ItemStack itemStack = ItemStack.fromNbt(itemTag);
            if (!itemStack.isEmpty()) {
                matrices.push();

                if (itemStack.isOf(CookItItems.RAW_CINNAMON_ROLL) || itemStack.isOf(CookItItems.CINNAMON_ROLL)) {

                    matrices.scale(0.3125f,0.1f,0.3125f);
                    matrices.translate((double) (i % 2) / 1.25f + 1.25f, 0.5625f, (double) (i % 8) / 3.25f + 0.525f);//(double) (i % 8) / 3.375f + 0.525f);
                } else {
                    matrices.scale(0.5625f,0.5625f,0.5625f);
                    matrices.translate((double) (i % 2) / 2.375 + 0.6875f, 0.5625f, (double) (i % 8) / 6 + 0.3125f);
                }
                MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(itemStack.getItem()), ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, MinecraftClient.getInstance().world, 0);
                matrices.pop();
            }
        }
    }
}
