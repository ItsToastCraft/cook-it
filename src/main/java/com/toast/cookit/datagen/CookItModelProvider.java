package com.toast.cookit.datagen;

import com.toast.cookit.CookIt;
import com.toast.cookit.registries.CookItItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.state.property.Properties;
import com.toast.cookit.block.containers.Bowl;
import com.toast.cookit.block.containers.Plate;
import com.toast.cookit.registries.CookItBlocks;
import net.minecraft.util.math.Direction;

import static com.toast.cookit.datagen.CookItModels.*;

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
