package com.toast.cookit.item;

import com.toast.cookit.registries.CookItFoodTypes;
import com.toast.cookit.registries.CookItItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Donut extends CookItFood {
    public Donut(Settings settings) {
        super(settings, CookItFoodTypes.DONE);
    }


    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getItem().equals(CookItItems.SWEET_BERRY_DONUT_SPRINKLES) || stack.getItem().equals(CookItItems.CHOCOLATE_DONUT_SPRINKLES) || stack.getItem().equals(CookItItems.VANILLA_DONUT_SPRINKLES)) {
            tooltip.add(1, Text.literal("With Sprinkles").formatted(Formatting.ITALIC, Formatting.YELLOW));
        } else if (stack.getItem().equals(CookItItems.CHOCOLATE_DONUT_STRIPED) || stack.getItem().equals(CookItItems.VANILLA_DONUT_STRIPED)) {
            tooltip.add(1, Text.literal("Striped").formatted(Formatting.ITALIC, Formatting.YELLOW));
        }
    }
}
