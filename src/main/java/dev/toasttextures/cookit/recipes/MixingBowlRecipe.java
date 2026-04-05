package dev.toasttextures.cookit.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static dev.toasttextures.cookit.registries.CookItRecipes.validateItemStack;

public class MixingBowlRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final List<Ingredient> ingredients;
    private final int interactions;
    private final ItemStack liquid;
    private final boolean goop;
    private final int goopColor;

    public MixingBowlRecipe(ResourceLocation id, List<Ingredient> ingredients, ItemStack output, int interactions, ItemStack liquid, boolean outputIsGoop, int goopColor) {
        this.id = id;
        this.output = output;
        this.ingredients = ingredients;
        this.interactions = interactions;
        this.liquid = liquid;
        this.goop = outputIsGoop;
        this.goopColor = goopColor;
    }

    @Override
    public boolean matches(SimpleContainer inventory, Level world) {
        if (world.isClientSide) return false;

        StackedContents matcher = new StackedContents();
        int added = 0;
        for (ItemStack stack : inventory.items) {
            if (stack.isEmpty()) continue;
            added++;
            matcher.accountStack(stack, 1);
        }

        return added == this.ingredients.size() && matcher.canCraft(this, null);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull ItemStack assemble(SimpleContainer inventory, RegistryAccess registryManager) {
        return output.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.createWithCapacity(this.ingredients.size());
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
        public MixingBowlRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray array = json.getAsJsonArray("inputs");
            NonNullList<Ingredient> inputs = NonNullList.withSize(array.size(), Ingredient.EMPTY);
            for (int i = 0; i < array.size(); i++) {
                inputs.set(i, Ingredient.fromJson(array.get(i)));
            }

            return new MixingBowlRecipe(
                    id,
                    inputs,
                    validateItemStack(json.getAsJsonObject("output"), false),
                    GsonHelper.getAsInt(json, "interactions", 1),
                    validateItemStack(json.getAsJsonObject("liquid"), true),
                    GsonHelper.getAsBoolean(json, "outputs_goop", false),
                    GsonHelper.getAsInt(json, "goop_color", 0)
            );
        }

        @Override
        public MixingBowlRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);
            inputs.replaceAll(_unused -> Ingredient.fromNetwork(buf));
            return new MixingBowlRecipe(
                    id,
                    inputs,
                    buf.readItem(),
                    buf.readInt(),
                    buf.readItem(),
                    buf.readBoolean(),
                    buf.readInt()
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MixingBowlRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.output);
            buf.writeInt(recipe.interactions);
            buf.writeItem(recipe.liquid);
            buf.writeBoolean(recipe.hasGoop());
            buf.writeInt(recipe.goopColor());
        }
    }
}