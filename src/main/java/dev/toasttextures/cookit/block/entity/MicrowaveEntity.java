package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.client.sound.MicrowaveSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
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

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN;

public class MicrowaveEntity extends CookingBlockEntity<MicrowaveRecipe> {
    private int progress = 0;
    private int maxProgress = 0;
    private ItemStack cachedItem = ItemStack.EMPTY;
    @Environment(EnvType.CLIENT)
    private final SoundInstance inst = new MicrowaveSoundInstance(this);
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
        if (!state.getValue(OPEN)) {
            world.setBlockAndUpdate(pos, state.setValue(LIT, on));
        }
    }

    private void tickRecipe(Level world, BlockState state) {
        if (maxProgress <= progress) {
            status = CookingStatus.DONE;
            if (cachedRecipe != null) {
                addMicrowaveEffects(world, worldPosition, state, false);
                craft(world, cachedRecipe);
            }
            reset();
        } else if (!state.getValue(OPEN)) {
            progress++;
            setChanged();
        }
    }

    private void loadRecipe(Level world, BlockState state) {
        cachedRecipe = getRecipes(0).stream().findFirst().orElse(null);

        if (cachedRecipe == null) return;

        status = CookingStatus.PROCESSING;
        maxProgress = cachedRecipe.getMaxProgress();

        addMicrowaveEffects(world, getBlockPos(), state, true);
        Minecraft.getInstance().getSoundManager().play(inst);
        setChanged();
    }

    public static void tick(Level world, BlockPos pos, BlockState state, MicrowaveEntity entity) {
        ItemStack first = entity.getFirst();

        if (first.isEmpty()) {
            if (entity.status != CookingStatus.IDLE) {
                world.setBlockAndUpdate(pos, state.setValue(LIT, false));
                entity.reset();
            }
            return;
        }

        if (!ItemStack.matches(first, entity.cachedItem)) {
            entity.reset();
            entity.cachedItem = first.copy();
            entity.loadRecipe(world, state);
        } else if (entity.cachedRecipe != null && entity.status == CookingStatus.PROCESSING) {
            entity.tickRecipe(world, state);
        }
    }

    @Override
    public void craft(Level world, MicrowaveRecipe recipe) {
        ItemStack first = getFirst();
        if (recipe.hasEvent()) {
            recipe.getEvent().apply((ServerLevel) world, worldPosition);
        }

        if (!first.isEmpty()) {
            setItem(0, recipe.assemble(new SimpleContainer(first), world.registryAccess()));
            reset();
        }
        world.playSound(null, getBlockPos(), CookItSounds.MICROWAVE_BEEP, SoundSource.BLOCKS);
    }

    @Override
    public void reset() {
        cachedItem = ItemStack.EMPTY;
        cachedRecipe = null;
        status = CookingStatus.IDLE;
        progress = 0;
        setChanged();
    }
}