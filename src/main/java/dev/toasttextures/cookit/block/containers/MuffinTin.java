package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
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
                ItemStack sheet = this.asItem().getDefaultStack();
                if (!blockEntity.isEmpty()) {
                    blockEntity.setStackNbt(sheet);
                }
                player.getInventory().insertStack(sheet);
                world.breakBlock(blockPos,false);
                return ActionResult.SUCCESS;
            }
            if (!item.isEmpty()) {
                // Check if there's goop that can be transferred to the muffin tin
                if (item.isOf(CookItBlocks.MIXING_BOWL.asItem()) && item.getSubNbt("BlockEntityTag") != null) {
                    blockEntity.transfer(player, item);
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
        NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
        if (nbt == null || !nbt.contains("Items")) return;

        NbtList itemsTag = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

        if (!itemsTag.isEmpty()) {
            tooltip.add((Text.literal("Items:").formatted(Formatting.GRAY)));
        }

        for (int i = 0; i < itemsTag.size(); i++) {
            NbtCompound itemTag = itemsTag.getCompound(i);
            ItemStack itemStack = ItemStack.fromNbt(itemTag);
            if (!itemStack.isEmpty()) {
                MutableText itemName;
                if (itemStack.isOf(CookItItems.GOOP) && itemStack.getNbt() != null) {
                    itemName = (MutableText) ItemStack.fromNbt(itemStack.getNbt().getCompound("output")).getName();
                } else {
                    itemName =(MutableText) itemStack.getName();
                }
                tooltip.add((itemName.formatted(Formatting.BLUE)));
            }
        }
    }
}