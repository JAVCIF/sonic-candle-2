package com.soniccandle.render;

/** Controla si los picos pueden salir del fotograma o se contienen suavemente. */
public enum PeakMode {
    FREE_OVERFLOW("Desborde libre"),
    SOFT_LIMIT("Normalizar picos");

    private final String displayName;

    PeakMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
