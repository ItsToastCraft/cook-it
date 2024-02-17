package toast.cook_it.block.containers.baking_sheet;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import toast.cook_it.block.CookingBlockEntity;
import toast.cook_it.block.ImplementedInventory;
import toast.cook_it.registries.CookItBlockEntities;

public class BakingSheetEntity extends CookingBlockEntity implements ImplementedInventory {
    public BakingSheetEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.BAKING_SHEET_ENTITY, pos, state, 8);
    }


}