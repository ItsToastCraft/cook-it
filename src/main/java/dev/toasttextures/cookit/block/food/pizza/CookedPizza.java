package dev.toasttextures.cookit.block.food.pizza;

import dev.toasttextures.cookit.block.entity.PizzaEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CookedPizza extends Pizza {
    public CookedPizza(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        int pizzaAmount = world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity ? pizzaEntity.getSliceCount() : 4;
        return Slices.values()[pizzaAmount - 1].shape;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        PizzaEntity entity = (PizzaEntity) world.getBlockEntity(pos);
        if (entity == null) return ActionResult.SUCCESS;

        int pizzaAmount = entity.getSliceCount();
        ItemStack heldItem = player.getStackInHand(hand);

        if (heldItem.isEmpty()) {
            NbtList toppings = entity.getToppings();

            ItemStack itemStack = new ItemStack(CookItItems.PIZZA_SLICE, 1);
            if (!toppings.isEmpty()) {
                itemStack.getOrCreateNbt().put(PizzaTopping.TOPPINGS_KEY, toppings);
            }

            player.getInventory().offerOrDrop(itemStack);
            world.playSound(null, pos, SoundEvents.BLOCK_WOOL_BREAK, SoundCategory.BLOCKS);

            if (pizzaAmount > 1) {
                entity.setSliceCount(entity.getSliceCount() - 1);
            } else {
                world.breakBlock(pos, false);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaEntity(pos, state, true);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity) {
            int pizzaAmount = pizzaEntity.getSliceCount();
            if (pizzaAmount > 0) {
                ItemStack itemStack = new ItemStack(CookItItems.PIZZA_SLICE, pizzaAmount);
                if (!pizzaEntity.getToppings().isEmpty()) {
                    itemStack.getOrCreateNbt().put(PizzaTopping.TOPPINGS_KEY, pizzaEntity.getToppings());
                }
                dropStack(world, pos, itemStack);
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext context) {
        appendTooltip(stack, tooltip);
    }

    public static void appendTooltip(ItemStack stack, List<Text> tooltip) {
        List<PizzaTopping> toppings = PizzaTopping.getToppings(stack);

        if (toppings.isEmpty()) return;

        tooltip.add((Text.literal("Toppings:").formatted(Formatting.GRAY)));

        for (PizzaTopping topping : toppings) {
            tooltip.add(topping.getTranslationKey());
        }
    }
}