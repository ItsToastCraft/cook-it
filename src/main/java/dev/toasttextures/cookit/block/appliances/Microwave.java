package dev.toasttextures.cookit.block.appliances;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.containers.CookingContainer;
import dev.toasttextures.cookit.block.entity.MicrowaveEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
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

import static dev.toasttextures.cookit.registries.CookItProperties.ON;
import static dev.toasttextures.cookit.registries.CookItProperties.OPEN;
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class Microwave extends CookingContainer {
    protected static final MapCodec<Microwave> CODEC = createCodec(Microwave::new);

    public Microwave(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(ON, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN, ON, HORIZONTAL_FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(HORIZONTAL_FACING)) {
            case EAST, WEST -> VoxelShapes.cuboid(0.1875f, 0f, 0.0625f, 0.8125f, 0.5f, 0.9375f);
            default -> VoxelShapes.cuboid(0.0625f, 0f, 0.1875f, 0.9375f, 0.5f, 0.8125f);
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        MicrowaveEntity blockEntity = (MicrowaveEntity) world.getBlockEntity(pos);
        if (world.isClient() || blockEntity == null) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getStackInHand(hand);
        boolean open = state.get(OPEN);

        if (open) {
            if (!heldItem.isEmpty()) {
                toggleDoor(state, world, pos, false);
                blockEntity.setStack(0, heldItem.split(1));
            } else if (!blockEntity.getStack(0).isEmpty()) {
                toggleDoor(state, world, pos, false);
                player.getInventory().insertStack(blockEntity.getStack(0));
            }
        } else {
            if (heldItem.isEmpty()) {
               toggleDoor(state, world, pos, true);
            }
        }
        return ActionResult.SUCCESS;
    }

    private void toggleDoor(BlockState state, World world, BlockPos pos, boolean open) {
        SoundEvent event = open ? SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN : SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE;
        world.setBlockState(pos, state.with(OPEN, open), Block.NOTIFY_LISTENERS);
        world.playSound(null, pos, event, SoundCategory.BLOCKS);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, CookItBlockEntities.MICROWAVE_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world, pos, state));
    }

    @Nullable
    @Override
    public MicrowaveEntity createBlockEntity(BlockPos pos, BlockState state) { return new MicrowaveEntity(pos, state); }
}