package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.registries.CookItFoodTypes;
import net.minecraft.item.Item;

public class CookItFood extends Item {

    private final CookItFoodTypes foodType;

    public CookItFood(Settings settings, CookItFoodTypes foodType) {
        super(settings);
        this.foodType = foodType;
    }

    public CookItFoodTypes getFoodType() { return this.foodType; }
}
