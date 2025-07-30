package dev.toasttextures.cookit.enums;

public enum FoodProcessingStatus {
    // Appliance is not currently doing anything
    IDLE,
    // Appliance does not accept this item (should not happen I hope)
    INVALID_INPUT,
    // Appliance accepted item
    PROCESSING,
    // Appliance is done processing the item but still has the output
    DONE;
}
