package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.WoodType;
import dev.toasttextures.cookit.block.entity.CuttingBoardEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class CuttingBoard extends BaseEntityBlock {
    private static final VoxelShape NORTH_SOUTH_SHAPE = box(0.0, 0f, 2.0, 16.0, 1.0f, 14.0);
    private static final VoxelShape EAST_WEST_SHAPE = box(2.0, 0f, 0.0, 14.0, 0.5f, 16.0);

    private final WoodType type;

    public CuttingBoard(Properties settings, WoodType type) {
        super(settings);
        this.type = type;
        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            default -> EAST_WEST_SHAPE;
        };
    }

    public WoodType getWoodType() {
        return type;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return InteractionResult.PASS;

        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.is(CookItItems.FRYER_BASKET)) return InteractionResult.PASS;

        if (heldItem.isEmpty()) {
            if (!blockEntity.process(world, heldItem, false)) {
                blockEntity.retrieve();
                blockEntity.reset();
            }
            return InteractionResult.SUCCESS;
        }
        if (blockEntity.isEmpty()) {
            blockEntity.setItem(0, heldItem.split(1));
        } else if (!blockEntity.process(world, heldItem, false)) {
            pickUpCookingBoardItems(state, world, pos, player);
        }

        return InteractionResult.SUCCESS;
    }

    // picks up items, boolean used to cancel the block break if the block wasn't empty
    public void pickUpCookingBoardItems(BlockState state, Level world, BlockPos pos, Player player) {
        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity != null && !blockEntity.isEmpty() && player.isShiftKeyDown()) {
            player.getInventory().add(blockEntity.getItem(0));
            blockEntity.setInteractions(0);
            world.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
    }

    public boolean resetRecipe(Level world, CuttingBoardEntity blockEntity) {
        if (blockEntity != null && !blockEntity.isEmpty()) {
            return !blockEntity.process(world, ItemStack.EMPTY, true);
        }
        return true;
    }

    // cancels particles
    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.isClientSide && world.getBlockEntity(pos) instanceof CuttingBoardEntity cuttingBoardEntity && !cuttingBoardEntity.isEmpty()) {
            return;
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    // pick up item in survival before break
    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        if (world.isClientSide) return;

        CuttingBoardEntity blockEntity = (CuttingBoardEntity) world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.process(world, ItemStack.EMPTY, true);
        }

        super.attack(state, world, pos, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return Objects.requireNonNull(super.getStateForPlacement(ctx)).setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardEntity(pos, state);
    }
}