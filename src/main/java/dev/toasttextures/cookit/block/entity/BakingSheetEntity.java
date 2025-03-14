package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.registry.CookItBlockEntities;

public class BakingSheetEntity extends CookingBlockEntity implements ImplementedInventory {
    public BakingSheetEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.BAKING_SHEET_ENTITY, pos, state, 8);
    }
}