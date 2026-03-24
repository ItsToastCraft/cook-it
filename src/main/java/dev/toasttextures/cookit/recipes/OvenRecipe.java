package dev.toasttextures.cookit.recipes;

import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import static dev.toasttextures.cookit.registries.CookItRecipes.validateItemStack;

public class OvenRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final Ingredient input;
    private final ItemStack output;
    private final int maxProgress;

    public OvenRecipe(Identifier id, Ingredient input, ItemStack itemStack, int maxProgress) {
        this.id = id;
        this.output = itemStack;
        this.input = input;
        this.maxProgress = maxProgress;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) return false;
        return input.test(inventory.getStack(0));
    }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(1);
        list.add(input);
        return list;
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

    public static class Type implements RecipeType<OvenRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<OvenRecipe> {
        public static final Serializer INSTANCE = new Serializer();
//        public static final Codec<OvenRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
//                validateAmount(Ingredient.DISALLOW_EMPTY_CODEC).fieldOf("ingredients").forGetter(OvenRecipe::getIngredients),
//                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output").forGetter(r -> r.output),
//
//                Codec.INT.fieldOf("time").forGetter(OvenRecipe::getMaxProgress),
//
//                Codec.STRING.optionalFieldOf("event", "none:none").forGetter(OvenRecipe::getEvent)
//        ).apply(in, OvenRecipe::new));
//
//        private static Codec<List<Ingredient>> validateAmount(Codec<Ingredient> delegate) {
//            return Codecs.validate(Codecs.validate(
//                    delegate.listOf(), list -> list.size() > 9 ? DataResult.error(() -> "Recipe has too many ingredients!") : DataResult.success(list)),
//                    list -> list.isEmpty() ? DataResult.error(() -> "Recipe has no ingredients!") : DataResult.success(list));
//        }
//
//        @Override
//        public Codec<OvenRecipe> codec() {
//            return CODEC;
//        }

        @Override
        public OvenRecipe read(Identifier id, JsonObject json) {
            return new OvenRecipe(
                id,
                Ingredient.fromJson(json.getAsJsonObject("input")),
                validateItemStack(json.getAsJsonObject("output"), false),
                JsonHelper.getInt(json, "time")
            );
        }

        @Override
        public OvenRecipe read(Identifier id, PacketByteBuf buf) {
            return new OvenRecipe(
                id,
                Ingredient.fromPacket(buf),
                buf.readItemStack(),
                buf.readInt()
            );
        }

        @Override
        public void write(PacketByteBuf buf, OvenRecipe recipe) {
            recipe.input.write(buf);
            buf.writeItemStack(recipe.getOutput(null));
            buf.writeInt(recipe.maxProgress);
        }
    }
}
