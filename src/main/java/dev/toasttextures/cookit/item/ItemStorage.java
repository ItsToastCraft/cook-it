package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.entity.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

// Ok so basically I like storing stuff in NBT and I need a better way to do that rather
// than checking if it's null and stuff every time

// And it really shouldn't have just been a FryerBasket specific thing...
public final class ItemStorage {
    private ItemStorage() {}

    public static ItemStack getStoredItem(ItemStack input) {
        return Container.getItems(input).get(0);
    }

    public static void setStoredItem(ItemStack input, ItemStack item) {
        CompoundTag nbt = input.getOrCreateTagElement(Container.CONTAINER_KEY);
        if (item.isEmpty()) {
            nbt.remove("Items");
            return;
        }
        Container.writeTo(input, List.of(item));
    }

    public static ItemStack split(ItemStack input) {
        ItemStack stored = getStoredItem(input).split(1);
        setStoredItem(input, stored);
        return stored;
    }
}