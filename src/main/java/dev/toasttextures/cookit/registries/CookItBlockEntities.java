package dev.toasttextures.cookit.registries;

import dev.toasttextures.cookit.block.entity.*;
import dev.toasttextures.cookit.client.render.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import dev.toasttextures.cookit.CookIt;

public class CookItBlockEntities {
    public static BlockEntityType<MuffinTinEntity> MUFFIN_TIN;
    public static BlockEntityType<BakingSheetEntity> BAKING_SHEET;
    public static BlockEntityType<MicrowaveEntity> MICROWAVE;
    public static BlockEntityType<OvenEntity> OVEN;
    public static BlockEntityType<PlateEntity> PLATE;
    public static BlockEntityType<FryerEntity> FRYER;
    public static BlockEntityType<CuttingBoardEntity> CUTTING_BOARD;
    public static BlockEntityType<PizzaPanEntity> PIZZA_PAN;
    public static BlockEntityType<PizzaEntity> PIZZA;
    public static BlockEntityType<MixingBowlEntity> MIXING_BOWL;

    public static void registerEntities() {
        BAKING_SHEET = registerBlockEntities("baking_sheet", BakingSheetEntity::new, new Block[]{CookItBlocks.BAKING_SHEET});
        MUFFIN_TIN =registerBlockEntities("muffin_tin", MuffinTinEntity::new, new Block[]{CookItBlocks.MUFFIN_TIN});
        MICROWAVE = registerBlockEntities("microwave", MicrowaveEntity::new, new Block[]{CookItBlocks.MICROWAVE});
        OVEN = registerBlockEntities("oven", OvenEntity::new, new Block[]{CookItBlocks.OVEN});
        PLATE = registerBlockEntities("plate", PlateEntity::new, CookItBlocks.PLATES.toArray(Block[]::new));
        FRYER = registerBlockEntities("fryer", FryerEntity::new, new Block[]{CookItBlocks.FRYER});
        CUTTING_BOARD = registerBlockEntities("cutting_board", CuttingBoardEntity::new, CookItBlocks.CUTTING_BOARDS.toArray(Block[]::new));
        PIZZA_PAN = registerBlockEntities("pizza_pan", PizzaPanEntity::new, new Block[]{CookItBlocks.PIZZA_PAN});
        PIZZA = registerBlockEntities("pizza", PizzaEntity::new, new Block[]{CookItBlocks.PIZZA_CRUST, CookItBlocks.UNCOOKED_PIZZA, CookItBlocks.PIZZA});
        MIXING_BOWL = registerBlockEntities("mixing_bowl", MixingBowlEntity::new, new Block[]{CookItBlocks.MIXING_BOWL});
    }

    public static void registerRenderers() {
        BlockEntityRenderers.register(BAKING_SHEET, BakingSheetEntityRenderer::new);
        BlockEntityRenderers.register(MUFFIN_TIN, MuffinTinEntityRenderer::new);
        BlockEntityRenderers.register(MICROWAVE, MicrowaveEntityRenderer::new);
        BlockEntityRenderers.register(OVEN, OvenEntityRenderer::new);
        BlockEntityRenderers.register(PLATE, PlateEntityRenderer::new);
        BlockEntityRenderers.register(FRYER, FryerEntityRenderer::new);
        BlockEntityRenderers.register(CUTTING_BOARD, CuttingBoardEntityRenderer::new);
        BlockEntityRenderers.register(PIZZA_PAN, PizzaPanEntityRenderer::new);
        BlockEntityRenderers.register(PIZZA, PizzaEntityRenderer::new);
        BlockEntityRenderers.register(MIXING_BOWL, MixingBowlEntityRenderer::new);
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntities(String name, FabricBlockEntityTypeBuilder.Factory<T> factory, Block[] block) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, CookIt.idOf(name), FabricBlockEntityTypeBuilder.create(factory, block).build());
    }
}
