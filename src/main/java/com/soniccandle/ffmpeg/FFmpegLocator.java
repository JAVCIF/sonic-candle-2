package com.soniccandle.ffmpeg;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class FFmpegLocator {

    private FFmpegLocator() {
    }

    public static Path findRequired(String executable) throws IOException, InterruptedException {
        List<Path> candidates = new ArrayList<>();
        boolean windows = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
        String fileName = windows ? executable + ".exe" : executable;

        String explicit = System.getProperty("soniccandle." + executable);
        if (explicit != null && !explicit.isBlank()) {
            candidates.add(Paths.get(explicit));
        }
        String environment = System.getenv("SONIC_CANDLE_" + executable.toUpperCase(Locale.ROOT));
        if (environment != null && !environment.isBlank()) {
            candidates.add(Paths.get(environment));
        }
        candidates.add(Paths.get("tools", fileName));

        for (Path candidate : candidates) {
            Path absolute = candidate.toAbsolutePath().normalize();
            if (Files.isRegularFile(absolute) && verify(absolute)) {
                return absolute;
            }
        }

        Path fromPath = Paths.get(fileName);
        if (verify(fromPath)) {
            return fromPath;
        }
        throw new IOException("No se encontró " + fileName + ". Instálalo en el PATH o cópialo dentro de la carpeta tools del proyecto.");
    }

    public static double probeDuration(Path media) throws IOException, InterruptedException {
        Path ffprobe = findRequired("ffprobe");
        Process process = new ProcessBuilder(
                ffprobe.toString(), "-v", "error", "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1", media.toString())
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        if (!process.waitFor(15, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            return -1;
        }
        try {
            return process.exitValue() == 0 ? Double.parseDouble(output) : -1;
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private static boolean verify(Path executable) {
        try {
            Process process = new ProcessBuilder(executable.toString(), "-version")
                    .redirectErrorStream(true)
                    .start();
            process.getInputStream().transferTo(java.io.OutputStream.nullOutputStream());
            return process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0;
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
    }
}
