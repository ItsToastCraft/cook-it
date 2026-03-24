package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static dev.toasttextures.cookit.registries.CookItTags.ROLLING_PINS;

public class CookItItemTagProvider extends FabricTagProvider<Item> {
    public CookItItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ROLLING_PINS).add(CookItItems.ROLLING_PINS.toArray(new Item[0]));
    }
}