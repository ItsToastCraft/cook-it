package dev.toasttextures.cookit.block.entity;


import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.recipes.MicrowaveRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItSounds;

import java.util.Objects;
import java.util.Optional;

import static dev.toasttextures.cookit.block.appliances.Microwave.ON;
import static dev.toasttextures.cookit.block.appliances.Microwave.OPEN;

public class MicrowaveEntity extends CookingBlockEntity implements ImplementedInventory {
    private static final int INPUT_SLOT = 0;
    private static final int MICROWAVE_SOUND_INTERVAL = 111;
    private int progress = 0;
    private int maxProgress = 0;

    public MicrowaveEntity(BlockPos pos, BlockState state) { super(CookItBlockEntities.MICROWAVE_ENTITY, pos, state, 2); }

    @Override
    public void readNbt(NbtCompound nbt) {
        items.clear();
        super.readNbt(nbt);

        Inventories.readNbt(nbt, items);
        progress = nbt.getInt("progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.items);
        nbt.putInt("progress", progress);
        super.writeNbt(nbt);
    }
    public int getProgress() { return this.progress; }
    public void tick(World world, BlockPos pos, BlockState state) {

        if (world.isClient()) { return; }
        if (this.hasRecipe()) {
            if (this.getProgress() == 0 || world.getTime() % MICROWAVE_SOUND_INTERVAL == 0) {
                playMicrowaveSound(world, pos, state, true);
            }
            if (state.get(OPEN)) playMicrowaveSound(world, pos, state, false);

            this.updateMaxProgress();
            this.addProgress();
            markDirty(world, pos, state);

            if (craftingFinished()) {
                // Stop the continuous microwave sound and play the world's most annoying beep sound
                playMicrowaveSound(world, pos, state, false);

                Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();
                this.craftRecipe();
                this.resetProgress();

                //Kaboom stuff
                float explosionPower = recipe.get().value().getExplosionPower();
                if (explosionPower > 0) {
                    if (this.world != null) {
                        this.world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), explosionPower, World.ExplosionSourceType.BLOCK);
                    }
                }
            }
        } else {
            this.resetProgress();
        }
    }

    private void updateMaxProgress() {
        Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();

        maxProgress = recipe.get().value().getMaxProgress();
    }

    private void craftRecipe() {
        Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();

        this.setStack(INPUT_SLOT, recipe.get().value().craft(new SimpleInventory(this.getStack(0)), Objects.requireNonNull(this.world).getRegistryManager()));
    }

    private void resetProgress() { this.progress = 0; }

    private boolean craftingFinished() { return this.maxProgress == this.progress;}

    private void addProgress() { progress++; }

    private boolean hasRecipe() {
        Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();
        return recipe.isPresent();
    }

    private Optional<RecipeEntry<MicrowaveRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());

        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }
        return Objects.requireNonNull(getWorld()).getRecipeManager().getFirstMatch(MicrowaveRecipe.Type.INSTANCE, inv, getWorld());
    }

    private void playMicrowaveSound(World world, BlockPos pos, BlockState state, boolean on) {
        if (on && !state.get(OPEN)) {
            world.playSound(null, pos, CookItSounds.MICROWAVE_SOUND_EVENT, SoundCategory.BLOCKS, 0.3f, 1.0f);
            world.setBlockState(pos, state.with(ON, true));
        } else {
            world.setBlockState(pos, state.with(ON, false));
            if (state.get(OPEN)) return;
            world.playSound(null, pos, CookItSounds.MICROWAVE_BEEP_EVENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }
}

