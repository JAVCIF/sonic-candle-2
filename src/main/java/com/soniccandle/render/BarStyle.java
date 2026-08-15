package com.soniccandle.render;

public enum BarStyle {
    THICK_BLOCK("01 Bloque grueso"),
    OUTLINE_BLOCK("02 Bloque contorno"),
    THIN("03 Línea fina"),
    ROUND_FILLED("04 Redondeado relleno"),
    ROUND_OUTLINE("05 Redondeado contorno"),
    POP_UP_BLOCK("06 Bloque elevado"),
    ETCHED_BLOCK("07 Bloque grabado"),
    OVAL_FILLED("08 Óvalo relleno"),
    OVAL_OUTLINE("09 Óvalo contorno"),
    SEGMENTED_BLOCKS("Extra: bloques segmentados"),
    FLUID_HALO("Extra: halo fluido");

    private final String displayName;

    BarStyle(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
