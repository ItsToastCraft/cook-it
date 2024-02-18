package toast.cook_it.block.appliances.microwave;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import toast.cook_it.CookIt;
import toast.cook_it.block.CookingBlockEntity;
import toast.cook_it.block.ImplementedInventory;
import toast.cook_it.recipe.MicrowaveRecipe;
import toast.cook_it.registries.CookItBlockEntities;
import toast.cook_it.registries.CookItSounds;

import java.util.Objects;
import java.util.Optional;

public class MicrowaveEntity extends CookingBlockEntity implements ImplementedInventory {
    private final int INPUT_SLOT = 0;
    private final int OUTPUT_SLOT = 1;
    private int progress = 0;
    private int maxProgress = 0;
    public MicrowaveEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MICROWAVE_ENTITY,pos, state, 2);
    }

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

    public void tick(World world, BlockPos pos, BlockState state, BlockEntity entity) {
        if (world.isClient()) {
            return;
        }

        if (outputAvailable()) {

            if (this.hasRecipe()) {
                world.playSound(null, pos, CookItSounds.MICROWAVE_EVENT, SoundCategory.BLOCKS);
                this.updateMaxProgress();
                this.addProgress();
                markDirty(world, pos, state);
                if (craftingFinished()) {
                    Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();
                    boolean curr_event = Objects.equals(recipe.get().value().getEvent(), "advancement:eggplosion");
                    this.craftRecipe();
                    this.resetProgress();


                    //now events :D
                    if (curr_event) {
                        CookIt.LOGGER.info("explosion!");
                        if (this.world != null) {
                            this.world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, World.ExplosionSourceType.BLOCK);
                        }
                    };
                }
            } else {
                this.resetProgress();
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

        this.setStack(INPUT_SLOT, new ItemStack(recipe.get().value().getResult(null).getItem(),
                getStack(INPUT_SLOT).getCount() + recipe.get().value().getResult(null).getCount()));
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private boolean craftingFinished() {
        return this.maxProgress<=this.progress;
    }

    private void addProgress() {
        progress++;
    }

    private boolean hasRecipe() {

        Optional<RecipeEntry<MicrowaveRecipe>> recipe = getCurrentRecipe();

        return recipe.isPresent() && insertableOutputAmount(recipe.get().value().getResult(null)) &&
                insertableOutputItem(recipe.get().value().getResult(null).getItem());
    }

    private Optional<RecipeEntry<MicrowaveRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());

        for(int i=0;i<this.size();i++) {
            inv.setStack(i, this.getStack(i));

        }


        return getWorld().getRecipeManager().getFirstMatch(MicrowaveRecipe.Type.INSTANCE, inv, getWorld());
    }

    private boolean insertableOutputItem(Item item) {
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean insertableOutputAmount(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }



    private boolean outputAvailable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }
}