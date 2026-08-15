package com.soniccandle.logging;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** Registro rotativo para diagnósticos y reportes de errores. */
public final class AppLogger {

    private static final Logger LOGGER = Logger.getLogger("com.soniccandle");
    private static final int MAX_LOG_BYTES = 2 * 1024 * 1024;
    private static final int LOG_HISTORY = 5;
    private static volatile boolean initialized;
    private static volatile Path logDirectory;

    private AppLogger() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        Path workingDirectory = Path.of(System.getProperty("user.dir", "."))
                .toAbsolutePath().normalize().resolve("logs");
        try {
            initializeAt(workingDirectory);
        } catch (IOException primaryFailure) {
            Path fallback = Path.of(System.getProperty("user.home", "."))
                    .toAbsolutePath().normalize().resolve(".sonic-candle").resolve("logs");
            try {
                initializeAt(fallback);
                LOGGER.log(Level.WARNING,
                        "No se pudo usar la carpeta de logs del programa; se usa la alternativa.",
                        primaryFailure);
            } catch (IOException fallbackFailure) {
                // No se bloquea el programa si el sistema impide crear ambos destinos.
                LOGGER.setUseParentHandlers(true);
                LOGGER.log(Level.SEVERE, "No se pudo inicializar el registro de errores.",
                        fallbackFailure);
                initialized = true;
            }
        }
    }

    /** Inicialización dirigida para verificaciones automatizadas. */
    public static synchronized void initialize(Path directory) throws IOException {
        closeHandlers();
        initialized = false;
        initializeAt(directory.toAbsolutePath().normalize());
    }

    public static void info(String message) {
        ensureInitialized();
        LOGGER.info(message);
    }

    public static void warning(String message, Throwable error) {
        ensureInitialized();
        LOGGER.log(Level.WARNING, message, error);
    }

    public static void error(String message, Throwable error) {
        ensureInitialized();
        LOGGER.log(Level.SEVERE, message, error);
    }

    public static Path logDirectory() {
        ensureInitialized();
        return logDirectory;
    }

    public static Path currentLogHint() {
        Path directory = logDirectory();
        return directory == null ? null : directory.resolve("sonic-candle-0.log");
    }

    public static synchronized void flush() {
        for (Handler handler : LOGGER.getHandlers()) {
            handler.flush();
        }
    }

    public static synchronized void shutdown() {
        info("Sonic Candle finalizó.");
        closeHandlers();
        initialized = false;
    }

    private static void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }

    private static void initializeAt(Path directory) throws IOException {
        Files.createDirectories(directory);
        FileHandler handler = new FileHandler(
                directory.resolve("sonic-candle-%g.log").toString(),
                MAX_LOG_BYTES, LOG_HISTORY, true);
        handler.setEncoding(StandardCharsets.UTF_8.name());
        handler.setLevel(Level.ALL);
        handler.setFormatter(new DiagnosticFormatter());
        LOGGER.setUseParentHandlers(false);
        LOGGER.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
        logDirectory = directory;
        initialized = true;
    }

    private static void closeHandlers() {
        for (Handler handler : LOGGER.getHandlers()) {
            handler.flush();
            handler.close();
            LOGGER.removeHandler(handler);
        }
    }

    private static final class DiagnosticFormatter extends Formatter {

        private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter
                .ofPattern("uuuu-MM-dd HH:mm:ss.SSS XXX")
                .withZone(ZoneId.systemDefault());

        @Override
        public String format(LogRecord record) {
            StringBuilder text = new StringBuilder(256);
            text.append(TIMESTAMP.format(Instant.ofEpochMilli(record.getMillis())))
                    .append(" [").append(record.getLevel().getName()).append("] [")
                    .append(Thread.currentThread().getName()).append("] ")
                    .append(formatMessage(record)).append(System.lineSeparator());
            if (record.getThrown() != null) {
                StringWriter trace = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(trace));
                text.append(trace);
            }
            return text.toString();
        }
    }
}
