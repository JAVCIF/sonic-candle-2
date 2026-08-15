package com.soniccandle.analysis;

import java.util.Arrays;

/** Verifica presencia aguda, proporcionalidad y conservación de la cadencia. */
public final class FrequencyDistributionProcessorTest {

    private FrequencyDistributionProcessorTest() {
    }

    public static void main(String[] args) {
        float[][] source = syntheticSong(210, 32);
        testStandardIsIdentity(source);
        testBalancedRightPresence(source);
        testProportionalUsage(source);
        System.out.println("Distribución correcta: estándar intacto, derecha presente y ritmo conservado.");
    }

    private static void testStandardIsIdentity(float[][] source) {
        float[][] result = copy(source);
        FrequencyDistributionProcessor.apply(result, FrequencyDistributionMode.STANDARD);
        for (int frame = 0; frame < source.length; frame++) {
            if (!Arrays.equals(source[frame], result[frame])) {
                throw new AssertionError("El modo Estándar modificó el espectro existente.");
            }
        }
    }

    private static void testBalancedRightPresence(float[][] source) {
        float[][] result = copy(source);
        FrequencyDistributionProcessor.apply(result, FrequencyDistributionMode.BALANCED);
        double before = rightToLeftPresence(source);
        double after = rightToLeftPresence(result);
        if (!(after > before * 1.70)) {
            throw new AssertionError("Equilibrado no reforzó suficientemente la derecha: "
                    + before + " -> " + after);
        }
        assertCadencePreserved(source, result);
        assertSilencePreserved(result);
    }

    private static void testProportionalUsage(float[][] source) {
        float[][] result = copy(source);
        FrequencyDistributionProcessor.apply(result, FrequencyDistributionMode.PROPORTIONAL);
        double disparityBefore = bandDisparity(source);
        double disparityAfter = bandDisparity(result);
        if (!(disparityAfter < disparityBefore * 0.62)) {
            throw new AssertionError("Proporcional no distribuyó las bandas: "
                    + disparityBefore + " -> " + disparityAfter);
        }
        if (!(rightToLeftPresence(result) > rightToLeftPresence(source) * 2.0)) {
            throw new AssertionError("Proporcional dejó sin presencia el extremo derecho.");
        }
        assertCadencePreserved(source, result);
        assertSilencePreserved(result);
    }

    private static void assertCadencePreserved(float[][] source, float[][] result) {
        for (int band = 0; band < source[0].length; band++) {
            float expectedGain = -1f;
            for (int frame = 0; frame < source.length; frame++) {
                float input = source[frame][band];
                if (input <= 0.0001f) {
                    continue;
                }
                float gain = result[frame][band] / input;
                if (expectedGain < 0f) {
                    expectedGain = gain;
                } else if (Math.abs(gain - expectedGain) > 0.000_05f) {
                    throw new AssertionError("La ganancia cambió con el tiempo en la banda " + band);
                }
            }
        }
    }

    private static void assertSilencePreserved(float[][] result) {
        for (int frame = 0; frame < 12; frame++) {
            for (float value : result[frame]) {
                if (value != 0f) {
                    throw new AssertionError("La distribución inventó frecuencia durante el silencio.");
                }
            }
        }
    }

    private static double rightToLeftPresence(float[][] frames) {
        int quarter = frames[0].length / 4;
        double left = 0.0;
        double right = 0.0;
        for (float[] frame : frames) {
            for (int band = 0; band < quarter; band++) {
                left += frame[band];
                right += frame[frame.length - 1 - band];
            }
        }
        return right / Math.max(0.000_001, left);
    }

    private static double bandDisparity(float[][] frames) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = 0.0;
        for (int band = 0; band < frames[0].length; band++) {
            double sum = 0.0;
            int count = 0;
            for (float[] frame : frames) {
                if (frame[band] > 0.0001f) {
                    sum += frame[band];
                    count++;
                }
            }
            double average = sum / Math.max(1, count);
            minimum = Math.min(minimum, average);
            maximum = Math.max(maximum, average);
        }
        return maximum / Math.max(0.000_001, minimum);
    }

    private static float[][] syntheticSong(int frameCount, int bandCount) {
        float[][] frames = new float[frameCount][bandCount];
        for (int frame = 12; frame < frameCount; frame++) {
            double rhythm = 0.18
                    + Math.pow(Math.sin(frame * 0.19), 4.0) * 0.72
                    + Math.pow(Math.sin(frame * 0.047), 6.0) * 0.20;
            for (int band = 0; band < bandCount; band++) {
                double spectralSlope = 1.0 / (1.0 + band * 0.17);
                double localMotion = 0.82 + Math.sin(frame * 0.11 + band * 0.23) * 0.18;
                frames[frame][band] = (float) (rhythm * spectralSlope * localMotion);
            }
        }
        return frames;
    }

    private static float[][] copy(float[][] source) {
        float[][] result = new float[source.length][];
        for (int frame = 0; frame < source.length; frame++) {
            result[frame] = source[frame].clone();
        }
        return result;
    }
}
