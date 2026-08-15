package com.soniccandle.render;

public enum LoadBarShape {
    SQUARE("Cuadrada"),
    ROUNDED("Redondeada");

    private final String displayName;

    LoadBarShape(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
