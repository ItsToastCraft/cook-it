package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.food_blocks.pizza.Pizza;
import dev.toasttextures.cookit.registry.CookItFoodTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class PizzaSlice extends CookItFood{
    public PizzaSlice(Settings settings) {
        super(settings, CookItFoodTypes.DONE);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        Pizza.createTooltip(stack, context, tooltip, options);
    }
}
