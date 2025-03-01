package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.registries.CookItFoodTypes;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class Donut extends CookItFood {
    public Donut(Settings settings) {
        super(settings, CookItFoodTypes.DONE);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.getItem().equals(CookItItems.SWEET_BERRY_DONUT_SPRINKLES) || stack.getItem().equals(CookItItems.CHOCOLATE_DONUT_SPRINKLES) || stack.getItem().equals(CookItItems.VANILLA_DONUT_SPRINKLES)) {
            tooltip.add(1, Text.literal("With Sprinkles").formatted(Formatting.ITALIC, Formatting.YELLOW));
        } else if (stack.getItem().equals(CookItItems.CHOCOLATE_DONUT_STRIPED) || stack.getItem().equals(CookItItems.VANILLA_DONUT_STRIPED)) {
            tooltip.add(1, Text.literal("Striped").formatted(Formatting.ITALIC, Formatting.YELLOW));
        }
    }
}
