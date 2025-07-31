package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.recipes.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.CookIt;

public class CookItRecipes {

    public static <T extends Recipe<?>> void register(String name, RecipeSerializer<T> serializer, RecipeType<T> type) {
        Identifier id = new Identifier(CookIt.MOD_ID, name);
        Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
        Registry.register(Registries.RECIPE_TYPE, id, type);
    }

    public static void registerRecipes() {
        register("microwaving", MicrowaveRecipe.Serializer.INSTANCE, MicrowaveRecipe.Type.INSTANCE);
        register("baking", OvenRecipe.Serializer.INSTANCE, OvenRecipe.Type.INSTANCE);
        register("cutting", CuttingBoardRecipe.Serializer.INSTANCE, CuttingBoardRecipe.Type.INSTANCE);
        register("baking", OvenRecipe.Serializer.INSTANCE, OvenRecipe.Type.INSTANCE);
        register("frying", FryerRecipe.Serializer.INSTANCE, FryerRecipe.Type.INSTANCE);
        register("mixing", MixingBowlRecipe.Serializer.INSTANCE, MixingBowlRecipe.Type.INSTANCE);
    }
}