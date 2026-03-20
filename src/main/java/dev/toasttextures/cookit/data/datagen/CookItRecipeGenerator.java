package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.containers.CuttingBoard;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.block.containers.Bowl;
import dev.toasttextures.cookit.block.containers.Plate;
import dev.toasttextures.cookit.registries.CookItBlocks;

import java.util.function.Consumer;

import static dev.toasttextures.cookit.registries.CookItItems.ROLLING_PINS;

public class CookItRecipeGenerator extends FabricRecipeProvider {
    public CookItRecipeGenerator(FabricDataOutput output) {
        super(output);
    }
    
    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        for (Plate plate : CookItBlocks.PLATES) {
            String color = plate.getColor().asString();
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, plate).pattern("ccc")
                    .input('c', Registries.BLOCK.get(new Identifier("minecraft", color + "_concrete")))
                    .criterion(FabricRecipeProvider.hasItem(plate),
                            FabricRecipeProvider.conditionsFromItem(plate))
                    .offerTo(exporter);
        }
        for (Bowl bowl : CookItBlocks.BOWLS) {
            String color = bowl.getColor().asString();
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, bowl).pattern("ccc").pattern("ccc")
                    .input('c', Registries.BLOCK.get(new Identifier("minecraft", color + "_concrete")))
                    .criterion(FabricRecipeProvider.hasItem(bowl),
                            FabricRecipeProvider.conditionsFromItem(bowl))
                    .offerTo(exporter);
        }

        for (CuttingBoard cuttingBoard : CookItBlocks.CUTTING_BOARDS) {
            String woodType = cuttingBoard.getType().toString();
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

