package com.soniccandle.render;

import java.awt.Color;

public final class LoadBarConfig {

    private final int count;
    private final LoadBarOrientation orientation;
    private final HorizontalPlacement horizontalPlacement;
    private final VerticalPlacement verticalPlacement;
    private final boolean reversed;
    private final LoadBarShape shape;
    private final Color borderColor;
    private final LoadBarFillStyle fillStyle;
    private final LoadBarBorderStyle borderStyle;
    private final LoadBarResponseMode responseMode;
    private final LoadBarAnimationMode animationMode;

    public LoadBarConfig(int count, LoadBarOrientation orientation,
            HorizontalPlacement horizontalPlacement, VerticalPlacement verticalPlacement,
            boolean reversed, LoadBarShape shape, Color borderColor) {
        this(count, orientation, horizontalPlacement, verticalPlacement, reversed,
                shape, borderColor, LoadBarFillStyle.DEFAULT,
                LoadBarBorderStyle.DEFAULT, LoadBarResponseMode.NORMAL,
                LoadBarAnimationMode.NORMAL);
    }

    public LoadBarConfig(int count, LoadBarOrientation orientation,
            HorizontalPlacement horizontalPlacement, VerticalPlacement verticalPlacement,
            boolean reversed, LoadBarShape shape, Color borderColor,
            LoadBarFillStyle fillStyle, LoadBarBorderStyle borderStyle,
            LoadBarResponseMode responseMode, LoadBarAnimationMode animationMode) {
        this.count = Math.max(1, Math.min(2, count));
        this.orientation = orientation == null
                ? LoadBarOrientation.HORIZONTAL : orientation;
        this.horizontalPlacement = horizontalPlacement == null
                ? HorizontalPlacement.CENTER : horizontalPlacement;
        this.verticalPlacement = verticalPlacement == null
                ? VerticalPlacement.LEFT : verticalPlacement;
        this.reversed = reversed;
        this.shape = shape == null ? LoadBarShape.ROUNDED : shape;
        this.borderColor = borderColor == null ? Color.WHITE : borderColor;
        this.fillStyle = fillStyle == null ? LoadBarFillStyle.DEFAULT : fillStyle;
        this.borderStyle = borderStyle == null ? LoadBarBorderStyle.DEFAULT : borderStyle;
        this.responseMode = responseMode == null ? LoadBarResponseMode.NORMAL : responseMode;
        this.animationMode = animationMode == null ? LoadBarAnimationMode.NORMAL : animationMode;
    }

    public static LoadBarConfig defaults() {
        return new LoadBarConfig(1, LoadBarOrientation.HORIZONTAL,
                HorizontalPlacement.CENTER, VerticalPlacement.LEFT,
                false, LoadBarShape.ROUNDED, Color.WHITE);
    }

    public int count() { return count; }
    public LoadBarOrientation orientation() { return orientation; }
    public HorizontalPlacement horizontalPlacement() { return horizontalPlacement; }
    public VerticalPlacement verticalPlacement() { return verticalPlacement; }
    public boolean reversed() { return reversed; }
    public LoadBarShape shape() { return shape; }
    public Color borderColor() { return borderColor; }
    public LoadBarFillStyle fillStyle() { return fillStyle; }
    public LoadBarBorderStyle borderStyle() { return borderStyle; }
    public LoadBarResponseMode responseMode() { return responseMode; }
    public LoadBarAnimationMode animationMode() { return animationMode; }
}
