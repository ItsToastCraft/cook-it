package dev.toasttextures.cookit.block.appliances;


import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import dev.toasttextures.cookit.registries.CookItItems;


public class Toaster extends HorizontalFacingBlock {
    // New blockstate (yippee) | 0 = no bread, 1 = 1 bread, 2 = 2 bread, 3 = 1 toasted, 4 = 2 toasted

    public static IntProperty TOASTER_STATE = IntProperty.of("toaster_state", 0, 4);

    public Toaster(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH).with(TOASTER_STATE, 0));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
        builder.add(Toaster.TOASTER_STATE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {

        Direction dir = state.get(FACING);
        return switch (dir) {
            case NORTH, SOUTH -> VoxelShapes.cuboid(0.25f, 0f, 0.125f, 0.75f, 0.4375f, 0.875f);
            case EAST, WEST -> VoxelShapes.cuboid(0.125f, 0f, 0.25f, 0.875f, 0.4375f, 0.75f);
            default -> VoxelShapes.fullCube();
        };

    }

    // I know this allows you to just put a piece of bread at the last second but like I don't care :cat_plushie:
    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        int toastState = state.get(TOASTER_STATE);
        // Check if the player is holding bread
        if (heldItem.getItem() == Items.BREAD) {

            // Increment TOASTER_STATE only if it is less than 2 ( if there's no toasted bread)
            if (toastState < 2) {
                heldItem.decrement(1);
                world.setBlockState(pos, state.with(TOASTER_STATE, toastState + 1));
                this.scheduleTick(world, pos);
            }

            return ItemActionResult.SUCCESS;
        }
        if (heldItem.getItem() == Items.AIR) {

            if (toastState > 2) {
                player.getInventory().insertStack(new ItemStack(CookItItems.TOAST));
                world.setBlockState(pos, state.with(TOASTER_STATE, toastState - 1));
                if (toastState == 3) {
                    world.setBlockState(pos, state.with(TOASTER_STATE, 0));
                }
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; // If not holding bread, do nothing
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    private void scheduleTick(WorldAccess world, BlockPos pos) {
        if (!world.isClient() && !world.getBlockTickScheduler().isQueued(pos, this)) {

            world.scheduleBlockTick(pos, this, 200);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int toastState = state.get(TOASTER_STATE);
        if (!world.isClient) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.MASTER, 1f, 1f);
        }
        if (toastState == 1) {
            world.setBlockState(pos, state.with(TOASTER_STATE, 3));
        } else if (toastState == 2) {
            world.setBlockState(pos, state.with(TOASTER_STATE, 4));
        }
    }

}