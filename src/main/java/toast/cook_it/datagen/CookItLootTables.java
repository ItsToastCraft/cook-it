package toast.cook_it.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import toast.cook_it.registries.CookItBlocks;

import java.util.List;

public class CookItLootTables extends FabricBlockLootTableProvider {
    List<Block> blocks = CookItBlocks.BLOCKS;
    public CookItLootTables(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        for(Block block : blocks) {
            addDrop(block, drops(block));
        }
    }

}
