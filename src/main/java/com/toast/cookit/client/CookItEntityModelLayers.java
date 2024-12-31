package com.toast.cookit.client;

import com.toast.cookit.client.render.PizzaEntityRenderer;
import com.toast.cookit.item.armor.ChefOutfit.render.ChefOutfitModel;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import static com.toast.cookit.CookIt.MOD_ID;

public class CookItEntityModelLayers {
    public static final EntityModelLayer CHEF_OUTFIT = new EntityModelLayer(new Identifier(MOD_ID,"chef_outfit"), "main");
    public static final EntityModelLayer PIZZA = new EntityModelLayer(new Identifier(MOD_ID,"pizza"), "main");
    public static final EntityModelLayer PIZZA_TOPPING = new EntityModelLayer(new Identifier(MOD_ID,"pizza"), "topping");

    public static void registerLayers() {
        EntityModelLayerRegistry.registerModelLayer(CHEF_OUTFIT, () -> TexturedModelData.of(ChefOutfitModel.getModelData(), 64, 64));
        EntityModelLayerRegistry.registerModelLayer(PIZZA, PizzaEntityRenderer::getBaseModelData);
        EntityModelLayerRegistry.registerModelLayer(PIZZA_TOPPING, PizzaEntityRenderer::getToppingModelData);

    }
}
