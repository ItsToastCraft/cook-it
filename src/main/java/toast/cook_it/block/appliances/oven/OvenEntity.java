package toast.cook_it.block.appliances.oven;


import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import toast.cook_it.block.CookingBlockEntity;
import toast.cook_it.block.ImplementedInventory;
import toast.cook_it.registries.CookItBlockEntities;

public class OvenEntity extends CookingBlockEntity implements ImplementedInventory {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public OvenEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.OVEN_ENTITY,pos, state);
    }


    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

}