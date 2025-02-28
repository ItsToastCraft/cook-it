package dev.toasttextures.cookit.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class CuttingBoardRecipe implements Recipe<SimpleInventory> {
    private final ItemStack output;
    private final Ingredient ingredient;
    private final Ingredient tool;
    private final boolean usesItem;
    private final boolean resettable;
    private final int clicks;

    public CuttingBoardRecipe(Ingredient ingredient, ItemStack itemStack, Ingredient tool, int clicks, boolean usesItem, boolean resettable) {
        this.output = itemStack;
        this.ingredient = ingredient;
        this.tool = tool;
        this.usesItem = usesItem;
        this.resettable = resettable;
        this.clicks = clicks;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }
        return ingredient.test(inventory.getStack(0));
    }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }
    @Override
    public ItemStack craft(SimpleInventory inventory, RegistryWrapper.WrapperLookup lookup) {
        return output.copy();
    }

    public ItemStack[] getTool() {
        if (tool.isEmpty()) {return new ItemStack[]{ ItemStack.EMPTY}; }
        return tool.getMatchingStacks();
    }
    public boolean isResettable() { return this.resettable; }

    public boolean usesItem() { return this.usesItem; }

    public int getClicks() { return this.clicks; }

    public int getOutputCount() {
        return this.output.getCount();
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

    public static class Type implements RecipeType<CuttingBoardRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<CuttingBoardRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        private static final MapCodec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
                ItemStack.VALIDATED_CODEC.fieldOf("output").forGetter(r -> r.output),
                Ingredient.ALLOW_EMPTY_CODEC.optionalFieldOf("tool", Ingredient.EMPTY).forGetter(r -> r.tool),
                Codec.INT.optionalFieldOf("clicks", 1).forGetter(r -> r.clicks),
                Codec.BOOL.optionalFieldOf("usesItem", false).forGetter(r -> r.usesItem),
                Codec.BOOL.optionalFieldOf("resettable", false).forGetter(r -> r.resettable)


        ).apply(in, CuttingBoardRecipe::new));

        private static final PacketCodec<RegistryByteBuf, CuttingBoardRecipe> PACKET_CODEC = PacketCodec.ofStatic(CuttingBoardRecipe.Serializer::write, CuttingBoardRecipe.Serializer::read);

        @Override
        public MapCodec<CuttingBoardRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, CuttingBoardRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static CuttingBoardRecipe read(RegistryByteBuf buf) {
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            Ingredient tool = Ingredient.PACKET_CODEC.decode(buf);
            int clicks = buf.readInt();
            boolean usesItem = buf.readBoolean();
            boolean resettable = buf.readBoolean();
            return new CuttingBoardRecipe(ingredient, output, tool, clicks, usesItem, resettable);
        }

        public static void write(RegistryByteBuf buf, CuttingBoardRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.ingredient);
            ItemStack.PACKET_CODEC.encode(buf, recipe.output);
            Ingredient.PACKET_CODEC.encode(buf, recipe.tool);
            buf.writeInt(recipe.clicks);
            buf.writeBoolean(recipe.usesItem());
            buf.writeBoolean(recipe.isResettable());
        }
    }
}
