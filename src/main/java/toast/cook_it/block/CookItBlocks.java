package toast.cook_it.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import toast.cook_it.CookIt;
import toast.cook_it.block.baking_sheet.BakingSheet;
import toast.cook_it.block.baking_sheet.BakingSheetEntity;

public class CookItBlocks {
    public static final Block CUTTING_BOARD = registerBlock("cutting_board", new CuttingBoard(FabricBlockSettings.create().strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).burnable().nonOpaque()));
    public static final Block TOASTER = registerBlock("toaster", new Toaster(FabricBlockSettings.create()));
    public static final Block BAKING_SHEET = registerBlock("baking_sheet", new BakingSheet(FabricBlockSettings.create()));
    public static final BlockEntityType<BakingSheetEntity> BAKING_SHEET_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(CookIt.MOD_ID, "baking_sheet"), FabricBlockEntityTypeBuilder.create(BakingSheetEntity::new, BAKING_SHEET).build());
    public static final Block BENCH = registerBlock("bench", new Block(FabricBlockSettings.create()));
    public static final Block OVEN = registerBlock("oven", new Block(FabricBlockSettings.create()));

    public static final Block MICROWAVE = registerBlock("microwave", new Microwave(FabricBlockSettings.create()));

    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(CookIt.MOD_ID, name), block);
    }

    public static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(CookIt.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerBlocks() {

    }


}
