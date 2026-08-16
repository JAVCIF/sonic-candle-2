package com.soniccandle.render;

public record CardiogramConfig(CardiogramBeatMode beatMode,
        CardiogramSpeedMode speedMode, CardiogramStyle style, boolean reverse,
        boolean adaptiveSweep) {

    public CardiogramConfig {
        beatMode = beatMode == null
                ? CardiogramBeatMode.ADAPTIVE_HEART_RATE : beatMode;
        speedMode = speedMode == null
                ? CardiogramSpeedMode.SYNCHRONIZED : speedMode;
        style = style == null ? CardiogramStyle.ROUNDED : style;
    }

    public CardiogramConfig(CardiogramSpeedMode speedMode,
            CardiogramStyle style, boolean reverse) {
        this(CardiogramBeatMode.MUSICAL_HITS, speedMode, style, reverse, false);
    }

    public CardiogramConfig(CardiogramBeatMode beatMode,
            CardiogramSpeedMode speedMode, CardiogramStyle style,
            boolean reverse) {
        this(beatMode, speedMode, style, reverse, false);
    }

    public static CardiogramConfig defaults() {
        return new CardiogramConfig(CardiogramBeatMode.ADAPTIVE_HEART_RATE,
                CardiogramSpeedMode.SYNCHRONIZED, CardiogramStyle.ROUNDED,
                false, true);
    }

    public boolean usesConfigurableSweepSpeed() {
        return beatMode == CardiogramBeatMode.MUSICAL_HITS;
    }

    public CardiogramSpeedMode effectiveSpeedMode() {
        return usesConfigurableSweepSpeed()
                ? speedMode : CardiogramSpeedMode.SYNCHRONIZED;
    }

    public boolean usesAdaptiveSweep() {
        return beatMode == CardiogramBeatMode.ADAPTIVE_HEART_RATE
                && adaptiveSweep;
    }

    public int visibleFramesAt(int framesPerSecond, float intensity) {
        return effectiveSpeedMode().visibleFrames(framesPerSecond);
    }
}
