package com.soniccandle.render;

/** Destinos de exportación disponibles y sus reglas de composición. */
public enum ExportFormat {
    MP4("MP4 — Video completo", "mp4", false, false),
    PRORES_4444("ProRes 4444 — Transparencia máxima", "mov", true, false),
    WEBM_VP9("WebM VP9 — Transparencia moderada", "webm", true, false),
    PNG_SEQUENCE("PNG — Secuencia transparente", "", true, true);

    private final String displayName;
    private final String extension;
    private final boolean transparentCanvas;
    private final boolean directoryOutput;

    ExportFormat(String displayName, String extension,
            boolean transparentCanvas, boolean directoryOutput) {
        this.displayName = displayName;
        this.extension = extension;
        this.transparentCanvas = transparentCanvas;
        this.directoryOutput = directoryOutput;
    }

    public String extension() {
        return extension;
    }

    public boolean transparentCanvas() {
        return transparentCanvas;
    }

    public boolean directoryOutput() {
        return directoryOutput;
    }

    public String suggestedSuffix() {
        return switch (this) {
            case MP4 -> "_sonic-candle.mp4";
            case PRORES_4444 -> "_sonic-candle-alpha.mov";
            case WEBM_VP9 -> "_sonic-candle-alpha.webm";
            case PNG_SEQUENCE -> "_sonic-candle-png";
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}
