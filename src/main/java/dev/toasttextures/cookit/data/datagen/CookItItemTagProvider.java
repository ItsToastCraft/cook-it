package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import static dev.toasttextures.cookit.registries.CookItTags.FRYABLE;
import static dev.toasttextures.cookit.registries.CookItTags.ROLLING_PINS;

public class CookItItemTagProvider extends FabricTagProvider<Item> {
    public CookItItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(ROLLING_PINS).add(CookItItems.ROLLING_PINS.toArray(Item[]::new));

        getOrCreateTagBuilder(FRYABLE).add(CookItItems.UNCOOKED_FRIES, CookItItems.RAW_DONUT);
    }
}