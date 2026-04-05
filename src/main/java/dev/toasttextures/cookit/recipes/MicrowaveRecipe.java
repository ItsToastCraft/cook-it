package dev.toasttextures.cookit.recipes;

import com.google.gson.JsonObject;
import dev.toasttextures.cookit.block.appliances.Microwave;
import net.minecraft.core.NonNullList;
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

import java.util.Objects;

import static dev.toasttextures.cookit.registries.CookItRecipes.validateItemStack;

public class MicrowaveRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient input;
    private final int maxProgress;
    private final Microwave.Event event;

    public MicrowaveRecipe(ResourceLocation id, Ingredient input, ItemStack itemStack, int maxProgress, Microwave.Event event) {
        this.id = id;
        this.output = itemStack;
        this.input = input;
        this.maxProgress = maxProgress;
        this.event = event;
    }

    @Override
    public boolean matches(SimpleContainer inventory, Level world) {
        if (world.isClientSide()) return false;
        return input.test(inventory.getItem(0));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public ItemStack assemble(SimpleContainer inventory, RegistryAccess registryManager) {
        return output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.createWithCapacity(1);
        list.add(input);
        return list;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public Microwave.Event getEvent() {
        return event;
    }

    public boolean hasEvent() {
        return event != null && event != Microwave.Event.NONE;
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

    public static class Type implements RecipeType<MicrowaveRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<MicrowaveRecipe> {
        public static final Serializer INSTANCE = new Serializer();
//        public static final Codec<MicrowaveRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
//                validateAmount(Ingredient.DISALLOW_EMPTY_CODEC, 9).fieldOf("ingredients").forGetter(MicrowaveRecipe::getIngredients),
//                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output").forGetter(r -> r.output),
//
//                Codec.INT.fieldOf("time").forGetter(MicrowaveRecipe::getMaxProgress),
//
//                Codec.STRING.optionalFieldOf("event", "none:none").forGetter(MicrowaveRecipe::getEvent),
//
//                Codecs.validate(Codec.FLOAT, a -> a < 0 ? DataResult.error(() -> "Negative recipe explosion power") : DataResult.success(a))
//                        .optionalFieldOf("explosion_power", 0.0f).forGetter(MicrowaveRecipe::getExplosionPower)
//        ).apply(in, MicrowaveRecipe::new));
//
//        private static Codec<List<Ingredient>> validateAmount(Codec<Ingredient> delegate, int max) {
//            return Codecs.validate(Codecs.validate(
//                    delegate.listOf(), list -> list.size() > max ? DataResult.error(() -> "Recipe has too many ingredients!") : DataResult.success(list)),
//                    list -> list.isEmpty() ? DataResult.error(() -> "Recipe has no ingredients!") : DataResult.success(list));
//        }
//
//        @Override
//        public Codec<MicrowaveRecipe> codec() {
//            return CODEC;
//        }

        @Override
        public MicrowaveRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new MicrowaveRecipe(
                    id,
                    Ingredient.fromJson(json.getAsJsonObject("input")),
                    validateItemStack(json.getAsJsonObject("output"), true),
                    GsonHelper.getAsInt(json, "time", 200),
                    Microwave.Event.valueOf(GsonHelper.getAsString(json, "event", "none").toUpperCase())
            );
        }

        @Override
        public MicrowaveRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new MicrowaveRecipe(
                    id,
                    Ingredient.fromNetwork(buf),
                    buf.readItem(),
                    buf.readInt(),
                    Objects.requireNonNullElse(buf.readEnum(Microwave.Event.class), Microwave.Event.NONE)
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MicrowaveRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.maxProgress);
            buf.writeEnum(recipe.event);
        }
    }
}
