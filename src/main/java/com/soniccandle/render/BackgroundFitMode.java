package com.soniccandle.render;

/** Ajuste espacial aplicado a un fondo de video. */
public enum BackgroundFitMode {
    COVER("Cubrir"),
    CONTAIN("Contener"),
    STRETCH("Estirar");

    private final String displayName;

    BackgroundFitMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
