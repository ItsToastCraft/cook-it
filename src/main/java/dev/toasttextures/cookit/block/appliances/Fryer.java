package dev.toasttextures.cookit.block.appliances;

import dev.toasttextures.cookit.block.entity.FryerEntity;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class Fryer extends BaseEntityBlock implements EntityBlock {
    private static final VoxelShape NORTH_SOUTH_SHAPE = box(3.0, 0.0, 1.0, 13.0, 8.0, 15.0);
    private static final VoxelShape EAST_WEST_SHAPE = box(1.0, 0.0, 3.0, 15.0, 8.0, 13.0);

    public Fryer(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(LIT, false).setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            default -> EAST_WEST_SHAPE;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        FryerEntity blockEntity = (FryerEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return InteractionResult.FAIL;

        ItemStack heldItem = player.getItemInHand(hand);
        blockEntity.transfer(player, heldItem);
        world.sendBlockUpdated(pos, state, state, UPDATE_CLIENTS);

        return InteractionResult.SUCCESS;
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, HORIZONTAL_FACING);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CookItBlockEntities.FRYER, FryerEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FryerEntity(pos, state);
    }
}