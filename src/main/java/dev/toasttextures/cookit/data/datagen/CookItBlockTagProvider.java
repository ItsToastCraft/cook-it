package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.registries.CookItTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import static dev.toasttextures.cookit.registries.CookItBlocks.*;

import java.util.concurrent.CompletableFuture;

public class CookItBlockTagProvider extends FabricTagProvider<Block> {
    public CookItBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.BLOCK,  completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .add(CUTTING_BOARDS.toArray(Block[]::new));

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(PLATES.toArray(Block[]::new))
            .addTag(CookItTags.APPLIANCES)
            .addTag(CookItTags.CONTAINERS);

        getOrCreateTagBuilder(CookItTags.CONTAINERS)
            .add(MUFFIN_TIN, BAKING_SHEET, MIXING_BOWL, PIZZA_PAN);

        getOrCreateTagBuilder(CookItTags.APPLIANCES)
            .add(FRYER, TOASTER, OVEN, MICROWAVE);
    }
}