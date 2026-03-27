package dev.toasttextures.cookit.item;

import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FryerBasket extends Item {
    public FryerBasket(Properties settings) {
        super(settings);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
        if (clickType != ClickAction.SECONDARY) return false;
        ItemStack item = ItemStorage.getStoredItem(stack);

        if (!otherStack.isEmpty() && item.isEmpty()) {
            ItemStorage.setStoredItem(stack, otherStack.split(1));
        } else if (!item.isEmpty() && otherStack.isEmpty()) {
            player.getInventory().placeItemBackInInventory(item);
            ItemStorage.setStoredItem(stack, ItemStack.EMPTY);
        }

        return true;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        ItemStack stored = ItemStorage.getStoredItem(stack);
        Component text = Component.literal("Item: ").withStyle(ChatFormatting.GRAY).append(Component.literal(stored.getHoverName().getString()).withStyle(ChatFormatting.BLUE));
        if (!stored.isEmpty()) {
            tooltip.add(text);
        } else {
            tooltip.remove(text);
        }
    }
}