package com.soniccandle.render;

public final class IntroAnimationConfig {

    public static final int MIN_DURATION_MS = 500;
    public static final int MAX_DURATION_MS = 5_000;
    public static final int DEFAULT_DURATION_MS = 1_800;

    private final IntroAnimationMode mode;
    private final IntroAnimationDirection direction;
    private final int durationMs;

    public IntroAnimationConfig(IntroAnimationMode mode,
            IntroAnimationDirection direction, int durationMs) {
        this.mode = mode == null ? IntroAnimationMode.DISABLED : mode;
        this.direction = direction == null
                ? IntroAnimationDirection.OUTSIDE_IN : direction;
        this.durationMs = Math.max(MIN_DURATION_MS,
                Math.min(MAX_DURATION_MS, durationMs));
    }

    public static IntroAnimationConfig disabled() {
        return new IntroAnimationConfig(IntroAnimationMode.DISABLED,
                IntroAnimationDirection.OUTSIDE_IN, DEFAULT_DURATION_MS);
    }

    public IntroAnimationMode mode() { return mode; }
    public IntroAnimationDirection direction() { return direction; }
    public int durationMs() { return durationMs; }

    public int frameCount(int framesPerSecond) {
        return Math.max(1, (int) Math.ceil(durationMs * framesPerSecond / 1_000.0));
    }
}
