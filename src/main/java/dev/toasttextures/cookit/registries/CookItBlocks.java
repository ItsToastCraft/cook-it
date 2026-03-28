// TODO(Ravel): Failed to fully resolve file: null cannot be cast to non-null type com.intellij.psi.PsiJavaCodeReferenceElement
package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.WoodType;
import dev.toasttextures.cookit.block.appliances.Fryer;
import dev.toasttextures.cookit.block.appliances.Microwave;
import dev.toasttextures.cookit.block.appliances.Oven;
import dev.toasttextures.cookit.block.appliances.Toaster;
import dev.toasttextures.cookit.block.containers.*;
import dev.toasttextures.cookit.block.food.vanilla_vines.VanillaVinePlant;
import dev.toasttextures.cookit.block.food.vanilla_vines.VanillaVineStem;
import dev.toasttextures.cookit.block.food.pizza.CookedPizza;
import dev.toasttextures.cookit.block.food.pizza.Pizza;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CookItBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Plate> PLATES = registerDyed("plate", dyeColor -> FabricBlockSettings.create().strength(0.4f).sounds(SoundType.DECORATED_POT), Plate::new);
    public static final List<Bowl> BOWLS = registerDyed("bowl", dyeColor -> FabricBlockSettings.create().strength(0.4f).sounds(SoundType.DECORATED_POT), Bowl::new);
    public static final List<CuttingBoard> CUTTING_BOARDS = registerWooden("cutting_board", settings -> FabricBlockSettings.copyOf(Blocks.OAK_PLANKS), CuttingBoard::new);

    // -- Appliances --
    public static final Block FRYER = registerBlock("fryer", new Fryer(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).noOcclusion()));
    public static final Block TOASTER = registerBlock("toaster", new Toaster(FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE)));
    public static final Block OVEN = registerBlock("oven", new Oven(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).noOcclusion()));
    public static final Block MICROWAVE = registerBlock("microwave", new Microwave(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).noOcclusion()));
    // -- Food Blocks --
    public static final Block PIZZA = registerBlock("pizza", new CookedPizza(FabricBlockSettings.create()));
    public static final Block UNCOOKED_PIZZA = registerBlock("uncooked_pizza", new Pizza(FabricBlockSettings.create()));
    public static final Block PIZZA_CRUST = registerBlock("pizza_crust", new Pizza(FabricBlockSettings.create()));
    // -- Containers --
    public static final Block MUFFIN_TIN = registerBlock("muffin_tin", new MuffinTin(FabricBlockSettings.create().strength(0.2f).sounds(SoundType.METAL)));
    public static final Block BAKING_SHEET = registerBlock("baking_sheet", new BakingSheet(FabricBlockSettings.copyOf(MUFFIN_TIN)));
    public static final Block PIZZA_PAN = registerBlock("pizza_pan", new PizzaPan(FabricBlockSettings.copyOf(MUFFIN_TIN)));
    public static final Block MIXING_BOWL = registerBlock("mixing_bowl", new MixingBowl(FabricBlockSettings.copyOf(MUFFIN_TIN)));
    // -- Miscellaneous --
    public static final Block VANILLA_VINE_STEM = registerBlock("vanilla_vine_stem", new VanillaVineStem(FabricBlockSettings.copyOf(Blocks.VINE)), false);
    public static final Block VANILLA_VINE = registerBlock("vanilla_vine", new VanillaVinePlant(FabricBlockSettings.copyOf(Blocks.VINE)), false);

    private static <T extends Block> List<T> registerDyed(String suffix, Function<DyeColor, BlockBehaviour.Properties> settingsProvider, BiFunction<BlockBehaviour.Properties, DyeColor, T> block) {
        List<T> list = new ArrayList<>();
        for (DyeColor color : DyeColor.values()) {
            BlockBehaviour.Properties settings = settingsProvider.apply(color);
            list.add(registerBlock(color + "_" + suffix, block.apply(settings, color)));
        }
        return list;
    }

    private static <T extends Block> List<T> registerWooden(String suffix, Function<WoodType, BlockBehaviour.Properties> settingsProvider, BiFunction<BlockBehaviour.Properties, WoodType, T> block) {
        List<T> list = new ArrayList<>();
        for (WoodType type : WoodType.values()) {
            BlockBehaviour.Properties settings = settingsProvider.apply(type);
            list.add(registerBlock(type + "_" + suffix, block.apply(settings, type)));
        }
        return list;
    }

    public static <T extends Block> T registerBlock(String name, T block) {
        return registerBlock(name, block, true);
    }

    public static <T extends Block> T registerBlock(String name, T block, boolean withItem) {
        if (withItem) registerBlockItem(name, block);

        BLOCKS.add(block);
        return Registry.register(BuiltInRegistries.BLOCK, CookIt.idOf(name), block);
    }

    public static void registerBlockItem(String name, Block block) {
        Registry.register(BuiltInRegistries.ITEM, CookIt.idOf(name), new BlockItem(block, new FabricItemSettings()));
    }

    public static void register() {
        PLATES.addAll(registerDyed("large_plate", dyeColor -> FabricBlockSettings.create().strength(0.4f).sounds(SoundType.DECORATED_POT), LargePlate::new));
    }
}