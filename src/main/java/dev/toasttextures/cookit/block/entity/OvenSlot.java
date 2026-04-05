package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.OvenRecipe;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class OvenSlot extends Slot<OvenEntity> {
    private static final Map<Direction, Vec3> OFFSETS = Map.of(
            Direction.NORTH, new Vec3(0, 0, -0.5),
            Direction.SOUTH, new Vec3(0, 0, 0.5),
            Direction.EAST, new Vec3(0.5, 0, 0),
            Direction.WEST, new Vec3(-0.5, 0, 0));

    private static final Map<Direction, Pair<Vec3, Vec3>> ROTATION_CACHE = new HashMap<>();

    private int maxProgress;
    private ItemStack cachedItem = ItemStack.EMPTY;
    private OvenRecipe cachedRecipe = null;
    private CookingStatus status = CookingStatus.IDLE;

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
        ItemStack first = attachedEntity.getItem(index);

        if (!first.isEmpty()) {
            attachedEntity.setItem(index, cachedRecipe.assemble(new SimpleContainer(first), world.registryAccess()));
            attachedEntity.reset();
        }
    }
}