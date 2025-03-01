package dev.toasttextures.cookit.item.armor;

import dev.toasttextures.cookit.CookItClient;
import dev.toasttextures.cookit.compat.FiguraCompat;
import dev.toasttextures.cookit.item.armor.ChefOutfit.ChefOutfitItem;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class ChefOutfitRenderer {

    public static final List<Item> CHEF_OUTFIT =
            List.of(
                    CookItItems.CHEF_UNIFORM,
                    CookItItems.CHEF_PANTS
            );

    static void renderPart(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, Model model, Identifier texture) {
        VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getEntityTranslucent(texture), stack.hasGlint());
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0xFFFFFF);
    }

    public static void register() {
            ArmorRenderer renderer = (matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
            ChefOutfitItem armor = (ChefOutfitItem) stack.getItem();

            boolean shouldRender = true;

            if (contextModel instanceof PlayerEntityModel<?>) { shouldRender = (!CookItClient.isFiguraLoaded || FiguraCompat.renderArmorPart((PlayerEntity) entity, slot)); }

            var texture = armor.getArmorTexture();
            var model = armor.getArmorModel();
            if (shouldRender) {
                contextModel.copyBipedStateTo(model);
                renderPart(matrices, vertexConsumers, light, stack, model, texture);
            }
        };
        ArmorRenderer.register(renderer, CHEF_OUTFIT.toArray(new Item[0]));
    }

}
