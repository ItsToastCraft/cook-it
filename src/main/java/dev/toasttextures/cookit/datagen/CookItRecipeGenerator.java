package dev.toasttextures.cookit.datagen;

import dev.toasttextures.cookit.block.containers.CuttingBoard;
import dev.toasttextures.cookit.item.RollingPin;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
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

    @Override
    public void generate(RecipeExporter exporter) {
        for (Plate plate : CookItBlocks.PLATES) {
            String color = plate.getColor();
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, plate).pattern("ccc")
                    .input('c', Registries.BLOCK.get(new Identifier("minecraft", color + "_concrete")))
                    .criterion(FabricRecipeProvider.hasItem(plate),
                            FabricRecipeProvider.conditionsFromItem(plate))
                    .offerTo(exporter);
        }
        for (Bowl bowl : CookItBlocks.BOWLS) {
            String color = bowl.getColor();
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, bowl).pattern("ccc").pattern("ccc")
                    .input('c', Registries.BLOCK.get(new Identifier("minecraft", color + "_concrete")))
                    .criterion(FabricRecipeProvider.hasItem(bowl),
                            FabricRecipeProvider.conditionsFromItem(bowl))
                    .offerTo(exporter);
        }

        for (CuttingBoard cuttingBoard : CookItBlocks.CUTTING_BOARDS) {
            Block slab = Registries.BLOCK.get(new Identifier("minecraft", cuttingBoard.getWoodType() + "_slab"));
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, cuttingBoard).pattern("sss")
                    .input('s', slab)
                    .criterion(FabricRecipeProvider.hasItem(slab),
                    FabricRecipeProvider.conditionsFromItem(slab))
                    .offerTo(exporter);

        }

        for (RollingPin rollingPin : ROLLING_PINS) {

            Block planks = Registries.BLOCK.get(new Identifier("minecraft", rollingPin.getWoodType() + "_planks"));
            ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, rollingPin).pattern("b").pattern("p").pattern("b")
                    .input('b', Registries.BLOCK.get(new Identifier("minecraft", rollingPin + "_button")))
                    .input('p', planks)
                    .criterion(FabricRecipeProvider.hasItem(planks),
                            FabricRecipeProvider.conditionsFromItem(planks))
                    .offerTo(exporter);
        }
    }
}

