package dev.toasttextures.cookit.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FryerBasket extends Item {
    public FryerBasket(Settings settings) {
        super(settings);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT) return false;
        ItemStack item = ItemStorage.getStoredItem(stack);

        if (!otherStack.isEmpty() && item.isEmpty()) {
            ItemStorage.setStoredItem(stack, otherStack.split(1));
        } else if (!item.isEmpty() && otherStack.isEmpty()) {
            player.getInventory().offerOrDrop(item);
            ItemStorage.setStoredItem(stack, ItemStack.EMPTY);
        }

        return true;
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ItemStack stored = ItemStorage.getStoredItem(stack);
        Text text = Text.literal("Item: ").formatted(Formatting.GRAY).append(Text.literal(stored.getName().getString()).formatted(Formatting.BLUE));
        if (!stored.isEmpty()) {
            tooltip.add(text);
        } else {
            tooltip.remove(text);
        }
    }
}