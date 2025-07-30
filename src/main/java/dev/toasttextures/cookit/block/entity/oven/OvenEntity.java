package dev.toasttextures.cookit.block.entity.oven;

import dev.toasttextures.cookit.block.entity.Appliance;
import dev.toasttextures.cookit.block.entity.CookingBlockEntity;
import dev.toasttextures.cookit.enums.FoodProcessingStatus;
import dev.toasttextures.cookit.registries.CookItProperties;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.toasttextures.cookit.recipes.OvenRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OvenEntity extends CookingBlockEntity implements Appliance {

    private final List<OvenSlot> slots;
    private int[] progress;
    public OvenEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.OVEN_ENTITY, pos, state, 2);
        this.slots = List.of(new OvenSlot(0), new OvenSlot(1));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        items.clear();
        super.readNbt(nbt);

        Inventories.readNbt(nbt, items);
        this.progress = nbt.getIntArray("Progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.items);
        nbt.putIntArray("Progress", progress);
        super.writeNbt(nbt);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }

        if (this.isEmpty() || state.get(CookItProperties.OPEN))  {
            status = FoodProcessingStatus.IDLE;
            return;
        }
        for (OvenSlot slot : slots) {
            slot.processSlot(this);
            progress[slot.getSlot()] = slot.getProgress();
        }
        updateStatus();
    }

//  Checks both slots and updates the status appropriately
//  Will only be "done" / "idle" if both slots are done
//  Will be "processing" / "invalid" if any slots report them
    private void updateStatus() {
        FoodProcessingStatus firstSlotStatus = slots.get(0).getStatus();
        FoodProcessingStatus secondSlotStatus = slots.get(1).getStatus();
        if (firstSlotStatus.equals(secondSlotStatus)) {
            status = firstSlotStatus;
        } else if (firstSlotStatus.equals(FoodProcessingStatus.PROCESSING) || secondSlotStatus.equals(FoodProcessingStatus.PROCESSING)) {
            status = FoodProcessingStatus.PROCESSING;
        } else if(firstSlotStatus.equals(FoodProcessingStatus.INVALID_INPUT) ||  secondSlotStatus.equals(FoodProcessingStatus.INVALID_INPUT)) {
            status = FoodProcessingStatus.INVALID_INPUT;
        }
    }

    protected Optional<RecipeEntry<OvenRecipe>> getCurrentRecipe(ItemStack itemStack) {
        ArrayList<ItemStack> items = new ArrayList<>();
        List<ItemStack> containerItems = getContainerItems(itemStack);

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

    // It's defined by each slot so we don't need it here.
    @Override
    public void complete(@NotNull RecipeEntry<? extends Recipe<SimpleInventory>> entry) { }

}