package com.soniccandle.render;

public enum CardiogramStyle {
    THIN("Línea fina"),
    THICK("Línea gruesa"),
    ROUNDED("Trazo redondeado"),
    SEGMENTED("Línea segmentada"),
    FLUID_HALO("Halo fluido");

    private final String displayName;

    CardiogramStyle(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
