package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.BakingSheetEntity;
import dev.toasttextures.cookit.block.entity.Container;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class BakingSheet extends BlockWithEntity {
    private static final VoxelShape SHAPE = createCuboidShape(3.0, 0.0, 1.0, 13.0, 2.0, 15.0);

    public BakingSheet(Settings settings) {
        super(settings);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BakingSheetEntity blockEntity = (BakingSheetEntity) world.getBlockEntity(pos);
        if (blockEntity == null) return ActionResult.SUCCESS;

        if (player.isSneaking()) return blockEntity.dropAsContainer(player, world, this, pos);

        ItemStack heldItem = player.getStackInHand(hand);
        ItemStack retrieved = blockEntity.retrieve();
        if (!heldItem.isEmpty()) {
            blockEntity.fillFirst(heldItem);
        } else if (!retrieved.isEmpty()) {
            player.getInventory().offerOrDrop(retrieved);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }
    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext context) {
        Container.appendToolTip(stack, tooltip, item -> true);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BakingSheetEntity(pos, state);
    }
}