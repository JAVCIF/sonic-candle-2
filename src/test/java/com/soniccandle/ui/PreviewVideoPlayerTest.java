package com.soniccandle.ui;

import com.soniccandle.render.BackgroundFitMode;
import com.soniccandle.render.VideoEndMode;
import java.awt.Color;
import java.nio.file.Path;
import java.util.List;

/** Comprueba seek, streaming y ajuste espacial del fondo de video. */
public final class PreviewVideoPlayerTest {

    private PreviewVideoPlayerTest() {
    }

    public static void main(String[] args) {
        List<String> frame = PreviewVideoPlayer.command(Path.of("ffmpeg"),
                Path.of("background.mp4"), 12.5, 640, 360, 30,
                BackgroundFitMode.COVER, Color.BLACK,
                VideoEndMode.FREEZE, true);
        assertTrue(frame.contains("12.500000") && frame.contains("-frames:v"),
                "La captura estática no respeta el seek.");
        List<String> playback = PreviewVideoPlayer.command(Path.of("ffmpeg"),
                Path.of("background.mp4"), 4.0, 640, 360, 30,
                BackgroundFitMode.CONTAIN, new Color(12, 34, 56),
                VideoEndMode.LOOP, false);
        assertTrue(playback.contains("-re") && playback.contains("-stream_loop")
                && playback.stream().anyMatch(value -> value.contains("pad=640:360")),
                "La reproducción no transmite o repite correctamente.");
        System.out.println("Vista previa de video correcta: seek, ajuste y streaming.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
