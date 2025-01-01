package com.toast.cookit.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import com.toast.cookit.CookIt;

public class CookItDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        // Adding a provider example:
        //
        // pack.addProvider(AdvancementsProvider::new);
        pack.addProvider(CookItModelProvider::new);
        pack.addProvider(CookItLootTables::new);
        pack.addProvider(CookItRecipeGenerator::new);
        pack.addProvider(CookItBlockTagProvider::new);
        pack.addProvider(CookItItemTagProvider::new);
        CookIt.LOGGER.info("Generating assets!");

    }

}

