package dev.toasttextures.cookit.util;

import dev.toasttextures.cookit.block.entity.CookingBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockEntityUtils {
    private BlockEntityUtils() {
    }

    // Checks if the item is a container that stores more items in its NBT
    // Many items in this mod do this, like baking sheets, pizza pans, and muffin tins
    public static boolean isContainer(ItemStack item) {
        return isContainer(item, "BlockEntityTag");
    }

    public static boolean isContainer(ItemStack item, String key) {
        NbtList nbtList = item.getOrCreateSubNbt(key).getList("Items", NbtElement.COMPOUND_TYPE);
        return !nbtList.isEmpty();
    }


    public static ArrayList<ItemStack> getContainerItems(ItemStack container) {

        ArrayList<ItemStack> itemStackList = new ArrayList<>();
        NbtCompound nbt = container.getSubNbt("BlockEntityTag");
        if (nbt != null && nbt.contains("Items")) {
            NbtList itemsTag = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            for (int j = 0; j < itemsTag.size(); j++) {
                NbtCompound itemTag = itemsTag.getCompound(j);
                ItemStack itemStack = ItemStack.fromNbt(itemTag);

                itemStackList.add(itemStack);
            }
        }
        return itemStackList;
    }

    public static ItemStack[] formatItems(ItemStack stack, Item... exclusions) {
        NbtCompound nbt = stack.getSubNbt("BlockEntityTag");
        if (nbt == null || !nbt.contains("Items")) return new ItemStack[]{ItemStack.EMPTY};
        NbtList itemsTag = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

        if (!itemsTag.isEmpty()) {
            ItemStack[] itemStacks = new ItemStack[itemsTag.size()];
            for (int i = 0; i < itemsTag.size(); i++) {
                ItemStack itemStack = ItemStack.fromNbt(itemsTag.getCompound(i));
                if (!itemStack.isEmpty() && !Arrays.asList(exclusions).contains(stack.getItem())) {
                    itemStacks[i] = itemStack;
                }
            }
            return itemStacks;
        }
        return new ItemStack[]{ItemStack.EMPTY};
    }
    public static void appendTooltip(ItemStack stack, List<Text> tooltip) {
        appendTooltip(formatItems(stack, Items.AIR), tooltip);
    }
    public static void appendTooltip(ItemStack stack, List<Text> tooltip, Item... exclusions) {
        appendTooltip(formatItems(stack, exclusions), tooltip);
    }
    public static void appendTooltip(ItemStack[] items, List<Text> tooltip) {
       if (items.length == 0 || items[0].isOf(Items.AIR)) return;
       tooltip.add(Text.literal(items.length > 1 ? "Items:" : "Item:"));

       for (ItemStack itemStack : items) {

           Text name = Text.literal(itemStack.getName().getString()).formatted(Formatting.BLUE);
           tooltip.add(name);
       }
    }
    public static ActionResult dropOnUse(Block block, CookingBlockEntity entity, PlayerEntity player, World world, BlockPos pos) {
        ItemStack item = block.asItem().getDefaultStack();
        if (!entity.isEmpty()) {
            entity.setStackNbt(item);
        }
        player.getInventory().offerOrDrop(item);
        world.breakBlock(pos, false);
        return ActionResult.SUCCESS;
    }
}
