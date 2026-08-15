package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import java.nio.file.Path;

/** Comprueba que los modos rápidos abandonen antes un golpe ya terminado. */
public final class MotionModeTest {

    private MotionModeTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("MotionModeTest <audio-prueba.wav>");
        }
        Path audio = Path.of(args[0]);
        SpectrumData normal = analyze(audio, MotionMode.NORMAL);
        SpectrumData agile = analyze(audio, MotionMode.AGILE);
        SpectrumData fast = analyze(audio, MotionMode.FAST);

        float normalTail = framePeak(normal, 67);
        float agileTail = framePeak(agile, 67);
        float fastTail = framePeak(fast, 67);
        if (!(fastTail < agileTail && agileTail < normalTail)) {
            throw new AssertionError("La caída no respeta el orden esperado: "
                    + normalTail + ", " + agileTail + ", " + fastTail);
        }
        System.out.printf("Caída por modo: normal=%.4f, ágil=%.4f, rápido=%.4f%n",
                normalTail, agileTail, fastTail);
    }

    private static SpectrumData analyze(Path audio, MotionMode mode) throws Exception {
        return new AudioAnalyzer().analyze(
                audio, 30, 64, mode, SpectrumMode.STANDARD, value -> { }, () -> false);
    }

    private static float framePeak(SpectrumData spectrum, int frameIndex) {
        float peak = 0f;
        for (float value : spectrum.frame(frameIndex)) {
            peak = Math.max(peak, value);
        }
        return peak;
    }
}
