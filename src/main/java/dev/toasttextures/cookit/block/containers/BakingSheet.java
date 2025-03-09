package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.entity.BakingSheetEntity;
import dev.toasttextures.cookit.item.CookItFood;
import dev.toasttextures.cookit.registry.CookItFoodTypes;
import dev.toasttextures.cookit.registry.CookItItems;
import dev.toasttextures.cookit.util.BlockEntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
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

public class BakingSheet extends Block implements BlockEntityProvider {

    public BakingSheet(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BakingSheetEntity(pos, state);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        BakingSheetEntity blockEntity = (BakingSheetEntity) world.getBlockEntity(pos);
        if (world.isClient || blockEntity == null) {
            return ItemActionResult.SUCCESS;
        }
        if (player.isSneaking()) {
            return BlockEntityUtils.dropOnUse(this, blockEntity, player, world, pos);
        }
        if (!stack.isEmpty()) {
            // Check what is the first open slot and put an item from the player's hand there
            for (int i = 0; i < blockEntity.getItems().size(); i++) {
                if (blockEntity.getStack(i).isEmpty() && (stack.getItem() instanceof CookItFood food && food.getFoodType().equals(CookItFoodTypes.BAKING)) || stack.isIn(CookItItems.BAKING)) {
                    // Put the stack the player is holding into the inventory
                    blockEntity.setStack(i, stack.splitUnlessCreative(1, player));
                    break;
                }
            }
        } else {
            return BlockEntityUtils.returnItem(blockEntity, player, world, pos);
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
        BakingSheetEntity entity = (BakingSheetEntity) world.getBlockEntity(pos);
        BlockEntityUtils.convertCookingComponent(world, entity, itemStack);
    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return BlockEntityUtils.dropWithComponent(this, builder);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return BlockEntityUtils.getPickStack(this, (BakingSheetEntity) world.getBlockEntity(pos));
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        BlockEntityUtils.appendTooltip(BlockEntityUtils.formatItems(stack), tooltip);
    }
}