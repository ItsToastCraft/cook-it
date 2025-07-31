package dev.toasttextures.cookit.item;

import net.minecraft.item.Item;

public class RollingPin extends Item {
    private final String woodType;
    public RollingPin(Settings settings, String woodType) {
        super(settings);
        this.woodType = woodType;
    }

    public String getWoodType() {
        return woodType;
    }
}