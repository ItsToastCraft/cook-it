package dev.toasttextures.cookit.block.appliances;

import dev.toasttextures.cookit.block.entity.FryerEntity;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;
import static net.minecraft.state.property.Properties.LIT;

public class Fryer extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape NORTH_SOUTH_SHAPE = createCuboidShape(3.0, 0f, 1.0, 13.0, 0.5f, 15.0);
    private static final VoxelShape EAST_WEST_SHAPE = createCuboidShape(1.0, 0f, 3.0, 15.0, 0.5f, 13.0);

    public Fryer(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LIT, false).with(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            default -> EAST_WEST_SHAPE;
        };
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        FryerEntity blockEntity = (FryerEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return ActionResult.FAIL;

        ItemStack heldItem = player.getStackInHand(hand);
        blockEntity.transfer(player, heldItem);
        return ActionResult.SUCCESS;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, HORIZONTAL_FACING);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, CookItBlockEntities.FRYER, FryerEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FryerEntity(pos, state);
    }
}