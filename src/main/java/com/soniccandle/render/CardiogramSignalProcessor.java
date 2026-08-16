package com.soniccandle.render;

import com.soniccandle.analysis.SpectrumData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Crea una señal P-QRS-T temporal a partir de ataques musicales reales. */
public final class CardiogramSignalProcessor {

    private CardiogramSignalProcessor() {
    }

    public static float[] process(SpectrumData spectrum) {
        return processMusicalHits(spectrum);
    }

    public static float[] process(SpectrumData spectrum, CardiogramConfig config) {
        return timeline(spectrum, config).signal();
    }

    public static CardiogramTimeline timeline(SpectrumData spectrum,
            CardiogramConfig config) {
        CardiogramConfig safe = config == null ? CardiogramConfig.defaults() : config;
        if (safe.beatMode() == CardiogramBeatMode.ADAPTIVE_HEART_RATE) {
            float[] intensity = songIntensity(spectrum);
            return new CardiogramTimeline(
                    processAdaptiveHeartRate(spectrum, intensity), intensity,
                    sweepPositions(intensity));
        }
        return new CardiogramTimeline(processMusicalHits(spectrum),
                new float[spectrum.frameCount()], identityPositions(
                        spectrum.frameCount()));
    }

    static float sweepStepForIntensity(float intensity) {
        float safe = Math.max(0f, Math.min(1f,
                Float.isFinite(intensity) ? intensity : 0f));
        if (safe < 0.38f) {
            return 1f;
        }
        if (safe < 0.72f) {
            return 1.55f;
        }
        return 2.25f;
    }

    private static float[] sweepPositions(float[] intensity) {
        float[] positions = new float[intensity.length];
        for (int frame = 1; frame < positions.length; frame++) {
            positions[frame] = positions[frame - 1]
                    + sweepStepForIntensity(intensity[frame]);
        }
        return positions;
    }

    private static float[] identityPositions(int count) {
        float[] positions = new float[count];
        for (int frame = 1; frame < count; frame++) {
            positions[frame] = frame;
        }
        return positions;
    }

    private static float[] processMusicalHits(SpectrumData spectrum) {
        int count = spectrum.frameCount();
        float[] signal = new float[count];
        if (count == 0 || spectrum.bandCount() == 0) {
            return signal;
        }
        float[] bassEnergy = new float[count];
        float[] onset = new float[count];
        float[] previous = new float[spectrum.bandCount()];
        float previousBass = 0f;
        for (int frame = 0; frame < count; frame++) {
            float[] bands = spectrum.frame(frame);
            int lowCount = Math.max(3, Math.min(bands.length,
                    (int) Math.ceil(bands.length * 0.24)));
            float lowAverage = 0f;
            float lowPeak = 0f;
            float spectralFlux = 0f;
            for (int band = 0; band < bands.length; band++) {
                float value = Math.max(0f, bands[band]);
                if (band < lowCount) {
                    double weight = 1.0 - band / (double) (lowCount * 1.5);
                    lowAverage += value * Math.max(0.36, weight);
                    lowPeak = Math.max(lowPeak, value);
                }
                spectralFlux += Math.max(0f, value - previous[band]);
                previous[band] = value;
            }
            lowAverage /= lowCount;
            spectralFlux /= Math.max(1, bands.length);
            bassEnergy[frame] = lowAverage * 0.72f + lowPeak * 0.28f;
            float bassAttack = Math.max(0f, bassEnergy[frame] - previousBass);
            onset[frame] = bassAttack * 0.82f + spectralFlux * 0.18f;
            previousBass = bassEnergy[frame];
        }

        float[] score = normalizedBeatScore(onset, bassEnergy);
        List<Integer> beats = detectBeats(score, spectrum.framesPerSecond());
        float reference = Math.max(0.0001f, percentile(sortedPositive(score), 0.88));
        for (int beat : beats) {
            float strength = Math.max(0.38f,
                    Math.min(1f, score[beat] / reference));
            addHeartbeat(signal, beat, spectrum.framesPerSecond(), strength);
        }
        return signal;
    }

