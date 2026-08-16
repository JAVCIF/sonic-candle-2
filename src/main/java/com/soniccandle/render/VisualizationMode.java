package com.soniccandle.render;

public enum VisualizationMode {
    LINEAR("Lineal"),
    CIRCULAR("Circular"),
    DUAL_BAR("Doble barra"),
    LOAD_BAR("Barra de carga"),
    CARDIOGRAM("Electrocardiógrafo"),
    NEON_WAVE("Neon Wave");

    private final String displayName;

    VisualizationMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
