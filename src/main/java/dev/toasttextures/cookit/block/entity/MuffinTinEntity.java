package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.registry.CookItBlockEntities;

public class MuffinTinEntity extends CookingBlockEntity implements ImplementedInventory {
    public MuffinTinEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MUFFIN_TIN_ENTITY, pos, state, 6);
    }
}