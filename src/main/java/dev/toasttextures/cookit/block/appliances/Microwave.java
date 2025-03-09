package dev.toasttextures.cookit.block.appliances;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.MicrowaveEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

public class Microwave extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final MapCodec<Microwave> CODEC = createCodec(Microwave::new);

    public Microwave(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(ON, false).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN, ON, Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL /*that does something*/ ;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(Properties.HORIZONTAL_FACING)) {
            case EAST, WEST -> VoxelShapes.cuboid(0.1875f, 0f, 0.0625f, 0.8125f, 0.5f, 0.9375f);
            default -> VoxelShapes.cuboid(0.0625f, 0f, 0.1875f, 0.9375f, 0.5f, 0.8125f);
        };
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        boolean open = state.get(OPEN);
        MicrowaveEntity blockEntity = (MicrowaveEntity) world.getBlockEntity(pos);

        if (world.isClient || blockEntity == null) {
            return ItemActionResult.SUCCESS;
        }
        if (!open && stack.isEmpty()) {
            world.setBlockState(pos, state.with(OPEN, true));
            world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS);
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else if (!stack.isEmpty() && open) {
            world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS);
            world.setBlockState(pos, state.with(OPEN, false));
            blockEntity.setStack(0, stack.splitUnlessCreative(1, player));
        } else if (!blockEntity.getStack(0).isEmpty()){
            world.setBlockState(pos, state.with(OPEN, false).with(ON, false));
            player.getInventory().insertStack(blockEntity.getStack(0));
        } else {
            world.setBlockState(pos, state.with(OPEN, false).with(ON, false));
        }
        return ItemActionResult.SUCCESS;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, CookItBlockEntities.MICROWAVE_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world, pos, state));
    }

    @Nullable
    @Override
    public MicrowaveEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MicrowaveEntity(pos, state);
    }
}