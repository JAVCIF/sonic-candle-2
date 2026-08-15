package com.soniccandle.render;

public enum VerticalPlacement {
    LEFT("Izquierda"),
    CENTER("Centro"),
    RIGHT("Derecha");

    private final String displayName;

    VerticalPlacement(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
