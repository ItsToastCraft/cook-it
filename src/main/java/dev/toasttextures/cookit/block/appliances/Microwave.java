package dev.toasttextures.cookit.block.appliances;

import dev.toasttextures.cookit.block.entity.MicrowaveEntity;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class Microwave extends BaseEntityBlock {
    private static final VoxelShape NORTH_SOUTH_SHAPE = box(1.0, 0.0, 3.0, 15.0, 8.0, 13.0);
    private static final VoxelShape EAST_WEST_SHAPE = box(3.0, 0.0, 1.0, 13.0, 8.0, 15.0);

    public Microwave(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
            .setValue(HORIZONTAL_FACING, Direction.NORTH)
            .setValue(LIT, false)
            .setValue(OPEN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, LIT, OPEN);
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

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof MicrowaveEntity blockEntity)) return InteractionResult.PASS;
        ItemStack heldItem = player.getItemInHand(hand);
        boolean open = state.getValue(OPEN);

        if (!open && heldItem.isEmpty()) {
            toggleDoor(state, world, pos, true);
            return InteractionResult.SUCCESS;
        }

        if (!heldItem.isEmpty() && open) {
            blockEntity.fillFirst(player, heldItem);
        } else if (!blockEntity.getFirst().isEmpty()) {
            player.getInventory().placeItemBackInInventory(blockEntity.getFirst());
        }
        toggleDoor(state, world, pos, false);

        return InteractionResult.SUCCESS;
    }

    private void toggleDoor(BlockState state, Level world, BlockPos pos, boolean doorState) {
        if (world.isClientSide()) return;
        SoundEvent sound = doorState ? CookItSounds.MICROWAVE_OPEN : CookItSounds.MICROWAVE_CLOSE;

        world.playSound(null, pos, sound, SoundSource.BLOCKS);
        world.setBlockAndUpdate(pos, state.setValue(OPEN, doorState).setValue(LIT, doorState && state.getValue(LIT)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, CookItBlockEntities.MICROWAVE, MicrowaveEntity::tick);
    }

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
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}