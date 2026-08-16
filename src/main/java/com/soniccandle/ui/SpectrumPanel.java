package com.soniccandle.ui;

import com.soniccandle.render.FrameRenderer;
import com.soniccandle.render.RenderConfig;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public final class SpectrumPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private float[] spectrum = createPlaceholder();
    private RenderConfig config;
    private int playbackFrameIndex = Integer.MAX_VALUE;
    private float loadBarLevelOverride = Float.NaN;

    public SpectrumPanel() {
        setPreferredSize(new Dimension(720, 405));
        setMinimumSize(new Dimension(480, 270));
        setBackground(new Color(12, 13, 18));
    }

    public void showFrame(float[] spectrum, RenderConfig config) {
        showFrame(spectrum, config, Integer.MAX_VALUE);
    }

    public void showFrame(float[] spectrum, RenderConfig config, int playbackFrameIndex) {
        showFrame(spectrum, config, playbackFrameIndex, Float.NaN);
    }

    public void showFrame(float[] spectrum, RenderConfig config,
            int playbackFrameIndex, float loadBarLevelOverride) {
        this.spectrum = spectrum == null ? createPlaceholder() : spectrum;
        this.config = config;
        this.playbackFrameIndex = playbackFrameIndex;
        this.loadBarLevelOverride = loadBarLevelOverride;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (config == null || getWidth() <= 0 || getHeight() <= 0) {
            drawWelcome(g2);
        } else {
            RenderConfig previewConfig = new RenderConfig(
                    getWidth(), getHeight(), config.framesPerSecond(), config.barColor(),
                    config.backgroundColor(), config.backgroundImage(),
                    config.backgroundVideo(), config.backgroundFitMode(),
                    config.videoEndMode(), config.barStyle(), config.sensitivity(),
                    config.restingLineMode(), config.peakMode(),
                    config.visualizationMode(), config.circularConfig(),
                    config.reverseLinearSpectrum(), config.dualBarConfig(),
                    config.loadBarConfig(), config.introAnimationConfig());
            BufferedImage preview = new FrameRenderer(previewConfig)
                    .render(spectrum, playbackFrameIndex, loadBarLevelOverride);
            g2.drawImage(preview, 0, 0, null);
        }
        g2.dispose();
    }

    private void drawWelcome(Graphics2D graphics) {
        graphics.setColor(new Color(25, 27, 37));
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setColor(new Color(174, 145, 255));
        graphics.setFont(getFont().deriveFont(24f));
        String title = UiText.text("preview.welcomeTitle");
        int titleWidth = graphics.getFontMetrics().stringWidth(title);
        graphics.drawString(title, (getWidth() - titleWidth) / 2, getHeight() / 2 - 8);
        graphics.setColor(new Color(180, 184, 198));
        graphics.setFont(getFont().deriveFont(13f));
        String subtitle = UiText.text("preview.welcomeSubtitle");
        int subtitleWidth = graphics.getFontMetrics().stringWidth(subtitle);
        graphics.drawString(subtitle, (getWidth() - subtitleWidth) / 2, getHeight() / 2 + 22);
    }

    private static float[] createPlaceholder() {
        float[] values = new float[64];
        for (int i = 0; i < values.length; i++) {
            double center = (values.length - 1) / 2.0;
            double envelope = 1.0 - Math.abs(i - center) / center;
            values[i] = (float) (0.12 + envelope * 0.28 + Math.sin(i * 0.73) * 0.07);
        }
        return values;
    }
}
