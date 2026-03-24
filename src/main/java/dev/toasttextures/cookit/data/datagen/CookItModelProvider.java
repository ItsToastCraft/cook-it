package dev.toasttextures.cookit.data.datagen;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.appliances.Toaster;
import dev.toasttextures.cookit.block.containers.*;
import dev.toasttextures.cookit.block.food.vanilla_vines.VanillaVines;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.util.math.Direction;

import java.util.List;

import static dev.toasttextures.cookit.data.datagen.CookItModels.*;
import static net.minecraft.state.property.Properties.*;

public class CookItModelProvider extends FabricModelProvider {

    public CookItModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator modelGenerator) {
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
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (Item rollingPin : CookItItems.ROLLING_PINS) {
            ROLLING_PIN_TEMPLATE.upload(ModelIds.getItemModelId(rollingPin), TextureMap.of(ROLLING_PIN_KEY, CookIt.idOf("item/" + Registries.ITEM.getId(rollingPin).getPath())), itemModelGenerator.writer);
        }
    }

    private static void generatePlates(BlockStateModelGenerator modelGenerator) {
        for (Plate plate : CookItBlocks.PLATES) {
            TextureMap textureMap = coloredTextureMap(PLATE_KEY, plate, "plate");
            List<Model> templateSet = (plate instanceof LargePlate) ? LARGE_PLATE_TEMPLATE : PLATE_TEMPLATE;
            BlockStateVariantMap.SingleProperty<Integer> variantMap = BlockStateVariantMap.create(Plate.COUNT);

            for (int i = 0; i < 4; i++) {
                Model model = templateSet.get(i);
                Identifier id = model.upload(setModelOutput(plate, "block/plate","_" + i), textureMap, modelGenerator.modelCollector);
                if (i == 0) modelGenerator.registerParentedItemModel(plate, id);

                variantMap.register(i + 1, BlockStateVariant.create().put(VariantSettings.MODEL, id));
            }
            modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(plate).coordinate(variantMap));
        }
    }

    private static void generateBowls(BlockStateModelGenerator modelGenerator) {
        for (Bowl bowl : CookItBlocks.BOWLS) {
            TextureMap textureMap = coloredTextureMap(BOWL_KEY, bowl, "bowl");
            Identifier id = BOWL_TEMPLATE.upload(setModelOutput(bowl, "block/bowl/"), textureMap, modelGenerator.modelCollector);
            modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(bowl, BlockStateVariant.create().put(VariantSettings.MODEL, id)));
            modelGenerator.registerParentedItemModel(bowl, id);
        }
    }

    private static void generateCuttingBoards(BlockStateModelGenerator modelGenerator) {
        for (CuttingBoard board : CookItBlocks.CUTTING_BOARDS) {
            TextureMap textureMap = TextureMap.of(CUTTING_BOARD_KEY, setTextureOutput(board, board.getWoodType() + "_cutting_board"));
            Identifier identifier = CUTTING_BOARD_TEMPLATE.upload(setModelOutput(board, "block/"), textureMap, modelGenerator.modelCollector);

            modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(board).coordinate(directionMap(identifier)));
            modelGenerator.registerParentedItemModel(board, identifier);
        }
    }

    private static void generateVanillaBeanVines(BlockStateModelGenerator modelGenerator) {
        {
            Block vinePlant = CookItBlocks.VANILLA_VINE;
            Identifier id = setTextureOutput(vinePlant, "vanilla_vine");

            TextureMap BASE = TextureMap.of(TextureKey.TEXTURE, id);
            TextureMap DECOR = TextureMap.of(VINE_KEY, id).put(DECOR_KEY, setTextureOutput(vinePlant, "vanilla_vine_decor"));
            TextureMap HARVESTABLE = TextureMap.of(VINE_KEY, setTextureOutput(vinePlant, "vanilla_vine_done")).put(DECOR_KEY, setTextureOutput(vinePlant, "vanilla_vine_decor_done"));

            Identifier[] ids = new Identifier[]{
                PLANE.upload(setModelOutput(vinePlant, "block/"), BASE, modelGenerator.modelCollector),
                VANILLA_VINE_TEMPLATE.upload(setModelOutput(vinePlant, "block/", "_bloomed"), DECOR, modelGenerator.modelCollector),
                VANILLA_VINE_TEMPLATE.upload(setModelOutput(vinePlant, "block/", "_with_beans"), HARVESTABLE, modelGenerator.modelCollector)
            };

            BlockStateVariantMap.DoubleProperty<Direction, VanillaVines.Stage> variantMap = BlockStateVariantMap.create(HORIZONTAL_FACING, VanillaVines.PLANT_STATE);

            for (Direction dir : Direction.Type.HORIZONTAL) {
                for (VanillaVines.Stage stage : VanillaVines.Stage.values()) {
                    variantMap.register(dir, stage, createDirectionalVariant(dir,  ids[stage.ordinal()]));
                }
            }

            modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(vinePlant).coordinate(variantMap));
            modelGenerator.registerItemModel(vinePlant);
        }
        {
            Block vineStem = CookItBlocks.VANILLA_VINE_STEM;
            TextureMap textureMap = TextureMap.of(TextureKey.TEXTURE, setTextureOutput(CookItBlocks.VANILLA_VINE_STEM, "vanilla_vine_bottom"));
            Identifier identifier = PLANE.upload(setModelOutput(vineStem, "block/"), textureMap, modelGenerator.modelCollector);

            modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(vineStem).coordinate(directionMap(identifier)));
            Models.GENERATED.upload(ModelIds.getItemModelId(vineStem.asItem()), TextureMap.of(TextureKey.LAYER0, CookIt.idOf( "item/temp/vanilla_bean")), modelGenerator.modelCollector);
        }
    }

    private static void generateToaster(BlockStateModelGenerator modelGenerator) {
        Block toaster = CookItBlocks.TOASTER;
        BlockStateVariantMap.DoubleProperty<Direction, Toaster.Stage> variantMap = BlockStateVariantMap.create(HORIZONTAL_FACING, Toaster.STAGE);

        for (Toaster.Stage stage : Toaster.Stage.values()) {
            for (Direction dir : Direction.Type.HORIZONTAL) {
                variantMap.register(dir, stage, createDirectionalVariant(dir, setModelOutput(toaster, "block/toaster/",  "_" + stage.asString())));
            }
        }
        modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(toaster).coordinate(variantMap));
        modelGenerator.registerParentedItemModel(toaster,  setModelOutput(toaster, "block/toaster/", "_empty"));
    }

    private static void generateFryer(BlockStateModelGenerator modelGenerator) {
        Block fryer = CookItBlocks.FRYER;
        BlockStateVariantMap.DoubleProperty<Direction, Boolean> variantMap = BlockStateVariantMap.create(HORIZONTAL_FACING, LIT);
        Identifier normalModel = setModelOutput(fryer, "block/");
        Identifier litModel = setModelOutput(fryer, "block/","_on");
        for (Direction dir : Direction.Type.HORIZONTAL) {
            variantMap.register(dir, false, createDirectionalVariant(dir, normalModel));
            variantMap.register(dir, true, createDirectionalVariant(dir, litModel));
        }
        modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(fryer).coordinate(variantMap));
        modelGenerator.registerParentedItemModel(fryer,  normalModel);
    }

    private static void generateMicrowave(BlockStateModelGenerator modelGenerator) {
        Block microwave = CookItBlocks.MICROWAVE;
        BlockStateVariantMap.TripleProperty<Direction, Boolean, Boolean> variantMap = BlockStateVariantMap.create(HORIZONTAL_FACING, LIT, OPEN);
        Identifier closed = setModelOutput(microwave, "block/microwave/");
        Identifier open = setModelOutput(microwave, "block/microwave/","_open");
        Identifier closedOn = setModelOutput(microwave, "block/microwave/", "_on");
        Identifier openOn = setModelOutput(microwave, "block/microwave/", "_open_on");

        for (Direction dir : Direction.Type.HORIZONTAL) {
            variantMap.register(dir, false, false, createDirectionalVariant(dir, closed));
            variantMap.register(dir, true,  false, createDirectionalVariant(dir, closedOn));
            variantMap.register(dir, false, true,  createDirectionalVariant(dir, open));
            variantMap.register(dir, true,  true,  createDirectionalVariant(dir, openOn));
        }
        modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(microwave).coordinate(variantMap));
        modelGenerator.registerParentedItemModel(microwave,  closed);
    }

    private static void generateMixingBowl(BlockStateModelGenerator modelGenerator) {
        Identifier normal = setModelOutput(CookItBlocks.MIXING_BOWL, "block/");
        modelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(CookItBlocks.MIXING_BOWL)
                .coordinate(BlockStateVariantMap.create(MixingBowl.CONTAINS_LIQUID)
                        .register(false, BlockStateVariant.create().put(VariantSettings.MODEL, normal))
                        .register(true, BlockStateVariant.create().put(VariantSettings.MODEL, setModelOutput(CookItBlocks.MIXING_BOWL, "block/", "_filled")))
        ));
        modelGenerator.registerParentedItemModel(CookItBlocks.MIXING_BOWL,  normal);
    }

    private static BlockStateVariantMap.SingleProperty<Direction> directionMap(Identifier id) {
        BlockStateVariantMap.SingleProperty<Direction> variantMap = BlockStateVariantMap.create(HORIZONTAL_FACING);

        for (Direction dir : Direction.Type.HORIZONTAL) {
            variantMap.register(dir, BlockStateVariant.create().put(VariantSettings.Y, fromDirection(dir)).put(VariantSettings.MODEL, id).put(VariantSettings.UVLOCK, false));
        }
        return variantMap;
    }

    private static BlockStateVariant createDirectionalVariant(Direction dir, Identifier id) {
        return BlockStateVariant.create()
                .put(VariantSettings.Y, fromDirection(dir))
                .put(VariantSettings.MODEL, id)
                .put(VariantSettings.UVLOCK, false);
    }

    private static VariantSettings.Rotation fromDirection(Direction dir) {
        return switch (dir) {
            case EAST -> VariantSettings.Rotation.R90;
            case SOUTH -> VariantSettings.Rotation.R180;
            case WEST -> VariantSettings.Rotation.R270;
            default -> VariantSettings.Rotation.R0;
        };
    }
}