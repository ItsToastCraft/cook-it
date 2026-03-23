package dev.toasttextures.cookit.recipes;

import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import static dev.toasttextures.cookit.registries.CookItRecipes.validateItemStack;

public class FryerRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final Ingredient input;
    private final ItemStack output;
    private final int maxProgress;

    public FryerRecipe(Identifier id, Ingredient input, ItemStack output, int maxProgress) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.maxProgress = maxProgress;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }
        return input.test(inventory.getStack(0));
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<FryerRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<FryerRecipe> {
        public static final Serializer INSTANCE = new Serializer();
//        public static final Codec<FryerRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
//                validateAmount(Ingredient.DISALLOW_EMPTY_CODEC).fieldOf("ingredients").forGetter(FryerRecipe::getIngredients),
//                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output").forGetter(r -> r.output),
//
//                Codec.INT.fieldOf("time").forGetter(FryerRecipe::getMaxProgress),
//
//                Codec.STRING.optionalFieldOf("event", "none:none").forGetter(FryerRecipe::getEvent)
//        ).apply(in, FryerRecipe::new));
//
//        private static Codec<List<Ingredient>> validateAmount(Codec<Ingredient> delegate) {
//            return Codecs.validate(Codecs.validate(
//                            delegate.listOf(), list -> list.size() > 9 ? DataResult.error(() -> "Recipe has too many ingredients!") : DataResult.success(list)),
//                    list -> list.isEmpty() ? DataResult.error(() -> "Recipe has no ingredients!") : DataResult.success(list));
//        }
//
//        @Override
//        public Codec<FryerRecipe> codec() {
//            return CODEC;
//        }

        @Override
        public FryerRecipe read(Identifier id, JsonObject json) {
            return new FryerRecipe(
                    id,
                    Ingredient.fromJson(json.getAsJsonObject("input")),
                    validateItemStack(json.getAsJsonObject("output"), false),
                    JsonHelper.getInt(json, "time")
            );
        }

        @Override
        public FryerRecipe read(Identifier id, PacketByteBuf buf) {
            return new FryerRecipe(
                    id,
                    Ingredient.fromPacket(buf),
                    buf.readItemStack(),
                    buf.readInt()
            );
        }

        @Override
        public void write(PacketByteBuf buf, FryerRecipe recipe) {
            recipe.input.write(buf);
            buf.writeItemStack(recipe.output);
            buf.writeInt(recipe.maxProgress);
        }
    }
}