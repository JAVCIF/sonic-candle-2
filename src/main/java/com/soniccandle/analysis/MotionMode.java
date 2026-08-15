package com.soniccandle.analysis;

/** Controla la rapidez visual del espectro sin alterar el audio ni los FPS. */
public enum MotionMode {
    NORMAL("Normal", 4_096, 0.68f, 0.20f),
    AGILE("Ágil", 2_048, 0.84f, 0.38f),
    FAST("Rápido", 2_048, 0.96f, 0.62f);

    private final String displayName;
    private final int windowSize;
    private final float attack;
    private final float release;

    MotionMode(String displayName, int windowSize, float attack, float release) {
        this.displayName = displayName;
        this.windowSize = windowSize;
        this.attack = attack;
        this.release = release;
    }

    public int windowSize() {
        return windowSize;
    }

    public float attack() {
        return attack;
    }

    public float release() {
        return release;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
