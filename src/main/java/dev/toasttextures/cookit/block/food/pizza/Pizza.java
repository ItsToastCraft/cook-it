package dev.toasttextures.cookit.block.food.pizza;

import dev.toasttextures.cookit.block.entity.PizzaEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class Pizza extends BlockWithEntity implements BlockEntityProvider {
    public Pizza(Settings settings) {
        super(settings);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return Slices.FULL.shape;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        if (world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity) {
            pizzaEntity.setStackNbt(stack);
        }
        return stack;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaEntity(pos, state, false);
    }

    protected enum Slices {
        ONE_SLICE(createCuboidShape(1.0, 0.0, 1.0, 8.0, 2.0, 8.0)),
        TWO_SLICES(createCuboidShape(1.0, 0.0, 1.0, 15.0, 2.0, 8.0)),
        THREE_SLICES(VoxelShapes.union(TWO_SLICES.shape, createCuboidShape(8.0, 0.0, 8.0, 15.0, 2.0, 15.0))),
        FULL(createCuboidShape(1.0, 0.0, 1.0, 15.0, 2.0, 15.0));

        public final VoxelShape shape;

        Slices(VoxelShape shape) {
            this.shape = shape;
        }
    }
}