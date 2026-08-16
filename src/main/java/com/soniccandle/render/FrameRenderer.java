package com.soniccandle.render;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

public final class FrameRenderer {

    private static final double TWO_PI = Math.PI * 2.0;
    private final RenderConfig config;
    private final BufferedImage preparedBackground;
    private final boolean transparentCanvas;

    public FrameRenderer(RenderConfig config) {
        this(config, false);
    }

    public FrameRenderer(RenderConfig config, boolean transparentCanvas) {
        this.config = config;
        this.transparentCanvas = transparentCanvas;
        this.preparedBackground = transparentCanvas ? null : prepareBackground(config);
    }

    public BufferedImage render(float[] spectrum) {
        return render(spectrum, Integer.MAX_VALUE, Float.NaN, null, -1,
                null);
    }

    public BufferedImage render(float[] spectrum, int playbackFrameIndex) {
        return render(spectrum, playbackFrameIndex, Float.NaN, null, -1,
                null);
    }

    public BufferedImage render(float[] spectrum, int playbackFrameIndex,
            float loadBarLevelOverride) {
        return render(spectrum, playbackFrameIndex, loadBarLevelOverride,
                null, -1, null);
    }

    public BufferedImage render(float[] spectrum, int playbackFrameIndex,
            float loadBarLevelOverride, float[] cardiogramSignal,
            int cardiogramFrameIndex) {
        return render(spectrum, playbackFrameIndex, loadBarLevelOverride,
                cardiogramSignal, cardiogramFrameIndex, null);
    }

    public BufferedImage render(float[] spectrum, int playbackFrameIndex,
            float loadBarLevelOverride, float[] cardiogramSignal,
            int cardiogramFrameIndex, float[] cardiogramSweepPositions) {
        return render(spectrum, playbackFrameIndex, loadBarLevelOverride,
                cardiogramSignal, cardiogramFrameIndex,
                cardiogramSweepPositions, null, -1);
    }

