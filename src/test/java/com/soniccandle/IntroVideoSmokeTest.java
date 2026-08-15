package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import com.soniccandle.render.BarStyle;
import com.soniccandle.render.CircularConfig;
import com.soniccandle.render.DualBarConfig;
import com.soniccandle.render.IntroAnimationConfig;
import com.soniccandle.render.IntroAnimationDirection;
import com.soniccandle.render.IntroAnimationMode;
import com.soniccandle.render.LoadBarConfig;
import com.soniccandle.render.PeakMode;
import com.soniccandle.render.RenderConfig;
import com.soniccandle.render.RestingLineMode;
import com.soniccandle.render.VideoEncoder;
import com.soniccandle.render.VisualizationMode;
import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;

/** Exporta las dos cronologías de introducción para validarlas con FFprobe. */
public final class IntroVideoSmokeTest {

    private IntroVideoSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "IntroVideoSmokeTest <audio-prueba> <carpeta-salida>");
        }
        Path audio = Path.of(args[0]);
        Path output = Path.of(args[1]);
        Files.createDirectories(output);
        SpectrumData spectrum = new AudioAnalyzer().analyze(audio, 30, 48,
                MotionMode.AGILE, SpectrumMode.STANDARD,
                value -> { }, () -> false);
        encode(audio, output.resolve("intro-sincronizada.mp4"), spectrum,
                IntroAnimationMode.SYNCHRONIZED);
        encode(audio, output.resolve("intro-antes-del-audio.mp4"), spectrum,
                IntroAnimationMode.BEFORE_AUDIO);
        System.out.println("Videos de introducción creados correctamente en " + output);
    }

    private static void encode(Path audio, Path output, SpectrumData spectrum,
            IntroAnimationMode mode) throws Exception {
        RenderConfig config = new RenderConfig(320, 180, 30,
                new Color(255, 35, 174), Color.BLACK, null,
                BarStyle.THIN, 1f, RestingLineMode.DOTTED,
                PeakMode.SOFT_LIMIT, VisualizationMode.LINEAR,
                CircularConfig.defaults(), false, DualBarConfig.defaults(),
                LoadBarConfig.defaults(), new IntroAnimationConfig(mode,
                        IntroAnimationDirection.OUTSIDE_IN, 1_000));
        new VideoEncoder().encode(audio, output, spectrum, config,
                value -> { }, () -> false);
    }
}
