package com.soniccandle.ui;

import java.nio.file.Path;
import java.util.List;

/** Comprueba que la previsualización solicita PCM estéreo y respeta el seek. */
public final class PreviewAudioPlayerTest {

    private PreviewAudioPlayerTest() {
    }

    public static void main(String[] args) {
        List<String> command = PreviewAudioPlayer.command(Path.of("ffmpeg"),
                Path.of("song.mp3"), 12.3456789);
        assertTrue(command.contains("-ss") && command.contains("12.345679"),
                "El comando no conserva el instante del slider.");
        assertTrue(command.contains("s16le") && command.contains("44100")
                && command.contains("2"),
                "El comando no produce PCM compatible con Java Sound.");
        assertTrue(command.get(command.size() - 1).equals("pipe:1"),
                "El audio no se transmite por tubería.");
        System.out.println("Audio de vista previa correcto: seek y PCM multiformato.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
