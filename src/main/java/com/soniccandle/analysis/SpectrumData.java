package com.soniccandle.analysis;

import java.util.Objects;

public final class SpectrumData {

    private final float[][] frames;
    private final int framesPerSecond;
    private final int sampleRate;

    public SpectrumData(float[][] frames, int framesPerSecond, int sampleRate) {
        this.frames = Objects.requireNonNull(frames, "frames");
        this.framesPerSecond = framesPerSecond;
        this.sampleRate = sampleRate;
    }

    public int frameCount() {
        return frames.length;
    }

    public int bandCount() {
        return frames.length == 0 ? 0 : frames[0].length;
    }

    public float[] frame(int index) {
        if (frames.length == 0) {
            return new float[0];
        }
        return frames[Math.max(0, Math.min(index, frames.length - 1))];
    }

    public int framesPerSecond() {
        return framesPerSecond;
    }

    public int sampleRate() {
        return sampleRate;
    }

    public double durationSeconds() {
        return framesPerSecond == 0 ? 0 : (double) frames.length / framesPerSecond;
    }
}
