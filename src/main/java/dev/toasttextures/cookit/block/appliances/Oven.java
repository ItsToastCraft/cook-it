package dev.toasttextures.cookit.block.appliances;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.entity.oven.OvenEntity;
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
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import static dev.toasttextures.cookit.registries.CookItBlocks.CONTAINERS;
import static dev.toasttextures.cookit.registries.CookItProperties.OPEN;

public class Oven extends BlockWithEntity implements BlockEntityProvider {

    public static final Property<Direction> FACING = Properties.HORIZONTAL_FACING;

    public Oven(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(FACING, Direction.NORTH));
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
        builder.add(OPEN).add(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        OvenEntity blockEntity = (OvenEntity) world.getBlockEntity(pos);

        if (world.isClient || blockEntity == null) {
            return ActionResult.SUCCESS;
        }

        boolean open = state.get(OPEN);

        if (!open) {
            // Open the oven if it's closed and the player is not holding anything
            if (player.getStackInHand(hand).isEmpty()) { openOven(world, pos, state, true); }
        } else {
            ItemStack heldItem = player.getStackInHand(hand);

            if (heldItem.isEmpty()) {
                if (blockEntity.isDone()) {

                    for (int i = blockEntity.getItems().size() - 1; i >= 0; i--) {
                        if (!blockEntity.getStack(i).isEmpty()) {
                            player.getInventory().insertStack(blockEntity.getStack(i));
                            return ActionResult.SUCCESS;
                        }
                    }
                } else {
                    openOven(world, pos, state, false);
                    return ActionResult.SUCCESS;
                }
            } else if (CONTAINERS.contains(Block.getBlockFromItem(heldItem.getItem()))) {
                // If the oven is open and the player is holding something, try to put the held item into the oven
                for (int i = 0; i < blockEntity.getItems().size(); i++) {
                    if (blockEntity.getStack(i).isEmpty()) {
                        blockEntity.setStack(i, heldItem.split(1));
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }

        return ActionResult.FAIL;
    }
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    public void openOven(World world, BlockPos pos, BlockState state, boolean open) {
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
