package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface SlotProvider<T extends Slot<?>> {
    List<T> getSlots();

    <E extends BlockEntity> T createSlot(Vec3 pos, E entity, int index);

    default Optional<T> getSlotAt(Vec3 clickPos) {
        return getSlots().stream()
                .filter(slot -> clickPos.distanceToSqr(slot.pos) < 0.04)
                .min(Comparator.comparingDouble(slot -> clickPos.distanceToSqr(slot.pos)));
    }

    default boolean fillAt(Player player, @NotNull Vec3 interactionPos, ItemStack stack) {
        Optional<T> slotOpt = getSlotAt(interactionPos);

        if (slotOpt.isEmpty()) return false;

        T slot = slotOpt.get();

        if (!(slot.attachedEntity instanceof Container container)) return false; // Idk how you got here if you're not a container
        if (!container.getItem(slot.index).isEmpty()) return false;
        ItemStack inserted = player.isCreative() ? stack.copyWithCount(1) : stack.split(1);
        container.setItem(slot.index, inserted);
        Container.playRetrievalSound(container.getLevel(), container.getBlockPos());
        container.setChanged();
        return true;
    }

    default ItemStack retrieve(@NotNull Vec3 interactionPos, Predicate<Item> exclusions) {
        Optional<T> slotOpt = getSlotAt(interactionPos);

        if (slotOpt.isEmpty()) return ItemStack.EMPTY;

        T slot = slotOpt.get();

        if (!(slot.attachedEntity instanceof Container container)) return ItemStack.EMPTY; // Idk how you got here if you're not a container
        ItemStack retrieved = container.getItem(slot.index);
        if (retrieved.isEmpty() || !exclusions.test(retrieved.getItem())) return ItemStack.EMPTY;
        retrieved = retrieved.split(1);
        container.setChanged();
        Container.playRetrievalSound(container.getLevel(), container.getBlockPos());
        return retrieved;
    }
}