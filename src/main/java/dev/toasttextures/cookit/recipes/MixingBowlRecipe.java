package dev.toasttextures.cookit.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class MixingBowlRecipe implements Recipe<SimpleInventory> {
    private final ItemStack output;
    private final List<Ingredient> ingredients;
    private final int mixAmount;
    private final ItemStack liquid;
    private final boolean goop;
    private final int goopColor;

    public MixingBowlRecipe(List<Ingredient> ingredients, ItemStack output, int mixAmount, ItemStack liquid, boolean outputIsGoop, int goopColor) {
        this.output = output;
        this.ingredients = ingredients;
        this.mixAmount = mixAmount;
        this.liquid = liquid;
        this.goop = outputIsGoop;
        this.goopColor = goopColor;
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if(world.isClient()) {
            return false;
        }
        RecipeMatcher recipeMatcher = new RecipeMatcher();
        int i = 0;

        for(int j = 0; j < inventory.size(); ++j) {
            ItemStack itemStack = inventory.getStack(j);
            if (!itemStack.isEmpty()) {
                ++i;
                recipeMatcher.addInput(itemStack, 1);
            }
        }

        return i == this.ingredients.size() && recipeMatcher.match(this, null);
    }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public ItemStack craft(SimpleInventory inventory, RegistryWrapper.WrapperLookup registriesLookup) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(this.ingredients.size());
        list.addAll(ingredients);
        return list;
    }

    public ItemStack getLiquid() { return liquidPresent() ? this.liquid : ItemStack.EMPTY; }

    public int getMixes() {
        return this.mixAmount;
    }
    public int getUses() {return this.output.getCount();}
    public boolean liquidPresent() { return !this.liquid.isOf(Items.BUCKET); }

    public boolean hasGoop() { return this.goop; }

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

    public int goopColor() {
        return this.goopColor;
    }

    public static class Type implements RecipeType<MixingBowlRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<MixingBowlRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final MapCodec<MixingBowlRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
                Ingredient.DISALLOW_EMPTY_CODEC.listOf().fieldOf("ingredients")
                        .validate((list) -> (list.size() > 9 || list.isEmpty()) ? DataResult.error(() -> "Recipe doesn't have the right amount of ingredients (6)!") : DataResult.success(list)).forGetter(r -> r.ingredients),
                ItemStack.VALIDATED_CODEC.fieldOf("output").forGetter(r -> r.output),
                Codec.INT.fieldOf("clicks").forGetter(MixingBowlRecipe::getMixes),
                ItemStack.VALIDATED_CODEC.optionalFieldOf("liquid", new ItemStack(Items.BUCKET, 1)).forGetter(r -> r.liquid),
                Codec.BOOL.optionalFieldOf("outputs_goop", false).forGetter(r -> r.goop),
                Codec.INT.optionalFieldOf("goop_color", 0).forGetter(r -> r.goopColor)
        ).apply(in, MixingBowlRecipe::new));
        private static final PacketCodec<RegistryByteBuf, MixingBowlRecipe> PACKET_CODEC = PacketCodec.ofStatic(MixingBowlRecipe.Serializer::write, MixingBowlRecipe.Serializer::read);

        @Override
        public MapCodec<MixingBowlRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, MixingBowlRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        public static MixingBowlRecipe read(RegistryByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            inputs.replaceAll(ingredient -> Ingredient.PACKET_CODEC.decode(buf));
            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            int clicks = buf.readInt();
            ItemStack liquid = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
            boolean goop = buf.readBoolean();
            int goopColor = buf.readInt();
            return new MixingBowlRecipe(inputs, output, clicks, liquid, goop, goopColor);
        }

        public static void write(RegistryByteBuf buf, MixingBowlRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.PACKET_CODEC.encode(buf, ingredient);
            }
            ItemStack.PACKET_CODEC.encode(buf, recipe.output);
            buf.writeInt(recipe.mixAmount);
            ItemStack.PACKET_CODEC.encode(buf, recipe.liquid);
            buf.writeBoolean(recipe.goop);
            buf.writeInt(recipe.goopColor);
        }
    }
}
