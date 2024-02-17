package toast.cook_it.block.appliances.microwave;


import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import toast.cook_it.block.CookingBlockEntity;
import toast.cook_it.block.ImplementedInventory;
import toast.cook_it.registries.CookItBlockEntities;

public class MicrowaveEntity extends CookingBlockEntity implements ImplementedInventory {
    public MicrowaveEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MICROWAVE_ENTITY,pos, state, 1);
    }
}