package com.soniccandle.render;

public enum IntroAnimationDirection {
    OUTSIDE_IN("De afuera hacia dentro"),
    INSIDE_OUT("De dentro hacia afuera");

    private final String displayName;

    IntroAnimationDirection(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
