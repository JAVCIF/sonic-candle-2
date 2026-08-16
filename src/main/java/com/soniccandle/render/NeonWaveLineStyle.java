package com.soniccandle.render;

public enum NeonWaveLineStyle {
    ANGULAR("Angular"),
    SMOOTH("Suavizada"),
    ROUNDED("Redondeada");

    private final String displayName;

    NeonWaveLineStyle(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
