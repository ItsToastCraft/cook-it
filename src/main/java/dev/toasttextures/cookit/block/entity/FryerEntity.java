package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.client.sound.FryerSoundInstance;
import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.recipes.FryerRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static dev.toasttextures.cookit.registries.CookItTags.FRYABLE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class FryerEntity extends CookingBlockEntity<FryerRecipe> implements Transferable {
    private int progress = 0;
    private int maxProgress = 0;
    private ItemStack cachedItem = ItemStack.EMPTY;
    private @Nullable FryerRecipe cachedRecipe = null;

    public FryerEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.FRYER, FryerRecipe.Type.INSTANCE, pos, state, 1);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        progress = nbt.getInt(PROGRESS_KEY);
    }

    public float getProgressRatio() {
        return (float) progress / maxProgress;
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putInt(PROGRESS_KEY, progress);
        super.saveAdditional(nbt);
    }

    // Ok so only accept fryer baskets OR fryable items if there's already an empty basket
    @Override
    public void attemptTransfer(Player player, ItemStack stack, @Nullable Vec3 interactionPos) {
        ItemStack first = getFirst();

        if (first.isEmpty()) {
            if (stack.is(CookItItems.FRYER_BASKET)) {
                setItem(0, stack.split(1));
                setChanged();
            }
        } else if (stack.isEmpty()) {
            player.getInventory().placeItemBackInInventory(first.split(1));
            setChanged();
        } else if (stack.is(FRYABLE)) {
            ItemStorage.setStoredItem(first, stack.split(1));
            setChanged();
        }
    }

    private void addFryerEffects(Level world) {
        if (world.isClientSide) return;

        for (int i = 0; i < 3; i++) {
            double particleX = worldPosition.getX() + 0.5 + world.getRandom().nextDouble() * 0.375 - 0.1875;
            double particleY = worldPosition.getY() + 0.25;
            double particleZ = worldPosition.getZ() + 0.5 + world.getRandom().nextDouble() * 0.375 - 0.1875;

            ((ServerLevel) world).sendParticles(CookIt.OIL_PARTICLE, particleX, particleY, particleZ, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void craft(Level world, FryerRecipe recipe) {
        ItemStack first = getFirst();

        if (!getFirst().isEmpty() && status == CookingStatus.DONE) {
            ItemStorage.setStoredItem(first, recipe.assemble(new SimpleContainer(first), world.registryAccess()));
        }
    }

    @Override
    public void reset() {
        status = CookingStatus.IDLE;
        progress = 0;
        cachedItem = ItemStack.EMPTY;
        cachedRecipe = null;
        setChanged();
    }

    private void tickRecipe(Level world, BlockState state) {
        if (maxProgress <= progress) {
            status = CookingStatus.DONE;
            if (cachedRecipe != null && !world.isClientSide) {
                craft(world, cachedRecipe);
                world.setBlockAndUpdate(worldPosition, state.setValue(LIT, false));
            }
            reset();
        } else {
            progress++;
            addFryerEffects(world);
        }
    }

    @Override
    public List<FryerRecipe> getRecipes() {
        SimpleContainer inv = new SimpleContainer(ItemStorage.getStoredItem(getItem(0)));
        if (level == null) return Collections.emptyList();

        return level.getRecipeManager().getRecipesFor(FryerRecipe.Type.INSTANCE, inv, level);
    }

    private void loadRecipe(Level world, BlockState state) {
        cachedRecipe = getRecipes().stream().findFirst().orElse(null);
        if (cachedRecipe == null) return;

        status = CookingStatus.PROCESSING;
        maxProgress = cachedRecipe.getMaxProgress();
        setChanged();

        world.setBlockAndUpdate(getBlockPos(), state.setValue(LIT, true));
        if (world.isClientSide) {
            FryerSoundInstance.startSoundInstance(this);
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, FryerEntity entity) {
        if (world.isClientSide) return;
        ItemStack first = entity.getFirst();

        if (first.isEmpty()) {
            if (entity.status != CookingStatus.IDLE) {
                world.setBlockAndUpdate(pos, state.setValue(LIT, false));
                entity.reset();
            }
            return;
        }

        if (!ItemStack.matches(first, entity.cachedItem)) {
            if (entity.status == CookingStatus.IDLE) {
                entity.cachedItem = first.copy();
                entity.loadRecipe(world, state);
            }
        } else if (entity.cachedRecipe != null && entity.status == CookingStatus.PROCESSING) {
            entity.tickRecipe(world, state);
        }
    }
}