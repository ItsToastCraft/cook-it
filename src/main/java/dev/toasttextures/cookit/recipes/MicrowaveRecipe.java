package dev.toasttextures.cookit.recipes;

import com.google.gson.JsonObject;
import dev.toasttextures.cookit.block.appliances.Microwave;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Objects;

public class MicrowaveRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final Ingredient input;
    private final int maxProgress;
    private final Microwave.Event event;

    public MicrowaveRecipe(Identifier id, Ingredient input, ItemStack itemStack, int maxProgress, Microwave.Event event) {
        this.id = id;
        this.output = itemStack;
        this.input = input;
        this.maxProgress = maxProgress;
        this.event = event;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }
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

    public Microwave.Event getEvent() {
        return event;
    }
    public boolean hasEvent() {
        return event != null && event != Microwave.Event.NONE;
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
        public MicrowaveRecipe read(Identifier id, JsonObject json) {
            return null;
        }

        @Override
        public MicrowaveRecipe read(Identifier id, PacketByteBuf buf) {
            return new MicrowaveRecipe(
                    id,
                    Ingredient.fromPacket(buf),
                    buf.readItemStack(),
                    buf.readInt(),
                    Objects.requireNonNullElse(buf.readEnumConstant(Microwave.Event.class), Microwave.Event.NONE)
            );
        }

        @Override
        public void write(PacketByteBuf buf, MicrowaveRecipe recipe) {
            recipe.input.write(buf);
            buf.writeItemStack(recipe.output);
            buf.writeInt(recipe.maxProgress);
            buf.writeEnumConstant(recipe.event);
        }
    }
}
