package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MuffinTinEntityRenderer implements BlockEntityRenderer<MuffinTinEntity> {

    public MuffinTinEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(MuffinTinEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        render(blockEntity.getItems(), matrices, vertexConsumers, blockEntity.getWorld(), light, overlay);
    }

    public static void render(List<ItemStack> stacks, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay) {
        final MinecraftClient client = MinecraftClient.getInstance();
        if (stacks.isEmpty()) return;
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            if (stack.isEmpty()) continue;

            int x = i / 3;
            int z = i % 3;

            matrices.push();
            matrices.scale(0.359375f,0.359375f,0.359375f);
            matrices.translate(x * 0.8675 + 0.95625, 0.5625, z * 0.69125 + 0.6125);
            client.getItemRenderer().renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, world, 0);
            matrices.pop();
        }
    }
}