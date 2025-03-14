package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.MuffinTinEntity;
import dev.toasttextures.cookit.registry.CookItBlocks;
import dev.toasttextures.cookit.registry.CookItComponents;
import dev.toasttextures.cookit.registry.CookItItems;
import dev.toasttextures.cookit.registry.component.CookingComponent;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
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
        MuffinTinEntity entity = (MuffinTinEntity) world.getBlockEntity(pos);
        if (world.isClient || entity == null) {
            return ItemActionResult.SUCCESS;
        }
        if (player.isSneaking()) {
            return BlockEntityUtils.dropOnUse(this, entity, player, world, pos);
        }
        if (!stack.isEmpty()) {
            // Check if there's goop that can be transferred to the muffin tin
            if (stack.isOf(CookItBlocks.MIXING_BOWL.asItem()) && stack.contains(CookItComponents.COOKING_COMPONENT)) {
                for (int i = 0; i < entity.size(); i++) {
                    if (entity.getStack(i).isEmpty()) {
                        MixingBowl.transferTo(stack, entity, world, i);
                        return ItemActionResult.SUCCESS;
                    }
                }
            } else if (CookItItems.MUFFINS.contains(stack.getItem())) {
                for (int i = 0; i < entity.size(); i++) {
                    if (entity.getStack(i).isEmpty()) {
                        entity.setStack(i, stack.splitUnlessCreative(1, player));
                        return ItemActionResult.SUCCESS;
                    }
                }
            }
        } else {
            return BlockEntityUtils.returnItem(entity, player, world, pos, CookItItems.GOOP);
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
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return BlockEntityUtils.dropWithComponent(this, builder);
    }
    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return BlockEntityUtils.getPickStack(this, (MuffinTinEntity) world.getBlockEntity(pos));
    }
    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        ItemStack[] items = BlockEntityUtils.formatItems(stack, CookItItems.GOOP);
        for (int i = 0; i < items.length; i++) {
            if (items[i].contains(CookItComponents.COOKING_COMPONENT)) {
                items[i] = items[i].getOrDefault(CookItComponents.COOKING_COMPONENT, CookingComponent.DEFAULT).get(0);
            }
        }
        BlockEntityUtils.appendTooltip(items, tooltip);
    }
}