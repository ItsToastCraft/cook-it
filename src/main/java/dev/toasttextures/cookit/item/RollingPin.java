package dev.toasttextures.cookit.item;

import dev.toasttextures.cookit.block.WoodType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class RollingPin extends Item {
    private final WoodType type;
    public RollingPin(Properties settings, WoodType type) {
        super(settings);
        this.type = type;
    }

    public WoodType getWoodType() {
        return type;
    }
}