    public BufferedImage render(float[] spectrum, int playbackFrameIndex,
            float loadBarLevelOverride, float[] cardiogramSignal,
            int cardiogramFrameIndex, float[] cardiogramSweepPositions,
            NeonWaveTimeline neonWaveTimeline, int neonWaveFrameIndex) {
        BufferedImage frame = new BufferedImage(
                config.width(), config.height(), transparentCanvas
                        ? BufferedImage.TYPE_4BYTE_ABGR
                        : BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = frame.createGraphics();
        configureQuality(graphics);
        if (!transparentCanvas) {
            graphics.drawImage(preparedBackground, 0, 0, null);
        }
        if (isIntroFrame(playbackFrameIndex)) {
            drawIntroAnimation(graphics, playbackFrameIndex);
        } else {
            drawSpectrum(graphics, spectrum, loadBarLevelOverride,
                    cardiogramSignal, cardiogramFrameIndex,
                    cardiogramSweepPositions, neonWaveTimeline,
                    neonWaveFrameIndex);
        }
        graphics.dispose();
        return frame;
    }

    private boolean isIntroFrame(int playbackFrameIndex) {
        return config.introAnimationApplies()
                && playbackFrameIndex >= 0
                && playbackFrameIndex < config.introFrameCount();
    }

    private RestingLineMode effectiveRestingLineMode() {
        return config.introAnimationApplies()
                ? RestingLineMode.DOTTED : config.restingLineMode();
    }

    private void drawIntroAnimation(Graphics2D graphics, int frameIndex) {
        int frameCount = config.introFrameCount();
        if (frameCount <= 0) {
            return;
        }
        double progress = Math.min(1.0, (frameIndex + 1.0) / frameCount);
        int center = config.height() / 2;
        int[] baselines;
        if (config.visualizationMode() == VisualizationMode.DUAL_BAR) {
            DualBarVisibility visibility = config.dualBarConfig().visibility();
            if (visibility == DualBarVisibility.BOTH) {
                baselines = new int[]{center - 2, center + 2};
            } else {
                baselines = new int[]{center};
            }
        } else {
            baselines = new int[]{center};
        }

        Graphics2D line = (Graphics2D) graphics.create();
        configureQuality(line);
        line.setColor(config.barColor());
        line.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        int margin = Math.max(4, config.width() / 100);
        int left = margin;
        int right = config.width() - margin - 1;
        int middle = config.width() / 2;

        if (progress < 0.70) {
            double drawProgress = easeOutCubic(progress / 0.70);
            int halfLength = (int) Math.round((right - left) * 0.5 * drawProgress);
            for (int y : baselines) {
                if (config.introAnimationConfig().direction()
                        == IntroAnimationDirection.OUTSIDE_IN) {
                    line.drawLine(left, y, Math.min(middle, left + halfLength), y);
                    line.drawLine(right, y, Math.max(middle, right - halfLength), y);
                } else {
                    line.drawLine(middle, y, Math.max(left, middle - halfLength), y);
                    line.drawLine(middle, y, Math.min(right, middle + halfLength), y);
                }
            }
        } else if (progress < 0.90) {
            int blink = (int) Math.floor((progress - 0.70) / 0.20 * 6.0);
            if (blink % 2 == 0) {
                for (int y : baselines) {
                    line.drawLine(left, y, right, y);
                }
            }
        } else {
            float dash = Math.max(3f, config.width() / 220f);
            line.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_ROUND, 10f, new float[]{dash, dash}, 0f));
            for (int y : baselines) {
                line.drawLine(left, y, right, y);
            }
        }
        line.dispose();
    }

    private static double easeOutCubic(double value) {
        double inverse = 1.0 - Math.max(0.0, Math.min(1.0, value));
        return 1.0 - inverse * inverse * inverse;
    }

    private void drawSpectrum(Graphics2D graphics, float[] spectrum,
            float loadBarLevelOverride, float[] cardiogramSignal,
            int cardiogramFrameIndex, float[] cardiogramSweepPositions,
            NeonWaveTimeline neonWaveTimeline, int neonWaveFrameIndex) {
        if (config.visualizationMode() == VisualizationMode.CARDIOGRAM) {
            drawCardiogram(graphics, cardiogramSignal, cardiogramFrameIndex,
                    cardiogramSweepPositions);
            return;
        }
        if (spectrum.length == 0) {
            return;
        }
        switch (config.visualizationMode()) {
            case CIRCULAR -> drawCircularSpectrum(graphics, spectrum);
            case DUAL_BAR -> drawDualSpectrum(graphics, spectrum);
            case LOAD_BAR -> drawLoadBars(graphics, spectrum, loadBarLevelOverride);
            case CARDIOGRAM -> drawCardiogram(graphics, cardiogramSignal,
                    cardiogramFrameIndex, cardiogramSweepPositions);
            case NEON_WAVE -> drawNeonWave(graphics, spectrum,
                    neonWaveTimeline, neonWaveFrameIndex);
            case LINEAR -> drawLinearSpectrum(graphics, spectrum);
        }
    }

    private void drawNeonWave(Graphics2D graphics, float[] spectrum,
            NeonWaveTimeline timeline, int currentFrame) {
        NeonWaveConfig neon = config.neonWaveConfig();
        float[] current = timeline != null && currentFrame >= 0
                && currentFrame < timeline.frameCount()
                ? timeline.frame(currentFrame)
                : NeonWaveProcessor.reduceFrame(spectrum, neon.pointCount());
        if (current.length < 2) {
            return;
        }

        int marginX = Math.max(12, config.width() / 8);
        int left = marginX;
        int right = Math.max(left + 2, config.width() - marginX - 1);
        int marginY = Math.max(8, config.height() / 28);
        int groupHeight = Math.max(24, (int) Math.round(config.height() * 0.30));
        int groupTop = switch (neon.placement()) {
            case TOP -> marginY;
            case CENTER -> (config.height() - groupHeight) / 2;
            case BOTTOM -> config.height() - marginY - groupHeight;
        };
        int direction = neon.inverted() ? 1 : -1;
        int baseline = neon.inverted() ? groupTop : groupTop + groupHeight;
        double amplitude = Math.max(10.0, groupHeight - 2.0);
        float baseWidth = Math.max(1.5f, config.height() / 300f);

        Graphics2D neonGraphics = (Graphics2D) graphics.create();
        configureQuality(neonGraphics);
        int echoes = neon.echoCount();
        for (int echo = echoes; echo >= 1; echo--) {
            int sourceFrame = currentFrame - echo * neon.echoSpacing();
            if (timeline == null || sourceFrame < 0
                    || sourceFrame >= timeline.frameCount()) {
                continue;
            }
            float age = echo / (float) Math.max(1, echoes);
            float alpha = neon.echoOpacityPercent() / 100f
                    * (1f - age * 0.72f);
            Path2D echoPath = neonPath(timeline.frame(sourceFrame), left, right,
                    baseline, amplitude, direction, neon.lineStyle());
            neonGraphics.setComposite(AlphaComposite.SrcOver.derive(
                    Math.max(0.03f, Math.min(0.72f, alpha))));
            neonGraphics.setColor(config.barColor());
            neonGraphics.setStroke(neonStroke(neon.lineStyle(),
                    Math.max(0.8f, baseWidth * 0.68f)));
            neonGraphics.draw(echoPath);
        }

        Path2D mainPath = neonPath(current, left, right, baseline,
                amplitude, direction, neon.lineStyle());
        drawNeonGlow(neonGraphics, mainPath, config.barColor(), baseWidth,
                neon.glowPercent());
        drawNeonNodes(neonGraphics, current, left, right, baseline,
                amplitude, direction, baseWidth, neon.glowPercent());
        drawNeonParticles(neonGraphics, timeline, currentFrame, left, right,
                baseline, direction, neon.particleMode(), neon.glowPercent());
        neonGraphics.dispose();
    }

    private Path2D neonPath(float[] points, int left, int right, int baseline,
            double amplitude, int direction, NeonWaveLineStyle style) {
        double[] xs = new double[points.length];
        double[] ys = new double[points.length];
        for (int point = 0; point < points.length; point++) {
            xs[point] = left + point * (right - left)
                    / (double) Math.max(1, points.length - 1);
            double normalized = 1.0 - Math.exp(-Math.max(0f, points[point])
                    * Math.max(0f, config.sensitivity()) * 1.34);
            ys[point] = baseline + direction * amplitude
                    * Math.min(0.985, normalized);
        }
        Path2D path = new Path2D.Double();
        path.moveTo(xs[0], ys[0]);
        if (style == NeonWaveLineStyle.SMOOTH && points.length > 2) {
            for (int point = 1; point < points.length - 1; point++) {
                double nextX = (xs[point] + xs[point + 1]) * 0.5;
                double nextY = (ys[point] + ys[point + 1]) * 0.5;
                path.quadTo(xs[point], ys[point], nextX, nextY);
            }
            path.quadTo(xs[xs.length - 1], ys[ys.length - 1],
                    xs[xs.length - 1], ys[ys.length - 1]);
        } else {
            for (int point = 1; point < points.length; point++) {
                path.lineTo(xs[point], ys[point]);
            }
        }
        return path;
    }

    private void drawNeonGlow(Graphics2D graphics, Path2D path, Color color,
            float baseWidth, int glowPercent) {
        float glow = glowPercent / 100f;
        if (glow > 0f) {
            graphics.setComposite(AlphaComposite.SrcOver.derive(
                    Math.min(0.20f, 0.08f * glow)));
            graphics.setColor(color);
            graphics.setStroke(neonStroke(NeonWaveLineStyle.ROUNDED,
                    baseWidth * (7f + glow * 2f)));
            graphics.draw(path);
            graphics.setComposite(AlphaComposite.SrcOver.derive(
                    Math.min(0.42f, 0.18f * glow)));
            graphics.setStroke(neonStroke(NeonWaveLineStyle.ROUNDED,
                    baseWidth * (3.2f + glow)));
            graphics.draw(path);
        }
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setColor(color);
        graphics.setStroke(neonStroke(config.neonWaveConfig().lineStyle(),
                baseWidth));
        graphics.draw(path);
    }

    private void drawNeonNodes(Graphics2D graphics, float[] points,
            int left, int right, int baseline, double amplitude, int direction,
            float baseWidth, int glowPercent) {
        Color color = config.barColor();
        double radius = Math.max(2.2, baseWidth * 1.55);
        float glow = glowPercent / 100f;
        for (int point = 0; point < points.length; point++) {
            double x = left + point * (right - left)
                    / (double) Math.max(1, points.length - 1);
            double normalized = 1.0 - Math.exp(-Math.max(0f, points[point])
                    * Math.max(0f, config.sensitivity()) * 1.34);
            double y = baseline + direction * amplitude
                    * Math.min(0.985, normalized);
            if (glow > 0f) {
                double outer = radius * (2.5 + glow);
                graphics.setComposite(AlphaComposite.SrcOver.derive(
                        Math.min(0.24f, 0.10f * glow)));
                graphics.setColor(color);
                graphics.fill(new Ellipse2D.Double(x - outer, y - outer,
                        outer * 2, outer * 2));
            }
            graphics.setComposite(AlphaComposite.SrcOver);
            graphics.setColor(brightened(color));
            graphics.fill(new Ellipse2D.Double(x - radius, y - radius,
                    radius * 2, radius * 2));
        }
    }

    private void drawNeonParticles(Graphics2D graphics,
            NeonWaveTimeline timeline, int currentFrame, int left, int right,
            int baseline, int peakDirection, NeonWaveParticleMode mode,
            int glowPercent) {
        if (mode == NeonWaveParticleMode.DISABLED || timeline == null
                || currentFrame < 0 || timeline.frameCount() == 0) {
            return;
        }
        int particleDirection = -peakDirection;
        int life = Math.max(12, (int) Math.round(config.framesPerSecond() * 0.82));
        float density = mode.density();
        Color color = config.barColor();
        for (int age = 0; age < life; age++) {
            int emissionFrame = currentFrame - age;
            if (emissionFrame < 0 || emissionFrame >= timeline.frameCount()) {
                continue;
            }
            float energy = Math.min(1.25f, timeline.energy()[emissionFrame]);
            float onset = Math.min(1f, timeline.onset()[emissionFrame] * 4.5f);
            float drive = Math.min(1f, energy * 0.72f + onset * 0.9f);
            int candidates = mode == NeonWaveParticleMode.INTENSE ? 4 : 2;
            for (int particle = 0; particle < candidates; particle++) {
                long seed = mix64(((long) emissionFrame + 1L) * 0x9E3779B97F4A7C15L
                        + particle * 0xC2B2AE3D27D4EB4FL);
                double chance = unit(seed);
                if (chance > drive * density * 0.58) {
                    continue;
                }
                double xStart = left + unit(mix64(seed + 11)) * (right - left);
                double drift = (unit(mix64(seed + 23)) - 0.5)
                        * age * config.width() / 900.0;
                double speed = 0.45 + unit(mix64(seed + 37)) * 1.45;
                double y = baseline + particleDirection
                        * (4.0 + age * speed * config.height() / 420.0);
                if (y < 1 || y >= config.height() - 1) {
                    continue;
                }
                float fade = 1f - age / (float) life;
                float alpha = Math.min(0.72f,
                        fade * drive * (0.24f + density * 0.25f));
                double radius = Math.max(0.8, config.height() / 720.0)
                        * (0.7 + unit(mix64(seed + 53)) * 1.8);
                if (glowPercent > 0) {
                    graphics.setComposite(AlphaComposite.SrcOver.derive(
                            Math.max(0.01f, alpha * 0.18f)));
                    graphics.setColor(color);
                    graphics.fill(new Ellipse2D.Double(xStart + drift - radius * 3,
                            y - radius * 3, radius * 6, radius * 6));
                }
                graphics.setComposite(AlphaComposite.SrcOver.derive(
                        Math.max(0.02f, alpha)));
                graphics.setColor(brightened(color));
                graphics.fill(new Ellipse2D.Double(xStart + drift - radius,
                        y - radius, radius * 2, radius * 2));
            }
        }
        graphics.setComposite(AlphaComposite.SrcOver);
    }

    private static BasicStroke neonStroke(NeonWaveLineStyle style, float width) {
        int cap = style == NeonWaveLineStyle.ANGULAR
                ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
        int join = style == NeonWaveLineStyle.ANGULAR
                ? BasicStroke.JOIN_MITER : BasicStroke.JOIN_ROUND;
        return new BasicStroke(Math.max(0.5f, width), cap, join);
    }

    private static Color brightened(Color color) {
        return new Color(Math.min(255, color.getRed() + 46),
                Math.min(255, color.getGreen() + 34),
                Math.min(255, color.getBlue() + 24));
    }

    private static long mix64(long value) {
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdl;
        value ^= value >>> 33;
        value *= 0xc4ceb9fe1a85ec53l;
        return value ^ value >>> 33;
    }

    private static double unit(long value) {
        return (value >>> 11) * 0x1.0p-53;
    }

    private void drawCardiogram(Graphics2D graphics, float[] signal,
            int currentFrame, float[] sweepPositions) {
        CardiogramConfig cardiogram = config.cardiogramConfig();
        int margin = Math.max(8, config.width() / 40);
        int left = margin;
        int right = Math.max(left + 1, config.width() - margin - 1);
        int centerY = config.height() / 2;
        double amplitudeScale = Math.max(8.0, config.height() * 0.38)
                * Math.max(0.0f, config.sensitivity());
        int visibleFrames = cardiogram.effectiveSpeedMode()
                .visibleFrames(config.framesPerSecond());
        boolean adaptiveSweep = cardiogram.usesAdaptiveSweep()
                && sweepPositions != null && sweepPositions.length > 0
                && currentFrame >= 0 && currentFrame < sweepPositions.length;
        double currentSweepPosition = adaptiveSweep
                ? sweepPositions[currentFrame] : currentFrame;
        double initialAge = cardiogram.reverse() ? 0.0 : 1.0;
        double initialTarget = currentSweepPosition
                - initialAge * (visibleFrames - 1);
        int sweepCursor = adaptiveSweep ? lowerSweepIndex(sweepPositions,
                currentFrame, initialTarget) : Math.max(0, currentFrame);
        Path2D path = new Path2D.Double();
        int points = Math.max(2, right - left + 1);
        for (int point = 0; point < points; point++) {
            double progress = point / (double) (points - 1);
            double ageProgress = cardiogram.reverse() ? progress : 1.0 - progress;
            double targetPosition = currentSweepPosition
                    - ageProgress * (visibleFrames - 1);
            double sourceIndex;
            if (adaptiveSweep) {
                while (sweepCursor < currentFrame
                        && sweepPositions[sweepCursor + 1] <= targetPosition) {
                    sweepCursor++;
                }
                while (sweepCursor > 0
                        && sweepPositions[sweepCursor] > targetPosition) {
                    sweepCursor--;
                }
                sourceIndex = frameAtSweepPosition(sweepPositions,
                        sweepCursor, currentFrame, targetPosition);
            } else {
                sourceIndex = targetPosition;
            }
            float value = CardiogramSignalProcessor.sample(signal, sourceIndex);
            value = Math.max(-0.985f, Math.min(0.985f, value));
            double x = left + progress * (right - left);
            double y = centerY - value * amplitudeScale;
            y = Math.max(margin, Math.min(config.height() - margin - 1, y));
            if (point == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        Graphics2D trace = (Graphics2D) graphics.create();
        configureQuality(trace);
        trace.setColor(config.barColor());
        switch (cardiogram.style()) {
            case THIN -> trace.setStroke(new BasicStroke(Math.max(1.2f,
                    config.height() / 520f), BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER));
            case THICK -> trace.setStroke(new BasicStroke(Math.max(3.0f,
                    config.height() / 180f), BasicStroke.CAP_SQUARE,
                    BasicStroke.JOIN_MITER));
            case ROUNDED -> trace.setStroke(new BasicStroke(Math.max(2.2f,
                    config.height() / 260f), BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            case SEGMENTED -> {
                float width = Math.max(1.8f, config.height() / 300f);
                float dash = Math.max(5f, config.width() / 150f);
                trace.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND, 10f,
                        new float[]{dash, dash * 0.65f}, 0f));
            }
            case FLUID_HALO -> {
                float base = Math.max(2.4f, config.height() / 230f);
                trace.setComposite(AlphaComposite.SrcOver.derive(0.16f));
                trace.setStroke(new BasicStroke(base * 5.5f,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                trace.draw(path);
                trace.setComposite(AlphaComposite.SrcOver.derive(0.34f));
                trace.setStroke(new BasicStroke(base * 2.8f,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                trace.draw(path);
                trace.setComposite(AlphaComposite.SrcOver);
                trace.setStroke(new BasicStroke(base, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
            }
        }
        trace.draw(path);
        trace.dispose();
    }

    private static double frameAtSweepPosition(float[] positions, int lower,
            int maximumFrame, double target) {
        if (target < positions[0]) {
            return -1.0;
        }
        if (lower >= maximumFrame) {
            return maximumFrame;
        }
        int upper = lower + 1;
        double distance = positions[upper] - positions[lower];
        if (distance <= 0.0) {
            return lower;
        }
        double fraction = (target - positions[lower]) / distance;
        return lower + Math.max(0.0, Math.min(1.0, fraction));
    }

    private static int lowerSweepIndex(float[] positions, int maximumFrame,
            double target) {
        if (target <= positions[0]) {
            return 0;
        }
        int low = 0;
        int high = maximumFrame;
        while (low < high) {
            int middle = (low + high + 1) >>> 1;
            if (positions[middle] <= target) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }
        return low;
    }

    private void drawLinearSpectrum(Graphics2D graphics, float[] spectrum) {
        if (config.barStyle() == BarStyle.FLUID_HALO) {
            drawLinearFluidHalo(graphics, spectrum);
            return;
        }
        double slotWidth = (double) config.width() / spectrum.length;
        int barWidth = Math.max(2, (int) Math.round(slotWidth * 0.72));
        int baseline = config.height() / 2;
        int referenceHalfHeight = config.height() / 2;

        graphics.setColor(config.barColor());
        for (int index = 0; index < spectrum.length; index++) {
            float amplitude = scaledAmplitude(sampleAt(
                    spectrum, index, config.reverseLinearSpectrum()));
            if (amplitude <= 0f && effectiveRestingLineMode() == RestingLineMode.INVISIBLE) {
                continue;
            }
            if (config.peakMode() == PeakMode.SOFT_LIMIT) {
                amplitude = softLimitAmplitude(amplitude,
                        containedAmplitudeLimit(config.barStyle(), barWidth, referenceHalfHeight));
            }
            int halfHeight = safeHeight(amplitude, referenceHalfHeight);
            if (effectiveRestingLineMode() == RestingLineMode.DOTTED) {
                halfHeight = Math.max(2, halfHeight);
            }
            int centerX = (int) Math.round((index + 0.5) * slotWidth);
            drawBar(graphics, config.barStyle(), centerX, baseline,
                    barWidth, halfHeight, config.height());
        }
    }

    private void drawDualSpectrum(Graphics2D graphics, float[] spectrum) {
        DualBarConfig dual = config.dualBarConfig();
        int center = config.height() / 2;
        int safetyGap = Math.max(8, config.height() / 90);
        double halfHeight = config.height() / 2.0;
        double maxLength = Math.max(1.0,
                (halfHeight - safetyGap) * dual.reach().halfScreenFraction());

        int topAnchor;
        int topDirection;
        int bottomAnchor;
        int bottomDirection;
        if (dual.layout() == DualBarLayout.JOINED_CENTER) {
            boolean single = dual.visibility() != DualBarVisibility.BOTH;
            topAnchor = single ? center : center - 2;
            topDirection = -1;
            bottomAnchor = single ? center : center + 2;
            bottomDirection = 1;
        } else {
            topAnchor = 0;
            topDirection = 1;
            bottomAnchor = config.height() - 1;
            bottomDirection = -1;
        }

        if (config.barStyle() == BarStyle.FLUID_HALO) {
            if (dual.visibility().showsTop()) {
                drawDualFluidHalo(graphics, spectrum, topAnchor, topDirection,
                        dual.reverseTop(), maxLength);
            }
            if (dual.visibility().showsBottom()) {
                drawDualFluidHalo(graphics, spectrum, bottomAnchor, bottomDirection,
                        dual.reverseBottom(), maxLength);
            }
            return;
        }

        double slotWidth = (double) config.width() / spectrum.length;
        int barWidth = Math.max(2, (int) Math.round(slotWidth * 0.72));
        graphics.setColor(config.barColor());
        for (int index = 0; index < spectrum.length; index++) {
            int centerX = (int) Math.round((index + 0.5) * slotWidth);
            if (dual.visibility().showsTop()) {
                drawDualBar(graphics, spectrum, index, centerX, barWidth,
                        topAnchor, topDirection, dual.reverseTop(), maxLength);
            }
            if (dual.visibility().showsBottom()) {
                drawDualBar(graphics, spectrum, index, centerX, barWidth,
                        bottomAnchor, bottomDirection, dual.reverseBottom(), maxLength);
            }
        }
    }

    private void drawDualBar(Graphics2D graphics, float[] spectrum, int index,
            int centerX, int barWidth, int anchor, int direction,
            boolean reverse, double maxLength) {
        float amplitude = scaledAmplitude(sampleAt(spectrum, index, reverse));
        if (amplitude <= 0f && effectiveRestingLineMode() == RestingLineMode.INVISIBLE) {
            return;
        }
        amplitude = softLimitAmplitude(amplitude, 0.985f);
        double length = Math.min(maxLength, safeLength(amplitude, maxLength));
        if (effectiveRestingLineMode() == RestingLineMode.DOTTED) {
            double idleLength = Math.max(2.0, Math.min(maxLength, barWidth * 0.48));
            length = Math.max(length, idleLength);
        }
        drawDirectionalBar(graphics, config.barStyle(), centerX, anchor,
                direction, barWidth, length);
    }

    private void drawLoadBars(Graphics2D graphics, float[] spectrum,
            float loadBarLevelOverride) {
        LoadBarConfig load = config.loadBarConfig();
        float level = Float.isFinite(loadBarLevelOverride)
                ? Math.max(0f, Math.min(0.985f, loadBarLevelOverride))
                : LoadBarLevelProcessor.instantaneous(spectrum, config);
        if (load.orientation() == LoadBarOrientation.HORIZONTAL) {
            int thickness = Math.max(12, Math.min(52,
                    (int) Math.round(config.height() * 0.045)));
            int x = Math.max(18, (int) Math.round(config.width() * 0.055));
            int length = config.width() - x * 2;
            int[] centers = load.count() == 2
                    ? new int[]{safeHorizontalCenter(HorizontalPlacement.TOP, thickness),
                        safeHorizontalCenter(HorizontalPlacement.BOTTOM, thickness)}
                    : new int[]{safeHorizontalCenter(load.horizontalPlacement(), thickness)};
            for (int centerY : centers) {
                drawLoadTrack(graphics, x, centerY - thickness / 2,
                        length, thickness, level, true, load);
            }
        } else {
            int thickness = Math.max(12, Math.min(52,
                    (int) Math.round(config.width() * 0.028)));
            int y = Math.max(18, (int) Math.round(config.height() * 0.075));
            int length = config.height() - y * 2;
            int[] centers = load.count() == 2
                    ? new int[]{safeVerticalCenter(VerticalPlacement.LEFT, thickness),
                        safeVerticalCenter(VerticalPlacement.RIGHT, thickness)}
                    : new int[]{safeVerticalCenter(load.verticalPlacement(), thickness)};
            for (int centerX : centers) {
                drawLoadTrack(graphics, centerX - thickness / 2, y,
                        thickness, length, level, false, load);
            }
        }
    }

    private int safeHorizontalCenter(HorizontalPlacement placement, int thickness) {
        int margin = Math.max(thickness, (int) Math.round(config.height() * 0.11));
        return switch (placement) {
            case TOP -> margin;
            case CENTER -> config.height() / 2;
            case BOTTOM -> config.height() - margin;
        };
    }

    private int safeVerticalCenter(VerticalPlacement placement, int thickness) {
        int margin = Math.max(thickness, (int) Math.round(config.width() * 0.085));
        return switch (placement) {
            case LEFT -> margin;
            case CENTER -> config.width() / 2;
            case RIGHT -> config.width() - margin;
        };
    }

    private void drawLoadTrack(Graphics2D graphics, int x, int y, int width, int height,
            float level, boolean horizontal, LoadBarConfig load) {
        float borderWidth = Math.max(2f, Math.min(6f,
                Math.min(width, height) * 0.11f));
        double inset = borderWidth + 1.5;
        double innerX = x + inset;
        double innerY = y + inset;
        double innerWidth = Math.max(0.0, width - inset * 2.0);
        double innerHeight = Math.max(0.0, height - inset * 2.0);
        double radius = load.shape() == LoadBarShape.ROUNDED
                ? Math.min(width, height) : 0.0;
        Shape track = load.shape() == LoadBarShape.ROUNDED
                ? new RoundRectangle2D.Double(x, y, width, height, radius, radius)
                : new Rectangle2D.Double(x, y, width, height);
        Shape inner = load.shape() == LoadBarShape.ROUNDED
                ? new RoundRectangle2D.Double(innerX, innerY, innerWidth, innerHeight,
                        Math.max(1.0, radius - inset * 1.5),
                        Math.max(1.0, radius - inset * 1.5))
                : new Rectangle2D.Double(innerX, innerY, innerWidth, innerHeight);

        double fillX = innerX;
        double fillY = innerY;
        double fillWidth = innerWidth;
        double fillHeight = innerHeight;
        if (horizontal) {
            double filled = innerWidth * level;
            fillX = load.reversed() ? innerX + innerWidth - filled : innerX;
            fillWidth = filled;
        } else {
            double filled = innerHeight * level;
            fillY = load.reversed() ? innerY : innerY + innerHeight - filled;
            fillHeight = filled;
        }
        Shape filledShape = createLoadShape(fillX, fillY, fillWidth, fillHeight,
                load.shape(), Math.max(1.0, radius - inset * 1.5));
        if (fillWidth > 0.01 && fillHeight > 0.01) {
            if (load.fillStyle() == LoadBarFillStyle.FLUID_HALO) {
                drawLoadHalo(graphics, filledShape);
            }
            Graphics2D fill = (Graphics2D) graphics.create();
            configureQuality(fill);
            fill.clip(inner);
            drawStyledLoadFill(fill, filledShape, fillX, fillY,
                    fillWidth, fillHeight, horizontal, load);
            fill.dispose();
        }
        drawStyledLoadBorder(graphics, track, borderWidth, load);
    }

    private void drawStyledLoadFill(Graphics2D graphics, Shape shape,
            double x, double y, double width, double height,
            boolean horizontal, LoadBarConfig load) {
        Color base = config.barColor();
        switch (load.fillStyle()) {
            case DEFAULT -> {
                graphics.setColor(base);
                graphics.fill(shape);
            }
            case THICK_BLOCK -> {
                graphics.setColor(base);
                graphics.fill(shape);
                graphics.setColor(adjustColor(base, 0.58));
                graphics.setStroke(new BasicStroke(2.2f));
                graphics.draw(shape);
            }
            case POP_UP_BLOCK -> {
                Color bright = adjustColor(base, 1.42);
                Color dark = adjustColor(base, 0.52);
                graphics.setPaint(horizontal
                        ? new GradientPaint(0, (float) y, bright,
                                0, (float) (y + height), dark)
                        : new GradientPaint((float) x, 0, bright,
                                (float) (x + width), 0, dark));
                graphics.fill(shape);
                graphics.setColor(adjustColor(base, 1.65));
                graphics.setStroke(new BasicStroke(1.2f));
                graphics.draw(shape);
            }
            case ETCHED_BLOCK -> {
                graphics.setColor(adjustColor(base, 0.60));
                graphics.fill(shape);
                graphics.setColor(adjustColor(base, 1.28));
                graphics.setStroke(new BasicStroke(Math.max(1f,
                        (float) Math.min(width, height) * 0.20f)));
                if (horizontal) {
                    graphics.draw(new java.awt.geom.Line2D.Double(
                            x, y + height * 0.40, x + width, y + height * 0.40));
                } else {
                    graphics.draw(new java.awt.geom.Line2D.Double(
                            x + width * 0.40, y, x + width * 0.40, y + height));
                }
            }
            case SEGMENTED_BLOCKS -> drawLoadSegments(
                    graphics, x, y, width, height, horizontal, load.shape(), base);
            case FLUID_HALO -> {
                Color bright = adjustColor(base, 1.48);
                graphics.setPaint(horizontal
                        ? new GradientPaint((float) x, 0, bright,
                                (float) (x + width), 0, base)
                        : new GradientPaint(0, (float) (y + height), bright,
                                0, (float) y, base));
                graphics.fill(shape);
                graphics.setColor(adjustColor(base, 1.75));
                graphics.setStroke(new BasicStroke(1.2f,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.draw(shape);
            }
        }
    }

    private void drawLoadHalo(Graphics2D graphics, Shape filledShape) {
        Graphics2D halo = (Graphics2D) graphics.create();
        configureQuality(halo);
        halo.setColor(config.barColor());
        halo.setComposite(AlphaComposite.SrcOver.derive(0.16f));
        halo.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        halo.draw(filledShape);
        halo.setComposite(AlphaComposite.SrcOver.derive(0.28f));
        halo.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        halo.draw(filledShape);
        halo.dispose();
    }

    private static void drawLoadSegments(Graphics2D graphics,
            double x, double y, double width, double height,
            boolean horizontal, LoadBarShape shape, Color color) {
        graphics.setColor(color);
        double thickness = horizontal ? height : width;
        double block = Math.max(5.0, thickness * 1.05);
        double gap = Math.max(2.0, thickness * 0.28);
        double length = horizontal ? width : height;
        for (double offset = 0.0; offset < length; offset += block + gap) {
            double visible = Math.min(block, length - offset);
            double pieceX = horizontal ? x + offset : x;
            double pieceY = horizontal ? y : y + offset;
            double pieceWidth = horizontal ? visible : width;
            double pieceHeight = horizontal ? height : visible;
            graphics.fill(createLoadShape(pieceX, pieceY, pieceWidth, pieceHeight,
                    shape, Math.min(pieceWidth, pieceHeight)));
        }
    }

    private static void drawStyledLoadBorder(Graphics2D graphics,
            Shape track, float borderWidth, LoadBarConfig load) {
        Graphics2D border = (Graphics2D) graphics.create();
        configureQuality(border);
        int cap = load.shape() == LoadBarShape.ROUNDED
                ? BasicStroke.CAP_ROUND : BasicStroke.CAP_BUTT;
        Color color = load.borderColor();
        switch (load.borderStyle()) {
            case DEFAULT -> {
                border.setColor(color);
                border.setStroke(new BasicStroke(borderWidth, cap, BasicStroke.JOIN_ROUND));
                border.draw(track);
            }
            case THICK_BLOCK -> {
                border.setColor(color);
                border.setStroke(new BasicStroke(borderWidth * 1.65f,
                        cap, BasicStroke.JOIN_ROUND));
                border.draw(track);
            }
            case POP_UP_BLOCK -> {
                border.setColor(adjustColor(color, 0.48));
                border.setStroke(new BasicStroke(borderWidth * 1.65f,
                        cap, BasicStroke.JOIN_ROUND));
                border.draw(track);
                border.translate(-1.2, -1.2);
                border.setColor(adjustColor(color, 1.52));
                border.setStroke(new BasicStroke(borderWidth * 0.72f,
                        cap, BasicStroke.JOIN_ROUND));
                border.draw(track);
            }
            case ETCHED_BLOCK -> {
                border.setColor(adjustColor(color, 0.38));
                border.setStroke(new BasicStroke(borderWidth * 1.75f,
                        cap, BasicStroke.JOIN_ROUND));
                border.draw(track);
                border.setColor(adjustColor(color, 1.42));
                border.setStroke(new BasicStroke(Math.max(1f, borderWidth * 0.52f),
                        cap, BasicStroke.JOIN_ROUND));
                border.draw(track);
            }
            case SEGMENTED_BLOCKS -> {
                float dash = Math.max(5f, borderWidth * 3.2f);
                border.setColor(color);
                border.setStroke(new BasicStroke(borderWidth, cap,
                        BasicStroke.JOIN_ROUND, 10f,
                        new float[]{dash, dash * 0.62f}, 0f));
                border.draw(track);
            }
        }
        border.dispose();
    }

    private static Shape createLoadShape(double x, double y,
            double width, double height, LoadBarShape shape, double radius) {
        if (shape == LoadBarShape.ROUNDED) {
            double safeRadius = Math.max(1.0, Math.min(radius, Math.min(width, height)));
            return new RoundRectangle2D.Double(x, y, width, height,
                    safeRadius, safeRadius);
        }
        return new Rectangle2D.Double(x, y, width, height);
    }

    private static Color adjustColor(Color color, double factor) {
        return new Color(
                (int) Math.max(0, Math.min(255, Math.round(color.getRed() * factor))),
                (int) Math.max(0, Math.min(255, Math.round(color.getGreen() * factor))),
                (int) Math.max(0, Math.min(255, Math.round(color.getBlue() * factor))),
                color.getAlpha());
    }

    private static void drawDirectionalBar(Graphics2D graphics, BarStyle style,
            int centerX, int anchor, int direction, int barWidth, double length) {
        int roundedWidth = Math.max(1, barWidth - 2);
        int roundedX = centerX - roundedWidth / 2;
        int pixelLength = safeDimension(length);
        int startY = direction > 0 ? anchor : anchor - pixelLength + 1;
        int endY = direction > 0 ? anchor + pixelLength : anchor - pixelLength;
        int radius = Math.max(1, roundedWidth);

        switch (style) {
            case THICK_BLOCK -> {
                graphics.setStroke(new BasicStroke(Math.max(1f, barWidth / 2f),
                        BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                graphics.drawLine(centerX, anchor, centerX, endY);
            }
            case OUTLINE_BLOCK -> {
                int outlineWidth = Math.max(2, barWidth / 3);
                graphics.setStroke(new BasicStroke(1f));
                graphics.drawRect(centerX - outlineWidth / 2, startY,
                        outlineWidth, pixelLength);
            }
            case THIN -> {
                graphics.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND));
                graphics.drawLine(centerX, anchor, centerX, endY);
            }
            case ROUND_FILLED -> graphics.fillRoundRect(
                    roundedX, startY, roundedWidth, pixelLength, radius, radius);
            case ROUND_OUTLINE -> {
                graphics.setStroke(new BasicStroke(1f));
                graphics.drawRoundRect(roundedX, startY, roundedWidth,
                        pixelLength, radius, radius);
            }
            case POP_UP_BLOCK -> graphics.fill3DRect(
                    roundedX, startY, roundedWidth, pixelLength, true);
            case ETCHED_BLOCK -> graphics.fill3DRect(
                    roundedX, startY, roundedWidth, pixelLength, false);
            case OVAL_FILLED -> graphics.fillOval(
                    roundedX, startY, roundedWidth, pixelLength);
            case OVAL_OUTLINE -> {
                graphics.setStroke(new BasicStroke(1f));
                graphics.drawOval(roundedX, startY, roundedWidth, pixelLength);
            }
            case SEGMENTED_BLOCKS -> drawDirectionalBlocks(
                    graphics, centerX - barWidth / 2, anchor,
                    direction, barWidth, pixelLength);
            case FLUID_HALO -> throw new IllegalArgumentException(
                    "El halo fluido se dibuja como una forma continua.");
        }
    }

    private static void drawDirectionalBlocks(Graphics2D graphics, int x,
            int anchor, int direction, int width, int length) {
        int blockHeight = Math.max(3, width / 3);
        int blockGap = Math.max(1, blockHeight / 4);
        for (int distance = 0; distance < length; distance += blockHeight + blockGap) {
            int visible = Math.min(blockHeight, length - distance);
            int y = direction > 0
                    ? anchor + distance
                    : anchor - distance - visible + 1;
            graphics.fillRoundRect(x, y, width, visible, 3, 3);
        }
    }

    private void drawCircularSpectrum(Graphics2D graphics, float[] spectrum) {
        CircularConfig circular = config.circularConfig();
        List<CircleGeometry> circles = CircularLayoutCalculator.calculate(
                config.width(), config.height(), circular);
        for (CircleGeometry circle : circles) {
            drawCircleInterior(graphics, circle, circular);
            if (config.barStyle() == BarStyle.FLUID_HALO) {
                drawFluidHalo(graphics, spectrum, circle, circular);
            } else {
                drawRadialBars(graphics, spectrum, circle, circular);
            }
        }
    }

    private void drawCircleInterior(Graphics2D graphics, CircleGeometry circle,
            CircularConfig circular) {
        double diameter = circle.circleRadius() * 2.0;
        Shape clip = new Ellipse2D.Double(
                circle.centerX() - circle.circleRadius(),
                circle.centerY() - circle.circleRadius(), diameter, diameter);
        if (circular.fillMode() == CircleFillMode.TRANSPARENT) {
            return;
        }
        Graphics2D fillGraphics = (Graphics2D) graphics.create();
        configureQuality(fillGraphics);
        fillGraphics.clip(clip);
        if (circular.fillMode() == CircleFillMode.SOLID_COLOR || circular.image() == null) {
            fillGraphics.setColor(circular.fillColor());
            fillGraphics.fill(clip);
        } else {
            drawCircleImage(fillGraphics, circular.image(), circle, circular);
        }
        fillGraphics.dispose();
    }

    private static void drawCircleImage(Graphics2D graphics, BufferedImage image,
            CircleGeometry circle, CircularConfig circular) {
        double diameter = circle.circleRadius() * 2.0;
        double coverScale = Math.max(diameter / image.getWidth(), diameter / image.getHeight());
        double scale = coverScale * circular.imageZoomPercent() / 100.0;
        double scaledWidth = image.getWidth() * scale;
        double scaledHeight = image.getHeight() * scale;
        double overflowX = Math.max(0.0, (scaledWidth - diameter) / 2.0);
        double overflowY = Math.max(0.0, (scaledHeight - diameter) / 2.0);
        double x = circle.centerX() - scaledWidth / 2.0
                + overflowX * circular.imageOffsetXPercent() / 100.0;
        double y = circle.centerY() - scaledHeight / 2.0
                + overflowY * circular.imageOffsetYPercent() / 100.0;
        graphics.drawImage(image, (int) Math.round(x), (int) Math.round(y),
                (int) Math.ceil(scaledWidth), (int) Math.ceil(scaledHeight), null);
    }

    private void drawRadialBars(Graphics2D graphics, float[] spectrum,
            CircleGeometry circle, CircularConfig circular) {
        double slot = TWO_PI * circle.circleRadius() / spectrum.length;
        double barWidth = Math.max(1.0, Math.min(24.0, slot * 0.68));
        double startAngle = Math.toRadians(circular.rotationDegrees() - 90.0);
        graphics.setColor(config.barColor());

        for (int index = 0; index < spectrum.length; index++) {
            float amplitude = scaledAmplitude(spectrum[index]);
            if (amplitude <= 0f && effectiveRestingLineMode() == RestingLineMode.INVISIBLE) {
                continue;
            }
            if (config.peakMode() == PeakMode.SOFT_LIMIT) {
                amplitude = softLimitAmplitude(amplitude, 0.985f);
            }
            double length = safeLength(amplitude, circle.availableBarLength());
            if (effectiveRestingLineMode() == RestingLineMode.DOTTED) {
                length = Math.max(Math.max(1.0, barWidth * 0.38), length);
            }
            double angle = startAngle + TWO_PI * index / spectrum.length;
            drawRadialBar(graphics, config.barStyle(), circle, angle, barWidth, length);
        }
    }

    private static void drawRadialBar(Graphics2D graphics, BarStyle style,
            CircleGeometry circle, double angle, double width, double length) {
        Graphics2D radial = (Graphics2D) graphics.create();
        radial.translate(circle.centerX(), circle.centerY());
        radial.rotate(angle);
        double x = circle.circleRadius();
        double y = -width / 2.0;
        double roundedWidth = Math.max(1.0, width - 1.5);
        double roundedY = -roundedWidth / 2.0;
        double radius = Math.max(1.0, roundedWidth);

        switch (style) {
            case THICK_BLOCK -> {
                radial.setStroke(new BasicStroke((float) Math.max(1.0, width / 2.0),
                        BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                radial.drawLine((int) Math.round(x), 0, (int) Math.round(x + length), 0);
            }
            case OUTLINE_BLOCK -> {
                radial.setStroke(new BasicStroke(1f));
                radial.draw(new java.awt.geom.Rectangle2D.Double(x, y, length, width));
            }
            case THIN -> {
                radial.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                radial.draw(new java.awt.geom.Line2D.Double(x, 0, x + length, 0));
            }
            case ROUND_FILLED -> radial.fill(new RoundRectangle2D.Double(
                    x, roundedY, length, roundedWidth, radius, radius));
            case ROUND_OUTLINE -> {
                radial.setStroke(new BasicStroke(1f));
                radial.draw(new RoundRectangle2D.Double(
                        x, roundedY, length, roundedWidth, radius, radius));
            }
            case POP_UP_BLOCK -> radial.fill3DRect((int) Math.round(x),
                    (int) Math.round(roundedY), safeDimension(length),
                    Math.max(1, (int) Math.round(roundedWidth)), true);
            case ETCHED_BLOCK -> radial.fill3DRect((int) Math.round(x),
                    (int) Math.round(roundedY), safeDimension(length),
                    Math.max(1, (int) Math.round(roundedWidth)), false);
            case OVAL_FILLED -> radial.fill(new Ellipse2D.Double(
                    x, roundedY, length, roundedWidth));
            case OVAL_OUTLINE -> {
                radial.setStroke(new BasicStroke(1f));
                radial.draw(new Ellipse2D.Double(x, roundedY, length, roundedWidth));
            }
            case SEGMENTED_BLOCKS -> drawRadialBlocks(radial, x, width, length);
            case FLUID_HALO -> throw new IllegalArgumentException(
                    "El halo fluido se dibuja como una forma continua.");
        }
        radial.dispose();
    }

    private static void drawRadialBlocks(Graphics2D graphics, double start,
            double width, double length) {
        double blockLength = Math.max(3.0, width * 0.9);
        double gap = Math.max(1.0, blockLength * 0.25);
        double y = -width / 2.0;
        for (double distance = 0.0; distance < length; distance += blockLength + gap) {
            double visible = Math.min(blockLength, length - distance);
            graphics.fill(new RoundRectangle2D.Double(start + distance, y,
                    visible, width, Math.min(4.0, width), Math.min(4.0, width)));
        }
    }

    private void drawFluidHalo(Graphics2D graphics, float[] spectrum,
            CircleGeometry circle, CircularConfig circular) {
        double[] amplitudes = smoothedAmplitudes(spectrum, false);
        if (allSilent(amplitudes) && effectiveRestingLineMode() == RestingLineMode.INVISIBLE) {
            return;
        }
        double startAngle = Math.toRadians(circular.rotationDegrees() - 90.0);
        Area glow = createHaloArea(circle, amplitudes, startAngle, 1.08, 2.0);
        Area core = createHaloArea(circle, amplitudes, startAngle, 1.0,
                effectiveRestingLineMode() == RestingLineMode.DOTTED ? 1.3 : 0.0);

        Graphics2D halo = (Graphics2D) graphics.create();
        configureQuality(halo);
        halo.setColor(config.barColor());
        halo.setComposite(AlphaComposite.SrcOver.derive(0.16f));
        halo.fill(glow);
        halo.setComposite(AlphaComposite.SrcOver.derive(0.72f));
        halo.fill(core);
        halo.setComposite(AlphaComposite.SrcOver.derive(0.88f));
        halo.setStroke(new BasicStroke(1.15f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        halo.draw(core);
        halo.dispose();
    }

    private Area createHaloArea(CircleGeometry circle, double[] amplitudes,
            double startAngle, double scale, double idleThickness) {
        double[][] points = new double[amplitudes.length][2];
        for (int index = 0; index < amplitudes.length; index++) {
            float amplitude = (float) amplitudes[index];
            if (config.peakMode() == PeakMode.SOFT_LIMIT) {
                amplitude = softLimitAmplitude(amplitude, 0.985f);
            }
            double length = safeLength(amplitude, circle.availableBarLength()) * scale;
            if (config.peakMode() == PeakMode.SOFT_LIMIT) {
                length = Math.min(length, circle.availableBarLength() * 0.985);
            }
            double radius = circle.circleRadius() + Math.max(idleThickness, length);
            double angle = startAngle + TWO_PI * index / amplitudes.length;
            points[index][0] = circle.centerX() + Math.cos(angle) * radius;
            points[index][1] = circle.centerY() + Math.sin(angle) * radius;
        }
        Path2D outer = smoothClosedPath(points);
        Area area = new Area(outer);
        double innerRadius = Math.max(0.0, circle.circleRadius() - 0.6);
        area.subtract(new Area(new Ellipse2D.Double(
                circle.centerX() - innerRadius,
                circle.centerY() - innerRadius,
                innerRadius * 2.0, innerRadius * 2.0)));
        return area;
    }

    private void drawLinearFluidHalo(Graphics2D graphics, float[] spectrum) {
        double[] amplitudes = smoothedAmplitudes(
                spectrum, config.reverseLinearSpectrum());
        if (allSilent(amplitudes) && effectiveRestingLineMode() == RestingLineMode.INVISIBLE) {
            return;
        }
        int baseline = config.height() / 2;
        double idle = effectiveRestingLineMode() == RestingLineMode.DOTTED ? 1.2 : 0.0;
        double[][] top = new double[amplitudes.length][2];
        double[][] bottom = new double[amplitudes.length][2];
        for (int index = 0; index < amplitudes.length; index++) {
            float amplitude = (float) amplitudes[index];
            if (config.peakMode() == PeakMode.SOFT_LIMIT) {
                amplitude = softLimitAmplitude(amplitude, 0.985f);
            }
            double height = Math.max(idle, safeLength(amplitude, config.height() / 2.0));
            double x = (index + 0.5) * config.width() / amplitudes.length;
            top[index] = new double[]{x, baseline - height};
            bottom[index] = new double[]{x, baseline + height};
        }
        Path2D band = smoothOpenBand(top, bottom);
        Graphics2D halo = (Graphics2D) graphics.create();
        halo.setColor(config.barColor());
        halo.setComposite(AlphaComposite.SrcOver.derive(0.20f));
        halo.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        halo.draw(band);
        halo.setComposite(AlphaComposite.SrcOver.derive(0.74f));
        halo.fill(band);
        halo.dispose();
    }

    private void drawDualFluidHalo(Graphics2D graphics, float[] spectrum,
            int anchor, int direction, boolean reverse, double maxLength) {
        double[] amplitudes = smoothedAmplitudes(spectrum, reverse);
        if (allSilent(amplitudes) && effectiveRestingLineMode() == RestingLineMode.INVISIBLE) {
            return;
        }
        double idle = effectiveRestingLineMode() == RestingLineMode.DOTTED ? 1.2 : 0.0;
        double[][] wave = new double[amplitudes.length][2];
        double[][] baseline = new double[amplitudes.length][2];
        for (int index = 0; index < amplitudes.length; index++) {
            float amplitude = softLimitAmplitude((float) amplitudes[index], 0.985f);
            double length = Math.max(idle,
                    Math.min(maxLength, safeLength(amplitude, maxLength)));
            double x = (index + 0.5) * config.width() / amplitudes.length;
            wave[index] = new double[]{x, anchor + direction * length};
            baseline[index] = new double[]{x, anchor};
        }
        Path2D band = smoothOpenBand(wave, baseline);
        Graphics2D halo = (Graphics2D) graphics.create();
        configureQuality(halo);
        halo.setColor(config.barColor());
        halo.setComposite(AlphaComposite.SrcOver.derive(0.20f));
        halo.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        halo.draw(band);
        halo.setComposite(AlphaComposite.SrcOver.derive(0.74f));
        halo.fill(band);
        halo.dispose();
    }

    private double[] smoothedAmplitudes(float[] spectrum, boolean reverse) {
        double[] values = new double[spectrum.length];
        for (int index = 0; index < spectrum.length; index++) {
            float previous = scaledAmplitude(sampleAt(
                    spectrum, Math.floorMod(index - 1, spectrum.length), reverse));
            float current = scaledAmplitude(sampleAt(spectrum, index, reverse));
            float next = scaledAmplitude(sampleAt(
                    spectrum, (index + 1) % spectrum.length, reverse));
            values[index] = (previous + current * 2.0 + next) / 4.0;
        }
        return values;
    }

    private static float sampleAt(float[] spectrum, int index, boolean reverse) {
        return spectrum[reverse ? spectrum.length - 1 - index : index];
    }

    private float scaledAmplitude(float value) {
        return Math.max(0f, value * config.sensitivity());
    }

    private static boolean allSilent(double[] values) {
        for (double value : values) {
            if (value > 0.0001) {
                return false;
            }
        }
        return true;
    }

    private static Path2D smoothClosedPath(double[][] points) {
        Path2D path = new Path2D.Double();
        int last = points.length - 1;
        double startX = (points[last][0] + points[0][0]) / 2.0;
        double startY = (points[last][1] + points[0][1]) / 2.0;
        path.moveTo(startX, startY);
        for (int index = 0; index < points.length; index++) {
            int next = (index + 1) % points.length;
            double endX = (points[index][0] + points[next][0]) / 2.0;
            double endY = (points[index][1] + points[next][1]) / 2.0;
            path.quadTo(points[index][0], points[index][1], endX, endY);
        }
        path.closePath();
        return path;
    }

    private static Path2D smoothOpenBand(double[][] top, double[][] bottom) {
        Path2D path = new Path2D.Double();
        path.moveTo(top[0][0], top[0][1]);
        appendSmoothLine(path, top, false);
        path.lineTo(bottom[bottom.length - 1][0], bottom[bottom.length - 1][1]);
        appendSmoothLine(path, bottom, true);
        path.closePath();
        return path;
    }

    private static void appendSmoothLine(Path2D path, double[][] points, boolean reverse) {
        int length = points.length;
        for (int step = 1; step < length; step++) {
            int previous = reverse ? length - step : step - 1;
            int current = reverse ? length - step - 1 : step;
            double middleX = (points[previous][0] + points[current][0]) / 2.0;
            double middleY = (points[previous][1] + points[current][1]) / 2.0;
            path.quadTo(points[previous][0], points[previous][1], middleX, middleY);
        }
        int end = reverse ? 0 : length - 1;
        path.lineTo(points[end][0], points[end][1]);
    }

    private static void drawBar(Graphics2D graphics, BarStyle style, int centerX,
            int baseline, int barWidth, int halfHeight, int canvasHeight) {
        int x = centerX - barWidth / 2;
        int y = baseline - halfHeight;
        int totalHeight = halfHeight * 2;
        int roundedWidth = Math.max(1, barWidth - 2);
        int roundedX = centerX - roundedWidth / 2;
        int radius = Math.max(1, roundedWidth);

        switch (style) {
            case THICK_BLOCK -> {
                graphics.setStroke(new BasicStroke(Math.max(1f, barWidth / 2f)));
                graphics.drawLine(centerX, baseline - halfHeight, centerX, baseline + halfHeight);
            }
            case OUTLINE_BLOCK -> {
                int outlineWidth = Math.max(2, barWidth / 3);
                graphics.setStroke(new BasicStroke(1f));
                graphics.drawRect(centerX - outlineWidth / 2, y, outlineWidth, totalHeight);
            }
            case THIN -> {
                graphics.setStroke(new BasicStroke(2f));
                graphics.drawLine(centerX, baseline - halfHeight, centerX, baseline + halfHeight);
            }
            case ROUND_FILLED -> graphics.fillRoundRect(
                    roundedX, y, roundedWidth, totalHeight + 2, radius, radius);
            case ROUND_OUTLINE -> {
                graphics.setStroke(new BasicStroke(1f));
                graphics.drawRoundRect(roundedX, y, roundedWidth,
                        totalHeight + 2, radius, radius);
            }
            case POP_UP_BLOCK -> graphics.fill3DRect(
                    roundedX, y, roundedWidth, totalHeight + 2, true);
            case ETCHED_BLOCK -> graphics.fill3DRect(
                    roundedX, y, roundedWidth, totalHeight + 2, false);
            case OVAL_FILLED -> graphics.fillOval(
                    roundedX, y, roundedWidth, totalHeight + 2);
            case OVAL_OUTLINE -> {
                graphics.setStroke(new BasicStroke(1f));
                graphics.drawOval(roundedX, y, roundedWidth, totalHeight + 2);
            }
            case SEGMENTED_BLOCKS -> drawBlocks(
                    graphics, x, baseline, barWidth, halfHeight, canvasHeight);
            case FLUID_HALO -> throw new IllegalArgumentException(
                    "El halo fluido se dibuja como una forma continua.");
        }
    }

    static int safeHeight(float amplitude, int referenceHalfHeight) {
        double pixels = amplitude * referenceHalfHeight;
        if (!Double.isFinite(pixels) || pixels <= 0.0) {
            return 0;
        }
        return (int) Math.min(Integer.MAX_VALUE / 4.0, Math.round(pixels));
    }

    private static double safeLength(float amplitude, double referenceLength) {
        double pixels = amplitude * referenceLength;
        if (!Double.isFinite(pixels) || pixels <= 0.0) {
            return 0.0;
        }
        return Math.min(Integer.MAX_VALUE / 4.0, pixels);
    }

    private static int safeDimension(double value) {
        return (int) Math.max(0, Math.min(Integer.MAX_VALUE / 4.0, Math.round(value)));
    }

    static float softLimitAmplitude(float amplitude, float limit) {
        float safeAmplitude = Math.max(0f, amplitude);
        float safeLimit = Math.max(0.10f, Math.min(0.999f, limit));
        float knee = Math.min(0.72f, safeLimit * 0.75f);
        if (safeAmplitude <= knee) {
            return safeAmplitude;
        }
        double compressed = knee + (safeLimit - knee)
                * (1.0 - Math.exp(-(safeAmplitude - knee) / (safeLimit - knee)));
        return (float) Math.min(safeLimit, compressed);
    }

    private static float containedAmplitudeLimit(
            BarStyle style, int barWidth, int referenceHalfHeight) {
        float verticalPadding = switch (style) {
            case THICK_BLOCK -> barWidth / 4f + 2f;
            case THIN, OUTLINE_BLOCK -> 2f;
            default -> 4f;
        };
        return Math.max(0.80f,
                (referenceHalfHeight - verticalPadding) / referenceHalfHeight);
    }

    private static void drawBlocks(Graphics2D graphics, int x, int baseline, int width,
            int halfHeight, int canvasHeight) {
        if (halfHeight <= 0) {
            return;
        }
        int blockHeight = Math.max(3, width / 3);
        int blockGap = Math.max(1, blockHeight / 4);
        int visibleDistance = Math.min(halfHeight,
                Math.max(baseline, canvasHeight - baseline) + blockHeight);
        for (int distance = 0; distance < visibleDistance; distance += blockHeight + blockGap) {
            int height = Math.min(blockHeight, halfHeight - distance);
            graphics.fillRoundRect(x, baseline - distance - height, width, height, 3, 3);
            graphics.fillRoundRect(x, baseline + distance, width, height, 3, 3);
        }
    }

    private static BufferedImage prepareBackground(RenderConfig config) {
        BufferedImage background = new BufferedImage(
                config.width(), config.height(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = background.createGraphics();
        configureQuality(graphics);
        if (config.backgroundImage() != null) {
            drawCover(graphics, config.backgroundImage(), config.width(), config.height());
            graphics.setComposite(AlphaComposite.SrcOver.derive(0.22f));
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, config.width(), config.height());
        } else {
            Color base = config.backgroundColor();
            Color darker = new Color(
                    Math.max(0, base.getRed() / 3),
                    Math.max(0, base.getGreen() / 3),
                    Math.max(0, base.getBlue() / 3));
            graphics.setPaint(new GradientPaint(0, 0, base, 0, config.height(), darker));
            graphics.fillRect(0, 0, config.width(), config.height());
        }
        graphics.dispose();
        return background;
    }

    private static void drawCover(Graphics2D graphics, BufferedImage image, int width, int height) {
        double scale = Math.max((double) width / image.getWidth(),
                (double) height / image.getHeight());
        int scaledWidth = (int) Math.ceil(image.getWidth() * scale);
        int scaledHeight = (int) Math.ceil(image.getHeight() * scale);
        int x = (width - scaledWidth) / 2;
        int y = (height - scaledHeight) / 2;
        graphics.drawImage(image, x, y, scaledWidth, scaledHeight, null);
    }

    private static void configureQuality(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }
}
