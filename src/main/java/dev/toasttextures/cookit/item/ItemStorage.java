package dev.toasttextures.cookit.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

// Ok so basically I like storing stuff in NBT and I need a better way to do that rather
// than checking if it's null and stuff every time

// And it really shouldn't have just been a FryerBasket specific thing...
public final class ItemStorage {
    private ItemStorage() {}

    public static ItemStack getStoredItem(ItemStack input) {
        NbtCompound root = input.getNbt();
        if (root == null) return ItemStack.EMPTY;

        NbtList list = root.getList("Items", NbtElement.COMPOUND_TYPE);
        if (list.isEmpty()) return ItemStack.EMPTY;

        return ItemStack.fromNbt(list.getCompound(0));
    }

    public static void setStoredItem(ItemStack input, ItemStack item) {
        if (item.isEmpty()) {
            input.removeSubNbt("Items");
            return;
        }
        NbtCompound compound = new NbtCompound();
        NbtList list = new NbtList();

        compound.putByte("Slot", (byte) 0);
        item.writeNbt(compound);
        list.add(compound);

        input.getOrCreateNbt().put("Items", list);
    }
}