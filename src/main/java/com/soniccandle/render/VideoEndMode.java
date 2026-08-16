package com.soniccandle.render;

/** Comportamiento de un fondo que termina antes que la canción. */
public enum VideoEndMode {
    LOOP("Repetir"),
    FREEZE("Congelar último fotograma");

    private final String displayName;

    VideoEndMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
