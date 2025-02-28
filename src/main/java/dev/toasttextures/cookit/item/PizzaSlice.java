package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.food_blocks.pizza.PizzaToppings;
import dev.toasttextures.cookit.registries.CookItComponents;
import dev.toasttextures.cookit.registries.CookItFoodTypes;

import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PizzaSlice extends CookItFood{
    public PizzaSlice(Settings settings) {
        super(settings, CookItFoodTypes.DONE);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {

        ArrayList<String> toppings = stack.getOrDefault(CookItComponents.TOPPING_COMPONENT, new ArrayList<>());
        if (!toppings.isEmpty()) return;
        tooltip.add((Text.literal("Toppings:").formatted(Formatting.GRAY)));

        for (String s : toppings) {
            MutableText topping = Objects.requireNonNull(PizzaToppings.fromName(s)).getTranslationKey();
            tooltip.add(topping.formatted(Formatting.BLUE));
        }
    }
}
