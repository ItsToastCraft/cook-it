package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.*;
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
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import java.util.List;

public class MuffinTin extends Block implements EntityBlock {

    public MuffinTin(Properties settings) {
        super(settings);
    }
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MuffinTinEntity(pos, state);
    }

    public InteractionResult use(BlockState state, Level world, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hit) {
        world.sendBlockUpdated(blockPos, state, state, Block.UPDATE_CLIENTS);
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MuffinTinEntity blockEntity = (MuffinTinEntity) world.getBlockEntity(blockPos);
            if (blockEntity == null) return InteractionResult.FAIL;
            ItemStack item = player.getItemInHand(hand);
            if (player.isShiftKeyDown()) {
                ItemStack sheet = this.asItem().getDefaultInstance();
                if (!blockEntity.isEmpty()) {
                    blockEntity.saveToItem(sheet);
                }
                player.getInventory().add(sheet);
                world.destroyBlock(blockPos,false);
                return InteractionResult.SUCCESS;
            }
            if (!item.isEmpty()) {
                // Check if there's goop that can be transferred to the muffin tin
                if (item.is(CookItBlocks.MIXING_BOWL.asItem()) && item.getTagElement("BlockEntityTag") != null) {
                    blockEntity.transfer(player, item);
                }
            } else {
                for (int i = blockEntity.getContainerSize() - 1; i >= 0; i--) {
                    if (!blockEntity.getItem(i).isEmpty() && !blockEntity.getItem(i).is(CookItItems.GOOP)) {
                       player.getInventory().placeItemBackInInventory(blockEntity.getItem(i).split(1));
                       return InteractionResult.SUCCESS;
                    }
                }
            }

        }


        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return Shapes.box(0.1875, 0f, 0.0625f, 0.8125f, 0.125f, 0.9375f);
    }
    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag context) {
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt == null || !nbt.contains("Items")) return;

        ListTag itemsTag = nbt.getList("Items", Tag.TAG_COMPOUND);

        if (!itemsTag.isEmpty()) {
            tooltip.add((Component.literal("Items:").withStyle(ChatFormatting.GRAY)));
        }

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