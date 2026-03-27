package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.entity.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import static dev.toasttextures.cookit.block.entity.Container.CONTAINER_KEY;
import static dev.toasttextures.cookit.block.entity.Container.getItems;

// Ok so basically I like storing stuff in NBT and I need a better way to do that rather
// than checking if it's null and stuff every time

// And it really shouldn't have just been a FryerBasket specific thing...
public final class ItemStorage {
    private ItemStorage() {}

    public static ItemStack getStoredItem(ItemStack input) {
        return getItems(input).get(0);
    }

    public static void setStoredItem(ItemStack input, ItemStack item) {
        CompoundTag nbt = input.getOrCreateTagElement(CONTAINER_KEY);
        if (item.isEmpty()) {
            nbt.remove("Items");
            return;
        }
        Container.addTo(input, item);
    }
}