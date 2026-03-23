package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Vec3d;

public abstract class Slot<E extends BlockEntity> {
    public final Vec3d pos;
    protected final E attachedEntity;
    public final int index;

    public Slot(Vec3d pos, E attachedEntity, int index) {
        this.pos = pos;
        this.attachedEntity = attachedEntity;
        this.index = index;
    }
}
