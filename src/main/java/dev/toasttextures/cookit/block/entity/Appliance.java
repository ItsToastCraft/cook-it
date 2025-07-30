package dev.toasttextures.cookit.block.entity;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface Appliance {
     void complete(@NotNull RecipeEntry<? extends Recipe<SimpleInventory>> entry);

     default List<ItemStack> getContainerItems(ItemStack container) {
         ArrayList<ItemStack> itemStackList = new ArrayList<>();
         NbtCompound nbt = container.getSubNbt("BlockEntityTag");
         if (nbt != null && nbt.contains("Items")) {
             NbtList itemsTag = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
             for (int j = 0; j < itemsTag.size(); j++) {
                 ItemStack itemStack = ItemStack.fromNbt(itemsTag.getCompound(j));

                 itemStackList.add(itemStack);
             }
         }
         return itemStackList;
     }
}
