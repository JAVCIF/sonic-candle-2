package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import java.nio.file.Path;

/** Verifica que el análisis conserve contraste entre pasajes suaves, fuertes y silenciosos. */
public final class SpectrumResponseTest {

    private SpectrumResponseTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("SpectrumResponseTest <audio-prueba.wav>");
        }

        SpectrumData spectrum = new AudioAnalyzer().analyze(
                Path.of(args[0]), 30, 64, MotionMode.NORMAL, SpectrumMode.STANDARD,
                value -> { }, () -> false);
        float quiet = averageFramePeak(spectrum, 10, 25);
        float loud = averageFramePeak(spectrum, 40, 55);
        float silence = averageFramePeak(spectrum, 78, 87);

        if (loud < quiet * 4f) {
            throw new AssertionError("El pasaje fuerte no se separó del suave: " + loud + " / " + quiet);
        }
        if (silence > quiet * 0.35f) {
            throw new AssertionError("El espectro no cayó suficientemente en silencio: " + silence + " / " + quiet);
        }
        System.out.printf("Respuesta correcta: suave=%.4f, fuerte=%.4f, silencio=%.4f%n",
                quiet, loud, silence);
    }

    private static float averageFramePeak(SpectrumData spectrum, int start, int end) {
        float sum = 0f;
        int count = 0;
        for (int frameIndex = start; frameIndex <= end && frameIndex < spectrum.frameCount(); frameIndex++) {
            float peak = 0f;
            for (float value : spectrum.frame(frameIndex)) {
                peak = Math.max(peak, value);
            }
            sum += peak;
            count++;
        }
        return sum / Math.max(1, count);
    }
}
