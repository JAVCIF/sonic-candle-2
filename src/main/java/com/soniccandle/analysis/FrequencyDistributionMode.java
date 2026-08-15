package com.soniccandle.analysis;

/** Distribución visual de energía entre las bandas del espectro. */
public enum FrequencyDistributionMode {
    STANDARD("Estándar"),
    BALANCED("Equilibrado — más presencia a la derecha"),
    PROPORTIONAL("Proporcional — aprovecha todas las bandas");

    private final String displayName;

    FrequencyDistributionMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
