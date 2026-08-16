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
        return parseOutput(output);
    }

    static MediaInfo parseOutput(String output) {
        boolean audio = false;
        boolean video = false;
        double duration = -1.0;
        boolean insideStream = false;
        String streamType = null;
        boolean attachedPicture = false;
        for (String rawLine : output.split("\\R")) {
            String line = rawLine.trim();
            if (line.equalsIgnoreCase("[STREAM]")) {
                insideStream = true;
                streamType = null;
                attachedPicture = false;
            } else if (line.equalsIgnoreCase("[/STREAM]")) {
                if ("audio".equalsIgnoreCase(streamType)) {
                    audio = true;
                } else if ("video".equalsIgnoreCase(streamType)
                        && !attachedPicture) {
                    video = true;
                }
                insideStream = false;
            } else if (insideStream && line.toLowerCase(Locale.ROOT)
                    .startsWith("codec_type=")) {
                streamType = line.substring("codec_type=".length());
            } else if (insideStream && (line.equalsIgnoreCase("attached_pic=1")
                    || line.equalsIgnoreCase("DISPOSITION:attached_pic=1"))) {
                attachedPicture = true;
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
                "-show_entries",
                "stream=codec_type:stream_disposition=attached_pic:format=duration",
                "-of", "default=noprint_wrappers=0", media.toString());
    }
}
