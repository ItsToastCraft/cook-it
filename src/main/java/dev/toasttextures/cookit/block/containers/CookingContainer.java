package dev.toasttextures.cookit.block.containers;

import dev.toasttextures.cookit.block.ContainerLogic;
import net.minecraft.block.*;

public abstract class CookingContainer extends BlockWithEntity implements BlockEntityProvider, ContainerLogic {
    public CookingContainer(Settings settings) {
        super(settings);
    }
}