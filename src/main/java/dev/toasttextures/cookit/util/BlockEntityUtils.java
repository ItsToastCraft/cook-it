package dev.toasttextures.cookit.util;

import dev.toasttextures.cookit.block.entity.CookingBlockEntity;
import dev.toasttextures.cookit.block.entity.MixingBowlEntity;
import dev.toasttextures.cookit.registry.CookItComponents;
import dev.toasttextures.cookit.registry.component.CookingComponent;
import dev.toasttextures.cookit.registry.component.SingleCookingComponent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
        CookingComponent items = container.getComponents().get(CookItComponents.COOKING_COMPONENT);
        if (items != null && !items.isEmpty()) {
            itemStackList.addAll(items.stacks());
        }
        return itemStackList;
    }


    public static ItemStack[] formatItems(ItemStack stack, Item... exclusions) {
        if (!stack.getComponents().contains(CookItComponents.COOKING_COMPONENT)) return new ItemStack[]{ItemStack.EMPTY};

        List<ItemStack> items = stack.getComponents().getOrDefault(CookItComponents.COOKING_COMPONENT, CookingComponent.DEFAULT).stacks();
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
        ArrayList<ItemStack> items = new ArrayList<>();
        if (!entity.isEmpty()) {
            if (entity instanceof MixingBowlEntity mixingBowlEntity) {
                if (mixingBowlEntity.getGoopColor() != 0) {
                    item.set(CookItComponents.COLOR_COMPONENT, mixingBowlEntity.getGoopColor());
                } else if (mixingBowlEntity.getClicks() > 0) {
                    item.set(CookItComponents.CLICKS_COMPONENT, mixingBowlEntity.getClicks());
                }
            }
            if (entity.getItems().size() == 1) {
                item.set(CookItComponents.SINGLE_COOKING_COMPONENT, new SingleCookingComponent(entity.getStack(0).getRegistryEntry()));
            } else {
                for (ItemStack itemStack : entity.getItems()) {
                    if (!itemStack.isEmpty()) {
                        items.add(itemStack);
                    }
                }
            }
            item.set(CookItComponents.COOKING_COMPONENT, new CookingComponent(items));
        }
        player.getInventory().offerOrDrop(item);
        world.breakBlock(pos, false);
        return ItemActionResult.SUCCESS;
    }

    public static void convertCookingComponent(World world, CookingBlockEntity entity, ItemStack stack) {
        if (entity != null && !world.isClient) {
            entity.setItems(stack.getOrDefault(CookItComponents.COOKING_COMPONENT, CookingComponent.DEFAULT).stacks());
            ItemStack newItem = stack.copy();
            newItem.remove(CookItComponents.COOKING_COMPONENT);
            entity.readComponents(newItem);
        }
    }

    public static void convertSingleCookingComponent(World world, CookingBlockEntity entity, ItemStack stack) {
        if (entity != null && !world.isClient) {
            entity.setItems(List.of(stack.getOrDefault(CookItComponents.SINGLE_COOKING_COMPONENT, SingleCookingComponent.DEFAULT).getItem()));
            ItemStack newItem = stack.copy();
            newItem.remove(CookItComponents.SINGLE_COOKING_COMPONENT);
            entity.readComponents(newItem);
        }
    }
    public static ItemActionResult returnItem(CookingBlockEntity entity, PlayerEntity player, World world, BlockPos pos) {
        for (int i = entity.getItems().size() - 1; i >= 0; i--) {
            if (!entity.getStack(i).isEmpty()) {
                player.getInventory().offerOrDrop(entity.getStack(i).copyAndEmpty());
                world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1, 1.0f);
                return ItemActionResult.SUCCESS;
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
