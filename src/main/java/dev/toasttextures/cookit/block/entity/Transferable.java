package dev.toasttextures.cookit.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface Transferable {
    void transfer(PlayerEntity player, ItemStack stack);
}
