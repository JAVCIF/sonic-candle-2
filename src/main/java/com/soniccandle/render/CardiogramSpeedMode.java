package com.soniccandle.render;

public enum CardiogramSpeedMode {
    SLOW("Lento", 7.0),
    SYNCHRONIZED("Sincronizado", 4.5),
    FAST("Rápido", 2.6);

    private final String displayName;
    private final double visibleSeconds;

    CardiogramSpeedMode(String displayName, double visibleSeconds) {
        this.displayName = displayName;
        this.visibleSeconds = visibleSeconds;
    }

    public int visibleFrames(int framesPerSecond) {
        return Math.max(2, (int) Math.round(visibleSeconds
                * Math.max(1, framesPerSecond)));
    }

    @Override
    public String toString() {
        return displayName;
    }
}
