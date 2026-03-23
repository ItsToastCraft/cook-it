package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public interface SlotProvider<T extends Slot<?>> {
    List<T> getSlots();

    <E extends BlockEntity> T createSlot(Vec3d pos, E entity, int index);
    
    default T getSlotAt(Vec3d entityPos, Vec3d clickPos) {
        return getSlots().getFirst();
        //TODO
    };
}

