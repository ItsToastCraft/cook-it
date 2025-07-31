package dev.toasttextures.cookit.enums;

public enum DonutType {
    PLAIN(""),
    SPRINKLES("With Sprinkles"),
    STRIPED("Striped");

    private final String tooltip;
    DonutType(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getTooltip() {
        return tooltip;
    }
}