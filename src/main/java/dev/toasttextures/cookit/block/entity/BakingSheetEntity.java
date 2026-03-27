package dev.toasttextures.cookit.block.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import dev.toasttextures.cookit.registries.CookItBlockEntities;
import org.jetbrains.annotations.Nullable;

public class BakingSheetEntity extends Container {
    public BakingSheetEntity(BlockPos pos, BlockState state) {
        super(CookItBlockEntities.BAKING_SHEET, pos, state, 8);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}