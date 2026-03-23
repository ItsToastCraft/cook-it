package dev.toasttextures.cookit.registries;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toasttextures.cookit.recipes.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.CookIt;
import net.minecraft.util.JsonHelper;

public class CookItRecipes {
    public static void register() {
        registerRecipe("microwaving", MicrowaveRecipe.Serializer.INSTANCE, MicrowaveRecipe.Type.INSTANCE);
        registerRecipe("baking", OvenRecipe.Serializer.INSTANCE, OvenRecipe.Type.INSTANCE);
        registerRecipe("cutting", CuttingBoardRecipe.Serializer.INSTANCE, CuttingBoardRecipe.Type.INSTANCE);
        registerRecipe("frying", FryerRecipe.Serializer.INSTANCE, FryerRecipe.Type.INSTANCE);
        registerRecipe("microwaving", MixingBowlRecipe.Serializer.INSTANCE, MixingBowlRecipe.Type.INSTANCE);
    }
    private static <T extends Recipe<?>> void registerRecipe(String name, RecipeSerializer<T> serializer, RecipeType<T> type) {
        Identifier id = CookIt.idOf(name);
        Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
        Registry.register(Registries.RECIPE_TYPE, id, type);
    }

    public static ItemStack validateItemStack(JsonObject obj, boolean canBeEmpty) {
        ItemStack stack = new ItemStack(JsonHelper.getItem(obj, "item", Items.AIR), JsonHelper.getInt(obj, "count", 1));
        if (!canBeEmpty && stack.isEmpty()) {
            throw new JsonSyntaxException("Empty item not allowed here");
        }
        return stack;
    }
}