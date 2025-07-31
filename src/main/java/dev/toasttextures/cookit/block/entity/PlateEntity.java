package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class PlateEntity extends CookingBlockEntity implements ImplementedInventory {
    public PlateEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.PLATE_ENTITY, pos, state, 1);
    }
}