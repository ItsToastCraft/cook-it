package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.toasttextures.cookit.recipes.MicrowaveRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItSounds;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.Block.NOTIFY_LISTENERS;
import static net.minecraft.state.property.Properties.LIT;
import static net.minecraft.state.property.Properties.OPEN;

public class MicrowaveEntity extends CookingBlockEntity<MicrowaveRecipe> {
    private int progress = 0;
    private int maxProgress = 0;
    private ItemStack cachedItem = ItemStack.EMPTY;
    @Nullable
    private MicrowaveRecipe cachedRecipe = null;

    public MicrowaveEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MICROWAVE, MicrowaveRecipe.Type.INSTANCE, pos, state, 1);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        progress = nbt.getInt("Progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("Progress", progress);
        super.writeNbt(nbt);
    }

    public int getProgress() { return this.progress; }

    private void addMicrowaveEffects(World world, BlockPos pos, BlockState state, boolean on) {
        if (on && !state.get(OPEN)) {
            world.playSound(null, pos, CookItSounds.MICROWAVE_WORKING, SoundCategory.BLOCKS, 0.3f, 1.0f);
            world.setBlockState(pos, state.with(LIT, true), NOTIFY_LISTENERS);
        } else {
            world.setBlockState(pos, state.with(LIT, false), NOTIFY_LISTENERS);
            if (state.get(OPEN)) return;
            world.playSound(null, pos, CookItSounds.MICROWAVE_BEEP, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private void tickRecipe(World world, BlockState state) {
        if (maxProgress <= progress) {
            status = CookingStatus.DONE;
            if (cachedRecipe == null && !world.isClient) {
                addMicrowaveEffects(world, pos, state, false);
                craft(world, cachedRecipe);
            }
            reset();
        } else if (!state.get(OPEN)){
            progress++;
            addMicrowaveEffects(world, pos, state, true);
        }
    }

    private void loadRecipe(World world, BlockState state) {
        if (world.isClient) return;
        cachedRecipe = getRecipes(0).stream().findFirst().orElse(null);

        if (cachedRecipe == null) return;

        status = CookingStatus.PROCESSING;
        addMicrowaveEffects(world, pos, state, true);
        maxProgress = cachedRecipe.getMaxProgress();
        markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, MicrowaveEntity entity) {
        if (world.isClient) return;

        ItemStack first = entity.getStack(0);

        if (first.isEmpty()) {
            entity.status = CookingStatus.IDLE;
        } else if (first == entity.cachedItem) {
            entity.tickRecipe(world, state);
        } else if (entity.status != CookingStatus.INVALID) {
            entity.loadRecipe(world, state);
        }
    }

    @Override
    public void craft(World world, MicrowaveRecipe recipe) {
        ItemStack first = getStack(0);
        if (recipe.hasEvent()) {
            recipe.getEvent().apply((ServerWorld) world, pos);
        }

        if (!first.isEmpty()) {
            setStack(0, recipe.craft(new SimpleInventory(first), world.getRegistryManager()));
            reset();
        }
    }

    @Override
    public void reset() {
        cachedItem = ItemStack.EMPTY;
        cachedRecipe = null;
    }
}