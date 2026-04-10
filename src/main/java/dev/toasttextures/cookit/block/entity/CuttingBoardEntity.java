package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.block.food.pizza.PizzaTopping;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import dev.toasttextures.cookit.recipes.CuttingBoardRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.world.level.Level;

import java.util.List;

public class CuttingBoardEntity extends CookingBlockEntity<CuttingBoardRecipe> {
    private int interactions = 0;

    public CuttingBoardEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.CUTTING_BOARD, CuttingBoardRecipe.Type.INSTANCE, pos, state, 1);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.interactions = nbt.getInt(INTERACTIONS_KEY);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putInt(INTERACTIONS_KEY, interactions);
        super.saveAdditional(nbt);
    }

    public int getInteractions() {
        return this.interactions;
    }

    public void setInteractions(int interactions) {
        this.interactions = interactions;
    }

    public boolean process(Level world, ItemStack tool, boolean shouldReset) {
        ItemStack first = getFirst();
        if (world.isClientSide) return false;

        if (first.is(CookItBlocks.UNCOOKED_PIZZA.asItem())) {
            return craftPizza(tool);
        }
        List<CuttingBoardRecipe> recipes = getRecipes(0);

        if (recipes.isEmpty()) return false;

        for (CuttingBoardRecipe recipe : recipes) {
            if (recipe.resets() && shouldReset && tool.isEmpty()) {
                craft(world, recipe);
                return true;
            }
            for (ItemStack stack : recipe.getTool()) {
                if (!tool.is(stack.getItem())) continue;

                world.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.5f, 0.25f);
                ((ServerLevel) world).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, recipe.getResultItem(null)), worldPosition.getX() + 0.5, worldPosition.getY(), worldPosition.getZ() + 0.5, 10, 0.0, 0.125, 0.0, 0.125);

                this.interactions++;
                if (recipe.usesItem()) {
                    tool.shrink(1);
                }

                if (interactions < recipe.getInteractions()) return true;

                craft(world, recipe);
                return true;
            }
        }
        return false;
    }

    public boolean craftPizza(ItemStack stack) {
        PizzaTopping topping = PizzaTopping.byItem(stack.getItem());
        if (topping == null) return false;

        ListTag toppings = PizzaTopping.parse(getFirst().getOrCreateTag()).copy();
        if (toppings.size() == 3) return false;
        toppings.add(topping.asNbt());

        stack.shrink(1);
        getFirst().getTag().put(PizzaTopping.TOPPINGS_KEY, toppings);
        setChanged();
        return true;
    }

    @Override
    public void reset() {
        interactions = 0;
        setChanged();
    }

    @Override
    public void craft(Level world, CuttingBoardRecipe recipe) {
        ItemStack first = getItem(0);

        if (!first.isEmpty()) {
            setItem(0, recipe.assemble(new SimpleContainer(first), world.registryAccess()));
            reset();
        }
    }
}