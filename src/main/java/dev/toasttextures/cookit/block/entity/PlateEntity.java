package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class PlateEntity extends Container implements DefaultedInventory, Transferable {
    public PlateEntity(BlockPos pos, BlockState state) {
        this(CookItBlockEntities.PLATE, pos, state);
    }

    public PlateEntity(BlockEntityType<? extends PlateEntity> blockEntity, BlockPos pos, BlockState state) {
        super(blockEntity, pos, state, 1);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
    }

    @Override
    public void transfer(Player player, ItemStack stack) {
        ItemStack first = getItem(0);
        if (first.isEmpty() && stack.is(CookItItems.FRYER_BASKET)) {
            items.set(0, ItemStorage.getStoredItem(stack).split(1));
        } else if (!first.isEmpty()) {
            player.getInventory().placeItemBackInInventory(stack.copyAndClear());
        }
    }
}