package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.Container;
import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import java.util.List;

import static dev.toasttextures.cookit.block.entity.Container.CONTAINER_KEY;

public class MuffinTin extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = box(3.0, 0.0, 1.0, 13.0, 2.0, 15.0);
    public MuffinTin(Properties settings) {
        super(settings);
    }
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MuffinTinEntity(pos, state);
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;

        if (world.getBlockEntity(pos) instanceof MuffinTinEntity blockEntity) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (player.isShiftKeyDown()) {
                ItemStack sheet = this.asItem().getDefaultInstance();
                if (!blockEntity.isEmpty()) {
                    blockEntity.saveToItem(sheet);
                }
                player.getInventory().placeItemBackInInventory(sheet);
                world.destroyBlock(pos,false);
                return InteractionResult.SUCCESS;
            }
            if (!heldItem.isEmpty()) {
                blockEntity.attemptTransfer(player, heldItem);
            } else {
                ItemStack retrieved = blockEntity.retrieve(item -> item != CookItItems.GOOP);
                if (!retrieved.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(retrieved);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag context) {
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt == null || !nbt.contains("Items")) return;

        ListTag itemsTag = nbt.getList("Items", Tag.TAG_COMPOUND);

        if (!itemsTag.isEmpty()) {
            tooltip.add((Component.literal("Items:").withStyle(ChatFormatting.GRAY)));
        }
        Container.appendTooltip(stack, tooltip, item -> {
            ItemStack stored =
        } );

        for (int i = 0; i < itemsTag.size(); i++) {
            CompoundTag itemTag = itemsTag.getCompound(i);
            ItemStack itemStack = ItemStack.of(itemTag);
            if (!itemStack.isEmpty()) {
                MutableComponent itemName;
                if (itemStack.is(CookItItems.GOOP) && itemStack.getTag() != null) {
                    itemName = (MutableComponent) ItemStack.of(itemStack.getTag().getCompound("output")).getHoverName();
                } else {
                    itemName =(MutableComponent) itemStack.getHoverName();
                }
                tooltip.add((itemName.withStyle(ChatFormatting.BLUE)));
            }
        }
    }
}