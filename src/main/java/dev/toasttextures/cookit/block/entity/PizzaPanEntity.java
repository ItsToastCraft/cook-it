package dev.toasttextures.cookit.block.entity;


import dev.toasttextures.cookit.block.ImplementedInventory;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PizzaPanEntity extends CookingBlockEntity implements ImplementedInventory {


    public PizzaPanEntity(BlockPos pos, BlockState state) { super(CookItBlockEntities.PIZZA_PAN_ENTITY, pos, state, 1); }


}

