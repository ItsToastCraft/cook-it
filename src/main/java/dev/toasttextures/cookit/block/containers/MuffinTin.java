package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class MuffinTin extends Block implements BlockEntityProvider {

    public MuffinTin(Settings settings) {
        super(settings);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MuffinTinEntity(pos, state);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(blockPos, state, state, Block.NOTIFY_LISTENERS);
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            MuffinTinEntity blockEntity = (MuffinTinEntity) world.getBlockEntity(blockPos);
            if (blockEntity == null) return ActionResult.FAIL;
            ItemStack item = player.getStackInHand(hand);
            if (player.isSneaking()) {
                return BlockEntityUtils.dropOnUse(this, blockEntity, player, world, blockPos);
            }
            if (!item.isEmpty()) {
                // Check if there's goop that can be transferred to the muffin tin
                if (item.isOf(CookItBlocks.MIXING_BOWL.asItem()) && item.getSubNbt("BlockEntityTag") != null) {
                    for (int i = 0; i < blockEntity.size(); i++) {
                        if (blockEntity.getStack(i).isEmpty()) {
                            MixingBowl.transferTo(item, blockEntity, i);
                            return ActionResult.SUCCESS;
                        }
                    }
                } else if (CookItItems.MUFFINS.contains(item.getItem())) {
                    for (int i = 0; i < blockEntity.size(); i++) {
                        if (blockEntity.getStack(i).isEmpty()) {
                            blockEntity.setStack(i, item.split(1));
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            } else {
                for (int i = blockEntity.size() - 1; i >= 0; i--) {
                    if (!blockEntity.getStack(i).isEmpty() && !blockEntity.getStack(i).isOf(CookItItems.GOOP)) {
                        player.getInventory().offerOrDrop(blockEntity.getStack(i).split(1));
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.1875, 0f, 0.0625f, 0.8125f, 0.125f, 0.9375f);
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext context) {
        ItemStack[] items = BlockEntityUtils.formatItems(stack, CookItItems.GOOP);
        for (int i = 0; i < items.length; i++) {
            NbtCompound tag = items[i].getNbt();
            if (tag != null && tag.contains("output")) {
                items[i] = ItemStack.fromNbt(tag.getCompound("output"));
            }
        }
        BlockEntityUtils.appendTooltip(stack, tooltip);
    }
}