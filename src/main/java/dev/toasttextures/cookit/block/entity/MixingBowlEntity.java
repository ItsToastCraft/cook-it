package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.containers.MixingBowl;
import dev.toasttextures.cookit.recipes.MixingBowlRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

import static dev.toasttextures.cookit.block.containers.MixingBowl.CONTAINS_GOOP;
import static dev.toasttextures.cookit.block.containers.MixingBowl.LIQUID_LAYER;

public class MixingBowlEntity extends CookingBlockEntity<MixingBowlRecipe> implements DefaultedInventory {
    public static final String OUTPUT_KEY = "Output";
    public static final String COLOR_KEY = "Color";
    public static final String LIQUID_KEY = "Liquid";
    private int interactions = 0;
    private MixingBowl.Liquid liquid = MixingBowl.Liquid.NONE;
    private int color = 0;

    public MixingBowlEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MIXING_BOWL, MixingBowlRecipe.Type.INSTANCE, pos, state, 7);
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        this.liquid = MixingBowl.Liquid.values()[nbt.getInt(LIQUID_KEY)];

        this.interactions = nbt.getInt(INTERACTIONS_KEY);
        this.color = nbt.getInt(COLOR_KEY);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putInt(LIQUID_KEY, liquid.ordinal());
        nbt.putInt(INTERACTIONS_KEY, interactions);
        nbt.putInt(COLOR_KEY, color);
        super.saveAdditional(nbt);
    }

    public MixingBowl.Liquid getLiquid() {
        return liquid;
    }

    public void updateLiquid(MixingBowl.Liquid liquid) {
        if (hasLevel()) {
            this.liquid = liquid;
            CookIt.LOGGER.info("gup {}", this.liquid);
            level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(LIQUID_LAYER, true));
        }
    }

    // Amount of times the entity has been clicked (mixed)
    public int getInteractions() {
        return this.interactions;
    }

    public boolean process(Level world) {
        if (world.isClientSide) return false;

        MixingBowlRecipe recipe = getRecipes().stream().findFirst().orElse(null);

        if (recipe == null) return false;

        boolean hasGoop = recipe.hasGoop();
        this.interactions++;

        if (world instanceof ServerLevel serverWorld) {
            serverWorld.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.5f, 0.25f);
            serverWorld.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, recipe.getResultItem(null)), worldPosition.getX() + 0.5f, worldPosition.getY(), worldPosition.getZ() + 0.5f, 10, 0.0f, 0.025f,0.0f,0.125f);
        }

        if (this.interactions >= recipe.getMixes()) {
            craft(world, recipe);
            reset();
            return hasGoop;
        } else if (this.interactions == recipe.getMixes() - 1 && hasGoop) {
            this.color = recipe.goopColor();
            setChanged();
        }
        return false;
    }

    public void setGoopColor(int color) {
        this.color = color;
        level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(CONTAINS_GOOP, true));
    }

    public int getGoopColor() {
        return this.color;
    }

    @Override
    public void craft(Level world, MixingBowlRecipe recipe) {
        this.clearContent();
        ItemStack output = recipe.assemble(new SimpleContainer(), world.registryAccess());
        if (recipe.hasGoop()) {
            ItemStack goop = new ItemStack(CookItItems.GOOP, output.getCount());
            goop.getOrCreateTag().putInt("Color", this.getGoopColor());
            output.setCount(1);
            output.save(goop.getOrCreateTagElement("Output"));
            output = goop;
            setChanged();
        }
        this.setItem(0, output);
    }

    @Override
    public List<MixingBowlRecipe> getRecipes() {
        SimpleContainer inv = new SimpleContainer(getItems().subList(0, 6).toArray(ItemStack[]::new));
        if (level == null) return Collections.emptyList();
        return level.getRecipeManager().getRecipesFor(MixingBowlRecipe.Type.INSTANCE, inv, level);
    }

    @Override
    public void reset() {
        this.interactions = 0;
        this.liquid = MixingBowl.Liquid.NONE;
    }
}