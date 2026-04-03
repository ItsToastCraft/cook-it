package dev.toasttextures.cookit.block.food.pizza;

import dev.toasttextures.cookit.block.entity.PizzaEntity;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CookedPizza extends Pizza {
    public CookedPizza(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        int pizzaAmount = world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity ? pizzaEntity.getSliceCount() : 4;
        return SLICES.get(pizzaAmount - 1);
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) return InteractionResult.SUCCESS;
        PizzaEntity entity = (PizzaEntity) world.getBlockEntity(pos);
        if (entity == null) return InteractionResult.SUCCESS;

        int pizzaAmount = entity.getSliceCount();
        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.isEmpty()) {
            ListTag toppings = entity.getToppings();

            ItemStack itemStack = new ItemStack(CookItItems.PIZZA_SLICE, 1);
            if (!toppings.isEmpty()) {
                itemStack.getOrCreateTag().put(PizzaTopping.TOPPINGS_KEY, toppings);
            }

            player.getInventory().placeItemBackInInventory(itemStack);
            world.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS);

            if (pizzaAmount > 1) {
                entity.setSliceCount(entity.getSliceCount() - 1);
                world.sendBlockUpdated(pos, state, state, UPDATE_ALL);
            } else {
                world.destroyBlock(pos, false);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PizzaEntity(pos, state, true);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.getBlockEntity(pos) instanceof PizzaEntity pizzaEntity) {
            int pizzaAmount = pizzaEntity.getSliceCount();
            if (pizzaAmount > 0) {
                ItemStack itemStack = new ItemStack(CookItItems.PIZZA_SLICE, pizzaAmount);
                if (!pizzaEntity.getToppings().isEmpty()) {
                    itemStack.getOrCreateTag().put(PizzaTopping.TOPPINGS_KEY, pizzaEntity.getToppings());
                }
                popResource(world, pos, itemStack);
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag context) {
        appendTooltip(stack, tooltip);
    }

    public static void appendTooltip(ItemStack stack, List<Component> tooltip) {
        List<PizzaTopping> toppings = PizzaTopping.getToppings(stack);

        if (toppings.isEmpty()) return;

        tooltip.add((Component.literal("Toppings:").withStyle(ChatFormatting.GRAY)));

        for (PizzaTopping topping : toppings) {
            tooltip.add(topping.getTranslationKey());
        }
    }
}