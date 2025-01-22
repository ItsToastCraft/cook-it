package dev.toasttextures.cookit.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
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
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> list = DefaultedList.ofSize(this.ingredients.size());
        list.addAll(ingredients);
        return list;
    }

    public ItemStack getLiquid() { return liquidPresent() ? liquid : ItemStack.EMPTY; }

    public int getMixes() {
        return mixAmount;
    }
    public int getUses() {return output.getCount();}
    public boolean liquidPresent() { return !liquid.isOf(Items.BUCKET); }

    public boolean hasGoop() { return goop; }

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

    public int goopColor() {
        return goopColor;
    }

    public static class Type implements RecipeType<MixingBowlRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<MixingBowlRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final Codec<MixingBowlRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
                validateAmount().fieldOf("ingredients").forGetter(MixingBowlRecipe::getIngredients),
                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output").forGetter(r -> r.output),
                Codec.INT.fieldOf("clicks").forGetter(MixingBowlRecipe::getMixes),
                ItemStack.RECIPE_RESULT_CODEC.optionalFieldOf("liquid", new ItemStack(Items.BUCKET, 1)).forGetter(r -> r.liquid),
                Codec.BOOL.optionalFieldOf("outputs_goop", false).forGetter(r -> r.goop),
                Codec.INT.optionalFieldOf("goop_color", 0).forGetter(r -> r.goopColor)
        ).apply(in, MixingBowlRecipe::new));

        private static Codec<List<Ingredient>> validateAmount() {
            return Codecs.validate(Codecs.validate(
                    Ingredient.DISALLOW_EMPTY_CODEC.listOf(), list -> list.size() > 9 ? DataResult.error(() -> "Recipe has too many ingredients!") : DataResult.success(list)),
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
            ItemStack output = buf.readItemStack();
            int clicks = buf.readInt();
            ItemStack liquid = buf.readItemStack();
            boolean goop = buf.readBoolean();
            int goopColor = buf.readInt();
            return new MixingBowlRecipe(inputs, output, clicks, liquid, goop, goopColor);
        }

        @Override
        public void write(PacketByteBuf buf, MixingBowlRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.write(buf);
            }

            buf.writeItemStack(recipe.getResult(null));
            buf.writeInt(recipe.getMixes());
            buf.writeItemStack(recipe.getLiquid());
            buf.writeBoolean(recipe.hasGoop());
            buf.writeInt(recipe.goopColor());
        }
    }
}
