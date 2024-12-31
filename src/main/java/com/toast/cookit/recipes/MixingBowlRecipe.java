package com.toast.cookit.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;

public class MixingBowlRecipe implements Recipe<SimpleInventory> {
    private final ItemStack output;
    private final List<Ingredient> recipeItems;
    private final ItemStack liquid;
    private final int mixAmount;


    public MixingBowlRecipe(List<Ingredient> ingredients, ItemStack liquid, ItemStack output, int mixAmount) {
        this.output = output;
        this.recipeItems = ingredients;
        this.mixAmount = mixAmount;
        this.liquid = liquid;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }
        return recipeItems.get(0).test(inventory.getStack(0));
    }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(this.recipeItems.size());
        list.addAll(recipeItems);
        return list;
    }

    public ItemStack getLiquid() { return liquidPresent() ? liquid : ItemStack.EMPTY; }

    public int getMixes() {
        return mixAmount;
    }
    public boolean liquidPresent() { return !liquid.isOf(Items.BUCKET); }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(DynamicRegistryManager registryManager) {
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

    public static class Type implements RecipeType<MixingBowlRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<MixingBowlRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final Codec<MixingBowlRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
                validateAmount(Ingredient.DISALLOW_EMPTY_CODEC).fieldOf("ingredients").forGetter(MixingBowlRecipe::getIngredients),
                ItemStack.RECIPE_RESULT_CODEC.optionalFieldOf("liquid", new ItemStack(Items.BUCKET, 1)).forGetter(r -> r.liquid),
                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output").forGetter(r -> r.output),
                Codec.INT.fieldOf("clicks").forGetter(MixingBowlRecipe::getMixes)
        ).apply(in, MixingBowlRecipe::new));

        private static Codec<List<Ingredient>> validateAmount(Codec<Ingredient> delegate) {
            return Codecs.validate(Codecs.validate(
                    delegate.listOf(), list -> list.size() > 9 ? DataResult.error(() -> "Recipe has too many ingredients!") : DataResult.success(list)),
                    list -> list.isEmpty() ? DataResult.error(() -> "Recipe has no ingredients!") : DataResult.success(list));
        }

        @Override
        public Codec<MixingBowlRecipe> codec() {
            return CODEC;
        }

        @Override
        public MixingBowlRecipe read(PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            inputs.replaceAll(ignored -> Ingredient.fromPacket(buf));
            ItemStack liquid = buf.readItemStack();
            ItemStack output = buf.readItemStack();
            int clicks = buf.readInt();
            return new MixingBowlRecipe(inputs, liquid, output, clicks);
        }

        @Override
        public void write(PacketByteBuf buf, MixingBowlRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buf);
            }
            buf.writeItemStack(recipe.getResult(null));
            buf.writeItemStack(recipe.getResult(null));
            buf.writeInt(recipe.getMixes());
        }
    }
}
