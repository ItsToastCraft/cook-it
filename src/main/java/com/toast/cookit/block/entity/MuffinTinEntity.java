package com.toast.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import com.toast.cookit.block.ImplementedInventory;
import com.toast.cookit.registries.CookItBlockEntities;

public class MuffinTinEntity extends CookingBlockEntity implements ImplementedInventory {
    public MuffinTinEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MUFFIN_TIN_ENTITY, pos, state, 6);
    }


}