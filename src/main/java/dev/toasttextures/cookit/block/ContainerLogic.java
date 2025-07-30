package dev.toasttextures.cookit.block;

import dev.toasttextures.cookit.block.entity.CookingBlockEntity;
import dev.toasttextures.cookit.item.CookItFood;
import dev.toasttextures.cookit.enums.FoodTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ContainerLogic {
    default boolean hasFoodType(ItemStack item, FoodTypes foodType) {
        return foodType == null || (!(item.getItem() instanceof CookItFood food)) || food.getFoodType().equals(foodType);
    }

    default void addStack(ItemStack stack, CookingBlockEntity entity, @Nullable FoodTypes foodType) {
        if (stack.isEmpty() || !hasFoodType(stack, foodType)) return;

        for (int i = 0; i < entity.getItems().size(); i++) {
            if (entity.getStack(i).isEmpty()) {
                entity.setStack(i, stack.split(1));
                return;
            }
        }
    }

    default void retrieveStack(PlayerEntity player, CookingBlockEntity entity) {
        retrieveStack(player, entity, List.of());
    }

    default void retrieveStack(PlayerEntity player, CookingBlockEntity entity, List<Item> exclusions) {
        for (int i = entity.getItems().size() - 1; i >= 0; i--) {
            ItemStack stack = entity.getItems().get(i);
            if (!stack.isEmpty() && !exclusions.contains(stack.getItem())) {
                player.getInventory().offerOrDrop(entity.getStack(i).copyAndEmpty());
                break;
            }
        }
    }
}
