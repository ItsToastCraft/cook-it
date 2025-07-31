package dev.toasttextures.cookit.block.food_blocks;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class VanillaVinePlant extends AbstractPlantBlock implements Fertilizable, VanillaVines {
    public static final MapCodec<VanillaVinePlant> CODEC = createCodec(VanillaVinePlant::new);

    @Override
    public MapCodec<VanillaVinePlant> getCodec() {
        return CODEC;
    }

    public VanillaVinePlant(Settings settings) {
        super(settings, Direction.DOWN, EAST, false);
        this.setDefaultState(this.stateManager.getDefaultState().with(PLANT_STATE, 0).with(Properties.HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    protected AbstractPlantStemBlock getStem() {
        return (AbstractPlantStemBlock) CookItBlocks.VANILLA_VINE_STEM;
    }

    @Override
    protected BlockState copyState(BlockState from, BlockState to) {
        return to.with(PLANT_STATE, from.get(PLANT_STATE)).with(Properties.HORIZONTAL_FACING, from.get(Properties.HORIZONTAL_FACING));
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return new ItemStack(CookItItems.VANILLA_BEAN);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return VanillaVines.removeVanilla(player, state, world, pos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PLANT_STATE).add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return state.get(PLANT_STATE) < 2;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if (state.get(PLANT_STATE) == 0) {
            world.setBlockState(pos, state.with(PLANT_STATE, 1), Block.NOTIFY_LISTENERS);
        }
    }
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (state.get(PLANT_STATE) == 1) {

            world.setBlockState(pos, state.with(PLANT_STATE, 2), Block.NOTIFY_LISTENERS);
        }
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