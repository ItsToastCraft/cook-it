package toast.cook_it.registries;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;

import toast.cook_it.block.appliances.Toaster;
import toast.cook_it.block.Bench;


import toast.cook_it.block.appliances.microwave.Microwave;
import toast.cook_it.block.appliances.oven.Oven;
import toast.cook_it.block.containers.CuttingBoard;
import toast.cook_it.block.containers.baking_sheet.BakingSheet;
import toast.cook_it.block.containers.plate.Plate;
import toast.cook_it.block.food_blocks.pizza.Pizza;

import java.util.ArrayList;
import java.util.List;

public class CookItBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Block> PLATES = new ArrayList<>();

    public static final Block CUTTING_BOARD = registerBlock("cutting_board", new CuttingBoard(FabricBlockSettings.copyOf(Blocks.SPRUCE_PLANKS)));
    public static final Block TOASTER = registerBlock("toaster", new Toaster(FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE)));
    public static final Block BAKING_SHEET = registerBlock("baking_sheet", new BakingSheet(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    public static final Block BENCH = registerBlock("bench", new Bench(FabricBlockSettings.create()));
    public static final Block OVEN = registerBlock("oven", new Oven(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block MICROWAVE = registerBlock("microwave", new Microwave(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block RED_PLATE = registerBlock("red_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block RED_LARGE_PLATE = registerBlock("red_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block ORANGE_PLATE = registerBlock("orange_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block ORANGE_LARGE_PLATE = registerBlock("orange_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block YELLOW_PLATE = registerBlock("yellow_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block YELLOW_LARGE_PLATE = registerBlock("yellow_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block LIME_PLATE = registerBlock("lime_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block LIME_LARGE_PLATE = registerBlock("lime_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block GREEN_PLATE = registerBlock("green_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block GREEN_LARGE_PLATE = registerBlock("green_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block CYAN_PLATE = registerBlock("cyan_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block CYAN_LARGE_PLATE = registerBlock("cyan_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block LIGHT_BLUE_PLATE = registerBlock("light_blue_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block LIGHT_BLUE_LARGE_PLATE = registerBlock("light_blue_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block BLUE_PLATE = registerBlock("blue_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block BLUE_LARGE_PLATE = registerBlock("blue_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block PURPLE_PLATE = registerBlock("purple_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block PURPLE_LARGE_PLATE = registerBlock("purple_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block MAGENTA_PLATE = registerBlock("magenta_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block MAGENTA_LARGE_PLATE = registerBlock("magenta_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block PINK_PLATE = registerBlock("pink_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block PINK_LARGE_PLATE = registerBlock("pink_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block BROWN_PLATE = registerBlock("brown_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block BROWN_LARGE_PLATE = registerBlock("brown_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block BLACK_PLATE = registerBlock("black_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block BLACK_LARGE_PLATE = registerBlock("black_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block LIGHT_GRAY_PLATE = registerBlock("light_gray_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block LIGHT_GRAY_LARGE_PLATE = registerBlock("light_gray_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block GRAY_PLATE = registerBlock("gray_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block GRAY_LARGE_PLATE = registerBlock("gray_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));

    public static final Block WHITE_PLATE = registerBlock("white_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block WHITE_LARGE_PLATE = registerBlock("white_large_plate", new Plate(FabricBlockSettings.copyOf(Blocks.TERRACOTTA)));
    public static final Block PIZZA = registerBlock("pizza", new Pizza(FabricBlockSettings.copyOf(Blocks.CAKE)));


    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        CookItBlocks.BLOCKS.add(block);
        if (block instanceof Plate) {
            CookItBlocks.PLATES.add(block);
        }
        return Registry.register(Registries.BLOCK, new Identifier(CookIt.MOD_ID, name), block);
    }

    public static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(CookIt.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerBlocks() {

    }


}
