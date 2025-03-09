package dev.toasttextures.cookit.datagen;

import dev.toasttextures.cookit.registry.CookItBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import static dev.toasttextures.cookit.CookIt.MOD_ID;

import java.util.concurrent.CompletableFuture;

public class CookItBlockTagProvider extends FabricTagProvider<Block> {
    public CookItBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, RegistryKeys.BLOCK,  completableFuture);
    }
    public static final TagKey<Block> CONTAINERS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "containers"));
    public static final TagKey<Block> APPLIANCES = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "appliances"));
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
