package com.toast.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import com.toast.cookit.block.ImplementedInventory;
import com.toast.cookit.registries.CookItBlockEntities;

public class BakingSheetEntity extends CookingBlockEntity implements ImplementedInventory {
    public BakingSheetEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.BAKING_SHEET_ENTITY, pos, state, 8);
    }


}