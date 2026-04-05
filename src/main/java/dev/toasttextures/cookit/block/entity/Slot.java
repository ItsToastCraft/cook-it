package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class Slot<E extends BlockEntity> {
    public final Vec3 pos;
    protected final E attachedEntity;
    public final int index;

    public Slot(Vec3 pos, E attachedEntity, int index) {
        this.pos = Vec3.atLowerCornerOf(attachedEntity.getBlockPos()).add(pos);
        this.attachedEntity = attachedEntity;
        this.index = index;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "pos=" + pos +
                ", attachedEntity=" + attachedEntity +
                ", index=" + index +
                '}';
    }
}