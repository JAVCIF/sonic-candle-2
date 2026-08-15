package com.soniccandle.render;

public enum LoadBarOrientation {
    HORIZONTAL("Horizontal"),
    VERTICAL("Vertical");

    private final String displayName;

    LoadBarOrientation(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
