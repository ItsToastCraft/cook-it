package dev.toasttextures.cookit.block.appliances;

import dev.toasttextures.cookit.block.entity.MicrowaveEntity;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static net.minecraft.state.property.Properties.*;

public class Microwave extends BlockWithEntity {
    private static final VoxelShape NORTH_SOUTH_SHAPE = createCuboidShape(3.0, 0f, 1.0, 13.0, 0.5f, 15.0);
    private static final VoxelShape EAST_WEST_SHAPE = createCuboidShape(1.0, 0f, 3.0, 15.0, 0.5f, 13.0);

    public Microwave(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(OPEN, false)
                .with(LIT, false)
                .with(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, LIT, OPEN);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            default -> EAST_WEST_SHAPE;
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        MicrowaveEntity blockEntity = (MicrowaveEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return ActionResult.SUCCESS;

        ItemStack heldItem = player.getStackInHand(hand);
        boolean open = state.get(OPEN);

        if (!open && heldItem.isEmpty()) {
            toggleDoor(state, world, pos, true);
            world.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS);
        } else if (!heldItem.isEmpty() && open) {
            toggleDoor(state, world, pos, false);
            blockEntity.setStack(0, heldItem.split(1));
        } else if (!blockEntity.getStack(0).isEmpty()) {
            toggleDoor(state, world, pos, false);
            player.getInventory().insertStack(blockEntity.getStack(0));
        } else {
            toggleDoor(state, world, pos, false);
        }

        return ActionResult.SUCCESS;
    }

    private void toggleDoor(BlockState state, World world, BlockPos pos, boolean doorState) {
        if (world.isClient()) return;
        SoundEvent sound = doorState ? SoundEvents.BLOCK_IRON_DOOR_OPEN : SoundEvents.BLOCK_IRON_DOOR_CLOSE;

        world.playSound(null, pos, sound, SoundCategory.BLOCKS);
        world.setBlockState(pos, state.with(OPEN, doorState).with(LIT, doorState && state.get(LIT)), NOTIFY_LISTENERS);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, CookItBlockEntities.MICROWAVE, MicrowaveEntity::tick);
    }

    @Nullable
    @Override
    public MicrowaveEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MicrowaveEntity(pos, state);
    }

    public enum Event implements StringIdentifiable {
        NONE((world, pos) -> {}),
        EXPLOSION((world, pos) -> world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3, World.ExplosionSourceType.BLOCK));

        private final BiConsumer<ServerWorld, BlockPos> processor;

        Event(BiConsumer<ServerWorld, BlockPos> processor) {
            this.processor = processor;
        }

        public void apply(ServerWorld world, BlockPos pos) {
            this.processor.accept(world, pos);
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }
}
