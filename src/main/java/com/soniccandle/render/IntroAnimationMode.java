package com.soniccandle.render;

public enum IntroAnimationMode {
    DISABLED("Sin animación"),
    SYNCHRONIZED("Junto con la canción"),
    BEFORE_AUDIO("Antes de la canción");

    private final String displayName;

    IntroAnimationMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
