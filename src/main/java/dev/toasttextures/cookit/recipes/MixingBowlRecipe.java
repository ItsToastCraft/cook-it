package dev.toasttextures.cookit.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

import static dev.toasttextures.cookit.registries.CookItRecipes.validateItemStack;

public class MixingBowlRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final List<Ingredient> ingredients;
    private final int interactions;
    private final ItemStack liquid;
    private final boolean goop;
    private final int goopColor;

    public MixingBowlRecipe(Identifier id, List<Ingredient> ingredients, ItemStack output, int interactions, ItemStack liquid, boolean outputIsGoop, int goopColor) {
        this.id = id;
        this.output = output;
        this.ingredients = ingredients;
        this.interactions = interactions;
        this.liquid = liquid;
        this.goop = outputIsGoop;
        this.goopColor = goopColor;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient) return false;

        RecipeMatcher matcher = new RecipeMatcher();
        for (ItemStack stack : inventory.stacks) {
            matcher.addInput(stack, 1);
        }

        return matcher.inputs.size() == this.ingredients.size() && matcher.match(this, null);
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
        DefaultedList<Ingredient> list = DefaultedList.ofSize(this.ingredients.size());
        list.addAll(ingredients);
        return list;
    }

    public ItemStack getLiquid() {
        return liquid;
    }

    public int getMixes() {
        return interactions;
    }

    public int getUses() {
        return output.getCount();
    }
    public boolean liquidPresent() {
        return !liquid.isEmpty();
    }

    public boolean hasGoop() {
        return goop;
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

    public int goopColor() {
        return goopColor;
    }

    public static class Type implements RecipeType<MixingBowlRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<MixingBowlRecipe> {
        public static final Serializer INSTANCE = new Serializer();
//        public static final Codec<MixingBowlRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
//                validateAmount().fieldOf("ingredients").forGetter(MixingBowlRecipe::getIngredients),
//                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output").forGetter(r -> r.output),
//                Codec.INT.fieldOf("clicks").forGetter(MixingBowlRecipe::getMixes),
//                ItemStack.RECIPE_RESULT_CODEC.optionalFieldOf("liquid", new ItemStack(Items.BUCKET, 1)).forGetter(r -> r.liquid),
//                Codec.BOOL.optionalFieldOf("outputs_goop", false).forGetter(r -> r.goop),
//                Codec.INT.optionalFieldOf("goop_color", 0).forGetter(r -> r.goopColor)
//        ).apply(in, MixingBowlRecipe::new));
//
//        private static Codec<List<Ingredient>> validateAmount() {
//            return Codecs.validate(Codecs.validate(
//                    Ingredient.DISALLOW_EMPTY_CODEC.listOf(), list -> list.size() > 9 ? DataResult.error(() -> "Recipe has too many ingredients!") : DataResult.success(list)),
//                    list -> list.isEmpty() ? DataResult.error(() -> "Recipe has no ingredients!") : DataResult.success(list));
//        }
//
//        @Override
//        public Codec<MixingBowlRecipe> codec() {
//            return CODEC;
//        }


        @Override
        public MixingBowlRecipe read(Identifier id, JsonObject json) {
            JsonArray array = json.getAsJsonArray("inputs");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(array.size(), Ingredient.EMPTY);
            for (int i = 0; i < array.size(); i++) {
                inputs.set(i, Ingredient.fromJson(array.get(i)));
            }

            return new MixingBowlRecipe(
                    id,
                    inputs,
                    validateItemStack(json.getAsJsonObject("output"), false),
                    JsonHelper.getInt(json, "interactions", 1),
                    validateItemStack(json.getAsJsonObject("liquid"), true),
                    JsonHelper.getBoolean(json, "outputs_goop", false),
                    JsonHelper.getInt(json, "goop_color", 0)
            );
        }

        @Override
        public MixingBowlRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);
            inputs.replaceAll(_unused -> Ingredient.fromPacket(buf));
            return new MixingBowlRecipe(
                    id,
                    inputs,
                    buf.readItemStack(),
                    buf.readInt(),
                    buf.readItemStack(),
                    buf.readBoolean(),
                    buf.readInt()
            );
        }

        @Override
        public void write(PacketByteBuf buf, MixingBowlRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.output);
            buf.writeInt(recipe.interactions);
            buf.writeItemStack(recipe.liquid);
            buf.writeBoolean(recipe.hasGoop());
            buf.writeInt(recipe.goopColor());
        }
    }
}