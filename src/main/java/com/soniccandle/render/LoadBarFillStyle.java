package com.soniccandle.render;

public enum LoadBarFillStyle {
    DEFAULT("Predeterminado"),
    THICK_BLOCK("Bloque grueso"),
    POP_UP_BLOCK("Bloque elevado"),
    ETCHED_BLOCK("Bloque grabado"),
    SEGMENTED_BLOCKS("Bloque segmentado"),
    FLUID_HALO("Halo fluido");

    private final String displayName;

    LoadBarFillStyle(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
