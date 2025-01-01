package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class PlateEntity extends CookingBlockEntity implements ImplementedInventory {

    public PlateEntity(BlockPos pos, BlockState state) {
        this(CookItBlockEntities.PLATE_ENTITY, pos, state);
    }

    public PlateEntity(BlockEntityType<? extends PlateEntity> blockEntity, BlockPos pos, BlockState state) {
        super(blockEntity, pos, state, 1);

    }

}