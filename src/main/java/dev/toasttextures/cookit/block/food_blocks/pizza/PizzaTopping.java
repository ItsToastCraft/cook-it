package dev.toasttextures.cookit.block.food_blocks.pizza;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PizzaTopping {
    public static final Map<Identifier, PizzaTopping> REGISTRY = new HashMap<>();
    public static final String TOPPINGS_KEY = "Toppings";

    private final Text translationKey;
    private final Identifier id;
    private final Item item;
    private final Identifier texture;
    private NbtString nbt;

    private PizzaTopping(Identifier id, Item item, Identifier texture) {
        this.id = id;
        this.translationKey = Text.translatable("topping." + id.getNamespace() + "." + id.getPath()).formatted(Formatting.BLUE);
        this.item = item;
        this.texture = texture;
    }

    public NbtString asNbt() {
        if (nbt == null) {
            nbt = NbtString.of(this.toString());
        }
        return nbt;
    }

    public static PizzaTopping register(Identifier id, Item item, Identifier texture) {
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
    public static PizzaTopping byId(Identifier id) {
        return REGISTRY.get(id);
    }

    public static List<PizzaTopping> getToppings(ItemStack stack) {
        if (stack.isEmpty()) return Collections.emptyList();

        NbtList nbt = parse(stack.getNbt());
        if (nbt == null) return Collections.emptyList();

        return getToppings(nbt);
    }

    public static List<PizzaTopping> getToppings(NbtList list) {
        if (list.isEmpty()) return Collections.emptyList();

        List<PizzaTopping> toppings = new ArrayList<>();
        for (NbtElement element : list) {
            Identifier id = new Identifier(element.asString());
            PizzaTopping topping = byId(id);
            if (topping != null) {
                toppings.add(topping);
            }
        }
        return toppings;
    }

    @Nullable
    public static NbtList parse(@Nullable NbtCompound compound) {
        if (compound == null) {
            return null;
        }
        return compound.getList(TOPPINGS_KEY, NbtElement.STRING_TYPE);
    }

    public Identifier getTexture() {
        return texture;
    }

    public Identifier getId() {
        return id;
    }

    public Text getTranslationKey() {
        return translationKey;
    }

    public Item getItem() {
        return item;
    }
}