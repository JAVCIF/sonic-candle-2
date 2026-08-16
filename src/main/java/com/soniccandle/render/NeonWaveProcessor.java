package com.soniccandle.render;

import com.soniccandle.analysis.SpectrumData;

/** Reduce el espectro a una onda poligonal estable y calcula su energía. */
public final class NeonWaveProcessor {

    private NeonWaveProcessor() {
    }

    public static NeonWaveTimeline process(SpectrumData spectrum,
            NeonWaveConfig config) {
        NeonWaveConfig safe = config == null ? NeonWaveConfig.defaults() : config;
        float[][] points = new float[spectrum.frameCount()][safe.pointCount()];
        float[] energy = new float[spectrum.frameCount()];
        float[] onset = new float[spectrum.frameCount()];
        float previousEnergy = 0f;
        for (int frame = 0; frame < spectrum.frameCount(); frame++) {
            points[frame] = reduceFrame(spectrum.frame(frame), safe.pointCount());
            energy[frame] = frameEnergy(points[frame]);
            onset[frame] = Math.max(0f, energy[frame] - previousEnergy);
            previousEnergy = energy[frame];
        }
        return new NeonWaveTimeline(points, energy, onset);
    }

    public static float[] reduceFrame(float[] spectrum, int requestedPoints) {
        int pointCount = Math.max(NeonWaveConfig.MIN_POINTS,
                Math.min(NeonWaveConfig.MAX_POINTS, requestedPoints));
        float[] result = new float[pointCount];
        if (spectrum == null || spectrum.length == 0) {
            return result;
        }
        for (int point = 0; point < pointCount; point++) {
            int from = point * spectrum.length / pointCount;
            int to = Math.max(from + 1,
                    (point + 1) * spectrum.length / pointCount);
            to = Math.min(spectrum.length, to);
            float sum = 0f;
            float peak = 0f;
            for (int band = from; band < to; band++) {
                float value = Math.max(0f, spectrum[band]);
                sum += value;
                peak = Math.max(peak, value);
            }
            float average = sum / Math.max(1, to - from);
            result[point] = average * 0.58f + peak * 0.42f;
        }
        if (pointCount > 2) {
            float[] source = result.clone();
            for (int point = 1; point < pointCount - 1; point++) {
                result[point] = source[point - 1] * 0.14f
                        + source[point] * 0.72f
                        + source[point + 1] * 0.14f;
            }
        }
        return result;
    }

    static float frameEnergy(float[] points) {
        if (points.length == 0) {
            return 0f;
        }
        float sum = 0f;
        float peak = 0f;
        for (float value : points) {
            float safe = Math.max(0f, value);
            sum += safe;
            peak = Math.max(peak, safe);
        }
        return Math.min(1.5f,
                (sum / points.length) * 0.62f + peak * 0.38f);
    }
}
