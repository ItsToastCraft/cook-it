package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class Slot<E extends BlockEntity> {
    public final Vec3 pos;
    protected final E attachedEntity;
    public final int index;

    public Slot(Vec3 pos, E attachedEntity, int index) {
        this.pos = pos;
        this.attachedEntity = attachedEntity;
        this.index = index;
    }
}
