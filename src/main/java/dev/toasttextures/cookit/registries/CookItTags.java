package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.CookIt;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class CookItTags {
    public static final TagKey<Item> FRYABLE = TagKey.of(RegistryKeys.ITEM, CookIt.idOf("fryable"));
    public static final TagKey<Block> CONTAINERS = TagKey.of(RegistryKeys.BLOCK,  CookIt.idOf("containers"));
    public static final TagKey<Block> APPLIANCES = TagKey.of(RegistryKeys.BLOCK,  CookIt.idOf("appliances"));

    public static void register() {}
}
