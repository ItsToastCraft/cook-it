package dev.toasttextures.cookit.recipes;

import com.google.gson.JsonObject;
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

import static dev.toasttextures.cookit.registries.CookItRecipes.allowAirIngredient;
import static dev.toasttextures.cookit.registries.CookItRecipes.validateItemStack;

public class CuttingBoardRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final Ingredient input;
    private final int interactions;
    private final Ingredient tool;
    private final boolean usesItem;

    public CuttingBoardRecipe(ResourceLocation id, Ingredient input, ItemStack output, Ingredient tool, int interactions, boolean usesItem) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.tool = tool;
        this.usesItem = usesItem;
        this.interactions = interactions;
    }

    @Override
    public boolean matches(SimpleContainer inventory, Level world) {
        if (world.isClientSide()) {
            return false;
        }
        return input.test(inventory.getItem(0));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack assemble(SimpleContainer inventory, RegistryAccess registryManager) {
        return output.copy();
    }

    public ItemStack[] getTool() {
        if (tool.isEmpty()) return new ItemStack[]{ItemStack.EMPTY};

        return tool.getItems();
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

    @Override
    public ResourceLocation getId() {
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
        public CuttingBoardRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new CuttingBoardRecipe(
                    id,
                    Ingredient.fromJson(json.getAsJsonObject("input")),
                    validateItemStack(json.getAsJsonObject("output"), false),
                    allowAirIngredient(json, "tool"),
                    GsonHelper.getAsInt(json, "interactions", 1),
                    GsonHelper.getAsBoolean(json, "uses_item", false)
            );
        }

        @Override
        public CuttingBoardRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new CuttingBoardRecipe(
                    id,
                    Ingredient.fromNetwork(buf),
                    buf.readItem(),
                    Ingredient.fromNetwork(buf),
                    buf.readInt(),
                    buf.readBoolean()
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CuttingBoardRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.getResultItem(null));
            recipe.tool.toNetwork(buf);
            buf.writeInt(recipe.interactions);
            buf.writeBoolean(recipe.usesItem());
        }
    }
}