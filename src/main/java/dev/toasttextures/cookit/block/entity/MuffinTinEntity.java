package dev.toasttextures.cookit.block.entity;

import dev.toasttextures.cookit.registries.CookItBlocks;
import dev.toasttextures.cookit.registries.CookItItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;

public class MuffinTinEntity extends Container implements Transferable {
    public MuffinTinEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.MUFFIN_TIN, pos, state, 6);
    }

    @Override
    public void transfer(Player player, ItemStack stack) {
        if (!stack.is(CookItBlocks.MIXING_BOWL.asItem())) return;
        ListTag items = Container.getItemList(stack);
        if (items.isEmpty()) return;
        ItemStack goop = ItemStack.of((CompoundTag) items.get(0));
        if (!goop.is(CookItItems.GOOP)) return;
        if (!fillFirst(player, goop)) return;

        // Idk I might have to do further testing
        items.getCompound(0).putInt("Count", goop.getCount() - 1);
        stack.getOrCreateTagElement("BlockEntityTag").put(CONTAINER_KEY, items);
    }
}