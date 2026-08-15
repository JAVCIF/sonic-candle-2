package com.soniccandle.analysis;

import java.util.Arrays;

/** Ajusta presencia por banda usando ganancias constantes durante toda la canción. */
public final class FrequencyDistributionProcessor {

    private static final float MIN_ACTIVE_VALUE = 0.000_1f;

    private FrequencyDistributionProcessor() {
    }

    public static void apply(float[][] frames, FrequencyDistributionMode mode) {
        if (frames.length == 0 || frames[0].length == 0
                || mode == null || mode == FrequencyDistributionMode.STANDARD) {
            return;
        }
        int bandCount = frames[0].length;
        for (float[] frame : frames) {
            if (frame.length != bandCount) {
                throw new IllegalArgumentException(
                        "Todos los fotogramas deben tener la misma cantidad de bandas.");
            }
        }
        float[] gains = switch (mode) {
            case STANDARD -> unityGains(bandCount);
            case BALANCED -> balancedGains(bandCount);
            case PROPORTIONAL -> proportionalGains(frames, bandCount);
        };
        for (float[] frame : frames) {
            for (int band = 0; band < bandCount; band++) {
                float value = Math.max(0f, frame[band]);
                frame[band] = value * gains[band];
            }
        }
    }

    static float[] gainsFor(float[][] frames, FrequencyDistributionMode mode) {
        int bandCount = frames.length == 0 ? 0 : frames[0].length;
        return switch (mode) {
            case STANDARD -> unityGains(bandCount);
            case BALANCED -> balancedGains(bandCount);
            case PROPORTIONAL -> proportionalGains(frames, bandCount);
        };
    }

    private static float[] unityGains(int bandCount) {
        float[] gains = new float[bandCount];
        Arrays.fill(gains, 1f);
        return gains;
    }

    private static float[] balancedGains(int bandCount) {
        float[] gains = new float[bandCount];
        for (int band = 0; band < bandCount; band++) {
            double position = bandCount <= 1 ? 0.0 : (double) band / (bandCount - 1);
            double highBandPosition = Math.max(0.0, (position - 0.24) / 0.76);
            gains[band] = (float) (1.0 + 1.15 * Math.pow(highBandPosition, 1.55));
        }
        return gains;
    }

    private static float[] proportionalGains(float[][] frames, int bandCount) {
        float[] references = new float[bandCount];
        for (int band = 0; band < bandCount; band++) {
            references[band] = activePercentile(frames, band, 0.84);
        }

        float[] activeReferences = new float[bandCount];
        int activeCount = 0;
        for (float reference : references) {
            if (reference > MIN_ACTIVE_VALUE) {
                activeReferences[activeCount++] = reference;
            }
        }
        activeReferences = Arrays.copyOf(activeReferences, activeCount);
        if (activeReferences.length == 0) {
            return unityGains(bandCount);
        }
        Arrays.sort(activeReferences);
        float target = activeReferences[percentileIndex(activeReferences.length, 0.55)];

        float[] rawGains = new float[bandCount];
        for (int band = 0; band < bandCount; band++) {
            float reference = references[band];
            if (reference <= MIN_ACTIVE_VALUE) {
                rawGains[band] = 1f;
                continue;
            }
            double ratio = target / reference;
            rawGains[band] = (float) clamp(Math.pow(ratio, 0.76), 0.70, 4.25);
        }

        float[] gains = smoothGains(rawGains);
        float before = globalPercentile(frames, null, 0.995);
        float after = globalPercentile(frames, gains, 0.995);
        float compensation = after > before && before > MIN_ACTIVE_VALUE
                ? (float) clamp(before / after, 0.72, 1.0) : 1f;
        for (int band = 0; band < gains.length; band++) {
            gains[band] *= compensation;
        }
        return gains;
    }

    private static float[] smoothGains(float[] source) {
        if (source.length < 3) {
            return source.clone();
        }
        float[] result = new float[source.length];
        result[0] = (source[0] * 3f + source[1]) / 4f;
        for (int band = 1; band < source.length - 1; band++) {
            result[band] = (source[band - 1] + source[band] * 2f
                    + source[band + 1]) / 4f;
        }
        int last = source.length - 1;
        result[last] = (source[last - 1] + source[last] * 3f) / 4f;
        return result;
    }

    private static float activePercentile(float[][] frames, int band, double percentile) {
        float[] values = new float[frames.length];
        int count = 0;
        for (float[] frame : frames) {
            if (frame[band] > MIN_ACTIVE_VALUE) {
                values[count++] = frame[band];
            }
        }
        if (count == 0) {
            return 0f;
        }
        Arrays.sort(values, 0, count);
        return values[percentileIndex(count, percentile)];
    }

    private static float globalPercentile(float[][] frames, float[] gains, double percentile) {
        long total = (long) frames.length * frames[0].length;
        int stride = (int) Math.max(1L, total / 250_000L);
        int capacity = (int) ((total + stride - 1L) / stride);
        float[] values = new float[capacity];
        int count = 0;
        long index = 0;
        for (float[] frame : frames) {
            for (int band = 0; band < frame.length; band++, index++) {
                if (index % stride == 0) {
                    float gain = gains == null ? 1f : gains[band];
                    values[count++] = Math.max(0f, frame[band]) * gain;
                }
            }
        }
        Arrays.sort(values, 0, count);
        return values[percentileIndex(count, percentile)];
    }

    private static int percentileIndex(int length, double percentile) {
        return Math.max(0, Math.min(length - 1,
                (int) Math.round((length - 1) * percentile)));
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

}
