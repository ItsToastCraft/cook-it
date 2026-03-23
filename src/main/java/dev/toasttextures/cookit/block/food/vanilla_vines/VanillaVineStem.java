package dev.toasttextures.cookit.block.food.vanilla_vines;

import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.block.*;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.Objects;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class VanillaVineStem extends AbstractPlantStemBlock implements Fertilizable, VanillaVines {

    public VanillaVineStem(Settings settings) {
        super(settings, Direction.DOWN, EAST, false, GROW_CHANCE);
        setDefaultState(getDefaultState()
                .with(AGE, 0)
                .with(PLANT_STATE, Stage.EMPTY)
                .with(HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, PLANT_STATE, HORIZONTAL_FACING);
    }

    @Override
    protected BlockState copyState(BlockState from, BlockState to) {
        return to.with(PLANT_STATE, from.get(PLANT_STATE)).with(HORIZONTAL_FACING, from.get(HORIZONTAL_FACING));
    }

    @Override
    protected Block getPlant() {
        return CookItBlocks.VANILLA_VINE;
    }

    @Override
    protected int getGrowthLength(Random random) {
        return 1;
    }

    private boolean canPlaceOn(BlockView world, BlockPos pos, Direction side) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isSideSolidFullSquare(world, pos, side);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.offset(this.growthDirection.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        Direction direction = state.get(HORIZONTAL_FACING);
        return this.canPlaceOn(world, pos.offset(direction.getOpposite()), direction) || blockState.isOf(this.getStem()) || blockState.isOf(this.getPlant());
    }

    @Override
    protected boolean chooseStemState(BlockState state) {
        return state.isAir();
    }

    @Override
    protected BlockState age(BlockState state, Random random) {
        VanillaVines.Stage stage = state.get(PLANT_STATE);
        return super.age(state, random).with(PLANT_STATE, random.nextFloat() < GROW_CHANCE ? stage.increment() : stage);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx)).with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return false;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        super.neighborUpdate(state, world, pos, block, neighborPos, moved);

        BlockPos support = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());

        boolean invalidSupport = support.equals(neighborPos) && !world.getBlockState(support).isSolidBlock(world, support);
        boolean brokenOrigin = neighborPos.equals(pos.up()) && world.getBlockState(neighborPos).getBlock().equals(Blocks.AIR);
        if (invalidSupport || brokenOrigin) {
            world.removeBlock(pos, false);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state.get(HORIZONTAL_FACING));
    }
}