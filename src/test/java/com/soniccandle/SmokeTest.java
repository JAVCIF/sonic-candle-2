package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.FastFourierTransform;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import com.soniccandle.render.BarStyle;
import com.soniccandle.render.CircularConfig;
import com.soniccandle.render.PeakMode;
import com.soniccandle.render.RenderConfig;
import com.soniccandle.render.RestingLineMode;
import com.soniccandle.render.VisualizationMode;
import com.soniccandle.render.VideoEncoder;
import java.awt.Color;
import java.nio.file.Path;

/** Prueba integral sin dependencias de testing externas. */
public final class SmokeTest {

    private SmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        testFftPeak();
        if (args.length == 2) {
            Path audio = Path.of(args[0]);
            Path output = Path.of(args[1]);
            SpectrumData spectrum = new AudioAnalyzer().analyze(
                    audio, 30, 48, MotionMode.AGILE, SpectrumMode.STANDARD,
                    value -> { }, () -> false);
            if (spectrum.frameCount() < 1 || spectrum.bandCount() != 48) {
                throw new AssertionError("El análisis no produjo el espectro esperado.");
            }
            RenderConfig config = new RenderConfig(
                    320, 180, 30, new Color(179, 127, 255), new Color(28, 18, 51),
                    null, BarStyle.ROUND_FILLED, 1.0f,
                    RestingLineMode.DOTTED, PeakMode.FREE_OVERFLOW,
                    VisualizationMode.LINEAR, CircularConfig.defaults());
            new VideoEncoder().encode(audio, output, spectrum, config, value -> { }, () -> false);
            System.out.println("Smoke test integral correcto: " + output);
        } else {
            System.out.println("FFT correcta. Para probar render: SmokeTest <audio> <salida.mp4>");
        }
    }

    private static void testFftPeak() {
        int size = 1_024;
        int expectedBin = 37;
        double[] real = new double[size];
        double[] imaginary = new double[size];
        for (int index = 0; index < size; index++) {
            real[index] = Math.sin(2.0 * Math.PI * expectedBin * index / size);
        }
        FastFourierTransform.transform(real, imaginary);
        int peakBin = 1;
        double peak = 0;
        for (int bin = 1; bin < size / 2; bin++) {
            double magnitude = Math.hypot(real[bin], imaginary[bin]);
            if (magnitude > peak) {
                peak = magnitude;
                peakBin = bin;
            }
        }
        if (peakBin != expectedBin) {
            throw new AssertionError("Pico FFT incorrecto: " + peakBin + " != " + expectedBin);
        }
    }
}
