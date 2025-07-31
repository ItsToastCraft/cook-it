package dev.toasttextures.cookit.datagen;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class CookItItemTagProvider extends FabricTagProvider<Item> {

    public static final TagKey<Item> ROLLING_PINS = TagKey.of(RegistryKeys.ITEM, new Identifier(CookIt.MOD_ID, "rolling_pins"));

    public CookItItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ROLLING_PINS).add(CookItItems.ROLLING_PINS.toArray(new Item[0]));
    }
}