package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.block.food.pizza.PizzaTopping;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.recipes.CuttingBoardRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.world.World;

import java.util.List;

public class CuttingBoardEntity extends CookingBlockEntity<CuttingBoardRecipe> {
    private int interactions = 0;

    public CuttingBoardEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.CUTTING_BOARD, CuttingBoardRecipe.Type.INSTANCE, pos, state, 1);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.interactions = nbt.getInt(INTERACTIONS_KEY);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt(INTERACTIONS_KEY, interactions);
        super.writeNbt(nbt);
    }

    public int getInteractions() {
        return this.interactions;
    }

    public void setInteractions(int interactions) {
        this.interactions = interactions;
    }

    public boolean process(World world, ItemStack tool, boolean shouldReset) {
        ItemStack first = items.get(0);
        if (world.isClient) return false;

        if (first.isOf(CookItBlocks.UNCOOKED_PIZZA.asItem())) {
            return craftPizza(tool);
        }
        List<CuttingBoardRecipe> recipes = getRecipes(0);
        if (recipes.isEmpty()) return false;

        for (CuttingBoardRecipe recipe : getRecipes(0)) {
            if (recipe.resets()) {
                if (shouldReset && tool.isEmpty()) {
                    craft(world, recipe);
                }
                return true;
            }
            for (ItemStack stack : recipe.getTool()) {
                if (!tool.isOf(stack.getItem())) continue;

                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 0.25f);
                ((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, recipe.getOutput(null)), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 10, 0.0, 0.125, 0.0, 0.125);

                this.interactions++;
                if (recipe.usesItem()) {
                    tool.decrement(1);
                }

                if (interactions < recipe.getInteractions()) return true;

                craft(world, recipe);
                return true;
            }
        }
        return false;
    }

    public boolean craftPizza(ItemStack stack) {
        NbtList toppings = PizzaTopping.parse(getStack(0).getSubNbt(PizzaTopping.TOPPINGS_KEY));
        if (toppings == null) return false;
        if (toppings.size() == 3) return false;

        PizzaTopping topping = PizzaTopping.byItem(stack.getItem());
        if (topping == null) return false;

        toppings.copy().add(NbtString.of(topping.toString()));
        stack.decrement(1);
        getStack(0).getOrCreateNbt().put(PizzaTopping.TOPPINGS_KEY, toppings);
        return true;
    }

    @Override
    public void reset() {
        interactions = 0;
        markDirty();
    }

    @Override
    public void craft(World world, CuttingBoardRecipe recipe) {
        ItemStack first = getStack(0);

        if (!first.isEmpty()) {
            setStack(0, recipe.craft(new SimpleInventory(first), world.getRegistryManager()));
            reset();
        }
    }
}