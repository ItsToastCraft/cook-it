package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public abstract class CookingBlockEntity<T extends Recipe<SimpleContainer>> extends Container {
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

    public abstract void craft(Level world, T recipe);

    public abstract void reset();

    public List<T> getRecipes() {
        return getRecipes(-1);
    }

    public List<T> getRecipes(int slot) {
        SimpleContainer inv = (slot < 0 || slot >= getContainerSize()) ? new SimpleContainer(this.getItems().toArray(new ItemStack[0])) : new SimpleContainer(getItem(slot));
        if (level == null) return Collections.emptyList();

        return level.getRecipeManager().getRecipesFor(recipeType, inv, level);
    }
}