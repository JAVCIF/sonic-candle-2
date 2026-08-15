package com.soniccandle.ui;

public enum AppTheme {
    MODERN("Azul moderno"),
    CLASSIC("Tema clásico");

    private final String displayName;

    AppTheme(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
