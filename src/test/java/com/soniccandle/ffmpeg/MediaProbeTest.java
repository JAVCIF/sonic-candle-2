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
        assertTrue(command.contains(
                "stream=codec_type:stream_disposition=attached_pic:format=duration"),
                "FFprobe no solicita pistas y duración.");
        attachedCoverIsNotVideo();
        actualVideoRemainsVideo();
        if (args.length > 0) {
            MediaInfo info = MediaProbe.probe(Path.of(args[0]));
            System.out.println("audio=" + info.hasAudio() + ", video="
                    + info.hasVideo() + ", duration=" + info.durationSeconds());
        } else {
            System.out.println("Comando de inspección multimedia correcto.");
        }
    }

    private static void attachedCoverIsNotVideo() {
        MediaInfo info = MediaProbe.parseOutput("""
                [STREAM]
                codec_type=audio
                DISPOSITION:attached_pic=0
                [/STREAM]
                [STREAM]
                codec_type=video
                DISPOSITION:attached_pic=1
                [/STREAM]
                [FORMAT]
                duration=123.5
                [/FORMAT]
                """);
        assertTrue(info.hasAudio(), "El MP3 con carátula perdió su audio.");
        assertTrue(!info.hasVideo(), "La carátula fue confundida con video real.");
        assertTrue(info.durationSeconds() == 123.5, "Se perdió la duración.");
    }

    private static void actualVideoRemainsVideo() {
        MediaInfo info = MediaProbe.parseOutput("""
                [STREAM]
                codec_type=video
                DISPOSITION:attached_pic=0
                [/STREAM]
                [STREAM]
                codec_type=audio
                DISPOSITION:attached_pic=0
                [/STREAM]
                [FORMAT]
                duration=8.0
                [/FORMAT]
                """);
        assertTrue(info.hasVideo() && info.hasAudio(),
                "Un video real con audio dejó de reconocerse.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
