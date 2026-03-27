package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Fries extends Item {
    public Fries(Properties settings) {
        super(settings);
    }

    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (stack.is(CookItItems.UNCOOKED_FRIES)) {
            tooltip.add(1, Component.translatable("stage.cook-it.uncooked").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }
}