package com.soniccandle.analysis;

/** Selecciona cómo se recorren las salidas real e imaginaria de la FFT. */
public enum SpectrumMode {
    STANDARD("Estándar"),
    CLASSIC_INTERLEAVED("Intercalado clásico");

    private final String displayName;

    SpectrumMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
