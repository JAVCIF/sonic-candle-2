package com.soniccandle.render;

public enum CardiogramBeatMode {
    ADAPTIVE_HEART_RATE("Ritmo cardíaco"),
    MUSICAL_HITS("Golpes musicales");

    private final String displayName;

    CardiogramBeatMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
