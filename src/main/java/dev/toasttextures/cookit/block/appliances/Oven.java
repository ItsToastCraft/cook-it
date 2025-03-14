package dev.toasttextures.cookit.block.appliances;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.OvenEntity;
import dev.toasttextures.cookit.util.BlockEntityUtils;
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
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import dev.toasttextures.cookit.registry.CookItBlockEntities;
import static dev.toasttextures.cookit.registry.CookItBlocks.CONTAINERS;

public class Oven extends BlockWithEntity implements BlockEntityProvider {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    public static final BooleanProperty DONE = BooleanProperty.of("done");

    public Oven(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(DONE, false).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL /*that does something*/ ;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN).add(DONE).add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        OvenEntity entity = (OvenEntity) world.getBlockEntity(pos);

        if (world.isClient || entity == null) {
            return ItemActionResult.SUCCESS;
        }
        boolean open = state.get(OPEN);

        if (stack.isEmpty()) {
            if (!open) {
                moveOvenDoor(world, pos, state, true);
                return ItemActionResult.SUCCESS;
            } else if (state.get(DONE)) {
                return BlockEntityUtils.returnItem(entity, player, world, pos);
            } else {
                moveOvenDoor(world, pos, state, false);
                return ItemActionResult.SUCCESS;
            }
        } else if (CONTAINERS.contains(Block.getBlockFromItem(stack.getItem())) && open) {
            // If the oven is open and the player is holding something, try to put the held item into the oven
            for (int i = 0; i < entity.getItems().size(); i++) {
                if (entity.getStack(i).isEmpty()) {
                    entity.setStack(i, stack.splitUnlessCreative(1, player));
                    break;
                }
            }
        }
        return ItemActionResult.SUCCESS;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    public void moveOvenDoor(World world, BlockPos pos, BlockState state, boolean open) {
        SoundEvent sound = open ? SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN : SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE;
        world.playSound(null, pos, sound, SoundCategory.BLOCKS);
        world.setBlockState(pos, state.with(OPEN, open));
    }

    @Nullable
    @Override
    public OvenEntity createBlockEntity(BlockPos pos, BlockState state) { return new OvenEntity(pos, state); }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, CookItBlockEntities.OVEN_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world, pos, state));
    }
}