package dev.toasttextures.cookit.datagen;

import dev.toasttextures.cookit.block.containers.Plate;
import dev.toasttextures.cookit.block.food_blocks.VanillaVines;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;

import java.util.List;

public class CookItLootTables extends FabricBlockLootTableProvider {
    List<Block> blocks = CookItBlocks.BLOCKS;

    public CookItLootTables(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        blocks.remove(CookItBlocks.BAKING_SHEET);
        blocks.remove(CookItBlocks.PIZZA_PAN);
        blocks.remove(CookItBlocks.PIZZA);
        blocks.remove(CookItBlocks.PLATES.toArray(new Block[0]));
        blocks.remove(CookItBlocks.VANILLA_VINE);
        blocks.remove(CookItBlocks.VANILLA_VINE_STEM);
        addDrop(CookItBlocks.VANILLA_VINE, (Block block) -> LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(ItemEntry.builder(CookItItems.VANILLA_BEAN))
                        .conditionally(BlockStatePropertyLootCondition.builder(block)
                        .properties(StatePredicate.Builder.create()
                        .exactMatch(VanillaVines.PLANT_STATE, 2)))));
        for (Block block : blocks) {
            addDrop(block, drops(block));
        }
        for (Block plate : CookItBlocks.PLATES) {
            addDrop(plate, (Block block) -> LootTable.builder()
                    .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(ItemEntry.builder(plate.asItem())
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f))
                            .conditionally(BlockStatePropertyLootCondition.builder(block)
                            .properties(StatePredicate.Builder.create()
                            .exactMatch(Plate.PLATES_AMOUNT, 1))))
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0f))
                            .conditionally(BlockStatePropertyLootCondition.builder(block)
                            .properties(StatePredicate.Builder.create()
                            .exactMatch(Plate.PLATES_AMOUNT, 2))))
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0f))
                            .conditionally(BlockStatePropertyLootCondition.builder(block)
                            .properties(StatePredicate.Builder.create()
                            .exactMatch(Plate.PLATES_AMOUNT, 3))))
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0f))
                            .conditionally(BlockStatePropertyLootCondition.builder(block)
                            .properties(StatePredicate.Builder.create()
                            .exactMatch(Plate.PLATES_AMOUNT, 4))))
                    )));
            // Nvm I don't like this I'll just have the plate drop the content later
//                    .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).with(ItemEntry.builder(plate.asItem())
//                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0f))) // Always one plate
//                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
//                        .withOperation("Items", "BlockEntityTag.Items", CopyNbtLootFunction.Operator.REPLACE)))
//                    ));
        }
    }

}
