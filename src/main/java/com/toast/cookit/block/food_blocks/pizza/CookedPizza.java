package com.toast.cookit.block.food_blocks.pizza;

import com.toast.cookit.block.entity.PizzaEntity;
import com.toast.cookit.registries.CookItBlocks;
import com.toast.cookit.registries.CookItItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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

        switch (pizzaAmount) {
            case (1) -> {
                return SLICE_1;
            }
            case (2) -> {
                return SLICE_2;
            }
            case (3) -> {
                return SLICE_3;
            }
            default -> {
                return Pizza.FULL;
            }
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        super.onUse(state, world, pos, player, hand, hit);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        ItemStack heldItem = player.getStackInHand(hand);
        PizzaEntity entity = (PizzaEntity) world.getBlockEntity(pos);
        if (entity == null || world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            int pizzaAmount = entity.getSliceCount();
            if (world.getBlockState(pos).getBlock() == CookItBlocks.PIZZA && heldItem.isEmpty()) {

                NbtList toppings = entity.getToppings();

                ItemStack itemStack = new ItemStack(CookItItems.PIZZA_SLICE, 1);
                if (!toppings.isEmpty()) {
                    itemStack.getOrCreateNbt().put("toppings", toppings);
                }

                player.getInventory().offerOrDrop(itemStack);
                world.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS);
                if (pizzaAmount > 1) {
                    entity.setSliceCount(entity.getSliceCount() - 1);
                } else {
                    world.breakBlock(pos, false);
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaEntity(pos, state, true);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity) {
            int pizzaAmount = pizzaEntity.getSliceCount();
            if (pizzaAmount > 0) {
                ItemStack itemStack = new ItemStack(CookItItems.PIZZA_SLICE, pizzaAmount);
                if (!pizzaEntity.getToppings().isEmpty()) {
                    itemStack.getOrCreateNbt().put("toppings", pizzaEntity.getToppings());
                }
                dropStack(world, pos, itemStack);
            }
        }
        return super.onBreak(world, pos, state, player);
    }
}
