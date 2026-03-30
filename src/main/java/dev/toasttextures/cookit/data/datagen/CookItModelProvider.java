package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.appliances.Toaster;
import dev.toasttextures.cookit.block.containers.*;
import dev.toasttextures.cookit.block.food.vanilla_vines.VanillaVines;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.core.Direction;

import java.util.List;

import static dev.toasttextures.cookit.data.datagen.CookItModels.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class CookItModelProvider extends FabricModelProvider {

    public CookItModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators modelGenerator) {
        generatePlates(modelGenerator);
        generateBowls(modelGenerator);
        generateCuttingBoards(modelGenerator);
        generateVanillaBeanVines(modelGenerator);
        generateToaster(modelGenerator);
        generateFryer(modelGenerator);
        generateMicrowave(modelGenerator);
        generateMixingBowl(modelGenerator);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        for (Item rollingPin : CookItItems.ROLLING_PINS) {
            ROLLING_PIN_TEMPLATE.create(ModelLocationUtils.getModelLocation(rollingPin), TextureMapping.singleSlot(ROLLING_PIN_KEY, CookIt.idOf("item/" + BuiltInRegistries.ITEM.getKey(rollingPin).getPath())), itemModelGenerator.output);
        }
    }

    private static void generatePlates(BlockModelGenerators modelGenerator) {
        for (Plate plate : CookItBlocks.PLATES) {
            TextureMapping textureMap = coloredTextureMap(PLATE_KEY, plate, "plate");
            List<ModelTemplate> templateSet = (plate instanceof LargePlate) ? LARGE_PLATE_TEMPLATE : PLATE_TEMPLATE;
            PropertyDispatch.C1<Integer> variantMap = PropertyDispatch.property(Plate.COUNT);

            for (int i = 0; i < 4; i++) {
                ModelTemplate model = templateSet.get(i);
                ResourceLocation id = model.create(setModelOutput(plate, "block/plate/","_" + i), textureMap, modelGenerator.modelOutput);
                if (i == 0) modelGenerator.delegateItemModel(plate, id);

                variantMap.select(i + 1, Variant.variant().with(VariantProperties.MODEL, id));
            }
            modelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(plate).with(variantMap));
        }
    }

    private static void generateBowls(BlockModelGenerators modelGenerator) {
        for (Bowl bowl : CookItBlocks.BOWLS) {
            TextureMapping textureMap = coloredTextureMap(BOWL_KEY, bowl, "bowl");
            ResourceLocation id = BOWL_TEMPLATE.create(setModelOutput(bowl, "block/bowl/"), textureMap, modelGenerator.modelOutput);
            modelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(bowl, Variant.variant().with(VariantProperties.MODEL, id)));
            modelGenerator.delegateItemModel(bowl, id);
        }
    }

    private static void generateCuttingBoards(BlockModelGenerators modelGenerator) {
        for (CuttingBoard board : CookItBlocks.CUTTING_BOARDS) {
            TextureMapping textureMap = TextureMapping.singleSlot(CUTTING_BOARD_KEY, setTextureOutput(board, board.getWoodType() + "_cutting_board"));
            ResourceLocation identifier = CUTTING_BOARD_TEMPLATE.create(setModelOutput(board, "block/"), textureMap, modelGenerator.modelOutput);

            modelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(board).with(directionMap(identifier)));
            modelGenerator.delegateItemModel(board, identifier);
        }
    }

    private static void generateVanillaBeanVines(BlockModelGenerators modelGenerator) {
        {
            Block vinePlant = CookItBlocks.VANILLA_VINE;
            ResourceLocation id = setTextureOutput(vinePlant, "vanilla_vine");

            TextureMapping BASE = TextureMapping.singleSlot(TextureSlot.TEXTURE, id);
            TextureMapping DECOR = TextureMapping.singleSlot(VINE_KEY, id).put(DECOR_KEY, setTextureOutput(vinePlant, "vanilla_vine_decor"));
            TextureMapping HARVESTABLE = TextureMapping.singleSlot(VINE_KEY, setTextureOutput(vinePlant, "vanilla_vine_done")).put(DECOR_KEY, setTextureOutput(vinePlant, "vanilla_vine_decor_done"));

            ResourceLocation[] ids = new ResourceLocation[]{
                PLANE.create(setModelOutput(vinePlant, "block/"), BASE, modelGenerator.modelOutput),
                VANILLA_VINE_TEMPLATE.create(setModelOutput(vinePlant, "block/", "_bloomed"), DECOR, modelGenerator.modelOutput),
                VANILLA_VINE_TEMPLATE.create(setModelOutput(vinePlant, "block/", "_with_beans"), HARVESTABLE, modelGenerator.modelOutput)
            };

            PropertyDispatch.C2<Direction, VanillaVines.Stage> variantMap = PropertyDispatch.properties(HORIZONTAL_FACING, VanillaVines.PLANT_STATE);

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                for (VanillaVines.Stage stage : VanillaVines.Stage.values()) {
                    variantMap.select(dir, stage, createDirectionalVariant(dir,  ids[stage.ordinal()]));
                }
            }

            modelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(vinePlant).with(variantMap));
            modelGenerator.createSimpleFlatItemModel(vinePlant);
        }
        {
            Block vineStem = CookItBlocks.VANILLA_VINE_STEM;
            TextureMapping textureMap = TextureMapping.singleSlot(TextureSlot.TEXTURE, setTextureOutput(CookItBlocks.VANILLA_VINE_STEM, "vanilla_vine_bottom"));
            ResourceLocation identifier = PLANE.create(setModelOutput(vineStem, "block/"), textureMap, modelGenerator.modelOutput);

            modelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(vineStem).with(directionMap(identifier)));
            ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(vineStem.asItem()), TextureMapping.singleSlot(TextureSlot.LAYER0, CookIt.idOf( "item/temp/vanilla_bean")), modelGenerator.modelOutput);
        }
    }

    private static void generateToaster(BlockModelGenerators modelGenerator) {
        Block toaster = CookItBlocks.TOASTER;
        PropertyDispatch.C2<Direction, Toaster.Stage> variantMap = PropertyDispatch.properties(HORIZONTAL_FACING, Toaster.STAGE);

        for (Toaster.Stage stage : Toaster.Stage.values()) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                variantMap.select(dir, stage, createDirectionalVariant(dir, setModelOutput(toaster, "block/toaster/",  "_" + stage.getSerializedName())));
            }
        }
        modelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(toaster).with(variantMap));
        modelGenerator.delegateItemModel(toaster,  setModelOutput(toaster, "block/toaster/", "_empty"));
    }

    private static void generateFryer(BlockModelGenerators modelGenerator) {
        Block fryer = CookItBlocks.FRYER;
        PropertyDispatch.C2<Direction, Boolean> variantMap = PropertyDispatch.properties(HORIZONTAL_FACING, LIT);
        ResourceLocation normalModel = setModelOutput(fryer, "block/");
        ResourceLocation litModel = setModelOutput(fryer, "block/","_on");
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            variantMap.select(dir, false, createDirectionalVariant(dir, normalModel));
            variantMap.select(dir, true, createDirectionalVariant(dir, litModel));
        }
        modelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(fryer).with(variantMap));
        modelGenerator.delegateItemModel(fryer,  normalModel);
    }

    private static void generateMicrowave(BlockModelGenerators modelGenerator) {
        Block microwave = CookItBlocks.MICROWAVE;
        PropertyDispatch.C3<Direction, Boolean, Boolean> variantMap = PropertyDispatch.properties(HORIZONTAL_FACING, LIT, OPEN);
        ResourceLocation closed = setModelOutput(microwave, "block/microwave/");
        ResourceLocation open = setModelOutput(microwave, "block/microwave/","_open");
        ResourceLocation closedOn = setModelOutput(microwave, "block/microwave/", "_on");
        ResourceLocation openOn = setModelOutput(microwave, "block/microwave/", "_open_on");

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            variantMap.select(dir, false, false, createDirectionalVariant(dir, closed));
            variantMap.select(dir, true,  false, createDirectionalVariant(dir, closedOn));
            variantMap.select(dir, false, true,  createDirectionalVariant(dir, open));
            variantMap.select(dir, true,  true,  createDirectionalVariant(dir, openOn));
        }
        modelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(microwave).with(variantMap));
        modelGenerator.delegateItemModel(microwave,  closed);
    }

    private static void generateMixingBowl(BlockModelGenerators modelGenerator) {
        ResourceLocation normal = setModelOutput(CookItBlocks.MIXING_BOWL, "block/");
        modelGenerator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(CookItBlocks.MIXING_BOWL)
                .with(PropertyDispatch.property(MixingBowl.CONTAINS_LIQUID)
                        .select(false, Variant.variant().with(VariantProperties.MODEL, normal))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, setModelOutput(CookItBlocks.MIXING_BOWL, "block/", "_filled")))
        ));
        modelGenerator.delegateItemModel(CookItBlocks.MIXING_BOWL,  normal);
    }

    private static PropertyDispatch.C1<Direction> directionMap(ResourceLocation id) {
        PropertyDispatch.C1<Direction> variantMap = PropertyDispatch.property(HORIZONTAL_FACING);

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            variantMap.select(dir, Variant.variant().with(VariantProperties.Y_ROT, fromDirection(dir)).with(VariantProperties.MODEL, id).with(VariantProperties.UV_LOCK, false));
        }
        return variantMap;
    }

    private static Variant createDirectionalVariant(Direction dir, ResourceLocation id) {
        return Variant.variant()
                .with(VariantProperties.Y_ROT, fromDirection(dir))
                .with(VariantProperties.MODEL, id)
                .with(VariantProperties.UV_LOCK, false);
    }

    private static VariantProperties.Rotation fromDirection(Direction dir) {
        return switch (dir) {
            case EAST -> VariantProperties.Rotation.R90;
            case SOUTH -> VariantProperties.Rotation.R180;
            case WEST -> VariantProperties.Rotation.R270;
            default -> VariantProperties.Rotation.R0;
        };
    }
}