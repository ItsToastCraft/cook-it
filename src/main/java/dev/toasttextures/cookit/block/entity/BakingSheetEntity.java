package dev.toasttextures.cookit.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import org.jetbrains.annotations.Nullable;

public class BakingSheetEntity extends Container {
    public BakingSheetEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.BAKING_SHEET, pos, state, 8);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}