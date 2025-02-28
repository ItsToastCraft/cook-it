package dev.toasttextures.cookit.block.entity;


import dev.toasttextures.cookit.CookIt;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.item.FryerBasket;
import dev.toasttextures.cookit.recipes.FryerRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static dev.toasttextures.cookit.block.appliances.Fryer.ON;


public class FryerEntity extends CookingBlockEntity implements ImplementedInventory {
    private static final int Fryer_SOUND_INTERVAL = 111;
    int progress = 0;
    private int maxProgress = 0;

    public FryerEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.FRYER_ENTITY, pos, state, 1);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        items.clear();
        super.readNbt(nbt);

        Inventories.readNbt(nbt, items);
        progress = nbt.getInt("fryer.progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.items);
        nbt.putInt("fryer.progress", progress);
        super.writeNbt(nbt);
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
            if (this.items.get(0).isEmpty()) {
                this.resetProgress();
                world.setBlockState(pos, state.with(ON, false));
                return;
            }
            world.setBlockState(pos, state.with(ON, true));

//            if (progress == 0 || world.getTime() % Fryer_SOUND_INTERVAL == 0) {
//                //playFryerSound(world, pos, state, true);
//            }
            addParticles();

            this.updateMaxProgress();
            this.addProgress();
            markDirty(world, pos, state);

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

        maxProgress = recipe.get().value().getMaxProgress();
    }

    private void craftRecipe() {
        ItemStack container = this.getStack(0);
        Optional<RecipeEntry<FryerRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent()) {
            if (!container.isEmpty() && recipe.get().value().getMaxProgress() <= this.progress) {
                FryerBasket.setItem(container, recipe.get().value().craft(new SimpleInventory(this.getStack(0)), Objects.requireNonNull(this.world).getRegistryManager()));
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
            assert world != null;

            ((ServerWorld) world).spawnParticles(CookIt.OIL_PARTICLE, particleX, particleY, particleZ, 2, 0f,0.0f,0.0f,0f);

            }
        }

    private void resetProgress() {
        this.progress = 0;
    }

    private boolean craftingFinished() {
        return this.maxProgress == this.progress;
    }

    private void addProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        return getCurrentRecipe().isPresent();
    }

    private Optional<RecipeEntry<FryerRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());
        ItemStack item = getContainerItem(this.getStack(0));
        if (item.isEmpty()) {
            return Optional.empty();
        }
        inv.setStack(0, item);

        return Objects.requireNonNull(getWorld()).getRecipeManager().getFirstMatch(FryerRecipe.Type.INSTANCE, inv, getWorld());
    }

    public static ItemStack getContainerItem(ItemStack container) {

        ItemStack itemStack = ItemStack.EMPTY;
        NbtCompound nbt = container.getNbt();
        if (nbt != null && nbt.contains("Items")) {
            NbtList itemsTag = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            NbtCompound itemTag = itemsTag.getCompound(0);
            itemStack = ItemStack.fromNbt(itemTag);
        }
        return itemStack;
    }

//    private void playFryerSound(World world, BlockPos pos, BlockState state, boolean on) {
//        if (on && !state.get(OPEN)) {
//            world.playSound(null, pos, CookItSounds.Fryer_SOUND_EVENT, SoundCategory.BLOCKS, 0.3f, 1.0f);
//            world.setBlockState(pos, state.with(Fryer.ON, true));
//        } else {
//            world.setBlockState(pos, state.with(ON, false));
//            if (state.get(OPEN)) return;
//            world.playSound(null, pos, CookItSounds.Fryer_BEEP_EVENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
//        }
//    }
}

