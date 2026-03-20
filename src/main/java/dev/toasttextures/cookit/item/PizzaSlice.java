package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.food_blocks.pizza.CookedPizza;
import dev.toasttextures.cookit.registries.CookItFoodTypes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PizzaSlice extends CookItFood {
    public PizzaSlice(Settings settings) {
        super(settings, CookItFoodTypes.DONE);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CookedPizza.appendTooltip(stack, tooltip);
    }
}