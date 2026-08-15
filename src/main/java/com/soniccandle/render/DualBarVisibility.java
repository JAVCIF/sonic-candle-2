package com.soniccandle.render;

public enum DualBarVisibility {
    BOTH("Superior e inferior"),
    TOP_ONLY("Solo superior"),
    BOTTOM_ONLY("Solo inferior");

    private final String displayName;

    DualBarVisibility(String displayName) {
        this.displayName = displayName;
    }

    public boolean showsTop() { return this != BOTTOM_ONLY; }
    public boolean showsBottom() { return this != TOP_ONLY; }

    @Override
    public String toString() { return displayName; }
}
