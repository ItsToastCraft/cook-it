package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.RecipeInventory;
import dev.toasttextures.cookit.registry.*;
import dev.toasttextures.cookit.registry.component.CookingComponent;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.BlockState;
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
import java.util.List;
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
        super.readNbt(nbt, registryLookup);
        this.progress = nbt.getIntArray("oven.progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putIntArray("oven.progress", progress);
        super.writeNbt(nbt, registryLookup);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            return;
        }
        world.setBlockState(pos, state.with(DONE, !this.getItems().isEmpty() && this.done));
        if (this.isEmpty()) {
            this.done = false;
            return;
        }
        if (state.get(OPEN)) {
            return;
        }

        for (int i = 0; i < this.size(); i++) {
            ItemStack item = this.getStack(i);
            if (item.isEmpty()) {
                break;
            }

            List<Optional<RecipeEntry<OvenRecipe>>> recipes = getCurrentRecipe(item);

            if (item.isOf(CookItBlocks.MUFFIN_TIN.asItem())) {
                this.processMuffinRecipe(world, pos, state, i);
            } else if (!recipes.isEmpty()) {
                Optional<RecipeEntry<OvenRecipe>> recipe = recipes.getFirst();
                if (recipe.isPresent()) {
                    if (recipe.get().value().getMaxProgress() >= this.progress[i]) {
                        this.progress[i]++;
                        this.done = false;
                    } else {
                        boolean markAsDone = craftRecipe(i);
                        this.markDirty();
                        this.done = markAsDone;
                        world.setBlockState(pos, state.with(DONE, true));
                        if (markAsDone) {
                            this.progress[i] = 0;
                        }
                    }
                } else {
                    this.done = true;
                    break;
                }
            } else {
                this.done = true;
                break;
            }
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
        }
    }

    private boolean craftRecipe(int index) {
        ItemStack stack = this.getStack(index);
        boolean done = true;
        if (BlockEntityUtils.isContainer(stack)) {
            ArrayList<ItemStack> containerItems = BlockEntityUtils.getContainerItems(stack);
            ArrayList<ItemStack> outputs = new ArrayList<>();
            for (ItemStack item : containerItems) {

                Optional<RecipeEntry<OvenRecipe>> recipe = getRecipeForItem(item);
                if (recipe.isPresent()) {
                    if (!item.isEmpty() && recipe.get().value().getMaxProgress() <= this.progress[index]) {
                        ItemStack output = recipe.get().value().craft(new RecipeInventory(stack), this.world.getRegistryManager());
                        if (item.getComponents() != null) {
                            output.applyComponentsFrom(item.getComponents());
                        }
                        outputs.add(output);
                    } else {
                        outputs.add(item);
                        done = false;
                    }
                } else {
                    outputs.add(item);
                    done = false;
                }
            }
            stack.set(CookItComponents.COOKING_COMPONENT, new CookingComponent(outputs));
        } else {
            Optional<RecipeEntry<OvenRecipe>> recipe = getRecipeForItem(stack);
            if (recipe.isPresent()) {
                ItemStack result = recipe.get().value().craft(new RecipeInventory(stack), this.world.getRegistryManager());
                result.applyComponentsFrom(stack.getComponents());
                this.setStack(index, result);
            }
        }
        assert world != null;

        world.playSound(null, this.getPos(), SoundEvent.of(Identifier.of("block.note_block.xylophone")), SoundCategory.BLOCKS, 3.0f, 1.5f);

        return done;
    }

    private List<Optional<RecipeEntry<OvenRecipe>>> getCurrentRecipe(ItemStack itemStack) {
        List<ItemStack> containerItems = BlockEntityUtils.getContainerItems(itemStack);
        return (containerItems.isEmpty() ? List.of(itemStack) : containerItems).stream().map(this::getRecipeForItem).filter(Optional::isPresent).toList();
    }

    private Optional<RecipeEntry<OvenRecipe>> getRecipeForItem(ItemStack item) {
        RecipeInventory inv = new RecipeInventory(1);
        inv.setStack(0, item);
        return Objects.requireNonNull(world).getRecipeManager().getFirstMatch(OvenRecipe.Type.INSTANCE, inv, world);
    }
}