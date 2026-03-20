package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.WoodType;
import net.minecraft.item.Item;

public class RollingPin extends Item {
    private final WoodType type;
    public RollingPin(Settings settings, WoodType type) {
        super(settings);
        this.type = type;
    }

    public WoodType getWoodType() {
        return type;
    }
}