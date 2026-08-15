package com.soniccandle.logging;

import java.nio.file.Files;
import java.nio.file.Path;

/** Verifica creación, mensaje y traza completa del registro rotativo. */
public final class AppLoggerTest {

    private AppLoggerTest() {
    }

    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("sonic-candle-log-test-");
        String marker = "diagnostic-marker-alpha17";
        AppLogger.initialize(directory);
        AppLogger.info(marker);
        AppLogger.error("fallo simulado", new IllegalStateException("trace-marker"));
        AppLogger.flush();

        Path log;
        try (var files = Files.list(directory)) {
            log = files.filter(path -> path.getFileName().toString().endsWith(".log"))
                    .findFirst().orElseThrow();
        }
        String contents = Files.readString(log);
        assertTrue(contents.contains(marker), "No se escribió el mensaje de diagnóstico.");
        assertTrue(contents.contains("IllegalStateException: trace-marker"),
                "No se guardó la traza completa.");
        AppLogger.shutdown();
        System.out.println("Registro de errores correcto: " + log);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
