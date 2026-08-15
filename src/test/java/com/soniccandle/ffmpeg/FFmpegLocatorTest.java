package com.soniccandle.ffmpeg;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/** Verifica la ubicación de FFmpeg dentro de una aplicación jpackage. */
public final class FFmpegLocatorTest {

    private FFmpegLocatorTest() {
    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
            System.out.println("Prueba de ruta jpackage omitida en Windows.");
            return;
        }
        Path packageRoot = Files.createTempDirectory("sonic-candle-jpackage-");
        Path launcher = packageRoot.resolve("Sonic Candle");
        Path executable = packageRoot.resolve("app/tools/ffmpeg");
        Files.createDirectories(executable.getParent());
        Files.writeString(executable, "#!/bin/sh\nexit 0\n");
        Files.setPosixFilePermissions(executable, Set.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE));

        String previous = System.getProperty("jpackage.app-path");
        try {
            System.setProperty("jpackage.app-path", launcher.toString());
            Path located = FFmpegLocator.findRequired("ffmpeg");
            assertTrue(located.equals(executable.toAbsolutePath().normalize()),
                    "No se encontró FFmpeg dentro de app/tools.");
        } finally {
            if (previous == null) {
                System.clearProperty("jpackage.app-path");
            } else {
                System.setProperty("jpackage.app-path", previous);
            }
        }
        System.out.println("Ruta FFmpeg de jpackage detectada correctamente.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
