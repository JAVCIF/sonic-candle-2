package com.soniccandle.analysis;

import com.soniccandle.ffmpeg.FFmpegLocator;
import com.soniccandle.ffmpeg.ProcessLog;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;

public final class AudioAnalyzer {

    public static final int SAMPLE_RATE = 44_100;
    private static final double MIN_FREQUENCY = 45.0;
    private static final double MAX_FREQUENCY = 16_000.0;
    private static final float MINIMUM_NOISE_FLOOR = 0.000_25f;
    private static final float REFERENCE_HEIGHT = 0.82f;

    public SpectrumData analyze(
            Path audio,
            int framesPerSecond,
            int bandCount,
            MotionMode motionMode,
            SpectrumMode spectrumMode,
            IntConsumer progress,
            BooleanSupplier cancelled) throws IOException, InterruptedException {
        return analyze(audio, framesPerSecond, bandCount, motionMode, spectrumMode,
                FrequencyDistributionMode.STANDARD, progress, cancelled);
    }

    public SpectrumData analyze(
            Path audio,
            int framesPerSecond,
            int bandCount,
            MotionMode motionMode,
            SpectrumMode spectrumMode,
            FrequencyDistributionMode distributionMode,
            IntConsumer progress,
            BooleanSupplier cancelled) throws IOException, InterruptedException {

        Path ffmpeg = FFmpegLocator.findRequired("ffmpeg");
        double duration = FFmpegLocator.probeDuration(audio);
        int expectedFrames = duration > 0 ? Math.max(1, (int) Math.ceil(duration * framesPerSecond)) : 0;
        int hopSize = Math.max(1, SAMPLE_RATE / framesPerSecond);
        int windowSize = motionMode.windowSize();

        List<String> command = List.of(
                ffmpeg.toString(), "-v", "error", "-i", audio.toString(),
                "-vn", "-ac", "1", "-ar", Integer.toString(SAMPLE_RATE),
                "-f", "f32le", "pipe:1");

        Process process = new ProcessBuilder(command).start();
        ProcessLog log = ProcessLog.drain(process.getErrorStream());
        List<float[]> rawFrames = new ArrayList<>(Math.max(expectedFrames, 256));
        double[] hann = createHannWindow(windowSize);
        float[] audioWindow = new float[windowSize];
        int validSamples;

        try (DataInputStream input = new DataInputStream(new BufferedInputStream(process.getInputStream(), 64 * 1024))) {
            validSamples = readFloats(input, audioWindow, 0, windowSize);
            while (validSamples > 0) {
                if (cancelled.getAsBoolean()) {
                    process.destroyForcibly();
                    throw new CancellationException("Análisis cancelado.");
                }

                rawFrames.add(analyzeWindow(
                        audioWindow, validSamples, hann, bandCount, spectrumMode));
                if (expectedFrames > 0) {
                    progress.accept(Math.min(99, rawFrames.size() * 100 / expectedFrames));
                }

                int consumed = Math.min(hopSize, validSamples);
                int remaining = validSamples - consumed;
                if (remaining > 0) {
                    System.arraycopy(audioWindow, consumed, audioWindow, 0, remaining);
                }
                validSamples = remaining + readFloats(input, audioWindow, remaining, windowSize - remaining);
            }
        } catch (IOException | RuntimeException exception) {
            process.destroyForcibly();
            throw exception;
        }

        int exitCode = process.waitFor();
        log.await();
        if (exitCode != 0) {
            throw new IOException("FFmpeg no pudo decodificar el audio.\n" + log.tail());
        }
        if (rawFrames.isEmpty()) {
            throw new IOException("El audio no produjo muestras analizables.");
        }

        float[][] normalized = normalizeAndSmooth(rawFrames, bandCount, motionMode);
        FrequencyDistributionProcessor.apply(normalized, distributionMode);
        progress.accept(100);
        return new SpectrumData(normalized, framesPerSecond, SAMPLE_RATE);
    }

    private static float[] analyzeWindow(float[] samples, int validSamples, double[] hann,
            int bandCount, SpectrumMode spectrumMode) {
        int windowSize = samples.length;
        double[] real = new double[windowSize];
        double[] imaginary = new double[windowSize];
        for (int i = 0; i < validSamples; i++) {
            real[i] = samples[i] * hann[i];
        }
        FastFourierTransform.transform(real, imaginary);

        if (spectrumMode == SpectrumMode.CLASSIC_INTERLEAVED) {
            return analyzeClassicInterleaved(real, imaginary, bandCount);
        }

        float[] bands = new float[bandCount];
        double ratio = MAX_FREQUENCY / MIN_FREQUENCY;
        int previousHighBin = Math.max(1,
                (int) Math.ceil(MIN_FREQUENCY * windowSize / SAMPLE_RATE) - 1);
        for (int band = 0; band < bandCount; band++) {
            double lowFrequency = MIN_FREQUENCY * Math.pow(ratio, (double) band / bandCount);
            double highFrequency = MIN_FREQUENCY * Math.pow(ratio, (double) (band + 1) / bandCount);
            int desiredLowBin = Math.max(1, (int) Math.ceil(lowFrequency * windowSize / SAMPLE_RATE));
            int lowBin = Math.max(desiredLowBin, previousHighBin + 1);
            int highBin = Math.min(windowSize / 2 - 1,
                    Math.max(lowBin, (int) Math.ceil(highFrequency * windowSize / SAMPLE_RATE) - 1));
            previousHighBin = highBin;

            double peakMagnitude = 0.0;
            for (int bin = lowBin; bin <= highBin; bin++) {
                peakMagnitude = Math.max(peakMagnitude, Math.hypot(real[bin], imaginary[bin]));
            }
            // La ventana Hann deja aproximadamente un cuarto de N como magnitud
            // para una senoide a escala completa. Esta conversión conserva la
            // amplitud real en lugar de comprimirla con log1p.
            bands[band] = (float) (peakMagnitude * 4.0 / windowSize);
        }
        return bands;
    }

