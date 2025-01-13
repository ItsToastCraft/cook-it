package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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

    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_LISTENERS);
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            MuffinTinEntity blockEntity = (MuffinTinEntity) world.getBlockEntity(blockPos);
            ItemStack item = player.getStackInHand(hand);
            if (!item.isEmpty()) {
                // Check if there's goop that can be transferred to the muffin tin
                if (item.isOf(CookItBlocks.MIXING_BOWL.asItem()) && item.getSubNbt("BlockEntityTag") != null) {

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
                String itemName = itemStack.getName().getString();
                tooltip.add((Text.literal(itemName).formatted(Formatting.BLUE)));
            }
        }
    }
}