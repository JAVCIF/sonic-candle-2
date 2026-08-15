package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import com.soniccandle.render.BarStyle;
import com.soniccandle.render.CircularConfig;
import com.soniccandle.render.ExportFormat;
import com.soniccandle.render.PeakMode;
import com.soniccandle.render.RenderConfig;
import com.soniccandle.render.RestingLineMode;
import com.soniccandle.render.VideoEncoder;
import com.soniccandle.render.VisualizationMode;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

/** Exporta videos alfa reales para validarlos con FFmpeg/FFprobe. */
public final class TransparentVideoSmokeTest {

    private TransparentVideoSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "TransparentVideoSmokeTest <audio-prueba> <carpeta-salida>");
        }
        Path audio = Path.of(args[0]);
        Path output = Path.of(args[1]);
        Files.createDirectories(output);
        SpectrumData spectrum = new AudioAnalyzer().analyze(audio, 30, 48,
                MotionMode.AGILE, SpectrumMode.STANDARD,
                value -> { }, () -> false);
        BufferedImage background = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = background.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, 40, 40);
        graphics.dispose();
        RenderConfig config = new RenderConfig(320, 180, 30,
                new Color(179, 127, 255), Color.RED, background,
                BarStyle.ROUND_FILLED, 1f, RestingLineMode.DOTTED,
                PeakMode.SOFT_LIMIT, VisualizationMode.LINEAR,
                CircularConfig.defaults());
        VideoEncoder encoder = new VideoEncoder();
        encoder.encode(audio, output.resolve("transparente-prores.mov"),
                spectrum, config, ExportFormat.PRORES_4444,
                value -> { }, () -> false);
        encoder.encode(audio, output.resolve("transparente-vp9.webm"),
                spectrum, config, ExportFormat.WEBM_VP9,
                value -> { }, () -> false);
        System.out.println("Videos alfa creados correctamente en " + output);
    }
}
