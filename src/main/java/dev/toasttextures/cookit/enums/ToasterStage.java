package dev.toasttextures.cookit.enums;

import net.minecraft.util.StringIdentifiable;

public enum ToasterStage implements StringIdentifiable {
    NONE("none"),
    HALF_UNTOASTED("half_untoasted"),
    FULL_UNTOASTED("full_untoasted"),
    HALF_TOASTED("half_toasted"),
    FULL_TOASTED("full_toasted");

    private final String name;
    ToasterStage(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return "ToasterStage{" +
                "name='" + name + '\'' +
                '}';
    }

    public boolean isUntoasted() {
        return this == HALF_UNTOASTED || this == FULL_UNTOASTED;
    }

    public ToasterStage incrementStage() {
        return switch (this) {
            case NONE -> HALF_UNTOASTED;
            case HALF_UNTOASTED -> FULL_UNTOASTED;
            case HALF_TOASTED -> FULL_TOASTED;
            default -> this;
        };
    }
    public ToasterStage decreaseStage() {
        return switch (this) {
            case HALF_TOASTED -> NONE;
            case FULL_TOASTED -> HALF_TOASTED;
            default -> this;
        };
    }
}
