package dev.toasttextures.cookit.util;

import dev.toasttextures.cookit.block.entity.CookingBlockEntity;
import dev.toasttextures.cookit.registries.CookItComponents;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BlockEntityUtils {
    private BlockEntityUtils() {
    }

    // Checks if the item is a container that stores more items in its NBT
    // Many items in this mod do this, like baking sheets, pizza pans, and muffin tins
    public static boolean isContainer(ItemStack item) {

        return !Objects.requireNonNull(item.getComponents().get(CookItComponents.COOKING_COMPONENT)).isEmpty();
    }


    public static ArrayList<ItemStack> getContainerItems(ItemStack container) {

        ArrayList<ItemStack> itemStackList = new ArrayList<>();
        List<ItemStack> items = container.getComponents().get(CookItComponents.COOKING_COMPONENT);
        if (items != null && !items.isEmpty()) {
            itemStackList.addAll(items);
        }
        return itemStackList;
    }


    public static ItemStack[] formatItems(ItemStack stack, Item... exclusions) {
        List<ItemStack> items = stack.getComponents().get(CookItComponents.COOKING_COMPONENT);
        if (items == null || items.isEmpty()) return new ItemStack[]{ItemStack.EMPTY};


        ItemStack[] itemStacks = new ItemStack[items.size()];
        for (int i = 0; i < items.size(); i++) {

            if (!items.get(i).isEmpty() && !Arrays.asList(exclusions).contains(stack.getItem())) {
                itemStacks[i] = items.get(i);
            }
        }
        return itemStacks;
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
    public static ItemActionResult dropOnUse(Block block, CookingBlockEntity entity, PlayerEntity player, World world, BlockPos pos) {
        ItemStack item = block.asItem().getDefaultStack();
        if (!entity.isEmpty()) {

            item.applyComponentsFrom(entity.createComponentMap());
        }
        player.getInventory().offerOrDrop(item);
        world.breakBlock(pos, false);
        return ItemActionResult.SUCCESS;
    }
}
