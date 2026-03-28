package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.BakingSheetEntity;
import dev.toasttextures.cookit.block.entity.Container;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakingSheet extends BaseEntityBlock {
    private static final VoxelShape SHAPE = box(3.0, 0.0, 1.0, 13.0, 2.0, 15.0);

    public BakingSheet(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        if (world.getBlockEntity(pos) instanceof BakingSheetEntity blockEntity) {
            if (player.isShiftKeyDown()) return blockEntity.dropAsContainer(player, world, this, pos);

            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack retrieved = blockEntity.retrieve();
            if (!heldItem.isEmpty()) {
                if (blockEntity.fillFirst(player, heldItem)) {
                    world.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.5f, 0.25f);
                    world.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                }
            } else if (!retrieved.isEmpty()) {
                world.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.5f, 0.25f);
                world.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                player.getInventory().placeItemBackInInventory(retrieved);
            }
            blockEntity.setChanged();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag context) {
        Container.appendTooltip(stack, tooltip, item -> true);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Container.onPlaced(world, pos, stack);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BakingSheetEntity(pos, state);
    }
}