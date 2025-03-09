package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import dev.toasttextures.cookit.registry.CookItBlocks;
import dev.toasttextures.cookit.registry.CookItComponents;
import dev.toasttextures.cookit.registry.CookItItems;
import dev.toasttextures.cookit.registry.component.SingleCookingComponent;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        MuffinTinEntity blockEntity = (MuffinTinEntity) world.getBlockEntity(pos);
        if (world.isClient || blockEntity == null) {
            return ItemActionResult.FAIL;
        }
        if (player.isSneaking()) {
            return BlockEntityUtils.dropOnUse(this, blockEntity, player, world, pos);
        }
        if (!stack.isEmpty()) {
            // Check if there's goop that can be transferred to the muffin tin
            if (stack.isOf(CookItBlocks.MIXING_BOWL.asItem()) && stack.contains(CookItComponents.COOKING_COMPONENT)) {
                for (int i = 0; i < blockEntity.size(); i++) {
                    if (blockEntity.getStack(i).isEmpty()) {
                        MixingBowl.transferTo(stack, blockEntity, world, i);
                        return ItemActionResult.SUCCESS;
                    }
                }
            } else if (CookItItems.MUFFINS.contains(stack.getItem())) {
                for (int i = 0; i < blockEntity.size(); i++) {
                    if (blockEntity.getStack(i).isEmpty()) {
                        blockEntity.setStack(i, stack.splitUnlessCreative(1, player));
                        return ItemActionResult.SUCCESS;
                    }
                }
            }
        } else {
            for (int i = blockEntity.size() - 1; i >= 0; i--) {
                if (!blockEntity.getStack(i).isEmpty() && !blockEntity.getStack(i).isOf(CookItItems.GOOP)) {
                    player.getInventory().offerOrDrop(blockEntity.getStack(i).split(1));
                    return ItemActionResult.SUCCESS;
                }
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.cuboid(0.1875, 0f, 0.0625f, 0.8125f, 0.125f, 0.9375f);
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        MuffinTinEntity entity = (MuffinTinEntity) world.getBlockEntity(pos);
        BlockEntityUtils.convertCookingComponent(world, entity, itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        ItemStack[] items = BlockEntityUtils.formatItems(stack, CookItItems.GOOP);
        for (int i = 0; i < items.length; i++) {

            if (items[i].contains(CookItComponents.SINGLE_COOKING_COMPONENT)) {
                items[i] = items[i].getOrDefault(CookItComponents.SINGLE_COOKING_COMPONENT, SingleCookingComponent.DEFAULT).getItem();
            }
        }
        BlockEntityUtils.appendTooltip(items, tooltip);
    }
}