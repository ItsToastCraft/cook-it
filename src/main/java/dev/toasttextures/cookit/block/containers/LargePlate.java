package dev.toasttextures.cookit.block.containers;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

public class LargePlate extends Plate {
    public LargePlate(Properties settings, DyeColor color) {
        super(settings, color);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return box(2.0, 0.0, 2.0, 14.0, state.getValue(COUNT), 14.0);
    }
}