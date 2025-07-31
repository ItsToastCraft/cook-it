package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.enums.FoodTypes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import dev.toasttextures.cookit.registries.CookItItems;

import java.util.List;

public class Fries extends CookItFood {
    public Fries(Settings settings, FoodTypes foodType) {
        super(settings, foodType);
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.isOf(CookItItems.UNCOOKED_FRENCH_FRIES)) {
            tooltip.add(1, Text.literal("Uncooked").formatted(Formatting.ITALIC, Formatting.GRAY));
        }
    }
}