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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MixingBowlEntity extends CookingBlockEntity<MixingBowlRecipe> implements DefaultedInventory {
    private static final String USES_KEY = "Uses";
    private static final String COLOR_KEY = "Color";
    private int interactions = 0;
    private int uses = 0;
    private int color = 0;

    public MixingBowlEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MIXING_BOWL, MixingBowlRecipe.Type.INSTANCE, pos, state, 7);
    }
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.interactions = nbt.getInt(INTERACTIONS_KEY);
        this.uses = nbt.getInt(USES_KEY);
        this.color = nbt.getInt(COLOR_KEY);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt(INTERACTIONS_KEY, interactions);
        nbt.putInt(USES_KEY, uses);
        nbt.putInt(COLOR_KEY, color);
        super.writeNbt(nbt);
    }
    public Item getLiquid() {
        return this.getStack(this.size() - 1).getItem();
    }

    // Amount of times the entity has been clicked (mixed)
    public int getClicks() {
        return this.interactions;
    }

    public boolean process(World world) {
        if (world.isClient) return false;

        MixingBowlRecipe recipe = getRecipes(0).stream().findFirst().orElse(null);

        if (recipe == null) return false;
        boolean hasGoop = recipe.hasGoop();
        this.interactions++;

        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 0.25f);
            serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, recipe.getOutput(null)), pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, 10, 0.0f, 0.025f,0.0f,0.125f);
        }

        if (this.interactions >= recipe.getMixes()) {
            craft(world, recipe);
            reset();
            return hasGoop;
        } else if (this.interactions == recipe.getMixes() - 1 && hasGoop) {
            this.color = recipe.goopColor();
            markDirty();
        }
        return false;
    }

    public void setGoopColor(int color) {
        this.color = color;
    }
    public int getGoopColor() {
        return this.color;
    }

    @Override
    public void craft(World world, MixingBowlRecipe recipe) {
        this.clear();
        ItemStack output = recipe.craft(new SimpleInventory(), world.getRegistryManager());
        if (recipe.hasGoop()) {
            ItemStack goop = new ItemStack(CookItItems.GOOP, output.getCount());
            goop.getOrCreateNbt().putInt("Color", this.getGoopColor());
            output.setCount(1);
            output.writeNbt(goop.getOrCreateSubNbt("Output"));
            output = goop;
        }
        this.setStack(0, output);
    }

    @Override
    public void reset() {
        this.interactions = 0;
    }
}