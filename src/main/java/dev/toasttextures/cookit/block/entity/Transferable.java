package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface Transferable {
    void transfer(Player player, ItemStack stack);
}
