package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class MuffinTinEntity extends Container implements Transferable {
    public MuffinTinEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MUFFIN_TIN, pos, state, 6);
    }

    @Override
    public void transfer(PlayerEntity player, ItemStack stack) {
        if (!stack.isOf(CookItBlocks.MIXING_BOWL.asItem())) return;
        NbtList items = Container.getItemList(stack);
        if (items.isEmpty()) return;
        ItemStack goop = ItemStack.fromNbt((NbtCompound) items.get(0));
        if (!goop.isOf(CookItItems.GOOP)) return;
        if (!fillFirst(player, goop)) return;

        // Idk I might have to do further testing
        items.getCompound(0).putInt("Count", goop.getCount() - 1);
        stack.getOrCreateSubNbt("BlockEntityTag").put(CONTAINER_KEY, items);
    }
}