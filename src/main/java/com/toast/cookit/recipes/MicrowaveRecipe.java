package com.toast.cookit.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
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

public class MicrowaveRecipe implements Recipe<SimpleInventory> {
    private final ItemStack output;
    private final List<Ingredient> recipeItems;
    private final int maxProgress;
    private final String event;
    private final float explosionPower;

    public MicrowaveRecipe(List<Ingredient> ingredients, ItemStack itemStack, int maxProgress, String event, float explosionPower) {
        this.output = itemStack;
        this.recipeItems = ingredients;
        this.maxProgress = maxProgress;
        this.event = event;
        this.explosionPower = explosionPower;
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

    public int getMaxProgress() {
        return maxProgress;
    }

    public String getEvent() {
        return event;
    }

    public float getExplosionPower() {
        return explosionPower;
    }

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

    public static class Type implements RecipeType<MicrowaveRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<MicrowaveRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final Codec<MicrowaveRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
                validateAmount(Ingredient.DISALLOW_EMPTY_CODEC, 9).fieldOf("ingredients").forGetter(MicrowaveRecipe::getIngredients),
                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output").forGetter(r -> r.output),

                Codec.INT.fieldOf("time").forGetter(MicrowaveRecipe::getMaxProgress),

                Codec.STRING.optionalFieldOf("event", "none:none").forGetter(MicrowaveRecipe::getEvent),

                Codecs.validate(Codec.FLOAT, a -> a < 0 ? DataResult.error(() -> "Negative recipe explosion power") : DataResult.success(a))
                        .optionalFieldOf("explosion_power", 0.0f).forGetter(MicrowaveRecipe::getExplosionPower)
        ).apply(in, MicrowaveRecipe::new));

        private static Codec<List<Ingredient>> validateAmount(Codec<Ingredient> delegate, int max) {
            return Codecs.validate(Codecs.validate(
                    delegate.listOf(), list -> list.size() > max ? DataResult.error(() -> "Recipe has too many ingredients!") : DataResult.success(list)),
                    list -> list.isEmpty() ? DataResult.error(() -> "Recipe has no ingredients!") : DataResult.success(list));
        }

        @Override
        public Codec<MicrowaveRecipe> codec() {
            return CODEC;
        }

        @Override
        public MicrowaveRecipe read(PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            inputs.replaceAll(ignored -> Ingredient.fromPacket(buf));

            ItemStack output = buf.readItemStack();
            int time = buf.readInt();
            String event = buf.readString();
            float explosionPower = buf.readFloat();
            return new MicrowaveRecipe(inputs, output, time, event, explosionPower);
        }

        @Override
        public void write(PacketByteBuf buf, MicrowaveRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {

                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.getResult(null));
            buf.writeInt(recipe.maxProgress);
            buf.writeString(recipe.event);
            buf.writeFloat(recipe.explosionPower);
        }
    }
}
