package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.food.pizza.CookedPizza;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PizzaSlice extends Item {
    public PizzaSlice(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        CookedPizza.appendTooltip(stack, tooltip);
    }
}