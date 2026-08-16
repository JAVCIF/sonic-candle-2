package com.soniccandle.render;

import com.soniccandle.analysis.SpectrumData;
import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;

/** Verifica comandos de video y secuencias PNG seguras. */
public final class ExportFormatTest {

    private ExportFormatTest() {
    }

    public static void main(String[] args) throws Exception {
        RenderConfig config = new RenderConfig(160, 90, 30,
                new Color(180, 90, 255), Color.BLACK, null,
                BarStyle.ROUND_FILLED, 1f, RestingLineMode.DOTTED,
                PeakMode.SOFT_LIMIT, VisualizationMode.LINEAR,
                CircularConfig.defaults());
        Path ffmpeg = Path.of("ffmpeg");
        Path audio = Path.of("song.wav");

        List<String> mp4 = VideoEncoder.command(ffmpeg, audio,
                Path.of("out.mp4"), config, ExportFormat.MP4);
        assertContains(mp4, "bgr24", "libx264", "yuv420p");
        List<String> prores = VideoEncoder.command(ffmpeg, audio,
                Path.of("out.mov"), config, ExportFormat.PRORES_4444);
        assertContains(prores, "abgr", "prores_ks", "yuva444p10le", "pcm_s16le");
        List<String> webm = VideoEncoder.command(ffmpeg, audio,
                Path.of("out.webm"), config, ExportFormat.WEBM_VP9);
        assertContains(webm, "abgr", "libvpx-vp9", "yuva420p", "libopus");

        RenderConfig videoBackground = new RenderConfig(160, 90, 30,
                new Color(180, 90, 255), Color.BLACK, null,
                Path.of("background.mp4"), BackgroundFitMode.COVER,
                VideoEndMode.LOOP, BarStyle.ROUND_FILLED, 1f,
                RestingLineMode.DOTTED, PeakMode.SOFT_LIMIT,
                VisualizationMode.LINEAR, CircularConfig.defaults(), false,
                DualBarConfig.defaults(), LoadBarConfig.defaults(),
                IntroAnimationConfig.disabled());
        List<String> videoMp4 = VideoEncoder.command(ffmpeg, audio,
                Path.of("video-background.mp4"), videoBackground, ExportFormat.MP4);
        assertContains(videoMp4, "abgr", "-stream_loop", "background.mp4",
                "-filter_complex", "[composed]", "2:a:0");
        assertTrue(videoMp4.stream().anyMatch(value -> value.contains("overlay=0:0")),
                "El MP4 no compone el visualizador sobre el fondo de video.");
        List<String> transparentWithVideo = VideoEncoder.command(ffmpeg, audio,
                Path.of("transparent.mov"), videoBackground, ExportFormat.PRORES_4444);
        assertTrue(!transparentWithVideo.contains("background.mp4")
                && transparentWithVideo.contains("1:a:0"),
                "La exportación transparente no ignoró el fondo global de video.");

        float[][] frames = new float[3][16];
        frames[0][3] = 0.8f;
        frames[1][5] = 1.1f;
        SpectrumData spectrum = new SpectrumData(frames, 30, 44_100);
        Path directory = Files.createTempDirectory("sonic-candle-png-");
        new VideoEncoder().encode(null, directory, spectrum, config,
                ExportFormat.PNG_SEQUENCE, value -> { }, () -> false);
        Path first = directory.resolve("sonic-candle_000001.png");
        Path third = directory.resolve("sonic-candle_000003.png");
        assertTrue(Files.isRegularFile(first) && Files.isRegularFile(third),
                "La secuencia PNG no creó todos los fotogramas.");
        assertTrue(ImageIO.read(first.toFile()).getColorModel().hasAlpha(),
                "Los PNG no contienen canal alfa.");

        byte[] original = Files.readAllBytes(first);
        boolean rejected = false;
        try {
            new VideoEncoder().encode(null, directory, spectrum, config,
                    ExportFormat.PNG_SEQUENCE, value -> { }, () -> false);
        } catch (IOException expected) {
            rejected = true;
        }
        assertTrue(rejected && java.util.Arrays.equals(original, Files.readAllBytes(first)),
                "La exportación PNG sobrescribió una secuencia existente.");
        System.out.println("Formatos correctos: MP4 con fondo estático/video, ProRes 4444, VP9 y PNG seguro.");
    }

    private static void assertContains(List<String> command, String... values) {
        for (String value : values) {
            assertTrue(command.contains(value), "Falta en el comando: " + value);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
