package toast.cook_it.block.appliances.microwave;


import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import toast.cook_it.CookIt;
import toast.cook_it.block.CookingBlockEntity;
import toast.cook_it.block.ImplementedInventory;
import toast.cook_it.recipes.MicrowaveRecipe;
import toast.cook_it.registries.CookItBlockEntities;
import toast.cook_it.registries.CookItSounds;

import java.util.Objects;
import java.util.Optional;

import static toast.cook_it.block.appliances.microwave.Microwave.ON;
import static toast.cook_it.block.appliances.microwave.Microwave.OPEN;

public class MicrowaveEntity extends CookingBlockEntity implements ImplementedInventory {
    private static final int INPUT_SLOT = 0;
    private static final int MICROWAVE_SOUND_INTERVAL = 111;
    int progress = 0;
    private int maxProgress = 0;

    public MicrowaveEntity(BlockPos pos, BlockState state) { super(CookItBlockEntities.MICROWAVE_ENTITY, pos, state, 2); }

    @Override
    public void readNbt(NbtCompound nbt) {
        items.clear();
        super.readNbt(nbt);

        Inventories.readNbt(nbt, items);
        progress = nbt.getInt("microwave.progress");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.items);
        nbt.putInt("microwave.progress", progress);
        super.writeNbt(nbt);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) return;
        if (this.hasRecipe()) {
            if (progress == 0 || world.getTime() % MICROWAVE_SOUND_INTERVAL == 0) {
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
                boolean curr_event = Objects.equals(recipe.get().value().getEvent(), "advancement:eggplosion");
                if (curr_event) {
                    CookIt.LOGGER.info("explosion!");
                    if (this.world != null) {
                        this.world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, World.ExplosionSourceType.BLOCK);
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

        this.removeStack(INPUT_SLOT, 1);

        this.setStack(INPUT_SLOT, new ItemStack(recipe.get().value().getResult(null).getItem(), getStack(INPUT_SLOT).getCount() + recipe.get().value().getResult(null).getCount()));
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
        return getWorld().getRecipeManager().getFirstMatch(MicrowaveRecipe.Type.INSTANCE, inv, getWorld());
    }

    private void playMicrowaveSound(World world, BlockPos pos, BlockState state, boolean on) {
        if (on && !state.get(OPEN)) {
            world.playSound(null, pos, CookItSounds.MICROWAVE_SOUND_EVENT, SoundCategory.BLOCKS, 0.3f, 1.0f);
            world.setBlockState(pos, state.with(Microwave.ON, true));
        } else {
            MinecraftClient.getInstance().getSoundManager().stopSounds(CookItSounds.MICROWAVE_SOUND, SoundCategory.BLOCKS);
            world.setBlockState(pos, state.with(ON, false));
            if (state.get(OPEN)) return;
            world.playSound(null, pos, CookItSounds.MICROWAVE_BEEP_EVENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }
}

