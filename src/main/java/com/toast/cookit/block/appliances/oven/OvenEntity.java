package com.toast.cookit.block.appliances.oven;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import com.toast.cookit.block.CookingBlockEntity;
import com.toast.cookit.block.ImplementedInventory;
import com.toast.cookit.recipes.OvenRecipe;
import com.toast.cookit.registries.CookItBlockEntities;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static com.toast.cookit.block.appliances.oven.Oven.DONE;
import static com.toast.cookit.block.appliances.oven.Oven.OPEN;

public class OvenEntity extends CookingBlockEntity implements ImplementedInventory {

    private int[] progress;
    private boolean done;

    public OvenEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.OVEN_ENTITY, pos, state, 2);
        this.progress = new int[2];
        this.done = false;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        items.clear();
        super.readNbt(nbt);

        Inventories.readNbt(nbt, items);

        this.progress = nbt.getIntArray("oven.progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.items);
        nbt.putIntArray("oven.progress", progress);
        super.writeNbt(nbt);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }
        world.setBlockState(pos, state.with(DONE, !this.getItems().isEmpty() && this.done));
        if (this.isEmpty()) { this.done = false; return;}
        if (state.get(OPEN)) { return; }

        for (int i = 0; i < this.size(); i++) {
            ItemStack item = this.getStack(i);
            if(item.isEmpty()) { break; }

            Optional<RecipeEntry<OvenRecipe>> recipe = getCurrentRecipe(item);
            if (recipe.isPresent()) {
                if (recipe.get().value().getMaxProgress() >= this.progress[i]) {
                    this.progress[i]++;
                    this.done = false;
                } else {
                    craftRecipe(i);
                    world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
                    this.done = true;
                    world.setBlockState(pos, state.with(DONE, true));


                    this.progress[i] = 0;
                }
            } else { this.done = true; break; }
        }

    }

    private void craftRecipe(int index) {
        ItemStack item = this.getStack(index);
        if (this.isContainer(item)) {
            ArrayList<ItemStack> containerItems = this.getContainerItems(item);
            NbtList nbtList = new NbtList();

            for (int i = 0; i < containerItems.size(); i++) {
                NbtCompound nbtCompound = new NbtCompound(); // Create a new compound for each iteration
                nbtCompound.putByte("Slot", (byte) i);

                Optional<RecipeEntry<OvenRecipe>> recipe = getCurrentRecipe(containerItems.get(i));
                if (recipe.isPresent()) {
                    if (!containerItems.get(i).isEmpty() && recipe.get().value().getMaxProgress() <= this.progress[index]) {
                        ItemStack output = recipe.get().value().getResult(null);
                        if (!containerItems.get(i).getNbt().isEmpty()) {
                            output.setNbt(containerItems.get(i).getNbt());
                        }
                        output.writeNbt(nbtCompound);
                    }
                } else {
                    containerItems.get(i).writeNbt(nbtCompound);
                }
                nbtList.add(nbtCompound);
            }
            item.getOrCreateSubNbt("BlockEntityTag").put("Items", nbtList);
        } else {
            Optional<RecipeEntry<OvenRecipe>> recipe = getCurrentRecipe(item);
            NbtCompound nbtCompound = item.getOrCreateNbt();
            this.removeStack(index, 1);
            ItemStack result = recipe.get().value().craft(new SimpleInventory(item), this.world.getRegistryManager());
            result.setNbt(nbtCompound);
            this.setStack(index, result);
        }
    }

    private Optional<RecipeEntry<OvenRecipe>> getCurrentRecipe(ItemStack itemStack) {
        ArrayList<ItemStack> items = new ArrayList<>();

        ArrayList<ItemStack> containerItems = this.getContainerItems(itemStack);

        if (!containerItems.isEmpty()) {
            items.addAll(containerItems);
        } else {
            items.add(itemStack);
        }

        SimpleInventory inv = new SimpleInventory(items.size());

        for (int i = 0; i < items.size(); i++) {
            inv.setStack(i, items.get(i));
        }
        return Objects.requireNonNull(getWorld()).getRecipeManager().getFirstMatch(OvenRecipe.Type.INSTANCE, inv, getWorld());
    }
}