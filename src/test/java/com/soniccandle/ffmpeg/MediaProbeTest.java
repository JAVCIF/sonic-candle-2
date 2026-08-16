package com.soniccandle.ffmpeg;

import java.nio.file.Path;
import java.util.List;

/** Comprueba la inspección de pistas y permite validar archivos reales. */
public final class MediaProbeTest {

    private MediaProbeTest() {
    }

    public static void main(String[] args) throws Exception {
        List<String> command = MediaProbe.command(Path.of("ffprobe"),
                Path.of("media.mp4"));
        assertTrue(command.contains("stream=codec_type:format=duration"),
                "FFprobe no solicita pistas y duración.");
        if (args.length > 0) {
            MediaInfo info = MediaProbe.probe(Path.of(args[0]));
            System.out.println("audio=" + info.hasAudio() + ", video="
                    + info.hasVideo() + ", duration=" + info.durationSeconds());
        } else {
            System.out.println("Comando de inspección multimedia correcto.");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
