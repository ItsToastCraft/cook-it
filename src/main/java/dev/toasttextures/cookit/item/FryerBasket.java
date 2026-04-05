package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.entity.Container;
import dev.toasttextures.cookit.block.entity.Transferable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
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

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();

        if (context.getLevel().getBlockEntity(pos) instanceof Transferable transferable) {
            transferable.attemptTransfer(context.getPlayer(), context.getItemInHand());
        }
        return InteractionResult.SUCCESS;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        Container.appendTooltip(stack, tooltip, itemStack -> {
            ItemStack storedItem = ItemStorage.getStoredItem(stack);
            return !storedItem.isEmpty() ? storedItem.getItem() : null;
        });
    }
}