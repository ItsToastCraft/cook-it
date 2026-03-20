package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class MuffinTinItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(CookItBlocks.MUFFIN_TIN.getDefaultState(), matrices, vertexConsumers, light, overlay);

        NbtCompound nbt = stack.getSubNbt("BlockEntityTag");


        if (nbt == null || !nbt.contains("Items")) return;

        NbtList itemsTag = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                ItemStack muffin = ItemStack.fromNbt(itemsTag.getCompound(i * 3 + j));

                if (!muffin.isEmpty()) {
                    matrices.push();
                    matrices.scale(0.359375f,0.359375f,0.359375f);
                    matrices.translate(i * 0.8675 + 0.95625, 0.5625, j * 0.69125 + 0.6125);
                    client.getItemRenderer().renderItem(muffin, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, null, 0);
                    matrices.pop();
                }
            }

        }
    }
}
