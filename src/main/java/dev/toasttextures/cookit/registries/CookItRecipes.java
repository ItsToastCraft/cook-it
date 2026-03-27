package dev.toasttextures.cookit.registries;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.toasttextures.cookit.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import dev.toasttextures.cookit.CookIt;
import net.minecraft.util.GsonHelper;

public final class CookItRecipes {
    public static void register() {
        registerRecipe("microwaving", MicrowaveRecipe.Serializer.INSTANCE, MicrowaveRecipe.Type.INSTANCE);
        registerRecipe("baking", OvenRecipe.Serializer.INSTANCE, OvenRecipe.Type.INSTANCE);
        registerRecipe("cutting", CuttingBoardRecipe.Serializer.INSTANCE, CuttingBoardRecipe.Type.INSTANCE);
        registerRecipe("frying", FryerRecipe.Serializer.INSTANCE, FryerRecipe.Type.INSTANCE);
        registerRecipe("mixing", MixingBowlRecipe.Serializer.INSTANCE, MixingBowlRecipe.Type.INSTANCE);
    }
    private static <T extends Recipe<?>> void registerRecipe(String name, RecipeSerializer<T> serializer, RecipeType<T> type) {
        ResourceLocation id = CookIt.idOf(name);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, id, serializer);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, id, type);
    }

    public static ItemStack validateItemStack(JsonObject obj, boolean canBeEmpty) {
        ItemStack stack = new ItemStack(GsonHelper.getAsItem(obj, "item", Items.AIR), GsonHelper.getAsInt(obj, "count", 1));
        if (!canBeEmpty && stack.isEmpty()) {
            throw new JsonSyntaxException("Empty item not allowed here");
        }
        return stack;
    }

    public static Ingredient allowAirIngredient(JsonObject json, String key) {
        return json.has(key) ? Ingredient.fromJson(json.getAsJsonObject(key)) : Ingredient.EMPTY;
    }
}