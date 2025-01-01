package dev.toasttextures.cookit.registries;
import dev.toasttextures.cookit.block.appliances.*;
import dev.toasttextures.cookit.block.containers.*;
import dev.toasttextures.cookit.block.food_blocks.pizza.CookedPizza;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.Bench;
import dev.toasttextures.cookit.block.food_blocks.pizza.Pizza;

import java.util.ArrayList;
import java.util.List;

import static dev.toasttextures.cookit.CookIt.SUPPORTED_WOOD_TYPES;

public class CookItBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Plate> PLATES = new ArrayList<>();
    public static final List<Bowl> BOWLS = new ArrayList<>();
    public static final List<Block> CUTTING_BOARDS = new ArrayList<>();
    public static final List<Block> APPLIANCES = new ArrayList<>();
    public static final List<Block> CONTAINERS = new ArrayList<>();

    // -- Appliances --

    public static final Block FRYER = registerBlock("fryer", new Fryer(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block TOASTER = registerBlock("toaster", new Toaster(FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE)));
    public static final Block OVEN = registerBlock("oven", new Oven(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block MICROWAVE = registerBlock("microwave", new Microwave(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    // -- Food Blocks --
    public static final Block PIZZA = registerBlock("pizza", new CookedPizza(FabricBlockSettings.create()));
    public static final Block UNCOOKED_PIZZA = registerBlock("uncooked_pizza", new Pizza(FabricBlockSettings.create()));
    public static final Block PIZZA_CRUST = registerBlock("pizza_crust", new Pizza(FabricBlockSettings.create()));

    // -- Containers --
    public static final Block MUFFIN_TIN = registerBlock("muffin_tin", new MuffinTin(FabricBlockSettings.create().strength(0.2f)));
    public static final Block BAKING_SHEET = registerBlock("baking_sheet", new BakingSheet(FabricBlockSettings.copyOf(MUFFIN_TIN)));
    public static final Block PIZZA_PAN = registerBlock("pizza_pan", new PizzaPan(FabricBlockSettings.copyOf(MUFFIN_TIN)));
    public static final Block MIXING_BOWL = registerBlock("mixing_bowl", new MixingBowl(FabricBlockSettings.copyOf(MUFFIN_TIN)));

    // -- Miscellaneous --
    public static final Block BENCH = registerBlock("bench", new Bench(FabricBlockSettings.create()));


    public static void registerColoredBlocks() {
        for (DyeColor color : DyeColor.values()) {
            Block PLATE = registerBlock(color + "_plate", new Plate(FabricBlockSettings.create().strength(0.4f).sounds(BlockSoundGroup.DECORATED_POT)));
            Block LARGE_PLATE = registerBlock(color + "_large_plate", new Plate(FabricBlockSettings.create().strength(0.4f).sounds(BlockSoundGroup.DECORATED_POT)));
            Block BOWL = registerBlock(color + "_bowl", new Bowl(FabricBlockSettings.create().strength(0.4f).sounds(BlockSoundGroup.DECORATED_POT)));
            PLATES.add((Plate) PLATE);
            PLATES.add((Plate) LARGE_PLATE);
            BOWLS.add((Bowl) BOWL);
        }
    }
    public static void registerWoodenBlocks() {

        for (String woodType : SUPPORTED_WOOD_TYPES) {
            Block CUTTING_BOARD = registerBlock(woodType + "_cutting_board", new CuttingBoard(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS)));
            CUTTING_BOARDS.add(CUTTING_BOARD);
        }
    }
    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        BLOCKS.add(block);
        return Registry.register(Registries.BLOCK, new Identifier(CookIt.MOD_ID, name), block);
    }

    public static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(CookIt.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerBlocks() {
        registerColoredBlocks();
        registerWoodenBlocks();
        APPLIANCES.add(FRYER);
        APPLIANCES.add(TOASTER);
        APPLIANCES.add(OVEN);
        APPLIANCES.add(MICROWAVE);
        CONTAINERS.add(MUFFIN_TIN);
        CONTAINERS.add(BAKING_SHEET);
        CONTAINERS.add(MIXING_BOWL);
        CONTAINERS.add(PIZZA_PAN);
    }


}
