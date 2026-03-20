package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.CookIt;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class CookItTags {
    public static final TagKey<Item> FRYABLE = TagKey.of(RegistryKeys.ITEM, CookIt.idOf("fryable"));

}
