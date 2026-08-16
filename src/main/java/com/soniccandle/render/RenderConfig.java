package com.soniccandle.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

public final class RenderConfig {

    private final int width;
    private final int height;
    private final int framesPerSecond;
    private final Color barColor;
    private final Color backgroundColor;
    private final BufferedImage backgroundImage;
    private final Path backgroundVideo;
    private final BackgroundFitMode backgroundFitMode;
    private final VideoEndMode videoEndMode;
    private final BarStyle barStyle;
    private final float sensitivity;
    private final RestingLineMode restingLineMode;
    private final PeakMode peakMode;
    private final VisualizationMode visualizationMode;
    private final CircularConfig circularConfig;
    private final boolean reverseLinearSpectrum;
    private final DualBarConfig dualBarConfig;
    private final LoadBarConfig loadBarConfig;
    private final IntroAnimationConfig introAnimationConfig;
    private final CardiogramConfig cardiogramConfig;
    private final NeonWaveConfig neonWaveConfig;

    public RenderConfig(int width, int height, int framesPerSecond, Color barColor,
            Color backgroundColor, BufferedImage backgroundImage, BarStyle barStyle,
            float sensitivity, RestingLineMode restingLineMode, PeakMode peakMode,
            VisualizationMode visualizationMode, CircularConfig circularConfig) {
        this(width, height, framesPerSecond, barColor, backgroundColor, backgroundImage,
                barStyle, sensitivity, restingLineMode, peakMode, visualizationMode,
                circularConfig, false, DualBarConfig.defaults(),
                LoadBarConfig.defaults(), IntroAnimationConfig.disabled());
    }

    public RenderConfig(int width, int height, int framesPerSecond, Color barColor,
            Color backgroundColor, BufferedImage backgroundImage, BarStyle barStyle,
            float sensitivity, RestingLineMode restingLineMode, PeakMode peakMode,
            VisualizationMode visualizationMode, CircularConfig circularConfig,
            boolean reverseLinearSpectrum, DualBarConfig dualBarConfig) {
        this(width, height, framesPerSecond, barColor, backgroundColor, backgroundImage,
                barStyle, sensitivity, restingLineMode, peakMode, visualizationMode,
                circularConfig, reverseLinearSpectrum, dualBarConfig,
                LoadBarConfig.defaults(), IntroAnimationConfig.disabled());
    }

    public RenderConfig(int width, int height, int framesPerSecond, Color barColor,
            Color backgroundColor, BufferedImage backgroundImage, BarStyle barStyle,
            float sensitivity, RestingLineMode restingLineMode, PeakMode peakMode,
            VisualizationMode visualizationMode, CircularConfig circularConfig,
            boolean reverseLinearSpectrum, DualBarConfig dualBarConfig,
            LoadBarConfig loadBarConfig, IntroAnimationConfig introAnimationConfig) {
        this(width, height, framesPerSecond, barColor, backgroundColor,
                backgroundImage, null, BackgroundFitMode.COVER, VideoEndMode.LOOP,
                barStyle, sensitivity, restingLineMode, peakMode,
                visualizationMode, circularConfig, reverseLinearSpectrum,
                dualBarConfig, loadBarConfig, introAnimationConfig,
                CardiogramConfig.defaults());
    }

    public RenderConfig(int width, int height, int framesPerSecond, Color barColor,
            Color backgroundColor, BufferedImage backgroundImage, BarStyle barStyle,
            float sensitivity, RestingLineMode restingLineMode, PeakMode peakMode,
            VisualizationMode visualizationMode, CircularConfig circularConfig,
            boolean reverseLinearSpectrum, DualBarConfig dualBarConfig,
            LoadBarConfig loadBarConfig, IntroAnimationConfig introAnimationConfig,
            CardiogramConfig cardiogramConfig) {
        this(width, height, framesPerSecond, barColor, backgroundColor,
                backgroundImage, null, BackgroundFitMode.COVER, VideoEndMode.LOOP,
                barStyle, sensitivity, restingLineMode, peakMode,
                visualizationMode, circularConfig, reverseLinearSpectrum,
                dualBarConfig, loadBarConfig, introAnimationConfig,
                cardiogramConfig);
    }

    public RenderConfig(int width, int height, int framesPerSecond, Color barColor,
            Color backgroundColor, BufferedImage backgroundImage, Path backgroundVideo,
            BackgroundFitMode backgroundFitMode, VideoEndMode videoEndMode,
            BarStyle barStyle, float sensitivity, RestingLineMode restingLineMode,
            PeakMode peakMode, VisualizationMode visualizationMode,
            CircularConfig circularConfig, boolean reverseLinearSpectrum,
            DualBarConfig dualBarConfig, LoadBarConfig loadBarConfig,
            IntroAnimationConfig introAnimationConfig,
            CardiogramConfig cardiogramConfig) {
        this(width, height, framesPerSecond, barColor, backgroundColor,
                backgroundImage, backgroundVideo, backgroundFitMode, videoEndMode,
                barStyle, sensitivity, restingLineMode, peakMode,
                visualizationMode, circularConfig, reverseLinearSpectrum,
                dualBarConfig, loadBarConfig, introAnimationConfig,
                cardiogramConfig, NeonWaveConfig.defaults());
    }

