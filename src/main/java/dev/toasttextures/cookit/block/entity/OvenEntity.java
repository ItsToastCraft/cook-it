package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.recipes.OvenRecipe;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItItems;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

import static dev.toasttextures.cookit.block.entity.MixingBowlEntity.OUTPUT_KEY;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.OPEN;

public class OvenEntity extends CookingBlockEntity<OvenRecipe> implements SlotProvider<OvenSlot> {
    private int[][] progress = new int[2][8];
    private OvenSlot completed = null;

    private final List<OvenSlot> slots;
    public OvenEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.OVEN, OvenRecipe.Type.INSTANCE, pos, state, 2);
        Direction dir = state.getValue(HORIZONTAL_FACING);
        Pair<Vec3, Vec3> slots = OvenSlot.rotated(dir);

        this.slots = List.of(
            createSlot(slots.right(), this, 0),
            createSlot(slots.left(), this, 1)
        );
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        ListTag list = nbt.getList(PROGRESS_KEY, CompoundTag.TAG_INT_ARRAY);

        for (int i = 0; i < list.size(); i++) {
            progress[i] = list.getIntArray(i);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (int[] row : progress) {
            list.add(new IntArrayTag(row));
        }

        nbt.put(PROGRESS_KEY, list);
        super.saveAdditional(nbt);
    }

    @Override
    public void craft(Level world, OvenRecipe recipe) {
        if (completed == null || recipe == null || completed.getLastTicked() == -1) return;
        ItemStack container = items.get(completed.index);
        if (!container.isEmpty() && completed.getStatus() == CookingStatus.DONE) {
            NonNullList<ItemStack> stored = Container.getItems(container);
            ItemStack lastTicked = stored.get(completed.getLastTicked());
            ItemStack output = recipe.assemble(new SimpleContainer(lastTicked), world.registryAccess());

            if (!lastTicked.is(CookItItems.GOOP) && lastTicked.hasTag()) {
                output.setTag(lastTicked.getTag());
            }

            stored.set(completed.getLastTicked(), output);
            ContainerHelper.saveAllItems(container.getTagElement(CONTAINER_KEY), stored);

            setItem(completed.index, container);
            setChanged();
        }
    }

    @Override
    public void reset() {
        if (completed == null) return;
        progress[completed.index][completed.getLastTicked()] = 0;
        completed = null;
        setChanged();
    }

    void updateStatus(OvenSlot slot) {
        completed = slot;
        CookingStatus first = slots.get(0).getStatus();
        CookingStatus second = slots.get(1).getStatus();

        if (first == second) {
            status = first;
            return;
        }

        List<CookingStatus> statuses = List.of(first, second);
        if (statuses.contains(CookingStatus.PROCESSING)) {
            status = CookingStatus.PROCESSING;
        } else if (statuses.contains(CookingStatus.DONE)) {
            status = CookingStatus.DONE;
        } else {
            status = CookingStatus.IDLE;
        }
    }

    @Override
    public List<OvenSlot> getSlots() {
        return slots;
    }

    @Override
    public <E extends BlockEntity> OvenSlot createSlot(Vec3 pos, E entity, int index) {
        return new OvenSlot(pos, (OvenEntity) entity, index);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, OvenEntity entity) {
        if (state.getValue(OPEN)) return;

        if (!world.isClientSide) {
            for (OvenSlot slot : entity.getSlots()) {
                slot.process(world, state);
            }
        }
    }

    public int[] getProgress(int index) {
        return progress[index];
    }

    public void addProgress(OvenSlot slot, int index) {
        progress[slot.index][index]++;
    }

    @Override
    public List<OvenRecipe> getRecipes(int slot) {
        if (!Container.isContainer(getItem(slot))) return Collections.emptyList();

        List<ItemStack> items = Container.getItems(getItem(slot));
        SimpleContainer inv;
        List<OvenRecipe> collected = new ArrayList<>(8);

        if (level == null) return Collections.emptyList();

        itemLoop:
        for (ItemStack item : items) {
            inv = new SimpleContainer(item);

            if (item.is(CookItItems.GOOP)) {
                List<OvenRecipe> recipes = level.getRecipeManager().getRecipesFor(OvenRecipe.Type.INSTANCE, inv, level);

                CompoundTag root = item.getTag();
                if (root == null) continue;
                CompoundTag nbt = root.getCompound(OUTPUT_KEY);
                if (nbt.isEmpty()) continue;
                ItemStack stored = ItemStack.of(nbt);
                for (OvenRecipe recipe : recipes) {

                    if (ItemStack.isSameItem(recipe.getResultItem(null), stored)) {
                        collected.add(recipe);
                        continue itemLoop;
                    }
                }
                collected.add(null);
            } else {
                Optional<OvenRecipe> recipe = level.getRecipeManager().getRecipeFor(OvenRecipe.Type.INSTANCE, inv, level);
                collected.add(recipe.orElse(null)); // I'm so evil who cares about optionals???
            }
        }

        return collected;
    }
    @Override
    public ItemStack retrieve(@NotNull Vec3 interactionPos, Predicate<Item> exclusions) {
        Optional<OvenSlot> slotOpt = getSlotAt(interactionPos);

        if (slotOpt.isEmpty()) return ItemStack.EMPTY;

        return slotOpt.get().getStatus() != CookingStatus.PROCESSING ? super.retrieve(exclusions) : ItemStack.EMPTY;
    }
}