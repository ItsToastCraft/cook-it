package dev.toasttextures.cookit.block.entity;


import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.enums.FoodProcessingStatus;
import dev.toasttextures.cookit.registries.CookItItems;
import dev.toasttextures.cookit.registries.CookItProperties;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.toasttextures.cookit.item.FryerBasket;
import dev.toasttextures.cookit.recipes.FryerRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class FryerEntity extends CookingBlockEntity implements Appliance {
    private int progress = 0;
    private int maxProgress = 0;
    private ItemStack prevItem = ItemStack.EMPTY;
    private static final Random RANDOM = new Random();

    public FryerEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.FRYER_ENTITY, pos, state, 1);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, items);
        progress = nbt.getInt("Progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.items);
        nbt.putInt("Progress", progress);
        super.writeNbt(nbt);
    }

    public void tick(World world, BlockPos pos, BlockState state) {

        if (world == null || world.isClient()) {
            return;
        }
        world.setBlockState(pos, state.with(CookItProperties.ON, status == FoodProcessingStatus.PROCESSING));
        ItemStack item = items.get(0);
        if (item.isEmpty()) {
            status = FoodProcessingStatus.IDLE;
            return;
        }
        if (!item.isOf(CookItItems.FRYER_BASKET)) {
            status = FoodProcessingStatus.INVALID_INPUT;
            return;
        }

        RecipeEntry<FryerRecipe> recipe = getCurrentRecipe();
        if (recipe == null) {
            return;
        }
        if (item.equals(prevItem)) {
            if (this.maxProgress <= this.progress) {
                status = FoodProcessingStatus.DONE;
                complete(recipe);
            } else {
                progress++;
                addEffects();
            }
        } else {
            status = FoodProcessingStatus.PROCESSING;
            this.maxProgress = recipe.value().getMaxProgress();
            this.markDirty();
        }
        prevItem = item;
    }

    public <T extends Recipe<?>> void complete(@NotNull RecipeEntry<T> entry) {
        ItemStack container = this.getStack(0);
        if (world != null) {
            if (!container.isEmpty() && isDone()) {
                FryerBasket.setItem(container, ((FryerRecipe) entry.value()).craft(new SimpleInventory(container), world.getRegistryManager()));
                this.progress = 0;
                this.markDirty();
                prevItem = ItemStack.EMPTY;
            }
        }
    }

    private void addEffects() {
        if (world == null) {
            return;
        }

        if (this.progress % 8 == 1) {
            world.playSound(null, this.pos, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.5f, 8.0f);
        } else if (this.progress % 30 == 1) {
            world.playSound(null, this.pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5f, 1.0f);
        }

        if (world instanceof ServerWorld serverWorld) {
            for (int i = 0; i < 3; i++) {
                double particleX = Math.round(((double) this.pos.getX() + 0.5 + RANDOM.nextFloat(-0.1875f, 0.1875f)) * 100) / 100d;
                double particleY = this.pos.getY() + 0.25;
                double particleZ = Math.round(((double) this.pos.getZ() + 0.5 + RANDOM.nextFloat(-0.1875f, 0.1875f)) * 100d) / 100d;

                serverWorld.spawnParticles(CookIt.OIL_PARTICLE, particleX, particleY, particleZ, 2, 0f, 0.0f, 0.0f, 0f);
            }
        }
    }

    private RecipeEntry<FryerRecipe> getCurrentRecipe() {
        if (world != null) {
            ItemStack item = getContainerItems(this.getStack(0)).get(0);
            if (item.isEmpty()) {
                return null;
            }
            return world.getRecipeManager().getFirstMatch(FryerRecipe.Type.INSTANCE, new SimpleInventory(item), world).orElse(null);
        }
        return null;
    }

    @Override
    public List<ItemStack> getContainerItems(ItemStack container) {
        ItemStack item = ItemStack.EMPTY;
        NbtCompound tag = container.getNbt();
        if (tag != null && tag.contains("Items")) {
            item = ItemStack.fromNbt(tag.getList("Items", NbtElement.COMPOUND_TYPE).getCompound(0));
        }
        return List.of(item);
    }
}