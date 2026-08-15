package com.soniccandle.render;

import com.soniccandle.analysis.SpectrumData;
import java.util.Arrays;

/** Convierte un espectro multibanda en un nivel temporal único y reproducible. */
public final class LoadBarLevelProcessor {

    private LoadBarLevelProcessor() {
    }

    public static float[] process(SpectrumData spectrum, RenderConfig config) {
        float[] levels = new float[spectrum.frameCount()];
        for (int frame = 0; frame < levels.length; frame++) {
            levels[frame] = rawLevel(spectrum.frame(frame), config.sensitivity());
        }
        applyResponse(levels, config.loadBarConfig().responseMode());
        applyAnimation(levels, config.loadBarConfig().animationMode(),
                config.framesPerSecond());
        return levels;
    }

    public static float instantaneous(float[] bands, RenderConfig config) {
        float level = rawLevel(bands, config.sensitivity());
        return switch (config.loadBarConfig().responseMode()) {
            case NORMAL -> FrameRenderer.softLimitAmplitude(level, 0.985f);
            case HIGH -> highResponse(level);
            case PROPORTIONAL -> FrameRenderer.softLimitAmplitude(level * 1.18f, 0.985f);
        };
    }

    static float rawLevel(float[] bands, float sensitivity) {
        if (bands.length == 0) {
            return 0f;
        }
        float[] ordered = bands.clone();
        Arrays.sort(ordered);
        int start = Math.max(0, (int) Math.floor(ordered.length * 0.72));
        double upperAverage = 0.0;
        for (int index = start; index < ordered.length; index++) {
            upperAverage += Math.max(0f, ordered[index]);
        }
        upperAverage /= Math.max(1, ordered.length - start);
        float peak = Math.max(0f, ordered[ordered.length - 1]);
        return Math.max(0f, (float) ((upperAverage * 0.62 + peak * 0.38)
                * sensitivity));
    }

    private static void applyResponse(float[] levels, LoadBarResponseMode mode) {
        if (mode == LoadBarResponseMode.PROPORTIONAL) {
            applyProportionalResponse(levels);
            return;
        }
        for (int index = 0; index < levels.length; index++) {
            levels[index] = mode == LoadBarResponseMode.HIGH
                    ? highResponse(levels[index])
                    : FrameRenderer.softLimitAmplitude(levels[index], 0.985f);
        }
    }

    private static float highResponse(float value) {
        if (value <= 0f) {
            return 0f;
        }
        float boosted = (float) (1.0 - Math.exp(-Math.max(0f, value) * 2.35));
        return FrameRenderer.softLimitAmplitude(boosted * 1.08f, 0.985f);
    }

    private static void applyProportionalResponse(float[] levels) {
        float[] activeBuffer = new float[levels.length];
        int activeCount = 0;
        for (float level : levels) {
            if (level > 0.0001f) {
                activeBuffer[activeCount++] = level;
            }
        }
        float[] active = Arrays.copyOf(activeBuffer, activeCount);
        if (active.length == 0) {
            Arrays.fill(levels, 0f);
            return;
        }
        Arrays.sort(active);
        float low = percentile(active, 0.12);
        float high = percentile(active, 0.92);
        float range = Math.max(0.0001f, high - low);
        for (int index = 0; index < levels.length; index++) {
            float source = levels[index];
            if (source <= 0.0001f) {
                levels[index] = 0f;
                continue;
            }
            float normalized = Math.max(0f, Math.min(1f, (source - low) / range));
            float smooth = normalized * normalized * (3f - 2f * normalized);
            levels[index] = FrameRenderer.softLimitAmplitude(
                    0.04f + smooth * 0.94f, 0.985f);
        }
    }

    private static void applyAnimation(float[] levels, LoadBarAnimationMode mode, int fps) {
        if (mode == LoadBarAnimationMode.NORMAL || levels.length == 0) {
            return;
        }
        double attackAt30 = mode == LoadBarAnimationMode.BALANCED ? 0.62 : 0.34;
        double releaseAt30 = mode == LoadBarAnimationMode.BALANCED ? 0.30 : 0.13;
        double attack = coefficientForFps(attackAt30, fps);
        double release = coefficientForFps(releaseAt30, fps);
        float previous = 0f;
        for (int index = 0; index < levels.length; index++) {
            float target = levels[index];
            double coefficient = target >= previous ? attack : release;
            previous += (float) ((target - previous) * coefficient);
            levels[index] = previous;
        }
    }

    private static double coefficientForFps(double coefficientAt30, int fps) {
        return 1.0 - Math.pow(1.0 - coefficientAt30, 30.0 / Math.max(1, fps));
    }

    private static float percentile(float[] sorted, double percentile) {
        if (sorted.length == 1) {
            return sorted[0];
        }
        double position = Math.max(0.0, Math.min(1.0, percentile)) * (sorted.length - 1);
        int lower = (int) Math.floor(position);
        int upper = Math.min(sorted.length - 1, lower + 1);
        double fraction = position - lower;
        return (float) (sorted[lower] * (1.0 - fraction) + sorted[upper] * fraction);
    }

}
