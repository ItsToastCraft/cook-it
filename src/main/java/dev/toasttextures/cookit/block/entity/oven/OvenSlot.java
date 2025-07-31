package dev.toasttextures.cookit.block.entity.oven;

import dev.toasttextures.cookit.enums.FoodProcessingStatus;
import dev.toasttextures.cookit.recipes.OvenRecipe;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeEntry;

import java.util.List;
import java.util.Optional;

public class OvenSlot {
    private static final int MAX_MUFFIN_TIME = 300;

    private final int slot;
    private int progress = 0;
    private int maxProgress = 0;
    private FoodProcessingStatus status = FoodProcessingStatus.IDLE;
    public OvenSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    public int getProgress() {
        return this.progress;
    }

    public void processSlot(OvenEntity entity) {
        ItemStack stack = entity.getStack(this.slot);
        if (status == FoodProcessingStatus.DONE) {
            return;
        }
        if (stack.isEmpty()) {
            return;
        }
        Optional<RecipeEntry<OvenRecipe>> recipe = entity.getCurrentRecipe(stack);
        if (recipe.isPresent()) {
            this.maxProgress = recipe.get().value().getMaxProgress();
            this.status = FoodProcessingStatus.PROCESSING;
            progress++;
        } else if (stack.isOf(CookItBlocks.MUFFIN_TIN.asItem())) {
            this.maxProgress = MAX_MUFFIN_TIME;
            this.status = FoodProcessingStatus.PROCESSING;
            progress++;
        }
        if (this.maxProgress <= this.progress) {
            complete(entity);
        }
    }

    public FoodProcessingStatus getStatus() {
        return this.status;
    }

    public void complete(OvenEntity entity) {
        ItemStack stack = entity.getStack(this.slot);

        if (BlockEntityUtils.isContainer(stack)) {
            completeContainerRecipe(entity, stack);
        } else {
            completeRecipe(entity, stack);
        }
        this.status = FoodProcessingStatus.DONE;
    }

    private void completeContainerRecipe(OvenEntity entity, ItemStack container) {
        NbtList nbtList = new NbtList();
        List<ItemStack> containerItems = entity.getContainerItems(container);

        for (int i = 0; i < containerItems.size(); i++) {
            ItemStack output = containerItems.get(i);
            Optional<RecipeEntry<OvenRecipe>> recipe = entity.getCurrentRecipe(output);

            if (recipe.isPresent()) {
                NbtCompound nbt = output.getNbt();
                output = recipe.get().value().craft(new SimpleInventory(container), entity.getWorld().getRegistryManager());
                output.setNbt(nbt);
            } else if (output.isOf(CookItItems.GOOP) && container.isOf(CookItBlocks.MUFFIN_TIN.asItem())) {
                output = ItemStack.fromNbt(output.getSubNbt("output"));
            }
            writeItemToNbt(nbtList, i, output);
        }
        container.getOrCreateSubNbt("BlockEntityTag").put("Items", nbtList);
    }

    private void completeRecipe(OvenEntity entity, ItemStack stack) {
        Optional<RecipeEntry<OvenRecipe>> recipe = entity.getCurrentRecipe(stack);
        NbtCompound nbtCompound = stack.getNbt();
        ItemStack result = recipe.get().value().craft(new SimpleInventory(stack), entity.getWorld().getRegistryManager());
        result.setNbt(nbtCompound);
        entity.setStack(this.slot, result);
    }

    public static void writeItemToNbt(NbtList list, int slot, ItemStack itemStack) {
        NbtCompound tag = new NbtCompound();
        tag.putByte("Slot", (byte) slot);
        itemStack.writeNbt(tag);
        list.add(tag);
    }
}