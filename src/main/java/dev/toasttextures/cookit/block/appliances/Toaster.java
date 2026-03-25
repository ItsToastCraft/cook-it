package dev.toasttextures.cookit.block.appliances;

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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import dev.toasttextures.cookit.registries.CookItItems;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class Toaster extends HorizontalFacingBlock {
    private static final VoxelShape NORTH_SOUTH_SHAPE = createCuboidShape(4.0, 0.0, 2.0, 12.0, 7.0, 14.0);
    private static final VoxelShape EAST_WEST_SHAPE = createCuboidShape(2.0, 0.0, 4.0, 14.0, 7.0, 12.0);

    public static final EnumProperty<Stage> STAGE = EnumProperty.of("stage", Stage.class);

    public Toaster(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(HORIZONTAL_FACING, Direction.NORTH)
                .with(STAGE, Stage.EMPTY));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, STAGE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return switch (state.get(HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            default -> EAST_WEST_SHAPE;
        };
    }
    
    // I know this allows you to just put a piece of bread at the last second but like I don't care :cat_plushie:
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        ItemStack heldItem = player.getStackInHand(hand);
        Stage stage = state.get(STAGE);

        if (!stage.isFull() && heldItem.isOf(Items.BREAD)) {
            heldItem.decrement(1);
            world.setBlockState(pos, state.with(STAGE, stage.increment()));
            this.scheduleTick(world, pos);
        }

        if (heldItem.isEmpty() && stage.isToasted()) {
            player.getInventory().insertStack(new ItemStack(CookItItems.TOAST));
            world.setBlockState(pos, state.with(STAGE, stage.retrieve()));

            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.MASTER);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    private void scheduleTick(WorldAccess world, BlockPos pos) {
        if (!world.isClient() && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.scheduleBlockTick(pos, this, 150);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.MASTER);
        world.setBlockState(pos, state.with(STAGE, state.get(STAGE).toast()), NOTIFY_LISTENERS);
    }

    public enum Stage implements StringIdentifiable {
        EMPTY,
        HALF_UNTOASTED,
        FULL_UNTOASTED,
        HALF_TOASTED,
        FULL_TOASTED;

        @Override
        public String asString() {
            return name().toLowerCase();
        }

        public boolean isToasted() {
            return this.ordinal() > 2;
        }

        public boolean isFull() {
            return this == FULL_TOASTED || this == FULL_UNTOASTED;
        }

        public Stage increment() {
            return switch (this) {
                case EMPTY -> HALF_UNTOASTED;
                case HALF_UNTOASTED -> FULL_UNTOASTED;
                default -> this;
            };
        }

        public Stage toast() {
            return switch (this) {
                case HALF_UNTOASTED -> HALF_TOASTED;
                case FULL_UNTOASTED -> FULL_TOASTED;
                default -> this;
            };
        }

        public Stage retrieve() {
            return switch (this) {
                case HALF_TOASTED -> EMPTY;
                case FULL_TOASTED -> HALF_TOASTED;
                default -> this;
            };
        }
    }
}