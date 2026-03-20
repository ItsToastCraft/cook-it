package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.WoodType;
import dev.toasttextures.cookit.block.entity.CuttingBoardEntity;
import dev.toasttextures.cookit.registries.CookItItems;
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
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CuttingBoard extends HorizontalFacingBlock implements BlockEntityProvider {
    private final WoodType type;

    public CuttingBoard(Settings settings, WoodType type) {
        super(settings);
        this.type = type;
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST));
    }

    public WoodType getType() {
        return type;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return ActionResult.PASS;

        ItemStack heldItem = player.getStackInHand(hand);

        if (heldItem.isOf(CookItItems.FRYER_BASKET)) return ActionResult.PASS;

        if (heldItem.isEmpty()) {
            if (!blockEntity.processRecipe(heldItem, false)) {
                blockEntity.retrieve();
                blockEntity.reset();
            }
            return ActionResult.SUCCESS;
        }
        if (blockEntity.isEmpty()) {
            blockEntity.setStack(0, heldItem.split(1));
        } else if (!blockEntity.processRecipe(heldItem, false)) {
            pickUpCookingBoardItems(state, world, pos, player);
        }

        return ActionResult.SUCCESS;
    }

    // pickups items, boolean used to cancel the block break if the block wasn't empty
    public void pickUpCookingBoardItems(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity != null && !blockEntity.isEmpty() && player.isSneaking()) {
            player.getInventory().insertStack(blockEntity.getStack(0));
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
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient && world.getBlockEntity(pos) instanceof CuttingBoardEntity cuttingBoardEntity && !cuttingBoardEntity.isEmpty()) {
            return;
        }
        super.onBreak(world, pos, state, player);
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
        Direction dir = state.get(FACING);
        return switch (dir) {
            case EAST, WEST -> VoxelShapes.cuboid(0.125f, 0.0f, 0.0f, 0.875f, 0.0625f, 1.0f);
            case NORTH, SOUTH -> VoxelShapes.cuboid(0.0f, 0.0f, 0.125f, 1.0f, 0.0625f, 0.875f);
            default -> VoxelShapes.fullCube();
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