    private static float[] analyzeClassicInterleaved(
            double[] real, double[] imaginary, int bandCount) {
        int windowSize = real.length;
        double[] interleaved = new double[windowSize * 2];
        for (int bin = 0; bin < windowSize; bin++) {
            interleaved[bin * 2] = real[bin];
            interleaved[bin * 2 + 1] = imaginary[bin];
        }

        float[] bands = new float[bandCount];
        int pointsPerBar = Math.max(2,
                (int) Math.round(((windowSize * 2.0) / bandCount) / 16.0));
        for (int band = 0; band < bandCount; band++) {
            int start = pointsPerBar * band;
            int end = Math.min(pointsPerBar * (band + 1), windowSize);
            double selectedReal = 0.0;
            double selectedMagnitude = 0.0;
            for (int index = start; index + 1 < end; index += 2) {
                double realPart = interleaved[index];
                double imaginaryPart = interleaved[index + 1];
                double magnitude = Math.hypot(realPart, imaginaryPart);
                if (Math.abs(realPart) > selectedReal) {
                    selectedReal = Math.abs(realPart);
                    selectedMagnitude = magnitude;
                }
            }
            bands[band] = (float) (selectedMagnitude * 4.0 / windowSize);
        }
        if (bands.length > 2 && bands[1] == 0f) {
            bands[1] = (bands[0] + bands[2]) / 2f;
        }
        return bands;
    }

    private static float[][] normalizeAndSmooth(
            List<float[]> rawFrames, int bandCount, MotionMode motionMode) {
        float[] noiseFloor = new float[bandCount];
        for (int band = 0; band < bandCount; band++) {
            noiseFloor[band] = Math.max(MINIMUM_NOISE_FLOOR,
                    percentileForBand(rawFrames, band, 0.10) * 1.10f);
        }

        float reference = Math.max(MINIMUM_NOISE_FLOOR,
                percentileAdjusted(rawFrames, noiseFloor, 0.995));
        float[][] result = new float[rawFrames.size()][bandCount];
        float[] previous = new float[bandCount];
        for (int frameIndex = 0; frameIndex < rawFrames.size(); frameIndex++) {
            float[] source = rawFrames.get(frameIndex);
            for (int band = 0; band < bandCount; band++) {
                float adjusted = Math.max(0f, source[band] - noiseFloor[band]);
                float relative = adjusted / reference;
                // No existe techo en 1.0: los picos que superan la referencia
                // siguen creciendo y el lienzo es quien los recorta físicamente.
                float target = (float) Math.pow(relative, 0.78) * REFERENCE_HEIGHT;
                float factor = target >= previous[band]
                        ? motionMode.attack() : motionMode.release();
                previous[band] += (target - previous[band]) * factor;
                result[frameIndex][band] = previous[band];
            }
        }
        return result;
    }

    private static float percentileForBand(List<float[]> frames, int band, double percentile) {
        float[] values = new float[frames.size()];
        for (int index = 0; index < frames.size(); index++) {
            values[index] = frames.get(index)[band];
        }
        Arrays.sort(values);
        return values[percentileIndex(values.length, percentile)];
    }

    private static float percentileAdjusted(List<float[]> frames, float[] noiseFloor, double percentile) {
        long totalValues = (long) frames.size() * noiseFloor.length;
        int stride = (int) Math.max(1L, totalValues / 300_000L);
        int sampleCount = (int) ((totalValues + stride - 1L) / stride);
        float[] sample = new float[sampleCount];
        int sampleIndex = 0;
        long valueIndex = 0;
        for (float[] frame : frames) {
            for (int band = 0; band < noiseFloor.length; band++, valueIndex++) {
                if (valueIndex % stride == 0) {
                    sample[sampleIndex++] = Math.max(0f, frame[band] - noiseFloor[band]);
                }
            }
        }
        if (sampleIndex != sample.length) {
            sample = Arrays.copyOf(sample, sampleIndex);
        }
        Arrays.sort(sample);
        return sample[percentileIndex(sample.length, percentile)];
    }

    private static int percentileIndex(int length, double percentile) {
        return Math.max(0, Math.min(length - 1,
                (int) Math.round((length - 1) * percentile)));
    }

    private static double[] createHannWindow(int windowSize) {
        double[] window = new double[windowSize];
        for (int i = 0; i < windowSize; i++) {
            window[i] = 0.5 * (1.0 - Math.cos(2.0 * Math.PI * i / (windowSize - 1)));
        }
        return window;
    }

    private static int readFloats(DataInputStream input, float[] target, int offset, int maximum) throws IOException {
        int count = 0;
        while (count < maximum) {
            try {
                int littleEndianBits = Integer.reverseBytes(input.readInt());
                target[offset + count] = Float.intBitsToFloat(littleEndianBits);
            } catch (EOFException eof) {
                break;
            }
            count++;
        }
        return count;
    }
}
