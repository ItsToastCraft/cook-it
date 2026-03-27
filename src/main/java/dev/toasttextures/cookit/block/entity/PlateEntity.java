package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.item.ItemStorage;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class PlateEntity extends Container implements DefaultedInventory, Transferable {
    public PlateEntity(BlockPos pos, BlockState state) {
        this(CookItBlockEntities.PLATE, pos, state);
    }

    public PlateEntity(BlockEntityType<? extends PlateEntity> blockEntity, BlockPos pos, BlockState state) {
        super(blockEntity, pos, state, 1);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void transfer(PlayerEntity player, ItemStack stack) {
        ItemStack first = getStack(0);
        if (first.isEmpty() && stack.isOf(CookItItems.FRYER_BASKET)) {
            items.set(0, ItemStorage.getStoredItem(stack).split(1));
        } else if (!first.isEmpty()) {
            player.getInventory().offerOrDrop(stack.copyAndEmpty());
        }
    }
}