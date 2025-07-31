package dev.toasttextures.cookit.block.containers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class Bowl extends Block {
    private final DyeColor color;
    public Bowl(Settings settings, DyeColor color) {
        super(settings);
        this.color = color;
    }

    public String getColor() {
        return color.getName();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.25f, 0f, 0.25f, 0.75f, 0.3125f, 0.75f);
    }
}
