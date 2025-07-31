package dev.toasttextures.cookit.block.food_blocks.pizza;

import dev.toasttextures.cookit.CookIt;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record PizzaTopping(String name, @NotNull Item item, Identifier texture) {
    public static final Map<String, PizzaTopping> toppings = new HashMap<>();
    public static final PizzaTopping EMPTY = new PizzaTopping("air", Items.AIR, new Identifier("air"));

    public PizzaTopping {
        CookIt.LOGGER.info("PizzaTopping has been created as {}", this.name());
        toppings.putIfAbsent(this.name(), this);
    }
    public static PizzaTopping fromItem(Item item) {
        return toppings.values().stream().filter(pizzaTopping -> pizzaTopping.item.equals(item)).findFirst().orElse(EMPTY);
    }
    public static PizzaTopping fromString(String string) {
        return toppings.get(string);
    }

    public Identifier getIdentifier() {
        return Registries.ITEM.getId(item);
    }

    public static List<PizzaTopping> fromNbt(NbtCompound tag) {
        List<PizzaTopping> result = new ArrayList<>();
        if (tag.contains("toppings",NbtElement.LIST_TYPE)) {
            NbtList toppingsList = tag.getList("toppings", NbtElement.STRING_TYPE);
            for (NbtElement element : toppingsList) {
                    PizzaTopping topping = toppings.get(element.asString());
                    if (topping != null) {
                        result.add(topping);
                    }
                }
            }
        return result;
    }

    public static NbtCompound toNbt(ArrayList<PizzaTopping> toppings) {
        NbtCompound tag = new NbtCompound();
        NbtList toppingsList = new NbtList();
        for (PizzaTopping topping : toppings) {
            toppingsList.add(NbtString.of(topping.name()));
        }
        tag.put("toppings", toppingsList);
        return tag;
    }
    public MutableText getTranslationKey() {
        return Text.translatable("topping.cook-it." + this.name);
    }

    @Override
    public @NotNull String toString() {
        return "PizzaTopping{" +
                "name='" + name + '\'' +
                ", item=" + item +
                '}';
    }
}