package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.registries.CookItFoodTypes;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import dev.toasttextures.cookit.registries.CookItItems;

import java.util.List;

public class Fries extends CookItFood {

    public Fries(Settings settings, CookItFoodTypes foodType) {
        super(settings, foodType);
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.getItem() == CookItItems.UNCOOKED_FRENCH_FRIES) {
            tooltip.add(1, Text.literal("Uncooked").formatted(Formatting.ITALIC, Formatting.GRAY));
        }
    }
}
