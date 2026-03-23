package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class MuffinTinEntity extends Container {
    public MuffinTinEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MUFFIN_TIN, pos, state, 6);
    }
}