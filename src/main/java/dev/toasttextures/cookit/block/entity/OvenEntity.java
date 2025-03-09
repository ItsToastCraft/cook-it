package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.RecipeInventory;
import dev.toasttextures.cookit.registry.*;
import dev.toasttextures.cookit.registry.component.CookingComponent;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.recipes.OvenRecipe;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static dev.toasttextures.cookit.block.appliances.Oven.DONE;
import static dev.toasttextures.cookit.block.appliances.Oven.OPEN;

public class OvenEntity extends CookingBlockEntity implements ImplementedInventory {

    private int[] progress;
    private boolean done;

    public OvenEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.OVEN_ENTITY, pos, state, 2);
        this.progress = new int[2];
        this.done = false;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        items.clear();
        super.readNbt(nbt,registryLookup);

        Inventories.readNbt(nbt, items, registryLookup);

        this.progress = nbt.getIntArray("oven.progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, this.items, registryLookup);
        nbt.putIntArray("oven.progress", progress);
        super.writeNbt(nbt, registryLookup);
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
            if (item.isOf(CookItBlocks.MUFFIN_TIN.asItem())) {
                this.processMuffinRecipe(world, pos, state, i);
            } else if (recipe.isPresent()) {
                if (recipe.get().value().getMaxProgress() >= this.progress[i]) {
                    this.progress[i]++;
                    this.done = false;
                } else {
                    craftRecipe(i);
                    this.markDirty();
                    this.done = true;
                    world.setBlockState(pos, state.with(DONE, true));
                    this.progress[i] = 0;
                }
            } else { this.done = true; break; }
        }

    }

    private void processMuffinRecipe(World world, BlockPos pos, BlockState state, int slot) {
        if (this.done) return;
        if (400 >= this.progress[slot]) {
            this.progress[slot]++;
        } else {
            ItemStack muffinTin = this.getStack(slot);

            ArrayList<ItemStack> containerItems = BlockEntityUtils.getContainerItems(muffinTin);
            ArrayList<ItemStack> outputs = new ArrayList<>();
            for (ItemStack item : containerItems) {
                if (item.isOf(CookItItems.GOOP)) {
                    ItemStack muffin = item.getOrDefault(CookItComponents.COOKING_COMPONENT, CookingComponent.DEFAULT).get(0);
                    if (!muffin.isEmpty()) {
                        outputs.add(muffin);
                    }
                } else {
                    outputs.add(item);
                }
            }
            muffinTin.set(CookItComponents.COOKING_COMPONENT, new CookingComponent(outputs));
            this.markDirty();
            this.done = true;
            world.setBlockState(pos, state.with(DONE, true));
            this.progress[slot] = 0;
            world.playSound(null, this.getPos(), SoundEvent.of(Identifier.of("block.note_block.xylophone")), SoundCategory.BLOCKS, 3.0f, 1.5f);
            world.playSound(null, this.getPos(), SoundEvent.of(Identifier.of("block.note_block.xylophone")), SoundCategory.BLOCKS, 3.0f, 2f);
            world.playSound(null, this.getPos(), SoundEvent.of(Identifier.of("block.note_block.xylophone")), SoundCategory.BLOCKS, 3.0f, 2.5f);
        }
    }
    private void craftRecipe(int index) {
        ItemStack stack = this.getStack(index);
        if (BlockEntityUtils.isContainer(stack)) {
            ArrayList<ItemStack> containerItems = BlockEntityUtils.getContainerItems(stack);
            ArrayList <ItemStack> outputs = new ArrayList<>();
            for (ItemStack item : containerItems) {
                Optional<RecipeEntry<OvenRecipe>> recipe = getCurrentRecipe(item);
                if (recipe.isPresent()) {
                    if (!item.isEmpty() && recipe.get().value().getMaxProgress() <= this.progress[index]) {
                        ItemStack output = recipe.get().value().craft(new RecipeInventory(stack), this.world.getRegistryManager());
                        if (item.getComponents() != null) {
                            output.applyComponentsFrom(item.getComponents());
                        }

                        outputs.add(output);
                    }
                } else {
                    outputs.add(item);
                }
            }
            stack.set(CookItComponents.COOKING_COMPONENT, new CookingComponent(outputs));
        } else {
            Optional<RecipeEntry<OvenRecipe>> recipe = getCurrentRecipe(stack);
            ItemStack result = recipe.get().value().craft(new RecipeInventory(stack), this.world.getRegistryManager());
            result.applyComponentsFrom(stack.getComponents());
            this.setStack(index, result);
        }
        assert world != null;
        world.playSound(null, this.getPos(), SoundEvent.of(Identifier.of("block.note_block.xylophone")), SoundCategory.BLOCKS, 3.0f, 1.5f);
    }

    private Optional<RecipeEntry<OvenRecipe>> getCurrentRecipe(ItemStack itemStack) {
        ArrayList<ItemStack> items = new ArrayList<>();

        ArrayList<ItemStack> containerItems = BlockEntityUtils.getContainerItems(itemStack);

        if (!containerItems.isEmpty()) {
            items.addAll(containerItems);
        } else {
            items.add(itemStack);
        }

        RecipeInventory inv = new RecipeInventory(items.size());

        for (int i = 0; i < items.size(); i++) {
            inv.setStack(i, items.get(i));
        }
        return Objects.requireNonNull(getWorld()).getRecipeManager().getFirstMatch(OvenRecipe.Type.INSTANCE, inv, getWorld());
    }
}