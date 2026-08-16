package com.soniccandle;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import com.soniccandle.render.BackgroundFitMode;
import com.soniccandle.render.BarStyle;
import com.soniccandle.render.CircularConfig;
import com.soniccandle.render.DualBarConfig;
import com.soniccandle.render.ExportFormat;
import com.soniccandle.render.IntroAnimationConfig;
import com.soniccandle.render.LoadBarConfig;
import com.soniccandle.render.PeakMode;
import com.soniccandle.render.RenderConfig;
import com.soniccandle.render.RestingLineMode;
import com.soniccandle.render.VideoEncoder;
import com.soniccandle.render.VideoEndMode;
import com.soniccandle.render.VisualizationMode;
import java.awt.Color;
import java.nio.file.Path;

/** Exportación integral pequeña para comprobar un fondo de video real. */
public final class VideoBackgroundSmokeTest {

    private VideoBackgroundSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3 || args.length > 4) {
            throw new IllegalArgumentException(
                    "VideoBackgroundSmokeTest <audio-o-video-con-audio> <video-fondo> <salida-mp4> [loop|freeze]");
        }
        Path audio = Path.of(args[0]);
        Path background = Path.of(args[1]);
        Path output = Path.of(args[2]);
        VideoEndMode endMode = args.length == 4
                && args[3].equalsIgnoreCase("freeze")
                ? VideoEndMode.FREEZE : VideoEndMode.LOOP;
        SpectrumData spectrum = new AudioAnalyzer().analyze(audio, 30, 32,
                MotionMode.AGILE, SpectrumMode.STANDARD,
                value -> { }, () -> false);
        RenderConfig config = new RenderConfig(320, 180, 30,
                new Color(190, 90, 255), Color.BLACK, null, background,
                BackgroundFitMode.COVER, endMode,
                BarStyle.ROUND_FILLED, 1f, RestingLineMode.DOTTED,
                PeakMode.SOFT_LIMIT, VisualizationMode.LINEAR,
                CircularConfig.defaults(), false, DualBarConfig.defaults(),
                LoadBarConfig.defaults(), IntroAnimationConfig.disabled());
        new VideoEncoder().encode(audio, output, spectrum, config,
                ExportFormat.MP4, value -> { }, () -> false);
        System.out.println("MP4 con fondo de video creado: " + output);
    }
}
