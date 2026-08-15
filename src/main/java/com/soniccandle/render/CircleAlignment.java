package com.soniccandle.render;

public enum CircleAlignment {
    LEFT("Izquierda"),
    CENTER("Centro"),
    RIGHT("Derecha");

    private final String displayName;

    CircleAlignment(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
