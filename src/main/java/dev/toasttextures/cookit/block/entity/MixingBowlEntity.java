package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.recipes.MixingBowlRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.Optional;

public class MixingBowlEntity extends CookingBlockEntity implements ImplementedInventory {
    private int clicks = 0;
    private int uses = 0;

    public MixingBowlEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MIXING_BOWL_ENTITY, pos, state, 5);
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
    public Item getLiquid() {
        return this.getStack(4).getItem();
    }

    // Amount of times the entity has been clicked (mixed)
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    public int getClicks() { return this.clicks; }

    // Amount outputted by the recipe
    public int getUses() { return this.uses; }

    public void processRecipe() {
        Optional<RecipeEntry<MixingBowlRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent() && recipe.get().value().getLiquid().isOf(this.getLiquid())) {
            this.clicks++;
            if (this.getClicks() >= recipe.get().value().getMixes()) {
                this.setItems(DefaultedList.ofSize(5, ItemStack.EMPTY));
                this.setStack(0, recipe.get().value().craft(new SimpleInventory(), world.getRegistryManager()));
            }
        }
    }

    private Optional<RecipeEntry<MixingBowlRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size() - 1);
        for (int i = 0; i < this.size() - 1; i++) {
            inv.setStack(i, this.getStack(i));
        }
        return Objects.requireNonNull(getWorld()).getRecipeManager().getFirstMatch(MixingBowlRecipe.Type.INSTANCE, inv, getWorld());
    }
}
