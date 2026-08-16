package com.soniccandle.render;

import com.soniccandle.analysis.SpectrumData;

public final class CardiogramSignalProcessorTest {

    private CardiogramSignalProcessorTest() {
    }

    public static void main(String[] args) {
        silenceStaysFlat();
        detectedBeatsOwnTheRPeak();
        backgroundVariationDoesNotInventBeats();
        adaptiveHeartRateFollowsIntensity();
        adaptiveHeartRateIgnoresSweepSpeed();
        adaptiveSweepAdvancesInThreeGears();
        speedChangesHistoryNotBeatTiming();
        System.out.println("CardiogramSignalProcessorTest OK");
    }

    private static void silenceStaysFlat() {
        float[] result = CardiogramSignalProcessor.process(
                new SpectrumData(new float[120][32], 30, 48_000));
        for (float value : result) {
            check(value == 0f, "Silence generated a false heartbeat");
        }
    }

    private static void detectedBeatsOwnTheRPeak() {
        float[][] frames = musicalFrames(210, 40);
        int[] beats = {24, 61, 103, 148, 186};
        for (int beat : beats) {
            for (int band = 0; band < 10; band++) {
                frames[beat][band] += 0.85f;
            }
            for (int band = 10; band < frames[beat].length; band++) {
                frames[beat][band] += 0.25f;
            }
        }
        float[] result = CardiogramSignalProcessor.process(
                new SpectrumData(frames, 30, 48_000));
        for (int beat : beats) {
            check(result[beat] > 0.72f,
                    "The R peak is not synchronized with musical frame " + beat);
            check(result[beat - 1] < result[beat]
                            && result[beat + 1] < result[beat],
                    "The detected beat is not the local R maximum");
        }
        check(countRPeaks(result) == beats.length,
                "The detector invented or lost musical beats");
    }

    private static void backgroundVariationDoesNotInventBeats() {
        float[] result = CardiogramSignalProcessor.process(new SpectrumData(
                musicalFrames(240, 48), 30, 48_000));
        check(countRPeaks(result) == 0,
                "Small continuous spectrum variation generated random beats");
    }

    private static float[][] musicalFrames(int count, int bands) {
        float[][] frames = new float[count][bands];
        for (int frame = 0; frame < count; frame++) {
            for (int band = 0; band < bands; band++) {
                frames[frame][band] = 0.025f
                        + (float) Math.sin(frame * 0.11 + band * 0.17) * 0.0025f;
            }
        }
        return frames;
    }

    private static int countRPeaks(float[] signal) {
        int count = 0;
        for (int frame = 1; frame < signal.length - 1; frame++) {
            if (signal[frame] > 0.70f && signal[frame] > signal[frame - 1]
                    && signal[frame] >= signal[frame + 1]) {
                count++;
            }
        }
        return count;
    }

    private static void speedChangesHistoryNotBeatTiming() {
        int slow = CardiogramSpeedMode.SLOW.visibleFrames(30);
        int normal = CardiogramSpeedMode.SYNCHRONIZED.visibleFrames(30);
        int fast = CardiogramSpeedMode.FAST.visibleFrames(30);
        check(slow > normal && normal > fast,
                "Sweep modes do not order their visible history correctly");
    }

    private static void adaptiveHeartRateFollowsIntensity() {
        int fps = 30;
        float[][] frames = new float[fps * 30][40];
        for (int frame = 0; frame < frames.length; frame++) {
            float level = frame < fps * 10 ? 0.06f
                    : frame < fps * 20 ? 0.34f : 0.86f;
            for (int band = 0; band < frames[frame].length; band++) {
                frames[frame][band] = level * (0.88f + band * 0.003f);
            }
        }
        SpectrumData spectrum = new SpectrumData(frames, fps, 48_000);
        float[] signal = CardiogramSignalProcessor.process(spectrum,
                new CardiogramConfig(CardiogramBeatMode.ADAPTIVE_HEART_RATE,
                        CardiogramSpeedMode.SYNCHRONIZED,
                        CardiogramStyle.ROUNDED, false));
        int calm = countRPeaks(signal, fps, fps * 9);
        int active = countRPeaks(signal, fps * 11, fps * 19);
        int intense = countRPeaks(signal, fps * 21, fps * 29);
        check(calm >= 6, "The resting heart rate stopped beating");
        check(active > calm, "Medium musical intensity did not raise heart rate");
        check(intense > active, "High intensity did not approach tachycardia");
        check(CardiogramSignalProcessor.bpmForIntensity(0f) == 58.0,
                "Resting BPM changed unexpectedly");
        check(CardiogramSignalProcessor.bpmForIntensity(1f) == 190.0,
                "Maximum BPM no longer reaches the tachycardic range");
    }

