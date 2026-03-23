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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class CuttingBoard extends BlockWithEntity {
    private static final VoxelShape NORTH_SOUTH_SHAPE = createCuboidShape(0.0, 0f, 2.0, 16.0, 1.0f, 14.0);
    private static final VoxelShape EAST_WEST_SHAPE = createCuboidShape(2.0, 0f, 0.0, 14.0, 0.5f, 16.0);

    private final WoodType type;

    public CuttingBoard(Settings settings, WoodType type) {
        super(settings);
        this.type = type;
        setDefaultState(getDefaultState().with(HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            default -> EAST_WEST_SHAPE;
        };
    }

    public WoodType getWoodType() {
        return type;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return ActionResult.PASS;

        ItemStack heldItem = player.getStackInHand(hand);

        if (heldItem.isOf(CookItItems.FRYER_BASKET)) return ActionResult.PASS;

        if (heldItem.isEmpty()) {
            if (!blockEntity.process(world, heldItem, false)) {
                blockEntity.retrieve();
                blockEntity.reset();
            }
            return ActionResult.SUCCESS;
        }
        if (blockEntity.isEmpty()) {
            blockEntity.setStack(0, heldItem.split(1));
        } else if (!blockEntity.process(world, heldItem, false)) {
            pickUpCookingBoardItems(state, world, pos, player);
        }

        return ActionResult.SUCCESS;
    }

    // picks up items, boolean used to cancel the block break if the block wasn't empty
    public void pickUpCookingBoardItems(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity != null && !blockEntity.isEmpty() && player.isSneaking()) {
            player.getInventory().insertStack(blockEntity.getStack(0));
            blockEntity.setInteractions(0);
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    public boolean resetRecipe(World world, CuttingBoardEntity blockEntity) {
        if (blockEntity != null && !blockEntity.isEmpty()) {
            return !blockEntity.process(world, ItemStack.EMPTY, true);
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
        if (world.isClient) return;

        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.process(world, ItemStack.EMPTY, true);
        }

        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx)).with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardEntity(pos, state);
    }
}