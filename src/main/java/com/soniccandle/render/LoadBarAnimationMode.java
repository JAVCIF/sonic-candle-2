package com.soniccandle.render;

public enum LoadBarAnimationMode {
    NORMAL("Normal"),
    BALANCED("Equilibrada"),
    SMOOTHED("Suavizada");

    private final String displayName;

    LoadBarAnimationMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
