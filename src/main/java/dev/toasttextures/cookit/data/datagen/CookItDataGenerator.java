package dev.toasttextures.cookit.data.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CookItDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(CookItModelProvider::new);
        pack.addProvider(CookItLootTables::new);
        pack.addProvider(CookItRecipeGenerator::new);
        pack.addProvider(CookItBlockTagProvider::new);
        pack.addProvider(CookItItemTagProvider::new);
    }
}

