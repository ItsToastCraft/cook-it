package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PlateEntity extends Container implements DefaultedInventory, Transferable {
    public PlateEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.PLATE, pos, state, 1);
    }

    @Override
    public void attemptTransfer(Player player, ItemStack stack, @Nullable Vec3 interactionPos) {
        ItemStack first = getFirst();
        if (first.isEmpty() && stack.is(CookItItems.FRYER_BASKET)) {
            items.set(0, ItemStorage.split(stack));
            setChanged();
        } else if (!first.isEmpty()) {
            player.getInventory().placeItemBackInInventory(retrieve());
        }
    }
}