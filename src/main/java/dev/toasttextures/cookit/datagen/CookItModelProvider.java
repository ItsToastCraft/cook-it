package dev.toasttextures.cookit.datagen;

import dev.toasttextures.cookit.CookIt;
import dev.toasttextures.cookit.block.food_blocks.VanillaVines;
import dev.toasttextures.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.state.property.Properties;
import dev.toasttextures.cookit.block.containers.Bowl;
import dev.toasttextures.cookit.block.containers.Plate;
import dev.toasttextures.cookit.registries.CookItBlocks;
import net.minecraft.util.math.Direction;

import static dev.toasttextures.cookit.datagen.CookItModels.*;

public class CookItModelProvider extends FabricModelProvider {

    public CookItModelProvider(FabricDataOutput output) {
        super(output);
    }


    private static void generateColoredBlocks(BlockStateModelGenerator blockStateModelGenerator) {
        for (Plate plate : CookItBlocks.PLATES) {
            TextureMap textureMap = coloredTextureMap(PLATE, plate, "plate");
            Identifier identifier, identifier2, identifier3, identifier4;
            if (Plate.isLargePlate(plate)) {
                identifier = TEMPLATE_LARGE_PLATE_1.upload(setModelOutput("block/plate/", plate, "_1"), textureMap, blockStateModelGenerator.modelCollector);
                identifier2 = TEMPLATE_LARGE_PLATE_2.upload(setModelOutput("block/plate/", plate, "_2"), textureMap, blockStateModelGenerator.modelCollector);
                identifier3 = TEMPLATE_LARGE_PLATE_3.upload(setModelOutput("block/plate/", plate, "_3"), textureMap, blockStateModelGenerator.modelCollector);
                identifier4 = TEMPLATE_LARGE_PLATE_4.upload(setModelOutput("block/plate/", plate, "_4"), textureMap, blockStateModelGenerator.modelCollector);
            } else {
                identifier = TEMPLATE_PLATE_1.upload(setModelOutput("block/plate/", plate, "_1"), textureMap, blockStateModelGenerator.modelCollector);
                identifier2 = TEMPLATE_PLATE_2.upload(setModelOutput("block/plate/", plate, "_2"), textureMap, blockStateModelGenerator.modelCollector);
                identifier3 = TEMPLATE_PLATE_3.upload(setModelOutput("block/plate/", plate, "_3"), textureMap, blockStateModelGenerator.modelCollector);
                identifier4 = TEMPLATE_PLATE_4.upload(setModelOutput("block/plate/", plate, "_4"), textureMap, blockStateModelGenerator.modelCollector);
            }
            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(plate).coordinate(BlockStateVariantMap.create(Plate.PLATES_AMOUNT).register(1, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)).register(2, BlockStateVariant.create().put(VariantSettings.MODEL, identifier2)).register(3, BlockStateVariant.create().put(VariantSettings.MODEL, identifier3)).register(4, BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))));
            blockStateModelGenerator.registerParentedItemModel(plate, identifier);
        }
        for (Bowl bowl : CookItBlocks.BOWLS) {
            TextureMap textureMap = coloredTextureMap(BOWL, bowl, "bowl");
            Identifier identifier = TEMPLATE_BOWL.upload(setModelOutput("block/bowl/", bowl), textureMap, blockStateModelGenerator.modelCollector);
            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(bowl, BlockStateVariant.create().put(VariantSettings.MODEL, identifier)));
            blockStateModelGenerator.registerParentedItemModel(bowl, identifier);
        }

        for (Block block : CookItBlocks.CUTTING_BOARDS) {
            String woodType = CookIt.SUPPORTED_WOOD_TYPES.get(CookItBlocks.CUTTING_BOARDS.indexOf(block));
            TextureMap textureMap = TextureMap.of(CUTTING_BOARD, setTextureOutput(block, woodType + "_cutting_board"));
            Identifier identifier = TEMPLATE_CUTTING_BOARD.upload(setModelOutput("block/", block), textureMap, blockStateModelGenerator.modelCollector);
            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                    .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R0).put(VariantSettings.MODEL, identifier).put(VariantSettings.UVLOCK, false))
                    .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.MODEL, identifier).put(VariantSettings.UVLOCK, false))
                    .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.MODEL, identifier).put(VariantSettings.UVLOCK, false))
                    .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.MODEL, identifier).put(VariantSettings.UVLOCK, false))));
            blockStateModelGenerator.registerParentedItemModel(block, identifier);
            }

        {
            Block block = CookItBlocks.VANILLA_VINE;
            Identifier id = setTextureOutput(CookItBlocks.VANILLA_VINE, "vanilla_vine");
            TextureMap texStage0 = TextureMap.of(TextureKey.TEXTURE, id);
            TextureMap texStage1 = new TextureMap().put(VINE, id).put(DECOR, setTextureOutput(block, "vanilla_vine_decor"));
            TextureMap texStage2 = new TextureMap().put(VINE, setTextureOutput(CookItBlocks.VANILLA_VINE, "vanilla_vine_done")).put(DECOR, setTextureOutput(block, "vanilla_vine_decor_done"));

            Model VANILLA_VINE_MODEL = newParent("block/blooming_vine", VINE, DECOR);
            Identifier stage0 = PLANE.upload(setModelOutput("block/", block), texStage0, blockStateModelGenerator.modelCollector);
            Identifier stage1 = VANILLA_VINE_MODEL.upload(setModelOutput("block/", block, "_bloomed"), texStage1, blockStateModelGenerator.modelCollector);
            Identifier stage2 = VANILLA_VINE_MODEL.upload(setModelOutput("block/", block, "_with_beans"), texStage2, blockStateModelGenerator.modelCollector);

            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING, VanillaVines.PLANT_STATE)
                    .register(Direction.NORTH, 0, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R0).put(VariantSettings.MODEL, stage0).put(VariantSettings.UVLOCK, false))
                    .register(Direction.EAST, 0, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.MODEL, stage0).put(VariantSettings.UVLOCK, false))
                    .register(Direction.SOUTH, 0, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.MODEL, stage0).put(VariantSettings.UVLOCK, false))
                    .register(Direction.WEST, 0, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.MODEL, stage0).put(VariantSettings.UVLOCK, false))
                    .register(Direction.NORTH, 1, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R0).put(VariantSettings.MODEL, stage1).put(VariantSettings.UVLOCK, false))
                    .register(Direction.EAST, 1, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.MODEL, stage1).put(VariantSettings.UVLOCK, false))
                    .register(Direction.SOUTH, 1, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.MODEL, stage1).put(VariantSettings.UVLOCK, false))
                    .register(Direction.WEST, 1, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.MODEL, stage1).put(VariantSettings.UVLOCK, false))
                    .register(Direction.NORTH, 2, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R0).put(VariantSettings.MODEL, stage2).put(VariantSettings.UVLOCK, false))
                    .register(Direction.EAST, 2, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.MODEL, stage2).put(VariantSettings.UVLOCK, false))
                    .register(Direction.SOUTH, 2, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.MODEL, stage2).put(VariantSettings.UVLOCK, false))
                    .register(Direction.WEST, 2, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.MODEL, stage2).put(VariantSettings.UVLOCK, false))));
            blockStateModelGenerator.registerItemModel(block);
        }
        {
            Block block = CookItBlocks.VANILLA_VINE_STEM;
            TextureMap textureMap = TextureMap.of(TextureKey.TEXTURE, setTextureOutput(CookItBlocks.VANILLA_VINE_STEM, "vanilla_vine_bottom"));
            Identifier identifier = PLANE.upload(setModelOutput("block/", block), textureMap, blockStateModelGenerator.modelCollector);
            blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                    .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R0).put(VariantSettings.MODEL, identifier).put(VariantSettings.UVLOCK, false))
                    .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.MODEL, identifier).put(VariantSettings.UVLOCK, false))
                    .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.MODEL, identifier).put(VariantSettings.UVLOCK, false))
                    .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.MODEL, identifier).put(VariantSettings.UVLOCK, false))));
            Models.GENERATED.upload(ModelIds.getItemModelId(block.asItem()), TextureMap.of(TextureKey.LAYER0, new Identifier(CookIt.MOD_ID, "item/temp/vanilla_bean")), blockStateModelGenerator.modelCollector);
        }
    }


    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        generateColoredBlocks(blockStateModelGenerator);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (Item rollingPin : CookItItems.ROLLING_PINS) {
            TEMPLATE_ROLLING_PIN.upload(ModelIds.getItemModelId(rollingPin), TextureMap.of(ROLLING_PIN, new Identifier(CookIt.MOD_ID, "item/" + Registries.ITEM.getId(rollingPin).getPath())), itemModelGenerator.writer);
        }
    }
}
