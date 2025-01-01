package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.food_blocks.pizza.PizzaToppings;
import dev.toasttextures.cookit.registries.CookItFoodTypes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PizzaSlice extends CookItFood{
    public PizzaSlice(Settings settings) {
        super(settings, CookItFoodTypes.DONE);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtList toppings = stack.getOrCreateNbt().getList("toppings", NbtElement.STRING_TYPE);
        if (toppings == null) return;

        tooltip.add((Text.literal("Toppings:").formatted(Formatting.GRAY)));

        for (int i = 0; i < toppings.size(); i++) {
            MutableText topping = Objects.requireNonNull(PizzaToppings.fromName(toppings.getString(i))).getTranslationKey();
            tooltip.add(topping.formatted(Formatting.BLUE));
        }
    }
}
