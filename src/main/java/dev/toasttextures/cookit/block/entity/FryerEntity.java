package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.RecipeInventory;
import dev.toasttextures.cookit.registry.CookItComponents;
import dev.toasttextures.cookit.registry.OilParticleEffect;
import dev.toasttextures.cookit.registry.component.SingleCookingComponent;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.item.FryerBasket;
import dev.toasttextures.cookit.recipes.FryerRecipe;
import dev.toasttextures.cookit.registry.CookItBlockEntities;

import java.util.*;

import static dev.toasttextures.cookit.block.appliances.Fryer.ON;

public class FryerEntity extends CookingBlockEntity implements ImplementedInventory {
    private int progress = 0;
    private int maxProgress = 0;

    public FryerEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.FRYER_ENTITY, pos, state, 1);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.progress = nbt.getInt("progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("progress", progress);
        super.writeNbt(nbt, registryLookup);
    }

    public void tick(World world, BlockPos pos, BlockState state) {

        if (world.isClient()) {
            return;
        }
        if (this.hasRecipe()) {
            if (this.progress % 8 == 1)
                world.playSound(null, this.pos, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.5f, 8.0f);
            if (this.progress % 30 == 1)
                world.playSound(null, this.pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1.0f);
            if (this.items.getFirst().isEmpty()) {
                this.resetProgress();
                world.setBlockState(pos, state.with(ON, false));
                return;
            }
            world.setBlockState(pos, state.with(ON, true));
            addParticles();

            this.updateMaxProgress();
            this.addProgress();

            if (craftingFinished()) {
                //playFryerSound(world, pos, state, false);
                world.setBlockState(pos, state.with(ON, false));
                this.craftRecipe();
                this.resetProgress();
                this.markDirty();
            }
        } else {
            this.resetProgress();
        }
    }

    private void updateMaxProgress() {
        Optional<RecipeEntry<FryerRecipe>> recipe = getCurrentRecipe();
        recipe.ifPresent(fryerRecipeRecipeEntry -> this.maxProgress = fryerRecipeRecipeEntry.value().getMaxProgress());
    }

    private void craftRecipe() {
        ItemStack container = this.getStack(0);
        Optional<RecipeEntry<FryerRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            if (!container.isEmpty() && recipe.get().value().getMaxProgress() <= this.progress) {
                FryerBasket.setItem(container, recipe.get().value().craft(new RecipeInventory(this.getStack(0)), Objects.requireNonNull(this.world).getRegistryManager()));
                this.markDirty();
            }
        }
    }

    private void addParticles() {
        for (int i = 0; i < 3; i++) {
            Random random = new Random();
            double particleX = Math.round(((double)this.pos.getX() + 0.5 + random.nextFloat(-0.1875f,0.1875f)) * 100d) / 100d;
            double particleY = ((double)this.pos.getY() + 0.25);
            double particleZ = Math.round(((double)this.pos.getZ() + 0.5 + random.nextFloat(-0.1875f,0.1875f)) * 100d) / 100d;

            ((ServerWorld) Objects.requireNonNull(world)).spawnParticles(new OilParticleEffect(), particleX, particleY, particleZ, 2, 0,0,0,0);
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private boolean craftingFinished() {
        return this.maxProgress == this.progress;
    }

    private void addProgress() {
        this.progress++;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<FryerRecipe>> recipe = getCurrentRecipe();
        return recipe.isPresent();
    }

    private Optional<RecipeEntry<FryerRecipe>> getCurrentRecipe() {
        RecipeInventory inv = new RecipeInventory(1);
        ItemStack item = getContainerItem(this.getStack(0));
        if (item.isEmpty()) {
            return Optional.empty();
        }
        inv.setStack(0, item);

        return Objects.requireNonNull(getWorld()).getRecipeManager().getFirstMatch(FryerRecipe.Type.INSTANCE, inv, getWorld());
    }

    public static ItemStack getContainerItem(ItemStack container) {
        return container.getOrDefault(CookItComponents.SINGLE_COOKING_COMPONENT, SingleCookingComponent.DEFAULT).getItem();
    }
}

