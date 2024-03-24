package toast.cook_it.block.containers.muffin_tin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import toast.cook_it.block.CookingBlockEntity;
import toast.cook_it.block.ImplementedInventory;
import toast.cook_it.registries.CookItBlockEntities;

public class MuffinTinEntity extends CookingBlockEntity implements ImplementedInventory {
    public MuffinTinEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MUFFIN_TIN_ENTITY, pos, state, 6);
    }


}