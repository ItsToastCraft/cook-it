package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface Transferable {
    default void attemptTransfer(Player player, ItemStack stack) {
        attemptTransfer(player, stack, null);
    }

    void attemptTransfer(Player player, ItemStack stack, @Nullable Vec3 interactionPos);
}