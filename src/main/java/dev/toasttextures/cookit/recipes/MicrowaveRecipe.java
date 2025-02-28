package dev.toasttextures.cookit.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class MicrowaveRecipe implements Recipe<SimpleInventory> {
    private final ItemStack output;
    private final Ingredient ingredient;
    private final int maxProgress;
    private final String event;
    private final float explosionPower;

    public MicrowaveRecipe(Ingredient ingredient, ItemStack itemStack, int maxProgress, String event, float explosionPower) {
        this.output = itemStack;
        this.ingredient = ingredient;
        this.maxProgress = maxProgress;
        this.event = event;
        this.explosionPower = explosionPower;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }
        return this.ingredient.test(inventory.getStack(0));
    }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public ItemStack craft(SimpleInventory inventory, RegistryWrapper.WrapperLookup registriesLookup) {
        return output.copy();
    }


    public int getMaxProgress() {
        return this.maxProgress;
    }

    public String getEvent() {
        return this.event;
    }

    public float getExplosionPower() {
        return this.explosionPower;
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

    public static class Type implements RecipeType<MicrowaveRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<MicrowaveRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private static final MapCodec<MicrowaveRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
                ItemStack.VALIDATED_CODEC.fieldOf("output").forGetter(r -> r.output),

                Codec.INT.fieldOf("time").forGetter(MicrowaveRecipe::getMaxProgress),

                Codec.STRING.optionalFieldOf("event", "none:none").forGetter(r -> r.event),
                Codec.FLOAT.fieldOf("explosion_power")
                        .validate((power) -> power < 0 ? DataResult.error(() -> "Negative recipe explosion power") : DataResult.success(power))
                        .forGetter(r -> r.explosionPower)
        ).apply(in, MicrowaveRecipe::new));
        private static final PacketCodec<RegistryByteBuf, MicrowaveRecipe> PACKET_CODEC = PacketCodec.ofStatic(MicrowaveRecipe.Serializer::write, MicrowaveRecipe.Serializer::read);

        @Override
        public MapCodec<MicrowaveRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MicrowaveRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static MicrowaveRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            int time = buf.readInt();
            String event = buf.readString();
            float explosionPower = buf.readFloat();
            return new MicrowaveRecipe(ingredient, output, time, event, explosionPower);
        }

        public static void write(RegistryByteBuf buf, MicrowaveRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient);
            ItemStack.PACKET_CODEC.encode(buf, recipe.output);
            buf.writeInt(recipe.maxProgress);
            buf.writeString(recipe.event);
            buf.writeFloat(recipe.explosionPower);
        }
    }
}
