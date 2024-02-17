package toast.cook_it.block.containers.plate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import toast.cook_it.registries.CookItBlockEntities;

public class LargePlateEntity extends PlateEntity{
    public LargePlateEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.LARGE_PLATE_ENTITY,pos, state);
    }
}
