package com.toast.cookit.block.entity;


import com.toast.cookit.block.ImplementedInventory;
import com.toast.cookit.registries.CookItBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PizzaPanEntity extends CookingBlockEntity implements ImplementedInventory {


    public PizzaPanEntity(BlockPos pos, BlockState state) { super(CookItBlockEntities.PIZZA_PAN_ENTITY, pos, state, 1); }


}

