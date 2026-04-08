package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.OvenRecipe;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.*;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class OvenSlot extends Slot<OvenEntity> {
    private static final Map<Direction, Vec3> OFFSETS = Map.of(
            Direction.NORTH, new Vec3(0, 0, -0.5),
            Direction.SOUTH, new Vec3(0, 0, 0.5),
            Direction.EAST, new Vec3(0.5, 0, 0),
            Direction.WEST, new Vec3(-0.5, 0, 0));

    private static final Map<Direction, Pair<Vec3, Vec3>> ROTATION_CACHE = new HashMap<>();

    private List<OvenRecipe> cachedRecipes = new ArrayList<>();
    private int lastTicked = 0;
    private ItemStack cachedItem = ItemStack.EMPTY;
    private CookingStatus status = CookingStatus.IDLE;

    private final int[] maxProgress = new int[8];

    public OvenSlot(Vec3 pos, OvenEntity attachedEntity, int index) {
        super(pos, attachedEntity, index);
    }

    public static Pair<Vec3, Vec3> rotated(Direction dir) {
        return ROTATION_CACHE.computeIfAbsent(dir, _dir -> {
            Vec3 offset = OFFSETS.get(_dir);
            return Pair.of(
                    offset.add(0.5, 0.625, 0.5),
                    offset.add(0.5, 0.375, 0.5)
            );
        });
    }

    private void tickRecipe(Level world, BlockState state) {
        for (int i = 0; i < cachedRecipes.size(); i++) {
            OvenRecipe recipe = cachedRecipes.get(i);
            if (recipe == null) continue;
            if (maxProgress[i] <= attachedEntity.getProgress(index)[i]) {
                lastTicked = i;

                status = CookingStatus.DONE;
                if (!world.isClientSide) {
                    world.setBlockAndUpdate(attachedEntity.getBlockPos(), state.setValue(LIT, false));
                    attachedEntity.updateStatus(this);
                    attachedEntity.craft(world, recipe);
                }

                reset();
            } else {
                attachedEntity.addProgress(this, i);
            }
        }
    }

    private void reset() {
        status = CookingStatus.IDLE;
        maxProgress[lastTicked] = 0;

        attachedEntity.reset();
        attachedEntity.setChanged();
        lastTicked = -1;
    }

    private void loadRecipe(Level world, BlockState state) {
        if (world.isClientSide) return;
        cachedRecipes = attachedEntity.getRecipes(index);

        if (cachedRecipes.stream().allMatch(Objects::isNull)) return; // Since it's always going to have at least 1 null, therefore isEmpty would be useless

        for (int i = 0; i < cachedRecipes.size(); i++) {
            OvenRecipe recipe = cachedRecipes.get(i);
            if (recipe == null) continue;
            maxProgress[i] = recipe.getMaxProgress();
        }

        status = CookingStatus.PROCESSING;
        world.setBlockAndUpdate(attachedEntity.getBlockPos(), state.setValue(LIT, true));

        attachedEntity.setChanged();
    }

    public int getLastTicked() {
        return lastTicked;
    }

    public CookingStatus getStatus() {
        return status;
    }

    public void process(Level world, BlockState state) {
        ItemStack first = attachedEntity.getItem(index);

        if (first.isEmpty()) {
            if (attachedEntity.getStatus() != CookingStatus.IDLE) {
                world.setBlockAndUpdate(attachedEntity.getBlockPos(), state.setValue(LIT, false));
                attachedEntity.reset();
            }
            return;
        }

        if (!ItemStack.matches(first, cachedItem)) {
            if (status == CookingStatus.IDLE) {
                attachedEntity.reset();
                cachedItem = first.copy();
                loadRecipe(world, state);
            }
        } else if (!cachedRecipes.isEmpty() && status == CookingStatus.PROCESSING) {
            tickRecipe(world, state);
        }
    }
}