package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.food_blocks.pizza.PizzaToppings;
import dev.toasttextures.cookit.recipes.RecipeInventory;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.recipes.CuttingBoardRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CuttingBoardEntity extends CookingBlockEntity implements ImplementedInventory {
    private int clicks = 0;

    public CuttingBoardEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.CUTTING_BOARD_ENTITY, pos, state, 1);
    }
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        this.clicks = nbt.getInt("clicks");
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("clicks", clicks);
        super.writeNbt(nbt, registryLookup);
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    public int getClicks() { return this.clicks; }

    // This function returns whether it was successful
    public boolean processRecipe(ItemStack tool, boolean tryReset) {
        if (this.getStack(0).isOf(CookItBlocks.UNCOOKED_PIZZA.asItem())) {
            if (processPizza(tool))
                return true;
        }

        List<RecipeEntry<CuttingBoardRecipe>> recipes = getCurrentRecipe();
        if (!recipes.isEmpty()) {
            for(RecipeEntry<CuttingBoardRecipe> recipeEntry : recipes) {
                if (tryReset) {
                    if (recipeEntry.value().isResettable() && tool.isEmpty()) {
                        complete(recipeEntry);

                        return true;
                    }
                } else {
                    for (ItemStack otherTool : recipeEntry.value().getTool()) {
                        if (tool.getItem().asItem().equals(otherTool.getItem()) && !recipeEntry.value().isResettable()) {
                            this.clicks++;
                            Objects.requireNonNull(world).playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 0.25f);
                            ((ServerWorld) Objects.requireNonNull(world)).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, recipeEntry.value().getResult(null)), pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, 10, 0f,0.025f,0.0f,0.125f);
                            if (recipeEntry.value().usesItem()) { tool.decrement(1); }
                            if (this.getClicks() < recipeEntry.value().getClicks()) { return true; }
                            complete(recipeEntry);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean processPizza(ItemStack tool) {

        ArrayList<String> toppings = this.getStack(0).getOrDefault(CookItComponents.TOPPING_COMPONENT, new ArrayList<>());

        // The pizza has maxed out toppings, so no change happened
        if (toppings.size() == 3) {
            return false;
        }

        // Try and get the topping from the held item, so the pizza has changed
        PizzaToppings topping = PizzaToppings.fromItem(tool.getItem());
        if (topping != null) {

            // no need to loop, at this point we're already sure there's a topping slot available.
            toppings.add(topping.asString());
            tool.decrement(1);
            // set the topping to whatever it is
            this.getStack(0).getOrDefault(CookItComponents.TOPPING_COMPONENT, new ArrayList<String>()).add(topping.asString());
            this.markDirty();
            Objects.requireNonNull(world).updateListeners(pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
            return true;
        }
        return false;
    }

    private void complete(RecipeEntry<CuttingBoardRecipe> recipeEntry) {

        ItemStack output = recipeEntry.value().craft(new RecipeInventory(this.getStack(0)), this.world.getRegistryManager());
        output.setCount(recipeEntry.value().getOutputCount());
        this.setStack(0, output);
        this.setClicks(0);
        this.markDirty();
        world.updateListeners(pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
    }

    private List<RecipeEntry<CuttingBoardRecipe>> getCurrentRecipe() {
        RecipeInventory inv = new RecipeInventory(this.size());
        inv.setStack(0, this.getStack(0));
        CookIt.LOGGER.info(String.valueOf(Objects.requireNonNull(world).getRecipeManager().sortedValues()));
        CookIt.LOGGER.info(String.valueOf(Objects.requireNonNull(world).getRecipeManager().getAllMatches(CuttingBoardRecipe.Type.INSTANCE, inv, world)));
        return Objects.requireNonNull(world).getRecipeManager().getAllMatches(CuttingBoardRecipe.Type.INSTANCE, inv, world);
    }
}
