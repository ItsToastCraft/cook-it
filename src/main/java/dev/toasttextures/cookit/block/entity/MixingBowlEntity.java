package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.MixingBowlRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class MixingBowlEntity extends CookingBlockEntity {
    private int clicks = 0;
    private int uses = 0;
    private int color = 0;

    public MixingBowlEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MIXING_BOWL_ENTITY, pos, state, 7);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.clicks = nbt.getInt("clicks");
        this.uses = nbt.getInt("uses");
        this.color = nbt.getInt("color");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("clicks", clicks);
        nbt.putInt("uses", uses);
        nbt.putInt("color", color);
        super.writeNbt(nbt);
    }

    public boolean hasRecipe() {
        return getCurrentRecipe().isPresent();
    }

    public Item getLiquid() {
        return this.getStack(this.size() - 1).getItem();
    }

    // Amount of times the entity has been clicked (mixed)
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    public int getClicks() { return this.clicks; }

    // Amount outputted by the recipe
    public int getUses() { return this.uses; }

    public boolean processRecipe() {
        Optional<RecipeEntry<MixingBowlRecipe>> recipeEntry = getCurrentRecipe();
        if (recipeEntry.isPresent()) {
            MixingBowlRecipe recipe = recipeEntry.get().value();
            if(!recipe.getLiquid().isOf(this.getLiquid())) {
                return false;
            }

            this.clicks++;

            int mixes = recipe.getMixes();
            if (world != null && world instanceof ServerWorld serverWorld) {
                serverWorld.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 0.25f);
                serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, recipe.getResult(null)), pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, 10, 0f,0.025f,0.0f,0.125f);
            }

            if (this.getClicks() >= mixes) {
                complete(recipeEntry.get());
            } else if (this.getClicks() == mixes - 1 && recipe.hasGoop()) {
                // Set the color one click early
                this.color = recipe.goopColor();
                if (world != null && !world.isClient()) {
                    markDirty();
                }
            }
        }
        return false;
    }

    public void complete(@NotNull RecipeEntry<? extends Recipe<SimpleInventory>> entry) {
        MixingBowlRecipe recipe = (MixingBowlRecipe) entry.value();
        this.setItems(DefaultedList.ofSize(this.size(), ItemStack.EMPTY));
        ItemStack output = recipe.craft(new SimpleInventory(), world.getRegistryManager());

        if (recipe.hasGoop()){
            ItemStack goop = new ItemStack(CookItItems.GOOP, output.getCount());
            output.setCount(1);
            output.writeNbt(goop.getOrCreateSubNbt("output"));
            Objects.requireNonNull(goop.getNbt()).putInt("color", this.getGoopColor());

            output = goop;
        }
        this.setStack(0, output);
        this.setClicks(0);
    }

    private Optional<RecipeEntry<MixingBowlRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size() - 1);
        for (int i = 0; i < this.size() - 1; i++) {
            inv.setStack(i, this.getStack(i));
        }
        return Objects.requireNonNull(getWorld()).getRecipeManager().getFirstMatch(MixingBowlRecipe.Type.INSTANCE, inv, getWorld());
    }
    public void setGoopColor(int color) {
        this.color = color;
    }

    public int getGoopColor() {
        return this.color;
    }
}