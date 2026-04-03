package dev.toasttextures.cookit.client.render.entity;

import dev.toasttextures.cookit.compatibility.FiguraCompatibility;
import dev.toasttextures.cookit.item.armor.chef.ChefOutfitItem;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;

public class ChefOutfitRenderer {
    static void renderPart(PoseStack matrices, MultiBufferSource consumers, int light, Model model) {
        model.renderToBuffer(matrices, consumers.getBuffer(RenderType.armorCutoutNoCull(ChefOutfitItem.texture)), light, OverlayTexture.NO_OVERLAY, 1.0f,1.0f,1.0f,1.0f);
    }

    public static void register() {
        ArmorRenderer renderer = (matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
            boolean shouldRender = !(entity instanceof Player player) || (FiguraCompatibility.renderArmorPart(player, slot));
            if (!shouldRender) return;

            ChefOutfitItem armor = (ChefOutfitItem) stack.getItem();
            var model = armor.getArmorModel();
            contextModel.copyPropertiesTo(model);
            renderPart(matrices, vertexConsumers, light, model);
        };

        ArmorRenderer.register(renderer, CookItItems.CHEF_UNIFORM, CookItItems.CHEF_PANTS);
    }
}