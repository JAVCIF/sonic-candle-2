package com.soniccandle.render;

public enum LoadBarBorderStyle {
    DEFAULT("Predeterminado"),
    THICK_BLOCK("Bloque grueso"),
    POP_UP_BLOCK("Bloque elevado"),
    ETCHED_BLOCK("Bloque grabado"),
    SEGMENTED_BLOCKS("Bloque segmentado");

    private final String displayName;

    LoadBarBorderStyle(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
