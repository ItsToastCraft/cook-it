package dev.toasttextures.cookit.client;

import dev.toasttextures.cookit.client.render.entity.PizzaEntityRenderer;
import dev.toasttextures.cookit.client.render.model.ChefOutfitModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import static dev.toasttextures.cookit.CookIt.MOD_ID;

public class CookItEntityModelLayers {
    public static final ModelLayerLocation CHEF_OUTFIT = new ModelLayerLocation(new ResourceLocation(MOD_ID,"chef_outfit"), "main");
    public static final ModelLayerLocation PIZZA = new ModelLayerLocation(new ResourceLocation(MOD_ID,"pizza"), "main");
    public static final ModelLayerLocation PIZZA_TOPPING = new ModelLayerLocation(new ResourceLocation(MOD_ID,"pizza"), "topping");

    public static void registerLayers() {
        EntityModelLayerRegistry.registerModelLayer(CHEF_OUTFIT, ChefOutfitModel::getModelData);
        EntityModelLayerRegistry.registerModelLayer(PIZZA, PizzaEntityRenderer::getBaseModelData);
        EntityModelLayerRegistry.registerModelLayer(PIZZA_TOPPING, PizzaEntityRenderer::getToppingModelData);
    }
}
