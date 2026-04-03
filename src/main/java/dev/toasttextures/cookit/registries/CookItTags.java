package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.CookIt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;

public final class CookItTags {
    public static final TagKey<Item> FRYABLE = TagKey.create(Registries.ITEM, CookIt.idOf("fryable"));
    public static final TagKey<Block> CONTAINERS = TagKey.create(Registries.BLOCK,  CookIt.idOf("containers"));
    public static final TagKey<Block> APPLIANCES = TagKey.create(Registries.BLOCK,  CookIt.idOf("appliances"));
    public static final TagKey<Item> ROLLING_PINS = TagKey.create(Registries.ITEM, new ResourceLocation(CookIt.MOD_ID, "rolling_pins"));

    public static final TagKey<Item> MUFFIN = TagKey.create(Registries.ITEM, CookIt.idOf("muffins"));

    public static void register() {}
}