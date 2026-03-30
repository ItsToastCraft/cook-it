package dev.toasttextures.cookit.block.containers;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class LargePlate extends Plate {
    private static final VoxelShape[] SHAPES = IntStream.rangeClosed(1, 4)
            .mapToObj(i -> box(2.0, 0.0, 2.0, 14.0, i, 14.0)).toArray(VoxelShape[]::new);

    public LargePlate(Properties settings, DyeColor color) {
        super(settings, color);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPES[state.getValue(COUNT) - 1];
    }
}