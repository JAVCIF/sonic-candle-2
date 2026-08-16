package com.soniccandle.render;

public enum NeonWavePlacement {
    TOP("Arriba"),
    CENTER("Centro"),
    BOTTOM("Abajo");

    private final String displayName;

    NeonWavePlacement(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
