package com.soniccandle.render;

public enum HorizontalPlacement {
    TOP("Arriba"),
    CENTER("Centro"),
    BOTTOM("Abajo");

    private final String displayName;

    HorizontalPlacement(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
