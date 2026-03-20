package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Fries extends Item {
    public Fries(Settings settings) {
        super(settings);
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.isOf(CookItItems.UNCOOKED_FRENCH_FRIES)) {
            tooltip.add(1, Text.translatable("stage.cook-it.uncooked").formatted(Formatting.ITALIC, Formatting.GRAY));
        }
    }
}