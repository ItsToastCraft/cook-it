package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.block.food_blocks.pizza.PizzaTopping;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.recipes.CuttingBoardRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CuttingBoardEntity extends CookingBlockEntity implements Appliance {
    private int clicks = 0;

    public CuttingBoardEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.CUTTING_BOARD_ENTITY, pos, state, 1);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.clicks = nbt.getInt("clicks");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("clicks", clicks);
        super.writeNbt(nbt);
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    public int getClicks() {
        return this.clicks;
    }

    // This function returns whether it processed the item successfully.
    public boolean processRecipe(ItemStack tool, boolean tryReset) {
        if (this.getStack(0).isOf(CookItBlocks.UNCOOKED_PIZZA.asItem())) {
            if (processPizza(tool))
                return true;
        }

        List<RecipeEntry<CuttingBoardRecipe>> recipes = getCurrentRecipes();

        for(RecipeEntry<CuttingBoardRecipe> recipeEntry : recipes) {
            CuttingBoardRecipe recipe = recipeEntry.value();
            if (recipe.isResettable()) {
                if (tryReset && tool.isEmpty()) {
                    complete(recipeEntry);
                    return true;
                }
                return false;
            }

            for (ItemStack otherTool : recipe.getTool()) {
                if (tool.isOf(otherTool.getItem())) {
                    this.clicks++;
                    if (world != null && world instanceof ServerWorld serverWorld) {
                        serverWorld.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 0.25f);
                        serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, recipe.getResult(null)), pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, 10, 0f,0.025f,0.0f,0.125f);
                    }
                    if (recipe.usesItem()) {
                        tool.decrement(1);
                    }
                    if (this.getClicks() > recipe.getClicks()) {
                        complete(recipeEntry);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean processPizza(ItemStack tool) {
        NbtCompound tag = this.getStack(0).getOrCreateSubNbt("BlockEntityTag");
        NbtList toppings = tag.getList("toppings", NbtElement.STRING_TYPE);

        // The pizza has maxed out toppings, so no change happened
        if (toppings.size() == 3) {
            return false;
        }
        // Try and get the topping from the held item, so the pizza has changed
        PizzaTopping topping = PizzaTopping.fromItem(tool.getItem());
        if (topping != null) {
            // no need to loop, at this point we're already sure there's a topping slot available.
            toppings.add(NbtString.of(topping.name()));
            tool.decrement(1);
            // set the topping to whatever it is
            tag.put("toppings", toppings);
            this.markDirty();
            Objects.requireNonNull(world).updateListeners(pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
            return true;
        }
        return false;
    }

    @Override
    public <T extends Recipe<?>> void complete(@NotNull RecipeEntry<T> recipeEntry) {
        if (world == null) {
            return;
        }
        CuttingBoardRecipe recipe = (CuttingBoardRecipe) recipeEntry.value();
        ItemStack output = recipe.craft(new SimpleInventory(this.getStack(0)), this.world.getRegistryManager());
        output.setCount(recipe.getOutputCount());
        this.setStack(0, output);
        this.setClicks(0);
        this.markDirty();
        world.updateListeners(pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
    }

    private List<RecipeEntry<CuttingBoardRecipe>> getCurrentRecipes() {
        SimpleInventory inv = new SimpleInventory(this.size());
        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }
        return Objects.requireNonNull(world).getRecipeManager().getAllMatches(CuttingBoardRecipe.Type.INSTANCE, inv, world);
    }
}