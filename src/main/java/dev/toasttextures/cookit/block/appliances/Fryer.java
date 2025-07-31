package dev.toasttextures.cookit.block.appliances;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.containers.CookingContainer;
import dev.toasttextures.cookit.block.entity.FryerEntity;
import dev.toasttextures.cookit.item.CookItFood;
import dev.toasttextures.cookit.item.FryerBasket;
import dev.toasttextures.cookit.enums.FoodTypes;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItItems;

import static dev.toasttextures.cookit.registries.CookItProperties.ON;

public class Fryer extends CookingContainer {
    protected static final MapCodec<Fryer> CODEC = createCodec(Fryer::new);

    public Fryer(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ON, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ON, Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(Properties.HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> VoxelShapes.cuboid(0.1875f, 0f, 0.0625f, 0.8125f, 0.5f, 0.9375f);
            default -> VoxelShapes.cuboid(0.0625f, 0f, 0.1875f, 0.9375f, 0.5f, 0.8125f);
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        FryerEntity blockEntity = (FryerEntity) world.getBlockEntity(pos);
        if (world.isClient() || blockEntity == null) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getStackInHand(hand);
        if (blockEntity.isEmpty()) {
            if (heldItem.getItem().equals(CookItItems.FRYER_BASKET)) {
                blockEntity.setStack(0, heldItem.copyAndEmpty());
            }
        } else if (heldItem.getItem() instanceof CookItFood food && food.getFoodType().equals(FoodTypes.FRYING)) {
            ItemStack stack = blockEntity.getStack(0);
            FryerBasket.setItem(stack, heldItem.split(1));
//            blockEntity.setStack(0, ItemStack.EMPTY);
            blockEntity.markDirty();
        } else {
            player.getInventory().insertStack(blockEntity.getStack(0));
        }
        return ActionResult.SUCCESS;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, CookItBlockEntities.FRYER_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world, pos, state));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FryerEntity(pos, state);
    }
}