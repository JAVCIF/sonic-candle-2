package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
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
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Confirma que el intercalado clásico produce una distribución distinta y válida. */
public final class InterleaveModeTest {

    private InterleaveModeTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1 || args.length > 2) {
            throw new IllegalArgumentException("InterleaveModeTest <audio-prueba.wav> [carpeta-png]");
        }
        Path audio = Path.of(args[0]);
        SpectrumData standard = analyze(audio, SpectrumMode.STANDARD);
        SpectrumData interleaved = analyze(audio, SpectrumMode.CLASSIC_INTERLEAVED);

        float difference = 0f;
        float[] standardFrame = standard.frame(Math.min(30, standard.frameCount() - 1));
        float[] interleavedFrame = interleaved.frame(Math.min(30, interleaved.frameCount() - 1));
        for (int index = 0; index < standardFrame.length; index++) {
            difference += Math.abs(standardFrame[index] - interleavedFrame[index]);
        }
        if (!(difference > 0.01f)) {
            throw new AssertionError("El intercalado no modificó el espectro: " + difference);
        }
        if (args.length == 2) {
            Path outputDirectory = Path.of(args[1]);
            java.nio.file.Files.createDirectories(outputDirectory);
            writeFrame(standardFrame, outputDirectory.resolve("espectro-estandar.png"));
            writeFrame(interleavedFrame, outputDirectory.resolve("intercalado-clasico.png"));
        }
        System.out.printf("Intercalado clásico activo; diferencia acumulada=%.4f%n", difference);
    }

    private static SpectrumData analyze(Path audio, SpectrumMode mode) throws Exception {
        return new AudioAnalyzer().analyze(
                audio, 30, 80, MotionMode.AGILE, mode, value -> { }, () -> false);
    }

    private static void writeFrame(float[] frame, Path output) throws Exception {
        RenderConfig config = new RenderConfig(
                640, 360, 30, new Color(255, 30, 175), Color.BLACK, null,
                BarStyle.ROUND_FILLED, 1.0f,
                RestingLineMode.INVISIBLE, PeakMode.SOFT_LIMIT,
                VisualizationMode.LINEAR, CircularConfig.defaults());
        ImageIO.write(new FrameRenderer(config).render(frame), "png", output.toFile());
    }
}
