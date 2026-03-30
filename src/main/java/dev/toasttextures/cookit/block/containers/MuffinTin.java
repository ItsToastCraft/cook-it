package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.Container;
import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MuffinTin extends BaseEntityBlock implements EntityBlock {
    private static final VoxelShape SHAPE = box(3.0, 0.0, 1.0, 13.0, 2.0, 15.0);

    public MuffinTin(Properties settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof MuffinTinEntity blockEntity)) return InteractionResult.SUCCESS;

        if (player.isShiftKeyDown()) return blockEntity.dropAsContainer(player, world, this, pos);

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack retrieved = blockEntity.retrieve(item -> item != CookItItems.GOOP);

        if (!heldItem.isEmpty()) {
            blockEntity.attemptTransfer(player, heldItem);
            return InteractionResult.CONSUME;
        } else if (!retrieved.isEmpty()) {
            player.getInventory().placeItemBackInInventory(retrieved);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag context) {
        Container.appendTooltip(stack, tooltip, itemStack -> {
            if (itemStack.is(CookItItems.GOOP)) {
                ItemStack stored = ItemStorage.getStoredItem(itemStack);
                return stored.isEmpty() ? null : stored.getItem();
            }
            return itemStack.getItem();
        });
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MuffinTinEntity(pos, state);
    }
}