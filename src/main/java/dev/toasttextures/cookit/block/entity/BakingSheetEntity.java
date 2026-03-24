package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class BakingSheetEntity extends Container implements DefaultedInventory {
    public BakingSheetEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.BAKING_SHEET, pos, state, 8);
    }
}