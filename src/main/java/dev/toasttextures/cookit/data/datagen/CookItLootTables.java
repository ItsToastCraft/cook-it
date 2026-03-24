package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.block.containers.Plate;
import dev.toasttextures.cookit.block.food.vanilla_vines.VanillaVines;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyStateFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;

import java.util.ArrayList;
import java.util.List;

public class CookItLootTables extends FabricBlockLootTableProvider {
    List<Block> blocks = new ArrayList<>(CookItBlocks.BLOCKS);

    public CookItLootTables(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        blocks.removeAll(List.of(
                CookItBlocks.BAKING_SHEET,
                CookItBlocks.PIZZA_PAN,
                CookItBlocks.PIZZA,
                CookItBlocks.VANILLA_VINE,
                CookItBlocks.VANILLA_VINE_STEM
        ));
        blocks.removeAll(CookItBlocks.PLATES);

        for (Block block : blocks) {
            addDrop(block, drops(block));
        }

        addDrop(CookItBlocks.VANILLA_VINE, (Block block) -> LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(ItemEntry.builder(CookItItems.VANILLA_BEAN))
                        .conditionally(BlockStatePropertyLootCondition.builder(block)
                        .properties(StatePredicate.Builder.create()
                        .exactMatch(VanillaVines.PLANT_STATE, VanillaVines.Stage.HARVESTABLE)))));

        for (Block plate : CookItBlocks.PLATES) {
            addDrop(plate, (Block block) -> LootTable.builder().pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0f))
                    .with(ItemEntry.builder(plate.asItem()).apply(CopyStateFunction.builder(block).addProperty(Plate.COUNT)))));
        }
    }
}