package com.soniccandle.render;

/** Define si las barras sin energía dejan una guía punteada en el centro. */
public enum RestingLineMode {
    INVISIBLE("Invisible"),
    DOTTED("Punteada");

    private final String displayName;

    RestingLineMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
