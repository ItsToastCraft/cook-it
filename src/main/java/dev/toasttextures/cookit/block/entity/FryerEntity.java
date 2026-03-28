package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.CookIt;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static dev.toasttextures.cookit.registries.CookItTags.FRYABLE;
import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class FryerEntity extends CookingBlockEntity<FryerRecipe> implements Transferable {
    private int progress = 0;
    private int maxProgress = 0;

    private ItemStack cachedItem = ItemStack.EMPTY;
    @Nullable
    private FryerRecipe cachedRecipe = null;

    public FryerEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.FRYER, FryerRecipe.Type.INSTANCE, pos, state, 1);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        progress = nbt.getInt(PROGRESS_KEY);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putInt(PROGRESS_KEY, progress);
        super.saveAdditional(nbt);
    }

    // Ok so only accept fryer baskets OR fryable items if there's already an empty basket
    @Override
    public void attemptTransfer(Player player, ItemStack stack) {
        ItemStack first = getItem(0);
        if (first.isEmpty() && stack.is(CookItItems.FRYER_BASKET)) {
            setItem(0, stack.split(1));
        } else if (!first.isEmpty()) {
            if (!stack.isEmpty() && stack.is(FRYABLE)) {  // Food
                ItemStorage.setStoredItem(first, stack.split(1));
            }
        } else {
            player.getInventory().placeItemBackInInventory(stack.copyAndClear());
        }
    }

    private void addFryerEffects(Level world) {
        if (world.isClientSide) return;
        if (progress % 8 == 0) {
            world.playSound(null, worldPosition, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.5f, 8.0f);
        }
        if (progress % 30 == 0) {
            world.playSound(null, worldPosition, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.5f, 1.0f);
        }

        for (int i = 0; i < 3; i++) {
            double particleX = worldPosition.getX() + 0.5 + world.getRandom().nextDouble() * 0.375 - 0.1875;
            double particleY = worldPosition.getY() + 0.25;
            double particleZ = worldPosition.getZ() + 0.5 + world.getRandom().nextDouble() * 0.375 - 0.1875;

            ((ServerLevel) world).sendParticles(CookIt.OIL_PARTICLE, particleX, particleY, particleZ, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void craft(Level world, FryerRecipe recipe) {
        ItemStack first = getItem(0);

        if (!first.isEmpty() && status == CookingStatus.DONE) {
            ItemStorage.setStoredItem(first, recipe.assemble(new SimpleContainer(first), world.registryAccess()));
        }
    }

    @Override
    public void reset() {
        progress = 0;
        cachedItem = ItemStack.EMPTY;
        cachedRecipe = null;
        setChanged();
    }

    private void tickRecipe(Level world, BlockState state) {
        if (maxProgress <= progress) {
            status = CookingStatus.DONE;
            if (cachedRecipe == null && !world.isClientSide) {
                world.setBlock(worldPosition, state.setValue(LIT, false), UPDATE_CLIENTS);
                craft(world, cachedRecipe);
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
        if (world.isClientSide) return;
        cachedRecipe = getRecipes().stream().findFirst().orElse(null);

        if (cachedRecipe == null) return;

        status = CookingStatus.PROCESSING;
        world.setBlock(worldPosition, state.setValue(LIT, true), UPDATE_CLIENTS);
        maxProgress = cachedRecipe.getMaxProgress();
        setChanged();
    }

    public static void tick(Level world, BlockPos pos, BlockState state, FryerEntity entity) {
        if (world.isClientSide) return;
        ItemStack first = entity.getItem(0);
        if (first.isEmpty()) {
            entity.status = CookingStatus.IDLE;
        } else if (!first.is(CookItItems.FRYER_BASKET)) {
            entity.status = CookingStatus.INVALID;
        } else if (ItemStack.matches(first, entity.cachedItem)) { // I don't wanna decode the stored ItemStack every tick
            entity.tickRecipe(world, state);
        } else {
            entity.loadRecipe(world, state);
        }
    }
}