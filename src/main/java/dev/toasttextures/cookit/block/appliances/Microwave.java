package dev.toasttextures.cookit.block.appliances;

import dev.toasttextures.cookit.block.entity.MicrowaveEntity;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.world.level.block.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class Microwave extends BaseEntityBlock {
    private static final VoxelShape NORTH_SOUTH_SHAPE = box(3.0, 0f, 1.0, 13.0, 0.5f, 15.0);
    private static final VoxelShape EAST_WEST_SHAPE = box(1.0, 0f, 3.0, 15.0, 0.5f, 13.0);

    public Microwave(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(OPEN, false)
                .setValue(LIT, false)
                .setValue(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, LIT, OPEN);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            default -> EAST_WEST_SHAPE;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        MicrowaveEntity blockEntity = (MicrowaveEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return InteractionResult.SUCCESS;

        ItemStack heldItem = player.getItemInHand(hand);
        boolean open = state.getValue(OPEN);

        if (!open && heldItem.isEmpty()) {
            toggleDoor(state, world, pos, true);
            world.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS);
        } else if (!heldItem.isEmpty() && open) {
            toggleDoor(state, world, pos, false);
            blockEntity.setItem(0, heldItem.split(1));
        } else if (!blockEntity.getItem(0).isEmpty()) {
            toggleDoor(state, world, pos, false);
            player.getInventory().add(blockEntity.getItem(0));
        } else {
            toggleDoor(state, world, pos, false);
        }

        return InteractionResult.SUCCESS;
    }

    private void toggleDoor(BlockState state, Level world, BlockPos pos, boolean doorState) {
        if (world.isClientSide()) return;
        SoundEvent sound = doorState ? SoundEvents.IRON_DOOR_OPEN : SoundEvents.IRON_DOOR_CLOSE;

        world.playSound(null, pos, sound, SoundSource.BLOCKS);
        world.setBlock(pos, state.setValue(OPEN, doorState).setValue(LIT, doorState && state.getValue(LIT)), UPDATE_CLIENTS);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CookItBlockEntities.MICROWAVE, MicrowaveEntity::tick);
    }

    @Nullable
    @Override
    public MicrowaveEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MicrowaveEntity(pos, state);
    }

    public enum Event implements StringRepresentable {
        NONE((world, pos) -> {}),
        EXPLOSION((world, pos) -> world.explode(null, pos.getX(), pos.getY(), pos.getZ(), 3, Level.ExplosionInteraction.BLOCK));

        private final BiConsumer<ServerLevel, BlockPos> processor;

        Event(BiConsumer<ServerLevel, BlockPos> processor) {
            this.processor = processor;
        }

        public void apply(ServerLevel world, BlockPos pos) {
            this.processor.accept(world, pos);
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