    private static void adaptiveHeartRateIgnoresSweepSpeed() {
        SpectrumData spectrum = new SpectrumData(musicalFrames(300, 32),
                30, 48_000);
        float[] slow = CardiogramSignalProcessor.process(spectrum,
                adaptive(CardiogramSpeedMode.SLOW));
        float[] fast = CardiogramSignalProcessor.process(spectrum,
                adaptive(CardiogramSpeedMode.FAST));
        check(java.util.Arrays.equals(slow, fast),
                "Sweep speed changed adaptive heartbeat timing");
        check(!adaptive(CardiogramSpeedMode.FAST).usesConfigurableSweepSpeed(),
                "Adaptive heartbeat incorrectly enables sweep speed");
        check(adaptive(CardiogramSpeedMode.FAST).effectiveSpeedMode()
                        == CardiogramSpeedMode.SYNCHRONIZED,
                "Adaptive heartbeat did not use its fixed sweep");
        CardiogramConfig hits = new CardiogramConfig(
                CardiogramBeatMode.MUSICAL_HITS, CardiogramSpeedMode.FAST,
                CardiogramStyle.THIN, false);
        check(hits.usesConfigurableSweepSpeed()
                        && hits.effectiveSpeedMode() == CardiogramSpeedMode.FAST,
                "Musical hits lost its configurable sweep speed");
    }

    private static void adaptiveSweepAdvancesInThreeGears() {
        int fps = 30;
        CardiogramConfig adaptiveSweep = new CardiogramConfig(
                CardiogramBeatMode.ADAPTIVE_HEART_RATE,
                CardiogramSpeedMode.SYNCHRONIZED,
                CardiogramStyle.ROUNDED, false, true);
        int normalWindow = CardiogramSpeedMode.SYNCHRONIZED
                .visibleFrames(fps);
        check(adaptiveSweep.visibleFramesAt(fps, 0f) == normalWindow
                        && adaptiveSweep.visibleFramesAt(fps, 0.5f)
                        == normalWindow
                        && adaptiveSweep.visibleFramesAt(fps, 1f)
                        == normalWindow,
                "Adaptive sweep resized the whole trace like an accordion");
        float normal = CardiogramSignalProcessor.sweepStepForIntensity(0.2f);
        float fast = CardiogramSignalProcessor.sweepStepForIntensity(0.55f);
        float veryFast = CardiogramSignalProcessor
                .sweepStepForIntensity(0.9f);
        check(normal == 1f && fast == 1.55f && veryFast == 2.25f,
                "Adaptive sweep lost its Normal, Fast and Very fast gears");
        check(normal < fast && fast < veryFast,
                "Adaptive sweep gears are not ordered correctly");

        CardiogramConfig fixedSweep = new CardiogramConfig(
                CardiogramBeatMode.ADAPTIVE_HEART_RATE,
                CardiogramSpeedMode.FAST, CardiogramStyle.ROUNDED,
                false, false);
        check(fixedSweep.visibleFramesAt(fps, 0f) == normalWindow
                        && fixedSweep.visibleFramesAt(fps, 1f) == normalWindow,
                "Disabling adaptive sweep did not keep synchronized speed");

        CardiogramConfig musicalHits = new CardiogramConfig(
                CardiogramBeatMode.MUSICAL_HITS, CardiogramSpeedMode.FAST,
                CardiogramStyle.THIN, false, true);
        check(!musicalHits.usesAdaptiveSweep()
                        && musicalHits.visibleFramesAt(fps, 0f)
                        == CardiogramSpeedMode.FAST.visibleFrames(fps)
                        && musicalHits.visibleFramesAt(fps, 1f)
                        == CardiogramSpeedMode.FAST.visibleFrames(fps),
                "Adaptive sweep leaked into Musical hits");
        check(CardiogramConfig.defaults().usesAdaptiveSweep(),
                "Adaptive sweep is not enabled in the default configuration");
    }

    private static CardiogramConfig adaptive(CardiogramSpeedMode speed) {
        return new CardiogramConfig(CardiogramBeatMode.ADAPTIVE_HEART_RATE,
                speed, CardiogramStyle.THIN, false);
    }

    private static int countRPeaks(float[] signal, int from, int to) {
        int count = 0;
        int start = Math.max(1, from);
        int end = Math.min(signal.length - 1, to);
        for (int frame = start; frame < end; frame++) {
            if (signal[frame] > 0.62f && signal[frame] > signal[frame - 1]
                    && signal[frame] >= signal[frame + 1]) {
                count++;
            }
        }
        return count;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
