package dev.toasttextures.cookit.block.food.pizza;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PizzaTopping {
    public static final Map<ResourceLocation, PizzaTopping> REGISTRY = new HashMap<>();
    public static final String TOPPINGS_KEY = "Toppings";

    private final Component translationKey;
    private final ResourceLocation id;
    private final Item item;
    private final ResourceLocation texture;
    private StringTag nbt;

    private PizzaTopping(ResourceLocation id, Item item, ResourceLocation texture) {
        this.id = id;
        this.translationKey = Component.translatable("topping." + id.getNamespace() + "." + id.getPath()).withStyle(ChatFormatting.BLUE);
        this.item = item;
        this.texture = texture;
    }

    public StringTag asNbt() {
        if (nbt == null) {
            nbt = StringTag.valueOf(this.toString());
        }
        return nbt;
    }

    public static PizzaTopping register(ResourceLocation id, Item item, ResourceLocation texture) {
        return REGISTRY.computeIfAbsent(id, (_id -> {
            PizzaTopping fromItem = byItem(item);
            if (fromItem != null) {
                return fromItem;
            }
            return new PizzaTopping(id, item, texture);
        }));
    }

    public static PizzaTopping byItem(Item item) {
        for (PizzaTopping topping : REGISTRY.values()) {
            if (topping.item == item) {
                return topping;
            }
        }
        return null;
    }

    @Nullable
    public static PizzaTopping byId(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static List<PizzaTopping> getToppings(ItemStack stack) {
        if (stack.isEmpty()) return Collections.emptyList();

        ListTag nbt = parse(stack.getTag());
        if (nbt == null) return Collections.emptyList();

        return getToppings(nbt);
    }

    public static List<PizzaTopping> getToppings(ListTag list) {
        if (list.isEmpty()) return Collections.emptyList();

        List<PizzaTopping> toppings = new ArrayList<>();
        for (Tag element : list) {
            ResourceLocation id = new ResourceLocation(element.getAsString());
            PizzaTopping topping = byId(id);
            if (topping != null) {
                toppings.add(topping);
            }
        }
        return toppings;
    }

    @Nullable
    public static ListTag parse(@Nullable CompoundTag compound) {
        if (compound == null) {
            return null;
        }
        return compound.getList(TOPPINGS_KEY, Tag.TAG_STRING);
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Component getTranslationKey() {
        return translationKey;
    }

    public Item getItem() {
        return item;
    }
}