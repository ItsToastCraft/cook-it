package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.OvenRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.toasttextures.cookit.CookIt.DIRECTION_TO_FLOAT;
import static net.minecraft.block.Block.NOTIFY_LISTENERS;
import static net.minecraft.state.property.Properties.LIT;

public class OvenSlot extends Slot<OvenEntity> {
    private static final Map<Direction, List<Vec3d>> DIR_TO_POS = new HashMap<>();
    private static final Vec3d FIRST = new Vec3d(0.0,0.0,0.0);
    private static final Vec3d SECOND = new Vec3d(0.0,0.0,0.0);

    private int maxProgress;
    private ItemStack cachedItem = ItemStack.EMPTY;
    private OvenRecipe cachedRecipe = null;
    private CookingStatus status = CookingStatus.IDLE;

    public OvenSlot(Vec3d pos, OvenEntity attachedEntity, int index) {
        super(pos, attachedEntity, index);
    }

    public static List<Vec3d> rotated(Direction dir) {
        return DIR_TO_POS.computeIfAbsent(dir, direction -> {
            float value = DIRECTION_TO_FLOAT.getOrDefault(direction, 0.0f);
            return List.of(
                    FIRST.rotateX(value),
                    SECOND.rotateX(value)
            );
        });
    }

    private void tickRecipe(World world, BlockState state) {
        if (maxProgress <= attachedEntity.getProgress(index)) {
            status = CookingStatus.DONE;
            if (cachedRecipe == null && !world.isClient) {
                world.setBlockState(attachedEntity.getPos(), state.with(LIT, false), NOTIFY_LISTENERS);
                attachedEntity.updateStatus(this);
                attachedEntity.craft(world, cachedRecipe);
            }
            attachedEntity.reset();
        } else {
            attachedEntity.addProgress(this);
        }
    }

    private void loadRecipe(World world, BlockState state) {
        if (world.isClient) return;
        List<OvenRecipe> recipes = attachedEntity.getRecipes(index);
        if (recipes.isEmpty()) return;
        cachedRecipe = recipes.getFirst();

        if (cachedRecipe == null) return;

        status = CookingStatus.PROCESSING;
        world.setBlockState(attachedEntity.getPos(), state.with(LIT, true), NOTIFY_LISTENERS);
        maxProgress = cachedRecipe.getMaxProgress();
        attachedEntity.markDirty();
    }

    public CookingStatus getStatus() {
        return status;
    }

    public void process(World world, BlockState state) {
        ItemStack stack = attachedEntity.getStack(index);
        if (stack.isEmpty()) return;
        if (status == CookingStatus.INVALID) return;

        if (stack == cachedItem) {
            tickRecipe(world, state);
        } else {
            loadRecipe(world, state);
            cachedItem = stack;
        }
    }
}