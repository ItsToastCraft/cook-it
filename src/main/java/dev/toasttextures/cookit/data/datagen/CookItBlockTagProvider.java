package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.registries.CookItBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import static dev.toasttextures.cookit.registries.CookItTags.*;

import java.util.concurrent.CompletableFuture;

public class CookItBlockTagProvider extends FabricTagProvider<Block> {
    public CookItBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.BLOCK,  completableFuture);
    }
    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(CookItBlocks.CUTTING_BOARDS.toArray(new Block[0]));

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(CookItBlocks.PLATES.toArray(new Block[0]))
                .add(CookItBlocks.APPLIANCES.toArray(new Block[0]))
                .add(CookItBlocks.CONTAINERS.toArray(new Block[0]));

        tag(CONTAINERS).add(CookItBlocks.CONTAINERS.toArray(new Block[0]));
        tag(APPLIANCES).add(CookItBlocks.APPLIANCES.toArray(new Block[0]));
    }
}
