package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.recipes.MixingBowlRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Objects;

public class MixingBowlEntity extends CookingBlockEntity implements ImplementedInventory {
    private int clicks = 0;
    private int uses = 0;

    public MixingBowlEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.CUTTING_BOARD_ENTITY, pos, state, 1);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.clicks = nbt.getInt("clicks");
        this.uses = nbt.getInt("uses");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("clicks", clicks);
        nbt.putInt("uses", uses);
        super.writeNbt(nbt);
    }

    // Amount of times the entity has been clicked (mixed)
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    public int getClicks() { return this.clicks; }

    // Amount outputted by the recipe
    public int getUses() { return this.uses; }

    public void processRecipe() {
        List<RecipeEntry<MixingBowlRecipe>> recipes = getCurrentRecipe();
        if (!recipes.isEmpty()) {
            this.clicks++;
            for(RecipeEntry<MixingBowlRecipe> recipeEntry : recipes) {
                if (this.getClicks() >= recipeEntry.value().getMixes()) {
                    
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
