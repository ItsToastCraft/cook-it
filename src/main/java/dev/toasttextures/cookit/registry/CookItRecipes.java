package dev.toasttextures.cookit.registry;

import dev.toasttextures.cookit.recipes.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.CookIt;

public class CookItRecipes {

    public static void registerRecipes() {
        CookIt.LOGGER.info("Registering recipes...");
       Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(CookIt.MOD_ID, "microwaving"),
               MicrowaveRecipe.Serializer.INSTANCE);
       Registry.register(Registries.RECIPE_TYPE, Identifier.of(CookIt.MOD_ID, "microwaving"),
               MicrowaveRecipe.Type.INSTANCE);

       Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(CookIt.MOD_ID, "baking"),
               OvenRecipe.Serializer.INSTANCE);

       Registry.register(Registries.RECIPE_TYPE, Identifier.of(CookIt.MOD_ID, "baking"),
               OvenRecipe.Type.INSTANCE);

       Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(CookIt.MOD_ID, "cutting"),
               CuttingBoardRecipe.Serializer.INSTANCE);

       Registry.register(Registries.RECIPE_TYPE, Identifier.of(CookIt.MOD_ID, "cutting"),
               CuttingBoardRecipe.Type.INSTANCE);

       Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(CookIt.MOD_ID, "frying"),
               FryerRecipe.Serializer.INSTANCE);

       Registry.register(Registries.RECIPE_TYPE, Identifier.of(CookIt.MOD_ID, "frying"),
               FryerRecipe.Type.INSTANCE);

        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(CookIt.MOD_ID, "mixing"),
                MixingBowlRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(CookIt.MOD_ID, "mixing"),
                MixingBowlRecipe.Type.INSTANCE);
    }
}