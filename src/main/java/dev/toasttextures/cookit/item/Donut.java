package dev.toasttextures.cookit.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Donut extends Item {
    private final Type donutType;

    public Donut(Settings settings, Type donutType) {
        super(settings);
        this.donutType = donutType;
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getItem() instanceof Donut donut) {
            tooltip.add(donut.donutType.getTranslationKey());
        }
    }

    public enum Type {
        PLAIN,
        SPRINKLES,
        STRIPED;

        private final MutableText translationKey;

        Type() {
            this.translationKey = Text.translatable("donut.cook-it." + name().toLowerCase())
                    .formatted(Formatting.ITALIC, Formatting.YELLOW);
        }

        public MutableText getTranslationKey() {
            return translationKey.copy();
        }
    }
}