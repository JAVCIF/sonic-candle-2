package com.soniccandle.render;

public enum LoadBarResponseMode {
    NORMAL("Normal"),
    HIGH("Alta"),
    PROPORTIONAL("Proporcional");

    private final String displayName;

    LoadBarResponseMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() { return displayName; }
}
