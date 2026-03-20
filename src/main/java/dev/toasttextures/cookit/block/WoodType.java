package dev.toasttextures.cookit.block;

public enum WoodType {
    ACACIA,
    BIRCH,
    CHERRY,
    CRIMSON,
    DARK_OAK,
    JUNGLE,
    OAK,
    MANGROVE,
    SPRUCE,
    WARPED;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
