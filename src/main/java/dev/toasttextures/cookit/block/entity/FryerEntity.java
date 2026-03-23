package dev.toasttextures.cookit.block.entity;


import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.recipes.FryerRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static dev.toasttextures.cookit.registries.CookItTags.FRYABLE;
import static net.minecraft.block.Block.NOTIFY_LISTENERS;
import static net.minecraft.state.property.Properties.LIT;

public class FryerEntity extends CookingBlockEntity<FryerRecipe> implements Transferable {
    private int progress = 0;
    private int maxProgress = 0;

    private ItemStack cachedItem = ItemStack.EMPTY;
    @Nullable
    private FryerRecipe cachedRecipe = null;

    public FryerEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.FRYER, pos, state, 1);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        progress = nbt.getInt(PROGRESS_KEY);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt(PROGRESS_KEY, progress);
        super.writeNbt(nbt);
    }

    @Override
    public void transfer(PlayerEntity player, ItemStack stack) {
        ItemStack first = items.getFirst();
        if (first.isEmpty() && stack.isOf(CookItItems.FRYER_BASKET)) {
            items.set(0, stack.split(1));
        } else if (!first.isEmpty()) {
            if (!stack.isEmpty() && stack.isIn(FRYABLE)) {
                ItemStorage.setStoredItem(first, stack.split(1));

            } else if (!first.isEmpty()) {
                player.getInventory().offerOrDrop(stack.copyAndEmpty());
            }
        }
    }

    private void addFryerEffects(World world) {
        if (world.isClient) return;
        if (progress % 8 == 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.5f, 8.0f);
        }
        if (progress % 30 == 0) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }

        for (int i = 0; i < 3; i++) {
            double particleX = pos.getX() + 0.5 + world.getRandom().nextDouble() * 0.375 - 0.1875;
            double particleY = pos.getY() + 0.25;
            double particleZ = pos.getZ() + 0.5 + world.getRandom().nextDouble() * 0.375 - 0.1875;

            ((ServerWorld) world).spawnParticles(CookIt.OIL_PARTICLE, particleX, particleY, particleZ, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public RecipeType<FryerRecipe> getRecipeType() {
        return FryerRecipe.Type.INSTANCE;
    }

    @Override
    public void craft(World world, FryerRecipe recipe) {
        ItemStack first = items.getFirst();

        if (!first.isEmpty() && status == CookingStatus.DONE) {
            ItemStorage.setStoredItem(first, recipe.craft(new SimpleInventory(first), world.getRegistryManager()));
        }
    }

    @Override
    public void reset() {
        progress = 0;
        cachedItem = ItemStack.EMPTY;
        cachedRecipe = null;
        markDirty();
    }

    private void tickRecipe(World world, BlockState state) {
        if (maxProgress <= progress) {
            status = CookingStatus.DONE;
            if (cachedRecipe == null && !world.isClient) {
                world.setBlockState(pos, state.with(LIT, false), NOTIFY_LISTENERS);
                craft(world, cachedRecipe);
            }
            reset();
        } else {
            progress++;
            addFryerEffects(world);
        }
    }

    private void loadRecipe(World world, BlockState state) {
        if (world.isClient) return;
        cachedRecipe = getRecipes(0).stream().findFirst().orElse(null);

        if (cachedRecipe == null) return;

        status = CookingStatus.PROCESSING;
        world.setBlockState(pos, state.with(LIT, true), NOTIFY_LISTENERS);
        maxProgress = cachedRecipe.getMaxProgress();
        markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, FryerEntity entity) {
        if (world.isClient) return;
        ItemStack first = entity.items.getFirst();
        if (first.isEmpty()) {
            entity.status = CookingStatus.IDLE;
        } else if (!first.isOf(CookItItems.FRYER_BASKET)) {
            entity.status = CookingStatus.INVALID;
        } else if (first == entity.cachedItem) {
            entity.tickRecipe(world, state);
        } else {
            entity.loadRecipe(world, state);
        }
    }
}