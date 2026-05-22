package dev.toasttextures.cookit.block.food.vanilla_vines;

import dev.toasttextures.cookit.registries.CookItBlocks;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class VanillaVineStem extends GrowingPlantHeadBlock implements VanillaVines {

    public VanillaVineStem(Properties settings) {
        super(settings, Direction.DOWN, EAST, false, GROW_CHANCE);
        registerDefaultState(defaultBlockState()
                .setValue(AGE, 0)
                .setValue(PLANT_STATE, Stage.EMPTY)
                .setValue(HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, PLANT_STATE, HORIZONTAL_FACING);
    }

    @Override
    protected @NotNull BlockState updateBodyAfterConvertedFromHead(BlockState from, BlockState to) {
        return to.setValue(PLANT_STATE, from.getValue(PLANT_STATE)).setValue(HORIZONTAL_FACING, from.getValue(HORIZONTAL_FACING));
    }

    @Override
    protected @NotNull Block getBodyBlock() {
        return CookItBlocks.VANILLA_VINE;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return 1;
    }

    private boolean canPlaceOn(BlockGetter world, BlockPos pos, Direction side) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isFaceSturdy(world, pos, side);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos blockPos = pos.relative(this.growthDirection.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        Direction direction = state.getValue(HORIZONTAL_FACING);
        return this.canPlaceOn(world, pos.relative(direction.getOpposite()), direction) || blockState.is(this.getHeadBlock()) || blockState.is(this.getBodyBlock());
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    protected @NotNull BlockState getGrowIntoState(BlockState state, RandomSource random) {
        VanillaVines.Stage stage = state.getValue(PLANT_STATE);
        return super.getGrowIntoState(state, random).setValue(PLANT_STATE, random.nextFloat() < GROW_CHANCE ? stage.increment() : stage);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return Objects.requireNonNull(super.getStateForPlacement(ctx)).setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        super.neighborChanged(state, world, pos, block, neighborPos, moved);

        BlockPos support = pos.relative(state.getValue(HORIZONTAL_FACING).getOpposite());

        boolean invalidSupport = support.equals(neighborPos) && !world.getBlockState(support).isRedstoneConductor(world, support);
        boolean brokenOrigin = neighborPos.equals(pos.above()) && world.getBlockState(neighborPos).getBlock().equals(Blocks.AIR);
        if (invalidSupport || brokenOrigin) {
            world.removeBlock(pos, false);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getOutlineShape(state.getValue(HORIZONTAL_FACING));
    }
}