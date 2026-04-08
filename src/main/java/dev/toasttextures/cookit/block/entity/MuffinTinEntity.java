package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import dev.toasttextures.cookit.registries.CookItTags;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
            NonNullList<ItemStack> items = Container.getItems(stack);
            if (items.isEmpty()) return;
            ItemStack goop = items.get(0);
            if (!goop.is(CookItItems.GOOP)) return;

            if (interactionPos != null) {
                fillAt(player, interactionPos, goop.split(1));
                // Idk I might have to do further testing
                ContainerHelper.saveAllItems(stack.getOrCreateTagElement(CONTAINER_KEY), items);
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
}