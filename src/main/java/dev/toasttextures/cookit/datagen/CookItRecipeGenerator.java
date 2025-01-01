package dev.toasttextures.cookit.datagen;

import dev.toasttextures.cookit.CookIt;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.block.containers.Bowl;
import dev.toasttextures.cookit.block.containers.Plate;
import dev.toasttextures.cookit.registries.CookItBlocks;

import static dev.toasttextures.cookit.registries.CookItItems.ROLLING_PINS;

public class CookItRecipeGenerator extends FabricRecipeProvider {


    public CookItRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    public static String getColor(Plate plate) {
        return Registries.BLOCK.getId(plate).getPath().replace(Plate.isLargePlate(plate) ? "_large_plate" : "_plate", "");
    }

    public static String getColor(Bowl bowl) {
        return Registries.BLOCK.getId(bowl).getPath().replace("_bowl", "");
    }

    @Override
    public void generate(RecipeExporter exporter) {
        for (Plate plate : CookItBlocks.PLATES) {
            String color = getColor(plate);
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, plate).pattern("ccc")
                    .input('c', Registries.BLOCK.get(new Identifier("minecraft", color + "_concrete")))
                    .criterion(FabricRecipeProvider.hasItem(plate),
                            FabricRecipeProvider.conditionsFromItem(plate))
                    .offerTo(exporter);
        }
        for (Bowl bowl : CookItBlocks.BOWLS) {
            String color = getColor(bowl);
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, bowl).pattern("ccc").pattern("ccc")
                    .input('c', Registries.BLOCK.get(new Identifier("minecraft", color + "_concrete")))
                    .criterion(FabricRecipeProvider.hasItem(bowl),
                            FabricRecipeProvider.conditionsFromItem(bowl))
                    .offerTo(exporter);
        }

        for (Block cuttingBoard : CookItBlocks.CUTTING_BOARDS) {
            String woodType = CookIt.SUPPORTED_WOOD_TYPES.get(CookItBlocks.CUTTING_BOARDS.indexOf(cuttingBoard));
            Block slab = Registries.BLOCK.get(new Identifier("minecraft", woodType + "_slab"));
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, cuttingBoard).pattern("sss")
                    .input('s', slab)
                    .criterion(FabricRecipeProvider.hasItem(slab),
                    FabricRecipeProvider.conditionsFromItem(slab))
                    .offerTo(exporter);

        }

        for (Item rollingPin : ROLLING_PINS) {
            String woodType = CookIt.SUPPORTED_WOOD_TYPES.get(ROLLING_PINS.indexOf(rollingPin));
            Block planks = Registries.BLOCK.get(new Identifier("minecraft", woodType + "_planks"));
            ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, rollingPin).pattern("b").pattern("p").pattern("b")
                    .input('b', Registries.BLOCK.get(new Identifier("minecraft", woodType + "_button")))
                    .input('p', planks)
                    .criterion(FabricRecipeProvider.hasItem(planks),
                            FabricRecipeProvider.conditionsFromItem(planks))
                    .offerTo(exporter);
        }
    }
}

