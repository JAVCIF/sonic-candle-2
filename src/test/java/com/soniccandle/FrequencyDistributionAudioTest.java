package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.FrequencyDistributionMode;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import com.soniccandle.render.BarStyle;
import com.soniccandle.render.CircularConfig;
import com.soniccandle.render.FrameRenderer;
import com.soniccandle.render.PeakMode;
import com.soniccandle.render.RenderConfig;
import com.soniccandle.render.RestingLineMode;
import com.soniccandle.render.VisualizationMode;
import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Valida las tres distribuciones con audio decodificado por FFmpeg. */
public final class FrequencyDistributionAudioTest {

    private FrequencyDistributionAudioTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            throw new IllegalArgumentException(
                    "FrequencyDistributionAudioTest <audio> [carpeta-png]");
        }
        Path audio = Path.of(args[0]);
        SpectrumData standard = analyze(audio, FrequencyDistributionMode.STANDARD);
        SpectrumData balanced = analyze(audio, FrequencyDistributionMode.BALANCED);
        SpectrumData proportional = analyze(audio, FrequencyDistributionMode.PROPORTIONAL);

        double standardPresence = rightPresence(standard);
        double balancedPresence = rightPresence(balanced);
        double proportionalPresence = rightPresence(proportional);
        if (!(balancedPresence > standardPresence * 1.25)) {
            throw new AssertionError("Equilibrado no reforzó la derecha del audio real.");
        }
        if (!(proportionalPresence > standardPresence * 1.35)) {
            throw new AssertionError("Proporcional no aprovechó el extremo derecho del audio real.");
        }
        assertSameCadence(standard, balanced);
        assertSameCadence(standard, proportional);

        if (args.length == 2) {
            Path output = Path.of(args[1]);
            Files.createDirectories(output);
            int frame = strongestFrame(standard);
            writeFrame(standard.frame(frame), output.resolve("distribucion-estandar.png"));
            writeFrame(balanced.frame(frame), output.resolve("distribucion-equilibrada.png"));
            writeFrame(proportional.frame(frame), output.resolve("distribucion-proporcional.png"));
        }
        System.out.printf("Presencia derecha real: estándar=%.4f, equilibrado=%.4f, proporcional=%.4f%n",
                standardPresence, balancedPresence, proportionalPresence);
    }

    private static SpectrumData analyze(Path audio, FrequencyDistributionMode mode)
            throws Exception {
        return new AudioAnalyzer().analyze(audio, 30, 80, MotionMode.AGILE,
                SpectrumMode.STANDARD, mode, value -> { }, () -> false);
    }

    private static double rightPresence(SpectrumData spectrum) {
        int quarter = spectrum.bandCount() / 4;
        double left = 0.0;
        double right = 0.0;
        for (int frame = 0; frame < spectrum.frameCount(); frame++) {
            float[] values = spectrum.frame(frame);
            for (int band = 0; band < quarter; band++) {
                left += values[band];
                right += values[values.length - 1 - band];
            }
        }
        return right / Math.max(0.000_001, left);
    }

    private static void assertSameCadence(SpectrumData standard, SpectrumData adjusted) {
        for (int band = 0; band < standard.bandCount(); band++) {
            float expectedGain = -1f;
            for (int frame = 0; frame < standard.frameCount(); frame++) {
                float original = standard.frame(frame)[band];
                if (original <= 0.0001f) {
                    continue;
                }
                float gain = adjusted.frame(frame)[band] / original;
                if (expectedGain < 0f) {
                    expectedGain = gain;
                } else if (Math.abs(gain - expectedGain) > 0.000_1f) {
                    throw new AssertionError("La cadencia cambió en la banda " + band);
                }
            }
        }
    }

    private static int strongestFrame(SpectrumData spectrum) {
        int strongest = 0;
        double strongestEnergy = -1.0;
        for (int frame = 0; frame < spectrum.frameCount(); frame++) {
            double energy = 0.0;
            for (float value : spectrum.frame(frame)) {
                energy += value;
            }
            if (energy > strongestEnergy) {
                strongestEnergy = energy;
                strongest = frame;
            }
        }
        return strongest;
    }

    private static void writeFrame(float[] spectrum, Path output) throws Exception {
        RenderConfig config = new RenderConfig(960, 540, 30,
                new Color(184, 128, 255), new Color(18, 24, 42), null,
                BarStyle.ROUND_FILLED, 1f, RestingLineMode.INVISIBLE,
                PeakMode.SOFT_LIMIT, VisualizationMode.LINEAR,
                CircularConfig.defaults());
        ImageIO.write(new FrameRenderer(config).render(spectrum),
                "png", output.toFile());
    }
}