    private static float[] processAdaptiveHeartRate(SpectrumData spectrum,
            float[] intensity) {
        int count = spectrum.frameCount();
        float[] signal = new float[count];
        if (count == 0 || spectrum.bandCount() == 0) {
            return signal;
        }
        int fps = Math.max(1, spectrum.framesPerSecond());
        double phase = 1.0;
        for (int frame = 0; frame < count; frame++) {
            double bpm = bpmForIntensity(intensity[frame]);
            phase += bpm / (60.0 * fps);
            if (phase >= 1.0) {
                phase -= Math.floor(phase);
                float strength = 0.72f + intensity[frame] * 0.28f;
                addHeartbeat(signal, frame, fps, strength);
            }
        }
        return signal;
    }

    static float[] songIntensity(SpectrumData spectrum) {
        float[] raw = new float[spectrum.frameCount()];
        for (int frame = 0; frame < raw.length; frame++) {
            float[] bands = spectrum.frame(frame);
            if (bands.length == 0) {
                continue;
            }
            float[] ordered = bands.clone();
            Arrays.sort(ordered);
            int start = Math.max(0, (int) Math.floor(ordered.length * 0.62));
            double activeAverage = 0.0;
            for (int band = start; band < ordered.length; band++) {
                activeAverage += Math.max(0f, ordered[band]);
            }
            activeAverage /= Math.max(1, ordered.length - start);
            float peak = Math.max(0f, ordered[ordered.length - 1]);
            raw[frame] = (float) (activeAverage * 0.72 + peak * 0.28);
        }
        float[] active = sortedPositive(raw);
        if (active.length == 0) {
            return raw;
        }
        float low = percentile(active, 0.12);
        float high = percentile(active, 0.94);
        float range = high - low;
        for (int frame = 0; frame < raw.length; frame++) {
            if (raw[frame] <= 0.000001f) {
                raw[frame] = 0f;
            } else if (range > 0.015f) {
                raw[frame] = Math.max(0f,
                        Math.min(1f, (raw[frame] - low) / range));
            } else {
                raw[frame] = Math.max(0f,
                        Math.min(1f, raw[frame] / 0.72f));
            }
        }

        double attack = coefficientForFps(0.13, fpsOrOne(spectrum));
        double release = coefficientForFps(0.055, fpsOrOne(spectrum));
        float smoothed = 0f;
        for (int frame = 0; frame < raw.length; frame++) {
            double coefficient = raw[frame] >= smoothed ? attack : release;
            smoothed += (float) ((raw[frame] - smoothed) * coefficient);
            raw[frame] = smoothed;
        }
        return raw;
    }

    static double bpmForIntensity(float intensity) {
        double safe = Math.max(0.0, Math.min(1.0, intensity));
        return 58.0 + Math.pow(safe, 1.12) * 132.0;
    }

    private static int fpsOrOne(SpectrumData spectrum) {
        return Math.max(1, spectrum.framesPerSecond());
    }

    private static double coefficientForFps(double coefficientAt30, int fps) {
        return 1.0 - Math.pow(1.0 - coefficientAt30, 30.0 / Math.max(1, fps));
    }

    private static float[] normalizedBeatScore(float[] onset, float[] bassEnergy) {
        float onsetReference = Math.max(0.0001f,
                percentile(sortedPositive(onset), 0.90));
        float energyReference = Math.max(0.0001f,
                percentile(sortedPositive(bassEnergy), 0.82));
        float[] score = new float[onset.length];
        for (int frame = 0; frame < score.length; frame++) {
            if (onset[frame] < energyReference * 0.012f) {
                score[frame] = 0f;
                continue;
            }
            float attack = Math.min(2f, onset[frame] / onsetReference);
            float presence = Math.min(1.35f, bassEnergy[frame] / energyReference);
            score[frame] = attack * (0.78f + presence * 0.22f);
        }
        if (score.length >= 3) {
            float[] source = score.clone();
            for (int frame = 1; frame < score.length - 1; frame++) {
                score[frame] = source[frame - 1] * 0.16f
                        + source[frame] * 0.68f + source[frame + 1] * 0.16f;
            }
        }
        return score;
    }

