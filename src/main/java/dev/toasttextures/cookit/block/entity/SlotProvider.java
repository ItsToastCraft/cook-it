package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface SlotProvider<T extends Slot<?>> {
    List<T> getSlots();

    <E extends BlockEntity> T createSlot(Vec3 pos, E entity, int index);
    
    default T getSlotAt(Vec3 entityPos, Vec3 clickPos) {
        return getSlots().get(0);
    }
}