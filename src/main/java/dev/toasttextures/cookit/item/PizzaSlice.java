package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.food_blocks.pizza.Pizza;
import dev.toasttextures.cookit.enums.FoodTypes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PizzaSlice extends CookItFood{
    public PizzaSlice(Settings settings) {
        super(settings, FoodTypes.DONE);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Pizza.addTooltip(stack.getNbt(), tooltip);
    }
}
