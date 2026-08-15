package com.soniccandle.render;

public enum CircleFillMode {
    TRANSPARENT("Transparente"),
    SOLID_COLOR("Color sólido"),
    IMAGE("Imagen");

    private final String displayName;

    CircleFillMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
