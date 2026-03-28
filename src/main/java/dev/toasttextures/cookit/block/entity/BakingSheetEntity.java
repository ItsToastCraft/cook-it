package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class BakingSheetEntity extends Container {
    public BakingSheetEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.BAKING_SHEET, pos, state, 8);
    }
}