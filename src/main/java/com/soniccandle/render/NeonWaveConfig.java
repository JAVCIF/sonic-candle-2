package com.soniccandle.render;

public record NeonWaveConfig(int pointCount, NeonWaveLineStyle lineStyle,
        int echoCount, int echoSpacing, int echoOpacityPercent,
        int glowPercent, NeonWavePlacement placement, boolean inverted,
        NeonWaveParticleMode particleMode) {

    public static final int MIN_POINTS = 8;
    public static final int MAX_POINTS = 32;
    public static final int MAX_ECHOES = 10;
    public static final int MAX_ECHO_SPACING = 8;

    public NeonWaveConfig {
        pointCount = Math.max(MIN_POINTS, Math.min(MAX_POINTS, pointCount));
        lineStyle = lineStyle == null ? NeonWaveLineStyle.ANGULAR : lineStyle;
        echoCount = Math.max(0, Math.min(MAX_ECHOES, echoCount));
        echoSpacing = Math.max(1, Math.min(MAX_ECHO_SPACING, echoSpacing));
        echoOpacityPercent = Math.max(10, Math.min(90, echoOpacityPercent));
        glowPercent = Math.max(0, Math.min(200, glowPercent));
        placement = placement == null ? NeonWavePlacement.CENTER : placement;
        particleMode = particleMode == null
                ? NeonWaveParticleMode.SUBTLE : particleMode;
    }

    public static NeonWaveConfig defaults() {
        return new NeonWaveConfig(12, NeonWaveLineStyle.ANGULAR,
                7, 2, 45, 100, NeonWavePlacement.CENTER,
                false, NeonWaveParticleMode.SUBTLE);
    }
}
