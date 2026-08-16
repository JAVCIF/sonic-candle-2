package com.soniccandle.ffmpeg;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/** Detecta pistas y duración sin decodificar el archivo completo. */
public final class MediaProbe {

    private MediaProbe() {
    }

    public static MediaInfo probe(Path media) throws IOException, InterruptedException {
        Path ffprobe = FFmpegLocator.findRequired("ffprobe");
        Process process = new ProcessBuilder(command(ffprobe, media))
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
        if (!process.waitFor(15, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new IOException("FFprobe tardó demasiado en inspeccionar el archivo.");
        }
        if (process.exitValue() != 0) {
            throw new IOException("FFprobe no pudo reconocer el archivo multimedia.\n"
                    + output.trim());
        }
        boolean audio = false;
        boolean video = false;
        double duration = -1.0;
        for (String rawLine : output.split("\\R")) {
            String line = rawLine.trim();
            if (line.equalsIgnoreCase("codec_type=audio")) {
                audio = true;
            } else if (line.equalsIgnoreCase("codec_type=video")) {
                video = true;
            } else if (line.toLowerCase(Locale.ROOT).startsWith("duration=")) {
                try {
                    duration = Double.parseDouble(line.substring("duration=".length()));
                } catch (NumberFormatException ignored) {
                    duration = -1.0;
                }
            }
        }
        return new MediaInfo(audio, video, duration);
    }

    static List<String> command(Path ffprobe, Path media) {
        return List.of(ffprobe.toString(), "-v", "error",
                "-show_entries", "stream=codec_type:format=duration",
                "-of", "default=noprint_wrappers=1", media.toString());
    }
}
