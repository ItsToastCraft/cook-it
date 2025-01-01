package com.toast.cookit.block.food_blocks.pizza;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum PizzaToppings implements StringIdentifiable {
    PORKCHOP(Items.PORKCHOP, "porkchop"),
    BEEF(Items.BEEF, "beef"),
    KELP(Items.KELP, "kelp");

    private final Item item;
    private final String name;
    PizzaToppings(Item item, String name) {
        this.item = item;
        this.name = name;
    }

    public Item getItem() {
        return this.item;
    }

    public static PizzaToppings fromItem(Item item) {
        for (PizzaToppings topping : values()) {
            if (topping.getItem().equals(item)) {
                return topping;
            }
        }
        return null;
    }

    public static PizzaToppings fromName(String name) {
        for (PizzaToppings topping : values()) {
            if (topping.asString().equals(name)) {
                return topping;
            }
        }
        return null;
    }

    public MutableText getTranslationKey() {
        return Text.translatable("topping.cook-it." + this.name);
    }

    @Override
    public String asString() {
        return this.name;
    }
}
