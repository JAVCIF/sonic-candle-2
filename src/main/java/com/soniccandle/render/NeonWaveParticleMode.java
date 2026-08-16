package com.soniccandle.render;

public enum NeonWaveParticleMode {
    DISABLED("Desactivadas", 0.0f),
    SUBTLE("Sutiles", 0.72f),
    INTENSE("Intensas", 1.35f);

    private final String displayName;
    private final float density;

    NeonWaveParticleMode(String displayName, float density) {
        this.displayName = displayName;
        this.density = density;
    }

    public float density() {
        return density;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
