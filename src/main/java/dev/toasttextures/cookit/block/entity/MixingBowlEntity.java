package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.recipes.MixingBowlRecipe;
import dev.toasttextures.cookit.recipes.RecipeInventory;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItComponents;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.Optional;

public class MixingBowlEntity extends CookingBlockEntity implements ImplementedInventory {
    private int clicks = 0;
    private int uses = 0;
    private int color = 0;

    public MixingBowlEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MIXING_BOWL_ENTITY, pos, state, 7);
    }
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.clicks = nbt.getInt("clicks");
        this.uses = nbt.getInt("uses");
        this.color = nbt.getInt("color");
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("clicks", clicks);
        nbt.putInt("uses", uses);
        nbt.putInt("color", color);
        super.writeNbt(nbt, registryLookup);
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
        Optional<RecipeEntry<MixingBowlRecipe>> recipe = getCurrentRecipe();
        if (recipe.isPresent() && recipe.get().value().getLiquid().isOf(this.getLiquid())) {
            this.clicks++;
            boolean hasGoop = recipe.get().value().hasGoop();
            int mixes = recipe.get().value().getMixes();
            Objects.requireNonNull(world).playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 0.25f);
            ((ServerWorld) Objects.requireNonNull(world)).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, recipe.get().value().getResult(null)), pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, 10, 0f,0.025f,0.0f,0.125f);

            if (this.getClicks() >= mixes) {
                this.setItems(DefaultedList.ofSize(this.size(), ItemStack.EMPTY));
                ItemStack output = recipe.get().value().craft(new RecipeInventory(), world.getRegistryManager());
                if (hasGoop){

                    ItemStack goop = new ItemStack(CookItItems.GOOP, output.getCount());
                    output.setCount(1);
                    output.set(CookItComponents.SINGLE_COOKING_COMPONENT, goop);
                    goop.set(CookItComponents.COLOR_COMPONENT, this.getGoopColor());

                    output = goop;
                }
                this.setStack(0, output);
                this.setClicks(0);
                return hasGoop;
            } else if (this.getClicks() == mixes - 1 && hasGoop) {
                // Set the color one click early
                this.color = recipe.get().value().goopColor();
                if (world != null && !world.isClient()) {
                    markDirty();
                }
            }

        }
        return false;
    }

    private Optional<RecipeEntry<MixingBowlRecipe>> getCurrentRecipe() {
        RecipeInventory inv = new RecipeInventory(this.size() - 1);
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
