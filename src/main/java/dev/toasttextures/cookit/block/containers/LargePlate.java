package dev.toasttextures.cookit.block.containers;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class LargePlate extends Plate {
    public LargePlate(Settings settings, DyeColor color) {
        super(settings, color);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return createCuboidShape(2.0, 0.0, 2.0, 14.0, state.get(COUNT), 14.0);
    }
}