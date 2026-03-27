package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.BakingSheetEntity;
import dev.toasttextures.cookit.block.entity.Container;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakingSheet extends BlockWithEntity {
    private static final VoxelShape SHAPE = createCuboidShape(3.0, 0.0, 1.0, 13.0, 2.0, 15.0);

    public BakingSheet(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BakingSheetEntity blockEntity = (BakingSheetEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return ActionResult.SUCCESS;

        if (player.isSneaking()) return blockEntity.dropAsContainer(player, world, this, pos);

        ItemStack heldItem = player.getStackInHand(hand);
        ItemStack retrieved = blockEntity.retrieve();
        if (!heldItem.isEmpty()) {
            if (blockEntity.fillFirst(player, heldItem)) {
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 0.25f);
                world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            }
        } else if (!retrieved.isEmpty()) {
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS);
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            player.getInventory().offerOrDrop(retrieved);
        }
        blockEntity.markDirty();

        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }
    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext context) {
        Container.appendTooltip(stack, tooltip, item -> true);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Container.onPlaced(world, pos, stack);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BakingSheetEntity(pos, state);
    }
}