package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class PlateEntity extends Container implements DefaultedInventory {
    public PlateEntity(BlockPos pos, BlockState state) {
        this(CookItBlockEntities.PLATE, pos, state);
    }

    public PlateEntity(BlockEntityType<? extends PlateEntity> blockEntity, BlockPos pos, BlockState state) {
        super(blockEntity, pos, state, 1);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }
}