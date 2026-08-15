package com.soniccandle.render;

import java.awt.Color;
import java.awt.image.BufferedImage;

/** Configuración del compositor radial. */
public final class CircularConfig {

    public static final int MIN_SIZE_PERCENT = 20;
    public static final int MAX_SIZE_PERCENT = 75;

    private final int circleCount;
    private final CircleAlignment alignment;
    private final int sizePercent;
    private final int rotationDegrees;
    private final CircleFillMode fillMode;
    private final Color fillColor;
    private final BufferedImage image;
    private final int imageZoomPercent;
    private final int imageOffsetXPercent;
    private final int imageOffsetYPercent;

    public CircularConfig(int circleCount, CircleAlignment alignment, int sizePercent,
            int rotationDegrees, CircleFillMode fillMode, Color fillColor,
            BufferedImage image, int imageZoomPercent,
            int imageOffsetXPercent, int imageOffsetYPercent) {
        this.circleCount = Math.max(1, Math.min(2, circleCount));
        this.alignment = alignment == null ? CircleAlignment.CENTER : alignment;
        this.sizePercent = Math.max(MIN_SIZE_PERCENT, Math.min(MAX_SIZE_PERCENT, sizePercent));
        this.rotationDegrees = Math.floorMod(rotationDegrees, 360);
        this.fillMode = fillMode == null ? CircleFillMode.TRANSPARENT : fillMode;
        this.fillColor = fillColor == null ? new Color(28, 18, 51) : fillColor;
        this.image = image;
        this.imageZoomPercent = Math.max(100, Math.min(300, imageZoomPercent));
        this.imageOffsetXPercent = Math.max(-100, Math.min(100, imageOffsetXPercent));
        this.imageOffsetYPercent = Math.max(-100, Math.min(100, imageOffsetYPercent));
    }

    public static CircularConfig defaults() {
        return new CircularConfig(1, CircleAlignment.CENTER, 62, 0,
                CircleFillMode.TRANSPARENT, new Color(28, 18, 51),
                null, 100, 0, 0);
    }

    public int circleCount() { return circleCount; }
    public CircleAlignment alignment() { return alignment; }
    public int sizePercent() { return sizePercent; }
    public int rotationDegrees() { return rotationDegrees; }
    public CircleFillMode fillMode() { return fillMode; }
    public Color fillColor() { return fillColor; }
    public BufferedImage image() { return image; }
    public int imageZoomPercent() { return imageZoomPercent; }
    public int imageOffsetXPercent() { return imageOffsetXPercent; }
    public int imageOffsetYPercent() { return imageOffsetYPercent; }
}
