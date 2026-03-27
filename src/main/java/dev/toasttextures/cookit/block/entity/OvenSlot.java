package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.OvenRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.toasttextures.cookit.CookIt.DIRECTION_TO_FLOAT;
import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class OvenSlot extends Slot<OvenEntity> {
    private static final Map<Direction, List<Vec3>> DIR_TO_POS = new HashMap<>();
    private static final Vec3 FIRST = new Vec3(0.0,0.0,0.0);
    private static final Vec3 SECOND = new Vec3(0.0,0.0,0.0);

    private int maxProgress;
    private ItemStack cachedItem = ItemStack.EMPTY;
    private OvenRecipe cachedRecipe = null;
    private CookingStatus status = CookingStatus.IDLE;

    public OvenSlot(Vec3 pos, OvenEntity attachedEntity, int index) {
        super(pos, attachedEntity, index);
    }

    public static List<Vec3> rotated(Direction dir) {
        return DIR_TO_POS.computeIfAbsent(dir, direction -> {
            float value = DIRECTION_TO_FLOAT.getOrDefault(direction, 0.0f);
            return List.of(
                    FIRST.xRot(value),
                    SECOND.xRot(value)
            );
        });
    }

    private void tickRecipe(Level world, BlockState state) {
        if (maxProgress <= attachedEntity.getProgress(index)) {
            status = CookingStatus.DONE;
            if (cachedRecipe == null && !world.isClientSide) {
                world.setBlock(attachedEntity.getBlockPos(), state.setValue(LIT, false), UPDATE_CLIENTS);
                attachedEntity.updateStatus(this);
                attachedEntity.craft(world, cachedRecipe);
            }
            attachedEntity.reset();
        } else {
            attachedEntity.addProgress(this);
        }
    }

    private void loadRecipe(Level world, BlockState state) {
        if (world.isClientSide) return;
        List<OvenRecipe> recipes = attachedEntity.getRecipes(index);
        if (recipes.isEmpty()) return;
        cachedRecipe = recipes.get(0);

        if (cachedRecipe == null) return;

        status = CookingStatus.PROCESSING;
        world.setBlock(attachedEntity.getBlockPos(), state.setValue(LIT, true), UPDATE_CLIENTS);
        maxProgress = cachedRecipe.getMaxProgress();
        attachedEntity.setChanged();
    }

    public CookingStatus getStatus() {
        return status;
    }

    public void process(Level world, BlockState state) {
        ItemStack stack = attachedEntity.getItem(index);
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