package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.enums.DonutType;
import dev.toasttextures.cookit.enums.FoodTypes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Donut extends CookItFood {

    private final DonutType type;
    public Donut(Settings settings) {
        super(settings, FoodTypes.DONE);
        this.type = DonutType.PLAIN;
    }

    public Donut(Settings settings, DonutType type) {
        super(settings, FoodTypes.DONE);
        this.type = type;
    }

    public DonutType getType() {
        return type;
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Item item =  stack.getItem();

        if (item instanceof Donut donut) {
            DonutType donutType = donut.getType();
            if  (donutType == DonutType.PLAIN) {
                tooltip.add(Text.literal(donutType.getTooltip()).formatted(Formatting.ITALIC, Formatting.YELLOW));
            }
        }
    }
}
