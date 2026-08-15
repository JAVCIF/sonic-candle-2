package com.soniccandle.ui;

public enum AppLanguage {
    SPANISH("Español"),
    ENGLISH("English");

    private final String displayName;

    AppLanguage(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
