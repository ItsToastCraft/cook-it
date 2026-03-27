package dev.toasttextures.cookit.block.containers;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

public class Bowl extends Block {
    private final DyeColor color;
    public Bowl(Properties settings, DyeColor color) {
        super(settings);
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return box(4.0, 0.0, 4.0, 12.0, 5.0, 12.0);
    }
}
