package dev.toasttextures.cookit.block.appliances;

import dev.toasttextures.cookit.block.entity.OvenEntity;
import dev.toasttextures.cookit.registries.CookItTags;
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

import static net.minecraft.state.property.Properties.*;

public class Oven extends BlockWithEntity implements BlockEntityProvider {
    public Oven(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(OPEN, false)
                .with(HORIZONTAL_FACING, Direction.NORTH)
                .with(LIT, false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN, HORIZONTAL_FACING, LIT);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        OvenEntity blockEntity = (OvenEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return ActionResult.SUCCESS;

        ItemStack heldItem = player.getStackInHand(hand);

        if (!state.get(OPEN) && heldItem.isEmpty()) {
            openOven(world, pos, state, true);
            return ActionResult.SUCCESS;
        }

        if (heldItem.isEmpty()) {
            if (state.get(LIT)) {
                ItemStack retrieved = blockEntity.retrieve();
                if (!retrieved.isEmpty()) {
                    player.getInventory().offerOrDrop(retrieved);
                }
            } else {
                openOven(world, pos, state, false);
            }
        } else if (Block.getBlockFromItem(heldItem.getItem()).getDefaultState().isIn(CookItTags.CONTAINERS)) {
            return blockEntity.fillFirst(heldItem) ? ActionResult.SUCCESS : ActionResult.FAIL;
        }

        return ActionResult.FAIL;
    }
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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
        return checkType(type, CookItBlockEntities.OVEN, OvenEntity::tick);
    }
}