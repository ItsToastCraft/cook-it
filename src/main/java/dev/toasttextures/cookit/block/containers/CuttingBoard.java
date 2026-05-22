package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.WoodType;
import dev.toasttextures.cookit.block.entity.CuttingBoardEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class CuttingBoard extends BaseEntityBlock {
    private static final VoxelShape NORTH_SOUTH_SHAPE = box(0.0, 0.0, 2.0, 16.0, 1.0, 14.0);
    private static final VoxelShape EAST_WEST_SHAPE = box(2.0, 0.0, 0.0, 14.0, 1.0, 16.0);

    private final WoodType type;

    public CuttingBoard(Properties settings, WoodType type) {
        super(settings);
        this.type = type;
        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.EAST));
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
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

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof CuttingBoardEntity blockEntity)) return InteractionResult.PASS;

        ItemStack heldItem = player.getItemInHand(hand);
        if (player.isCrouching()) {
            pickUpCookingBoardItems(world, pos, player);
            return InteractionResult.SUCCESS;
        }
        if (heldItem.isEmpty()) {
            if (!blockEntity.process(world, heldItem, false)) {
                pickUpCookingBoardItems(world, pos, player);
            }
            return InteractionResult.SUCCESS;
        } else if (heldItem.is(CookItItems.FRYER_BASKET)) return InteractionResult.PASS;

        if (blockEntity.isEmpty()) {
            blockEntity.fillFirst(player, heldItem);
        } else if (!blockEntity.process(world, heldItem, false)) {
            pickUpCookingBoardItems(world, pos, player);
        }

        return InteractionResult.SUCCESS;
    }

    // picks up items, boolean used to cancel the block break if the block wasn't empty
    public void pickUpCookingBoardItems(Level world, BlockPos pos, Player player) {
        if (world.getBlockEntity(pos) instanceof CuttingBoardEntity blockEntity && player.isCrouching()) {
            player.getInventory().placeItemBackInInventory(blockEntity.retrieve());
            blockEntity.reset();
        }
    }

    // cancels particles
    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.isClientSide && world.getBlockEntity(pos) instanceof CuttingBoardEntity cuttingBoardEntity && !cuttingBoardEntity.isEmpty() && player.isCreative()) {
            return;
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    // pick up item in survival before break
    @SuppressWarnings("deprecation")
    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        if (world.isClientSide) return;

        if (world.getBlockEntity(pos) instanceof CuttingBoardEntity blockEntity) {
            blockEntity.process(world, ItemStack.EMPTY, true);
        }

        super.attack(state, world, pos, player);
    }

    public static boolean beforeBlockBreak(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof CuttingBoardEntity cuttingBoardEntity && player.isCreative() && player.isCrouching()) {
            if (!cuttingBoardEntity.isEmpty()) {
                return !cuttingBoardEntity.process(world, ItemStack.EMPTY, true);
            }
        }
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return Objects.requireNonNull(super.getStateForPlacement(ctx)).setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardEntity(pos, state);
    }
}