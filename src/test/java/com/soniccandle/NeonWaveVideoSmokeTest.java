package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import com.soniccandle.render.BarStyle;
import com.soniccandle.render.CardiogramConfig;
import com.soniccandle.render.CircularConfig;
import com.soniccandle.render.DualBarConfig;
import com.soniccandle.render.IntroAnimationConfig;
import com.soniccandle.render.LoadBarConfig;
import com.soniccandle.render.NeonWaveConfig;
import com.soniccandle.render.NeonWaveLineStyle;
import com.soniccandle.render.NeonWaveParticleMode;
import com.soniccandle.render.NeonWavePlacement;
import com.soniccandle.render.PeakMode;
import com.soniccandle.render.RenderConfig;
import com.soniccandle.render.RestingLineMode;
import com.soniccandle.render.VideoEncoder;
import com.soniccandle.render.VisualizationMode;
import java.awt.Color;
import java.nio.file.Path;

/** Exportación integral pequeña de Neon Wave para FFmpeg. */
public final class NeonWaveVideoSmokeTest {

    private NeonWaveVideoSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "NeonWaveVideoSmokeTest <audio-prueba> <salida-mp4>");
        }
        Path audio = Path.of(args[0]);
        Path output = Path.of(args[1]);
        SpectrumData spectrum = new AudioAnalyzer().analyze(audio, 30, 64,
                MotionMode.AGILE, SpectrumMode.STANDARD,
                value -> { }, () -> false);
        NeonWaveConfig neon = new NeonWaveConfig(12,
                NeonWaveLineStyle.ANGULAR, 7, 2, 48, 120,
                NeonWavePlacement.CENTER, false,
                NeonWaveParticleMode.SUBTLE);
        RenderConfig config = new RenderConfig(640, 360, 30,
                new Color(255, 91, 18), new Color(7, 8, 14), null, null,
                com.soniccandle.render.BackgroundFitMode.COVER,
                com.soniccandle.render.VideoEndMode.LOOP,
                BarStyle.THIN, 1.15f, RestingLineMode.INVISIBLE,
                PeakMode.SOFT_LIMIT, VisualizationMode.NEON_WAVE,
                CircularConfig.defaults(), false, DualBarConfig.defaults(),
                LoadBarConfig.defaults(), IntroAnimationConfig.disabled(),
                CardiogramConfig.defaults(), neon);
        new VideoEncoder().encode(audio, output, spectrum, config,
                value -> { }, () -> false);
        System.out.println("Video Neon Wave creado: " + output);
    }
}
