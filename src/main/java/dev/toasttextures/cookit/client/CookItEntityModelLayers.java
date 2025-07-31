package dev.toasttextures.cookit.client;

import dev.toasttextures.cookit.client.render.PizzaEntityRenderer;
import dev.toasttextures.cookit.item.armor.ChefOutfit.render.ChefOutfitModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static dev.toasttextures.cookit.CookIt.MOD_ID;

public class CookItEntityModelLayers {
    public static final EntityModelLayer CHEF_OUTFIT = new EntityModelLayer(new Identifier(MOD_ID,"chef_outfit"), "main");
    public static final EntityModelLayer PIZZA = new EntityModelLayer(new Identifier(MOD_ID,"pizza"), "main");
    public static final EntityModelLayer PIZZA_TOPPING = new EntityModelLayer(new Identifier(MOD_ID,"pizza"), "topping");

    public static void registerLayers() {
        EntityModelLayerRegistry.registerModelLayer(CHEF_OUTFIT, ChefOutfitModel::getModelData);
        EntityModelLayerRegistry.registerModelLayer(PIZZA, PizzaEntityRenderer::getBaseModelData);
        EntityModelLayerRegistry.registerModelLayer(PIZZA_TOPPING, PizzaEntityRenderer::getToppingModelData);
    }
}