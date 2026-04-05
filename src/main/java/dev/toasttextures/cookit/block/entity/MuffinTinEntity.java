package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import dev.toasttextures.cookit.registries.CookItTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MuffinTinEntity extends Container implements Transferable, SlotProvider<Slot<MuffinTinEntity>> {
    protected static final List<Vec3> INITIAL_SLOTS = List.of(
            new Vec3(0.3125, 0.0, 0.25),
            new Vec3(0.3125, 0.0, 0.5),
            new Vec3(0.3125, 0.0, 0.75),
            new Vec3(0.625, 0.0, 0.25),
            new Vec3(0.625, 0.0, 0.5),
            new Vec3(0.625, 0.0, 0.75)
    );

    private final ArrayList<Slot<MuffinTinEntity>> slots;
    public MuffinTinEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MUFFIN_TIN, pos, state, 6);
        slots = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            slots.add(createSlot(INITIAL_SLOTS.get(i), this, i));
        }
    }

    @Override
    public void attemptTransfer(Player player, ItemStack stack, @Nullable Vec3 interactionPos) {
        if (stack.is(CookItTags.MUFFIN) && interactionPos != null) {
            fillAt(player, interactionPos, stack);
        } else if (stack.is(CookItBlocks.MIXING_BOWL.asItem())) {
            ListTag items = Container.getItemList(stack);
            if (items.isEmpty()) return;
            ItemStack goop = ItemStack.of((CompoundTag) items.get(0));
            if (!goop.is(CookItItems.GOOP)) return;

            if (interactionPos != null) {
                fillAt(player, interactionPos, goop);
                // Idk I might have to do further testing
                items.getCompound(0).putInt("Count", goop.getCount() - 1);
                stack.getOrCreateTag().put(CONTAINER_KEY, items);
            }
        }
    }

    @Override
    public List<Slot<MuffinTinEntity>> getSlots() {
        return slots;
    }

    @Override
    public <E extends BlockEntity> Slot<MuffinTinEntity> createSlot(Vec3 pos, E entity, int index) {
        return new Slot<>(pos, (MuffinTinEntity) entity, index);
    }

    // I'll make it rotational later
//    protected static final Map<Direction, List<Vec3>> ROTATION_CACHE = new HashMap<>(6);
//
//    public static List<Vec3> rotated(Direction dir) {
//        return ROTATION_CACHE.computeIfAbsent(dir, direction -> {
//            float facing = direction.toYRot();
//            List<Vec3> slots = new ArrayList<>(INITIAL_SLOTS.size());
//
//            for (Vec3 slotPos : INITIAL_SLOTS) {
//                slots.add(slotPos.yRot(facing));
//            }
//
//            return slots;
//        });
//    }
}