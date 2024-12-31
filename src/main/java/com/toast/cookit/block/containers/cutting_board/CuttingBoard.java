package com.toast.cookit.block.containers.cutting_board;

import com.mojang.serialization.MapCodec;
import com.toast.cookit.item.FryerBasket;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CuttingBoard extends HorizontalFacingBlock implements BlockEntityProvider {

    public CuttingBoard(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);

    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity == null) {
            return ActionResult.FAIL;
        }
        if (world.isClient) { return ActionResult.SUCCESS; }

        ItemStack heldItem = player.getStackInHand(hand);
        if (heldItem.getItem() instanceof FryerBasket) { return ActionResult.FAIL; }

        if (blockEntity.isEmpty()) {
            if (!heldItem.isEmpty()) {
                blockEntity.setStack(0, heldItem.split(1));
            } else {
                return ActionResult.FAIL;
            }
        } else if (!heldItem.isEmpty()) {
            blockEntity.processRecipe(heldItem, false);
        } else {
            blockEntity.processRecipe(heldItem, player.isSneaking());
        }
        return ActionResult.SUCCESS;
    }

    // pickups items, boolean used to cancel the block break if the block wasn't empty
    public boolean pickUpCookingBoardItems(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity != null && !blockEntity.isEmpty()) {
            player.getInventory().insertStack(blockEntity.getStack(0));
            blockEntity.setClicks(0);
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            return false;
        }
        return true;
    }

    // cancels particles
    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient && world.getBlockEntity(pos) instanceof CuttingBoardEntity cuttingBoardEntity) {
            if (!cuttingBoardEntity.isEmpty()) {
                return state;
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    // pick up item in survival before break
    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClient) {
            pickUpCookingBoardItems(state, world, pos, player);
        }
        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        Direction dir = state.get(FACING);
        return switch (dir) {
            case EAST, WEST -> VoxelShapes.cuboid(0.125f, 0.0f, 0.0f, 0.875f, 0.0625f, 1.0f);
            case NORTH, SOUTH -> VoxelShapes.cuboid(0.0f, 0.0f, 0.125f, 1.0f, 0.0625f, 0.875f);
            default -> VoxelShapes.fullCube();
        };
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardEntity(pos, state);
    }
}