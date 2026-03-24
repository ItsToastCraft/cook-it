package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import static dev.toasttextures.cookit.registries.CookItTags.*;

import java.util.concurrent.CompletableFuture;

public class CookItBlockTagProvider extends FabricTagProvider<Block> {
    public CookItBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, RegistryKeys.BLOCK,  completableFuture);
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(CookItBlocks.CUTTING_BOARDS.toArray(new Block[0]));

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(CookItBlocks.PLATES.toArray(new Block[0]))
                .add(CookItBlocks.APPLIANCES.toArray(new Block[0]))
                .add(CookItBlocks.CONTAINERS.toArray(new Block[0]));

        getOrCreateTagBuilder(CONTAINERS).add(CookItBlocks.CONTAINERS.toArray(new Block[0]));
        getOrCreateTagBuilder(APPLIANCES).add(CookItBlocks.APPLIANCES.toArray(new Block[0]));
    }
}
