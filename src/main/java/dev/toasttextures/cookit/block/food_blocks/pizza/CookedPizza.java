package dev.toasttextures.cookit.block.food_blocks.pizza;

import dev.toasttextures.cookit.block.entity.PizzaEntity;
import dev.toasttextures.cookit.registry.CookItBlocks;
import dev.toasttextures.cookit.registry.CookItComponents;
import dev.toasttextures.cookit.registry.CookItItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CookedPizza extends Pizza{
    public CookedPizza(Settings settings) {
        super(settings);
    }

    private final VoxelShape SLICE_1 = VoxelShapes.cuboid(0.0625f, 0.0f, 0.0625f, 0.5f, 0.125f, 0.5f);
    private final VoxelShape SLICE_2 = VoxelShapes.cuboid(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.125f, 0.5f);
    private final VoxelShape SLICE_3 = VoxelShapes.union(SLICE_2, createCuboidShape(8, 0.0f, 8, 15, 2, 15));

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        int pizzaAmount = world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity ? pizzaEntity.getSliceCount() : 4;

        return switch (pizzaAmount) {
            case (1) -> SLICE_1;
            case (2) -> SLICE_2;
            case (3) -> SLICE_3;
            default -> Pizza.FULL;
        };
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        PizzaEntity entity = (PizzaEntity) world.getBlockEntity(pos);
        if (entity == null || world.isClient) {
            return ItemActionResult.SUCCESS;
        }
        int pizzaAmount = entity.getSliceCount();
        if (world.getBlockState(pos).getBlock() == CookItBlocks.PIZZA && stack.isEmpty()) {

            ArrayList<String> toppings = entity.getToppings();

            ItemStack itemStack = new ItemStack(CookItItems.PIZZA_SLICE, 1);
            if (!toppings.isEmpty()) {
                itemStack.set(CookItComponents.TOPPING_COMPONENT, toppings);
            }

            player.getInventory().offerOrDrop(itemStack);
            world.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS);
            if (pizzaAmount > 1) {
                entity.setSliceCount(entity.getSliceCount() - 1);
            } else {
                world.breakBlock(pos, false);
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity) {
            int pizzaAmount = pizzaEntity.getSliceCount();
            if (pizzaAmount > 0) {
                ItemStack itemStack = new ItemStack(CookItItems.PIZZA_SLICE, pizzaAmount);
                if (!pizzaEntity.getToppings().isEmpty()) {
                    itemStack.set(CookItComponents.TOPPING_COMPONENT, pizzaEntity.getToppings());
                }
                dropStack(world, pos, itemStack);
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaEntity(pos, state, true);
    }
}
