package toast.cook_it.block.containers.plate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import toast.cook_it.block.CookingBlockEntity;
import toast.cook_it.registries.CookItBlockEntities;

public class PlateEntity extends CookingBlockEntity {

    public PlateEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.PLATE_ENTITY, pos, state);
    }


}