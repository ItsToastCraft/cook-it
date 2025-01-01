package com.toast.cookit.block.entity;

import com.toast.cookit.block.ImplementedInventory;
import com.toast.cookit.recipes.MixingBowlRecipe;
import com.toast.cookit.registries.CookItBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Objects;

public class MixingBowlEntity extends CookingBlockEntity implements ImplementedInventory {
    private int clicks = 0;

    public MixingBowlEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.CUTTING_BOARD_ENTITY, pos, state, 1);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.clicks = nbt.getInt("cutting_board.clicks");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("cutting_board.clicks", clicks);
        super.writeNbt(nbt);
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    public int getClicks() { return this.clicks; }


    public void processRecipe() {
        List<RecipeEntry<MixingBowlRecipe>> recipes = getCurrentRecipe();
        if (!recipes.isEmpty()) {
            this.clicks++;
            for(RecipeEntry<MixingBowlRecipe> recipeEntry : recipes) {
                if (this.getClicks() == recipeEntry.value().getMixes()) {
                    
                }





            }
        }
    }

    private List<RecipeEntry<MixingBowlRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());
        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }
        return Objects.requireNonNull(getWorld()).getRecipeManager().getAllMatches(MixingBowlRecipe.Type.INSTANCE, inv, getWorld());
    }
}
