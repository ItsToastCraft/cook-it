package dev.toasttextures.cookit.registry;
import dev.toasttextures.cookit.block.appliances.*;
import dev.toasttextures.cookit.block.containers.*;
import dev.toasttextures.cookit.block.food_blocks.LettucePlant;
import dev.toasttextures.cookit.block.food_blocks.TomatoPlant;
import dev.toasttextures.cookit.block.food_blocks.VanillaVinePlant;
import dev.toasttextures.cookit.block.food_blocks.VanillaVineStem;
import dev.toasttextures.cookit.block.food_blocks.pizza.CookedPizza;
import net.minecraft.block.AbstractBlock;
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

    public static final Block FRYER = registerBlock("fryer", new Fryer(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block TOASTER = registerBlock("toaster", new Toaster(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)));
    public static final Block OVEN = registerBlock("oven", new Oven(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block MICROWAVE = registerBlock("microwave", new Microwave(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).nonOpaque()));

    // -- Food Blocks --
    public static final Block PIZZA = registerBlock("pizza", new CookedPizza(AbstractBlock.Settings.create()));
    public static final Block UNCOOKED_PIZZA = registerBlock("uncooked_pizza", new Pizza(AbstractBlock.Settings.create()));
    public static final Block PIZZA_CRUST = registerBlock("pizza_crust", new Pizza(AbstractBlock.Settings.create()));

    // -- Crops --
    public static final Block LETTUCE = registerBlock("lettuce", new LettucePlant(AbstractBlock.Settings.copy(Blocks.WHEAT)));
    public static final Block TOMATO = registerBlockNoItem("tomato", new TomatoPlant(AbstractBlock.Settings.copy(Blocks.WHEAT)));
    // -- Containers --
    public static final Block MUFFIN_TIN = registerBlock("muffin_tin", new MuffinTin(AbstractBlock.Settings.create().strength(0.2f)));
    public static final Block BAKING_SHEET = registerBlock("baking_sheet", new BakingSheet(AbstractBlock.Settings.copy(MUFFIN_TIN)));
    public static final Block PIZZA_PAN = registerBlock("pizza_pan", new PizzaPan(AbstractBlock.Settings.copy(MUFFIN_TIN)));
    public static final Block MIXING_BOWL = registerBlock("mixing_bowl", new MixingBowl(AbstractBlock.Settings.copy(MUFFIN_TIN)));

    // -- Miscellaneous --
    public static final Block VANILLA_VINE_STEM = registerBlockNoItem("vanilla_vine_stem", new VanillaVineStem(AbstractBlock.Settings.copy(Blocks.VINE)));
    public static final Block VANILLA_VINE = registerBlockNoItem("vanilla_vine", new VanillaVinePlant(AbstractBlock.Settings.copy(Blocks.VINE)));

    public static void registerColoredBlocks() {
        for (DyeColor color : DyeColor.values()) {
            Block PLATE = registerBlock(color + "_plate", new Plate(AbstractBlock.Settings.create().strength(0.4f).sounds(BlockSoundGroup.DECORATED_POT)));
            Block LARGE_PLATE = registerBlock(color + "_large_plate", new Plate(AbstractBlock.Settings.create().strength(0.4f).sounds(BlockSoundGroup.DECORATED_POT)));
            Block BOWL = registerBlock(color + "_bowl", new Bowl(AbstractBlock.Settings.create().strength(0.4f).sounds(BlockSoundGroup.DECORATED_POT)));
            PLATES.add((Plate) PLATE);
            PLATES.add((Plate) LARGE_PLATE);
            BOWLS.add((Bowl) BOWL);
        }
    }
    public static void registerWoodenBlocks() {
        for (String woodType : SUPPORTED_WOOD_TYPES) {
            Block CUTTING_BOARD = registerBlock(woodType + "_cutting_board", new CuttingBoard(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS)));
            CUTTING_BOARDS.add(CUTTING_BOARD);
        }
    }
    public static Block registerBlockNoItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(CookIt.MOD_ID, name), block);
    }
    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        BLOCKS.add(block);
        return Registry.register(Registries.BLOCK, Identifier.of(CookIt.MOD_ID, name), block);
    }

    public static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(CookIt.MOD_ID, name), new BlockItem(block, new Item.Settings()));
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