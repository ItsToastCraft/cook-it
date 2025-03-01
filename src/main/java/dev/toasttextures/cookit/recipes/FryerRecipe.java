package dev.toasttextures.cookit.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class FryerRecipe implements Recipe<RecipeInventory> {
    private final ItemStack output;
    private final Ingredient ingredient;
    private final int maxProgress;
    private final String event;

    public FryerRecipe(Ingredient ingredient, ItemStack itemStack, int maxProgress, String event) {
        this.output = itemStack;
        this.ingredient = ingredient;
        this.maxProgress = maxProgress;
        this.event = event;
    }

    @Override
    public boolean matches(RecipeInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }
        return this.ingredient.test(inventory.getStack(0));
    }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public ItemStack craft(RecipeInventory inventory, RegistryWrapper.WrapperLookup lookup) {
        return this.output.copy();
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public String getEvent() {
        return this.event;
    }
    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return this.output;
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
        private static final MapCodec<FryerRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
                ItemStack.VALIDATED_CODEC.fieldOf("output").forGetter(r -> r.output),
                Codec.INT.fieldOf("time").forGetter(r -> r.maxProgress),
                Codec.STRING.optionalFieldOf("event", "none:none").forGetter(r -> r.event)
        ).apply(in, FryerRecipe::new));
        private static final PacketCodec<RegistryByteBuf, FryerRecipe> PACKET_CODEC = PacketCodec.ofStatic(FryerRecipe.Serializer::write, FryerRecipe.Serializer::read);

        @Override
        public MapCodec<FryerRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, FryerRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static FryerRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            int time = buf.readInt();
            String event = buf.readString();
            return new FryerRecipe(ingredient, output, time, event);
        }

        public static void write(RegistryByteBuf buf, FryerRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient);
            ItemStack.PACKET_CODEC.encode(buf, recipe.output);
            buf.writeInt(recipe.maxProgress);
            buf.writeString(recipe.event);
        }
    }
}
