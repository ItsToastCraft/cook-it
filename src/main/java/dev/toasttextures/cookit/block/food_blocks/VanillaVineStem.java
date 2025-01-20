package dev.toasttextures.cookit.block.food_blocks;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.block.*;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

import java.util.Objects;

public class VanillaVineStem extends AbstractPlantStemBlock implements Fertilizable, VanillaVines {

    public static final MapCodec<VanillaVineStem> CODEC = createCodec(VanillaVineStem::new);

    private static final float GROW_CHANCE = 0.15F;


    public VanillaVineStem(Settings settings) {
        super(settings, Direction.DOWN, EAST, false, GROW_CHANCE);
        setDefaultState(getDefaultState().with(AGE, 0).with(PLANT_STATE, 0).with(Properties.HORIZONTAL_FACING, Direction.EAST));

    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE).add(PLANT_STATE).add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public MapCodec<VanillaVineStem> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockState copyState(BlockState from, BlockState to) {
        return to.with(PLANT_STATE, from.get(PLANT_STATE)).with(Properties.HORIZONTAL_FACING, from.get(Properties.HORIZONTAL_FACING));
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
        Direction direction = state.get(Properties.HORIZONTAL_FACING);
        return this.canPlaceOn(world, pos.offset(direction.getOpposite()), direction) || blockState.isOf(this.getStem()) || blockState.isOf(this.getPlant());
    }

    @Override
    protected boolean chooseStemState(BlockState state) {
        return state.isAir();
    }

    @Override
    protected BlockState age(BlockState state, Random random) {
        return super.age(state, random).with(PLANT_STATE, random.nextFloat() < GROW_CHANCE ? 1 : 0);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx)).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state.with(PLANT_STATE, state.get(PLANT_STATE) + 1), Block.NOTIFY_LISTENERS);

        if (state.get(PLANT_STATE) == 1) {
            CookIt.LOGGER.info("Scheduling tick in stem...");
            world.scheduleBlockTick(pos, this, random.nextBetween(50, 100));
        }
    }
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        CookIt.LOGGER.info("Tick arrived!");
        if (state.get(PLANT_STATE) == 1) {

            world.setBlockState(pos, state.with(PLANT_STATE, 2));
        }
        super.scheduledTick(state, world, pos, random);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction dir = state.get(Properties.HORIZONTAL_FACING);
        switch (dir) {
            case NORTH -> { return NORTH; }
            case WEST -> { return WEST; }
            case SOUTH -> { return SOUTH; }
            default -> { return EAST; }
        }
    }
}
