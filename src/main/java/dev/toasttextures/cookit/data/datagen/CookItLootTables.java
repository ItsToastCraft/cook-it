package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.block.containers.Plate;
import dev.toasttextures.cookit.block.food.vanilla_vines.VanillaVines;
import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;

import java.util.List;

public class CookItLootTables extends FabricBlockLootTableProvider {
    List<Block> blocks = List.copyOf(CookItBlocks.BLOCKS);

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
            add(block, createSingleItemTable(block));
        }

        add(CookItBlocks.VANILLA_VINE, (Block block) -> LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).add(LootItem.lootTableItem(CookItItems.VANILLA_BEAN))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(VanillaVines.PLANT_STATE, VanillaVines.Stage.HARVESTABLE)))));

        for (Block plate : CookItBlocks.PLATES) {
            add(plate, (Block block) -> LootTable.lootTable().withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0f))
                    .add(LootItem.lootTableItem(plate.asItem()).apply(CopyBlockState.copyState(block).copy(Plate.COUNT)))));
        }
    }
}