    public RenderConfig(int width, int height, int framesPerSecond, Color barColor,
            Color backgroundColor, BufferedImage backgroundImage, Path backgroundVideo,
            BackgroundFitMode backgroundFitMode, VideoEndMode videoEndMode,
            BarStyle barStyle, float sensitivity, RestingLineMode restingLineMode,
            PeakMode peakMode, VisualizationMode visualizationMode,
            CircularConfig circularConfig, boolean reverseLinearSpectrum,
            DualBarConfig dualBarConfig, LoadBarConfig loadBarConfig,
            IntroAnimationConfig introAnimationConfig,
            CardiogramConfig cardiogramConfig, NeonWaveConfig neonWaveConfig) {
        this.width = width;
        this.height = height;
        this.framesPerSecond = framesPerSecond;
        this.barColor = barColor;
        this.backgroundColor = backgroundColor;
        this.backgroundImage = backgroundImage;
        this.backgroundVideo = backgroundVideo;
        this.backgroundFitMode = backgroundFitMode == null
                ? BackgroundFitMode.COVER : backgroundFitMode;
        this.videoEndMode = videoEndMode == null ? VideoEndMode.LOOP : videoEndMode;
        this.barStyle = barStyle;
        this.sensitivity = sensitivity;
        this.restingLineMode = restingLineMode;
        this.peakMode = peakMode;
        this.visualizationMode = visualizationMode == null
                ? VisualizationMode.LINEAR : visualizationMode;
        this.circularConfig = circularConfig == null
                ? CircularConfig.defaults() : circularConfig;
        this.reverseLinearSpectrum = reverseLinearSpectrum;
        this.dualBarConfig = dualBarConfig == null
                ? DualBarConfig.defaults() : dualBarConfig;
        this.loadBarConfig = loadBarConfig == null
                ? LoadBarConfig.defaults() : loadBarConfig;
        this.introAnimationConfig = introAnimationConfig == null
                ? IntroAnimationConfig.disabled() : introAnimationConfig;
        this.cardiogramConfig = cardiogramConfig == null
                ? CardiogramConfig.defaults() : cardiogramConfig;
        this.neonWaveConfig = neonWaveConfig == null
                ? NeonWaveConfig.defaults() : neonWaveConfig;
    }

    public RenderConfig(int width, int height, int framesPerSecond, Color barColor,
            Color backgroundColor, BufferedImage backgroundImage, Path backgroundVideo,
            BackgroundFitMode backgroundFitMode, VideoEndMode videoEndMode,
            BarStyle barStyle, float sensitivity, RestingLineMode restingLineMode,
            PeakMode peakMode, VisualizationMode visualizationMode,
            CircularConfig circularConfig, boolean reverseLinearSpectrum,
            DualBarConfig dualBarConfig, LoadBarConfig loadBarConfig,
            IntroAnimationConfig introAnimationConfig) {
        this(width, height, framesPerSecond, barColor, backgroundColor,
                backgroundImage, backgroundVideo, backgroundFitMode, videoEndMode,
                barStyle, sensitivity, restingLineMode, peakMode,
                visualizationMode, circularConfig, reverseLinearSpectrum,
                dualBarConfig, loadBarConfig, introAnimationConfig,
                CardiogramConfig.defaults());
    }

    public int width() { return width; }
    public int height() { return height; }
    public int framesPerSecond() { return framesPerSecond; }
    public Color barColor() { return barColor; }
    public Color backgroundColor() { return backgroundColor; }
    public BufferedImage backgroundImage() { return backgroundImage; }
    public Path backgroundVideo() { return backgroundVideo; }
    public BackgroundFitMode backgroundFitMode() { return backgroundFitMode; }
    public VideoEndMode videoEndMode() { return videoEndMode; }
    public BarStyle barStyle() { return barStyle; }
    public float sensitivity() { return sensitivity; }
    public RestingLineMode restingLineMode() { return restingLineMode; }
    public PeakMode peakMode() { return peakMode; }
    public VisualizationMode visualizationMode() { return visualizationMode; }
    public CircularConfig circularConfig() { return circularConfig; }
    public boolean reverseLinearSpectrum() { return reverseLinearSpectrum; }
    public DualBarConfig dualBarConfig() { return dualBarConfig; }
    public LoadBarConfig loadBarConfig() { return loadBarConfig; }
    public IntroAnimationConfig introAnimationConfig() { return introAnimationConfig; }
    public CardiogramConfig cardiogramConfig() { return cardiogramConfig; }
    public NeonWaveConfig neonWaveConfig() { return neonWaveConfig; }

    public boolean introAnimationApplies() {
        if (introAnimationConfig.mode() == IntroAnimationMode.DISABLED) {
            return false;
        }
        if (restingLineMode != RestingLineMode.DOTTED) {
            return false;
        }
        return visualizationMode == VisualizationMode.LINEAR
                || (visualizationMode == VisualizationMode.DUAL_BAR
                && dualBarConfig.layout() == DualBarLayout.JOINED_CENTER);
    }

    public int introFrameCount() {
        return introAnimationApplies()
                ? introAnimationConfig.frameCount(framesPerSecond) : 0;
    }
}
