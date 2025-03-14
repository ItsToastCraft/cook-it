package dev.toasttextures.cookit.block.appliances;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.FryerEntity;
import dev.toasttextures.cookit.item.CookItFood;
import dev.toasttextures.cookit.item.FryerBasket;
import dev.toasttextures.cookit.registry.CookItFoodTypes;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
import dev.toasttextures.cookit.registry.CookItBlockEntities;
import dev.toasttextures.cookit.registry.CookItItems;

public class Fryer extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final MapCodec<Fryer> CODEC = createCodec(Fryer::new);

    public Fryer(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ON, false).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
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
            default -> VoxelShapes.cuboid(0.0625f, 0f, 0.1875f, 0.9375f, 0.375f, 0.8125f);
        };
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        FryerEntity entity = (FryerEntity) world.getBlockEntity(pos);
        if (world.isClient || entity == null) {
            return ItemActionResult.SUCCESS;
        }
        if (entity.isEmpty()) {
            if (stack.isOf(CookItItems.FRYER_BASKET)) {
                entity.setStack(0, stack.copyAndEmpty());
            }
        } else if ((stack.getItem() instanceof CookItFood food && food.getFoodType().equals(CookItFoodTypes.FRYING)) || stack.isIn(CookItItems.FRYING)) {
            ItemStack entityStack = entity.getStack(0);
            FryerBasket.setItem(entityStack, stack.splitUnlessCreative(1, player));
            entity.setStack(0, entityStack);
            entity.markDirty();

        } else {
            player.getInventory().offerOrDrop(entity.getStack(0));
        }
        return ItemActionResult.SUCCESS;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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