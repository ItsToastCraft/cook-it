package dev.toasttextures.cookit.block.appliances;

import com.mojang.serialization.MapCodec;
import dev.toasttextures.cookit.block.containers.CookingContainer;
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
import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class Oven extends CookingContainer {
    protected static final MapCodec<Oven> CODEC = createCodec(Oven::new);

    public Oven(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false).with(HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN).add(HORIZONTAL_FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        OvenEntity blockEntity = (OvenEntity) world.getBlockEntity(pos);

        if (world.isClient() || blockEntity == null) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getStackInHand(hand);
        boolean open = state.get(OPEN);

        if (open) {
            if (heldItem.isEmpty()) {
                if (blockEntity.isDone()) {
                    retrieveStack(player, blockEntity);
                } else {
                    toggleOvenDoor(state, world, pos, false);
                }
            } else if (CONTAINERS.contains(Block.getBlockFromItem(heldItem.getItem()))) {
                // If the oven is open and the player is holding something, try to put the held item into the oven
                addStack(heldItem, blockEntity, null);
            }
        } else if (heldItem.isEmpty()) {
            toggleOvenDoor(state, world, pos, true);
        }

        return ActionResult.SUCCESS;
    }
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    public void toggleOvenDoor(BlockState state, World world, BlockPos pos, boolean open) {
        SoundEvent sound = open ? SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN : SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE;
        world.playSound(null, pos, sound, SoundCategory.BLOCKS);
        world.setBlockState(pos, state.with(OPEN, open));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, CookItBlockEntities.OVEN_ENTITY,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world, pos, state));
    }

    @Nullable
    @Override
    public OvenEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OvenEntity(pos, state);
    }
}