package dev.toasttextures.cookit.recipes;

import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import static dev.toasttextures.cookit.registries.CookItRecipes.allowAirIngredient;
import static dev.toasttextures.cookit.registries.CookItRecipes.validateItemStack;

public class CuttingBoardRecipe implements Recipe<SimpleInventory> {
    private final Identifier id;
    private final ItemStack output;
    private final Ingredient input;
    private final int interactions;
    private final Ingredient tool;
    private final boolean usesItem;

    public CuttingBoardRecipe(Identifier id, Ingredient input, ItemStack output, Ingredient tool, int interactions, boolean usesItem) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.tool = tool;
        this.usesItem = usesItem;
        this.interactions = interactions;
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
    public ItemStack craft(SimpleInventory inventory, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    public ItemStack[] getTool() {
        if (tool.isEmpty()) return new ItemStack[]{ ItemStack.EMPTY};

        return tool.getMatchingStacks();
    }

    public boolean resets() {
        return tool.isEmpty();
    }

    public boolean usesItem() {
        return usesItem;
    }

    public int getInteractions() {
        return interactions;
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

    @Override
    public Identifier getId() {
        return id;
    }

    public static class Type implements RecipeType<CuttingBoardRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<CuttingBoardRecipe> {
        public static final Serializer INSTANCE = new Serializer();
//        public static final Codec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.create(in -> in.group(
//                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(r -> r.input),
//                ItemStack.RECIPE_RESULT_CODEC.fieldOf("output").forGetter(r -> r.output),
//                Ingredient.ALLOW_EMPTY_CODEC.optionalFieldOf("tool", Ingredient.EMPTY).forGetter(r -> r.tool),
//                Codec.INT.optionalFieldOf("interactions", 1).forGetter(r -> r.interactions),
//                Codec.INT.optionalFieldOf("clicks", 1).forGetter(r -> r.clicks),
//                Codec.BOOL.optionalFieldOf("usesItem", false).forGetter(r -> r.usesItem),
//                Codec.BOOL.optionalFieldOf("resets", false).forGetter(r -> r.resetable)
//
//
//        ).apply(in, CuttingBoardRecipe::new));

        @Override
        public CuttingBoardRecipe read(Identifier id, JsonObject json) {
            return new CuttingBoardRecipe(
                id,
                Ingredient.fromJson(json.getAsJsonObject("input")),
                validateItemStack(json.getAsJsonObject("output"), false),
                allowAirIngredient(json, "tool"),
                JsonHelper.getInt(json, "interactions", 1),
                JsonHelper.getBoolean(json, "uses_item", false)
            );
        }

        @Override
        public CuttingBoardRecipe read(Identifier id, PacketByteBuf buf) {
            return new CuttingBoardRecipe(
                id,
                Ingredient.fromPacket(buf),
                buf.readItemStack(),
                Ingredient.fromPacket(buf),
                buf.readInt(),
                buf.readBoolean()
            );
        }

        @Override
        public void write(PacketByteBuf buf, CuttingBoardRecipe recipe) {
            recipe.input.write(buf);
            buf.writeItemStack(recipe.getOutput(null));
            recipe.tool.write(buf);
            buf.writeInt(recipe.interactions);
            buf.writeBoolean(recipe.usesItem());
        }
    }
}