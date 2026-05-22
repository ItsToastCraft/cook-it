package dev.toasttextures.cookit.block.food.vanilla_vines;

import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class VanillaVinePlant extends GrowingPlantBodyBlock implements BonemealableBlock, VanillaVines {

    public VanillaVinePlant(Properties settings) {
        super(settings, Direction.DOWN, EAST, false);
        registerDefaultState(defaultBlockState()
                .setValue(PLANT_STATE, Stage.EMPTY)
                .setValue(HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    protected @NotNull GrowingPlantHeadBlock getHeadBlock() {
        return CookItBlocks.VANILLA_VINE_STEM;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLANT_STATE, HORIZONTAL_FACING);
    }

    @Override
    protected @NotNull BlockState updateHeadAfterConvertedFromBody(BlockState from, BlockState to) {
        return to.setValue(PLANT_STATE, from.getValue(PLANT_STATE)).setValue(HORIZONTAL_FACING, from.getValue(HORIZONTAL_FACING));
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(CookItItems.VANILLA_BEAN);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return VanillaVines.removeVanilla(player, state, world, pos);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, boolean isClient) {
        return state.getValue(PLANT_STATE).ordinal() < 2;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        if (state.getValue(PLANT_STATE) == Stage.EMPTY) {
            world.setBlock(pos, state.setValue(PLANT_STATE, Stage.BLOOMED), Block.UPDATE_CLIENTS);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(state, world, pos, random);
        if (state.getValue(PLANT_STATE) != Stage.BLOOMED) return;

        world.setBlock(pos, state.setValue(PLANT_STATE, Stage.HARVESTABLE), Block.UPDATE_CLIENTS);
    }


    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getOutlineShape(state.getValue(HORIZONTAL_FACING));
    }
}