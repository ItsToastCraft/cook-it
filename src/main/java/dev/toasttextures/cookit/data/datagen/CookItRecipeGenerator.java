package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.block.containers.CuttingBoard;
import dev.toasttextures.cookit.item.RollingPin;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        for (Plate plate : CookItBlocks.PLATES) {
            String color = plate.getColor().getSerializedName();
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, plate).pattern("ccc")
                    .define('c', BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft", color + "_concrete")))
                    .unlockedBy(FabricRecipeProvider.getHasName(plate),
                            FabricRecipeProvider.has(plate))
                    .save(exporter);
        }
        for (Bowl bowl : CookItBlocks.BOWLS) {
            String color = bowl.getColor().getSerializedName();
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, bowl).pattern("ccc").pattern("ccc")
                    .define('c', BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft", color + "_concrete")))
                    .unlockedBy(FabricRecipeProvider.getHasName(bowl),
                            FabricRecipeProvider.has(bowl))
                    .save(exporter);
        }

        for (CuttingBoard cuttingBoard : CookItBlocks.CUTTING_BOARDS) {
            String woodType = cuttingBoard.getWoodType().toString();
            Block slab = BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft", woodType + "_slab"));
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, cuttingBoard).pattern("sss")
                    .define('s', slab)
                    .unlockedBy(FabricRecipeProvider.getHasName(slab),
                    FabricRecipeProvider.has(slab))
                    .save(exporter);

        }

        for (RollingPin rollingPin : ROLLING_PINS) {
            String woodType = rollingPin.getWoodType().toString();
            Block planks = BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft", woodType + "_planks"));
            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, rollingPin).pattern("b").pattern("p").pattern("b")
                    .define('b', BuiltInRegistries.BLOCK.get(new ResourceLocation("minecraft", woodType + "_button")))
                    .define('p', planks)
                    .unlockedBy(FabricRecipeProvider.getHasName(planks),
                            FabricRecipeProvider.has(planks))
                    .save(exporter);
        }
    }
}