package dev.toasttextures.cookit.block.containers;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.CuttingBoardEntity;
import dev.toasttextures.cookit.item.FryerBasket;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CuttingBoard extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final MapCodec<CuttingBoard> CODEC = createCodec(CuttingBoard::new);

    public CuttingBoard(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        CuttingBoardEntity entity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (world.isClient || entity == null) {
            return ItemActionResult.SUCCESS;
        }
        // Logic wil be handled by the fryer basket itself
        if (stack.getItem() instanceof FryerBasket) {
            return ItemActionResult.CONSUME;
        }
        if (player.isSneaking()) {
            pickUpCookingBoardItems(state,world,pos,player,entity);
            return ItemActionResult.SUCCESS;
        }
        if (!stack.isEmpty()) {
            if (entity.isEmpty()) {
                entity.setStack(0, stack.splitUnlessCreative(1, player));
            } else {
                entity.processRecipe(stack, false);
            }
        } else if (!entity.processRecipe(stack, false)) {
            pickUpCookingBoardItems(state, world, pos, player, entity);
        }
        return ItemActionResult.SUCCESS;
    }

    // pickup items, boolean used to cancel the block break if the block wasn't empty
    public void pickUpCookingBoardItems(BlockState state, World world, BlockPos pos, PlayerEntity player, CuttingBoardEntity blockEntity) {
        if (blockEntity != null && !blockEntity.isEmpty()) {
            player.getInventory().insertStack(blockEntity.getStack(0).copyAndEmpty());
            blockEntity.setClicks(0);
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    public boolean resetRecipe(CuttingBoardEntity blockEntity) {
        if (blockEntity != null && !blockEntity.isEmpty()) {
            return !blockEntity.processRecipe(ItemStack.EMPTY, true);
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
            CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.processRecipe(ItemStack.EMPTY, true);
            }
        }
        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(Properties.HORIZONTAL_FACING)) {
            case EAST, WEST -> VoxelShapes.cuboid(0.125f, 0.0f, 0.0f, 0.875f, 0.0625f, 1.0f);
            default -> VoxelShapes.cuboid(0.0f, 0.0f, 0.125f, 1.0f, 0.0625f, 0.875f);
        };
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx)).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardEntity(pos, state);
    }
}