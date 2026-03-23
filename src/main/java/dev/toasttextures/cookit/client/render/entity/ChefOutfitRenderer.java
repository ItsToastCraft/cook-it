package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.compatibility.FiguraCompatibility;
import dev.toasttextures.cookit.item.armor.chef.ChefOutfitItem;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ChefOutfitRenderer {
    static void renderPart(MatrixStack matrices, VertexConsumerProvider consumers, int light, ItemStack stack, Model model) {
        model.render(matrices, consumers.getBuffer(RenderLayer.getArmorCutoutNoCull(ChefOutfitItem.texture)), light, OverlayTexture.DEFAULT_UV, 1.0f,1.0f,1.0f,1.0f);
    }

    public static void register() {
        ArmorRenderer renderer = (matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
            boolean shouldRender = !(contextModel instanceof PlayerEntityModel<?>) || (FiguraCompatibility.renderArmorPart((PlayerEntity) entity, slot));
            if (!shouldRender) return;

            ChefOutfitItem armor = (ChefOutfitItem) stack.getItem();
            var model = armor.getArmorModel();
            contextModel.copyBipedStateTo(model);
            renderPart(matrices, vertexConsumers, light, stack, model);
        };

        ArmorRenderer.register(renderer, CookItItems.CHEF_UNIFORM, CookItItems.CHEF_PANTS);
    }
}