    private static List<Integer> detectBeats(float[] score, int fps) {
        List<Integer> beats = new ArrayList<>();
        if (score.length < 3) {
            return beats;
        }
        float[] active = sortedPositive(score);
        if (active.length == 0) {
            return beats;
        }
        float globalThreshold = Math.max(0.12f, percentile(active, 0.58) * 0.82f);
        int localRadius = Math.max(2, (int) Math.round(Math.max(1, fps) * 0.55));
        int peakRadius = Math.max(1, (int) Math.round(Math.max(1, fps) * 0.065));
        int minimumGap = Math.max(2, (int) Math.round(Math.max(1, fps) * 0.19));
        for (int frame = 1; frame < score.length - 1; frame++) {
            float value = score[frame];
            if (value < globalThreshold || !isLocalMaximum(score, frame, peakRadius)) {
                continue;
            }
            int from = Math.max(0, frame - localRadius);
            int to = Math.min(score.length - 1, frame + localRadius);
            double sum = 0.0;
            double squared = 0.0;
            for (int index = from; index <= to; index++) {
                sum += score[index];
                squared += score[index] * score[index];
            }
            int samples = to - from + 1;
            double mean = sum / samples;
            double deviation = Math.sqrt(Math.max(0.0,
                    squared / samples - mean * mean));
            double adaptiveThreshold = mean + deviation * 0.42;
            if (value < adaptiveThreshold) {
                continue;
            }
            if (!beats.isEmpty() && frame - beats.get(beats.size() - 1) < minimumGap) {
                int previous = beats.get(beats.size() - 1);
                if (value > score[previous]) {
                    beats.set(beats.size() - 1, frame);
                }
            } else {
                beats.add(frame);
            }
        }
        return beats;
    }

    private static boolean isLocalMaximum(float[] values, int center, int radius) {
        int from = Math.max(0, center - radius);
        int to = Math.min(values.length - 1, center + radius);
        for (int index = from; index <= to; index++) {
            if (index != center && values[index] > values[center]) {
                return false;
            }
        }
        return center == from || values[center] > values[center - 1];
    }

    private static void addHeartbeat(float[] signal, int beat, int fps,
            float strength) {
        int safeFps = Math.max(1, fps);
        double[] seconds = {-0.20, -0.14, -0.10, -0.055, -0.025,
            0.0, 0.050, 0.13, 0.22, 0.31, 0.42};
        double[] values = {0.00, 0.00, 0.16, 0.00, -0.20,
            1.00, -0.48, 0.00, 0.30, 0.00, 0.00};
        int firstOffset = (int) Math.floor(seconds[0] * safeFps);
        int lastOffset = (int) Math.ceil(seconds[seconds.length - 1] * safeFps);
        for (int offset = firstOffset; offset <= lastOffset; offset++) {
            int index = beat + offset;
            if (index < 0 || index >= signal.length) {
                continue;
            }
            double position = offset / (double) safeFps;
            int section = 0;
            while (section + 1 < seconds.length - 1
                    && position > seconds[section + 1]) {
                section++;
            }
            double range = seconds[section + 1] - seconds[section];
            double fraction = range <= 0.0 ? 0.0
                    : (position - seconds[section]) / range;
            fraction = Math.max(0.0, Math.min(1.0, fraction));
            double eased = fraction * fraction * (3.0 - 2.0 * fraction);
            float sample = (float) ((values[section] * (1.0 - eased)
                    + values[section + 1] * eased) * strength);
            signal[index] = Math.max(-1f, Math.min(1f, signal[index] + sample));
        }
    }

    private static float[] sortedPositive(float[] values) {
        float[] buffer = new float[values.length];
        int count = 0;
        for (float value : values) {
            if (value > 0.000001f && Float.isFinite(value)) {
                buffer[count++] = value;
            }
        }
        float[] result = Arrays.copyOf(buffer, count);
        Arrays.sort(result);
        return result;
    }

    static float sample(float[] signal, double position) {
        if (signal == null || signal.length == 0 || position < 0.0
                || position > signal.length - 1) {
            return 0f;
        }
        int lower = (int) Math.floor(position);
        int upper = Math.min(signal.length - 1, lower + 1);
        double fraction = position - lower;
        return (float) (signal[lower] * (1.0 - fraction)
                + signal[upper] * fraction);
    }

    private static float percentile(float[] sorted, double percentile) {
        if (sorted.length == 0) {
            return 0f;
        }
        double position = Math.max(0.0, Math.min(1.0, percentile))
                * (sorted.length - 1);
        int lower = (int) Math.floor(position);
        int upper = Math.min(sorted.length - 1, lower + 1);
        double fraction = position - lower;
        return (float) (sorted[lower] * (1.0 - fraction)
                + sorted[upper] * fraction);
    }
}
