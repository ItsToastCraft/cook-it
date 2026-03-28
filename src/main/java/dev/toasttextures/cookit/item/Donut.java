package dev.toasttextures.cookit.item;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Donut extends Item {
    private final Type type;

    public Donut(Properties settings, Type type) {
        super(settings);
        this.type = type;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (stack.getItem() instanceof Donut donut) {
            tooltip.add(donut.type.getTranslationKey());
        }
    }

    public enum Type {
        PLAIN,
        SPRINKLES,
        STRIPED;

        private final Component translationKey;

        Type() {
            this.translationKey = Component.translatable("donut.cook-it." + name().toLowerCase())
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.YELLOW);
        }

        public Component getTranslationKey() {
            return translationKey;
        }
    }
}