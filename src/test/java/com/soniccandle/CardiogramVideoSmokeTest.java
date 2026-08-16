package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import com.soniccandle.render.BarStyle;
import com.soniccandle.render.CardiogramConfig;
import com.soniccandle.render.CardiogramBeatMode;
import com.soniccandle.render.CardiogramSpeedMode;
import com.soniccandle.render.CardiogramStyle;
import com.soniccandle.render.CircularConfig;
import com.soniccandle.render.DualBarConfig;
import com.soniccandle.render.IntroAnimationConfig;
import com.soniccandle.render.LoadBarConfig;
import com.soniccandle.render.PeakMode;
import com.soniccandle.render.RenderConfig;
import com.soniccandle.render.RestingLineMode;
import com.soniccandle.render.VideoEncoder;
import com.soniccandle.render.VisualizationMode;
import java.awt.Color;
import java.nio.file.Path;

/** Exportación integral pequeña del electrocardiógrafo para FFmpeg. */
public final class CardiogramVideoSmokeTest {

    private CardiogramVideoSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "CardiogramVideoSmokeTest <audio-prueba> <salida-mp4>");
        }
        Path audio = Path.of(args[0]);
        Path output = Path.of(args[1]);
        SpectrumData spectrum = new AudioAnalyzer().analyze(audio, 30, 48,
                MotionMode.AGILE, SpectrumMode.STANDARD,
                value -> { }, () -> false);
        RenderConfig config = new RenderConfig(640, 360, 30,
                new Color(197, 105, 255), new Color(8, 10, 18), null,
                BarStyle.THIN, 1.15f, RestingLineMode.DOTTED,
                PeakMode.SOFT_LIMIT, VisualizationMode.CARDIOGRAM,
                CircularConfig.defaults(), false, DualBarConfig.defaults(),
                LoadBarConfig.defaults(), IntroAnimationConfig.disabled(),
                new CardiogramConfig(CardiogramBeatMode.ADAPTIVE_HEART_RATE,
                        CardiogramSpeedMode.SYNCHRONIZED,
                        CardiogramStyle.FLUID_HALO, false));
        new VideoEncoder().encode(audio, output, spectrum, config,
                value -> { }, () -> false);
        System.out.println("Video cardiográfico creado: " + output);
    }
}
