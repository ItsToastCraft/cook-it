package dev.toasttextures.cookit.recipes;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public class RecipeInventory extends SimpleInventory implements RecipeInput {

    public RecipeInventory(ItemStack... items) {
        super(items);
    }
    public RecipeInventory(int size) {
        super(size);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getStack(slot);
    }

    @Override
    public int getSize() {
        return this.size();
    }
}
