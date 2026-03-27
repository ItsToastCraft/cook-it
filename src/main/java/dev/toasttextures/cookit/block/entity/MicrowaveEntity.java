package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import dev.toasttextures.cookit.recipes.MicrowaveRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItSounds;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN;

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
    public void load(CompoundTag nbt) {
        super.load(nbt);
        progress = nbt.getInt("Progress");
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putInt("Progress", progress);
        super.saveAdditional(nbt);
    }

    public int getProgress() { return this.progress; }

    private void addMicrowaveEffects(Level world, BlockPos pos, BlockState state, boolean on) {
        if (on && !state.getValue(OPEN)) {
            world.playSound(null, pos, CookItSounds.MICROWAVE_WORKING, SoundSource.BLOCKS, 0.3f, 1.0f);
            world.setBlock(pos, state.setValue(LIT, true), UPDATE_CLIENTS);
        } else {
            world.setBlock(pos, state.setValue(LIT, false), UPDATE_CLIENTS);
            if (state.getValue(OPEN)) return;
            world.playSound(null, pos, CookItSounds.MICROWAVE_BEEP, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private void tickRecipe(Level world, BlockState state) {
        if (maxProgress <= progress) {
            status = CookingStatus.DONE;
            if (cachedRecipe == null && !world.isClientSide) {
                addMicrowaveEffects(world, worldPosition, state, false);
                craft(world, cachedRecipe);
            }
            reset();
        } else if (!state.getValue(OPEN)){
            progress++;
            addMicrowaveEffects(world, worldPosition, state, true);
        }
    }

    private void loadRecipe(Level world, BlockState state) {
        if (world.isClientSide) return;
        cachedRecipe = getRecipes(0).stream().findFirst().orElse(null);

        if (cachedRecipe == null) return;

        status = CookingStatus.PROCESSING;
        addMicrowaveEffects(world, worldPosition, state, true);
        maxProgress = cachedRecipe.getMaxProgress();
        setChanged();
    }

    public static void tick(Level world, BlockPos pos, BlockState state, MicrowaveEntity entity) {
        if (world.isClientSide) return;

        ItemStack first = entity.getItem(0);

        if (first.isEmpty()) {
            entity.status = CookingStatus.IDLE;
        } else if (first == entity.cachedItem) {
            entity.tickRecipe(world, state);
        } else if (entity.status != CookingStatus.INVALID) {
            entity.loadRecipe(world, state);
        }
    }

    @Override
    public void craft(Level world, MicrowaveRecipe recipe) {
        ItemStack first = getItem(0);
        if (recipe.hasEvent()) {
            recipe.getEvent().apply((ServerLevel) world, worldPosition);
        }

        if (!first.isEmpty()) {
            setItem(0, recipe.assemble(new SimpleContainer(first), world.registryAccess()));
            reset();
        }
    }

    @Override
    public void reset() {
        cachedItem = ItemStack.EMPTY;
        cachedRecipe = null;
    }
}