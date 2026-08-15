package com.soniccandle.render;

public enum DualBarLayout {
    EDGES("En los bordes"),
    JOINED_CENTER("Mitades unidas");

    private final String displayName;

    DualBarLayout(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
