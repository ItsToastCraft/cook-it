package dev.toasttextures.cookit.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import static dev.toasttextures.cookit.registries.CookItRecipes.validateItemStack;

public class FryerRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final int maxProgress;

    public FryerRecipe(ResourceLocation id, Ingredient input, ItemStack output, int maxProgress) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.maxProgress = maxProgress;
    }

    @Override
    public boolean matches(SimpleContainer inventory, Level world) {
        if (world.isClientSide()) {
            return false;
        }
        return input.test(inventory.getItem(0));
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer inventory, RegistryAccess registryManager) {
        return output.copy();
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryManager) {
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
        public FryerRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new FryerRecipe(
                    id,
                    Ingredient.fromJson(json.getAsJsonObject("input")),
                    validateItemStack(json.getAsJsonObject("output"), false),
                    GsonHelper.getAsInt(json, "time")
            );
        }

        @Override
        public FryerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new FryerRecipe(
                    id,
                    Ingredient.fromNetwork(buf),
                    buf.readItem(),
                    buf.readInt()
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FryerRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.maxProgress);
        }
    }
}