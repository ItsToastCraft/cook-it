package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.enums.FoodTypes;
import net.minecraft.item.Item;

public class CookItFood extends Item {

    private final FoodTypes foodType;

    public CookItFood(Settings settings, FoodTypes foodType) {
        super(settings);
        this.foodType = foodType;
    }

    public FoodTypes getFoodType() { return this.foodType; }
}
