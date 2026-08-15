package com.soniccandle.render;

public enum DualBarReach {
    LOW("Golpe bajo", 0.46),
    MEDIUM("Golpe medio", 0.70),
    HIGH("Golpe alto", 1.00);

    private final String displayName;
    private final double halfScreenFraction;

    DualBarReach(String displayName, double halfScreenFraction) {
        this.displayName = displayName;
        this.halfScreenFraction = halfScreenFraction;
    }

    public double halfScreenFraction() {
        return halfScreenFraction;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
