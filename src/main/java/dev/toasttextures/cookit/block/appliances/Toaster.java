package dev.toasttextures.cookit.block.appliances;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import dev.toasttextures.cookit.registries.CookItItems;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class Toaster extends HorizontalDirectionalBlock {
    private static final VoxelShape NORTH_SOUTH_SHAPE = box(4.0, 0.0, 2.0, 12.0, 7.0, 14.0);
    private static final VoxelShape EAST_WEST_SHAPE = box(2.0, 0.0, 4.0, 14.0, 7.0, 12.0);

    public static final EnumProperty<Stage> STAGE = EnumProperty.create("stage", Stage.class);

    public Toaster(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(STAGE, Stage.EMPTY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, STAGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(HORIZONTAL_FACING)) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            default -> EAST_WEST_SHAPE;
        };
    }
    
    // I know this allows you to just put a piece of bread at the last second but like I don't care :cat_plushie:
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        ItemStack heldItem = player.getItemInHand(hand);
        Stage stage = state.getValue(STAGE);

        if (!stage.isFull() && heldItem.is(Items.BREAD)) {
            heldItem.shrink(1);
            world.setBlockAndUpdate(pos, state.setValue(STAGE, stage.increment()));
            this.scheduleTick(world, pos);
        }

        if (heldItem.isEmpty() && stage.isToasted()) {
            player.getInventory().add(new ItemStack(CookItItems.TOAST));
            world.setBlockAndUpdate(pos, state.setValue(STAGE, stage.retrieve()));

            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.MASTER);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    private void scheduleTick(LevelAccessor world, BlockPos pos) {
        if (!world.isClientSide() && !world.getBlockTicks().hasScheduledTick(pos, this)) {
            world.scheduleTick(pos, this, 150);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        world.playSound(null, pos, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.MASTER);
        world.setBlock(pos, state.setValue(STAGE, state.getValue(STAGE).toast()), UPDATE_CLIENTS);
    }

    public enum Stage implements StringRepresentable {
        EMPTY,
        HALF_UNTOASTED,
        FULL_UNTOASTED,
        HALF_TOASTED,
        FULL_TOASTED;

        @Override
        public String getSerializedName() {
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