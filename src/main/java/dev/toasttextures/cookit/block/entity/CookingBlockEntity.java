package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public abstract class CookingBlockEntity<T extends Recipe<SimpleInventory>> extends Container {
    protected static final String PROGRESS_KEY = "Progress";
    protected static final String INTERACTIONS_KEY = "Interactions";
    private final RecipeType<T> recipeType;
    protected CookingStatus status = CookingStatus.IDLE;

    public CookingBlockEntity(BlockEntityType<?> blockEntity, RecipeType<T> recipeType, BlockPos pos, BlockState state, int invSize) {
        super(blockEntity, pos, state, invSize);
        this.recipeType = recipeType;
    }

    public CookingStatus getStatus() {
        return status;
    }

    public abstract void craft(World world, T recipe);

    public abstract void reset();

    public List<T> getRecipes() {
        return getRecipes(-1);
    }

    public List<T> getRecipes(int slot) {
        SimpleInventory inv = (slot < 0 || slot >= size()) ? new SimpleInventory(this.getItems().toArray(new ItemStack[0])) : new SimpleInventory(getStack(slot));
        if (world == null) return Collections.emptyList();

        return world.getRecipeManager().getAllMatches(recipeType, inv, world);
    }
}