package com.soniccandle.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/** Slider propio con salto directo en la pista y perilla circular. */
public final class SonicSliderUI extends BasicSliderUI {

    private static final int TRACK_THICKNESS = 5;
    private static final int THUMB_SIZE = 17;
    private final ThemeManager.Palette palette;

    public SonicSliderUI(JSlider slider, ThemeManager.Palette palette) {
        super(slider);
        this.palette = palette;
    }

    @Override
    protected Dimension getThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    public void paintTrack(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Color base = ThemeManager.current() == AppTheme.CLASSIC
                ? new Color(238, 238, 244) : new Color(198, 218, 237);
        g.setColor(base);
        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            int y = trackRect.y + (trackRect.height - TRACK_THICKNESS) / 2;
            g.fillRoundRect(trackRect.x, y, trackRect.width, TRACK_THICKNESS,
                    TRACK_THICKNESS, TRACK_THICKNESS);
            int thumbCenter = thumbRect.x + thumbRect.width / 2;
            int start = drawInverted() ? thumbCenter : trackRect.x;
            int end = drawInverted() ? trackRect.x + trackRect.width : thumbCenter;
            g.setColor(palette.accent());
            g.fillRoundRect(Math.min(start, end), y, Math.abs(end - start),
                    TRACK_THICKNESS, TRACK_THICKNESS, TRACK_THICKNESS);
        } else {
            int x = trackRect.x + (trackRect.width - TRACK_THICKNESS) / 2;
            g.fillRoundRect(x, trackRect.y, TRACK_THICKNESS, trackRect.height,
                    TRACK_THICKNESS, TRACK_THICKNESS);
            int thumbCenter = thumbRect.y + thumbRect.height / 2;
            int start = drawInverted() ? trackRect.y : thumbCenter;
            int end = drawInverted() ? thumbCenter : trackRect.y + trackRect.height;
            g.setColor(palette.accent());
            g.fillRoundRect(x, Math.min(start, end), TRACK_THICKNESS,
                    Math.abs(end - start), TRACK_THICKNESS, TRACK_THICKNESS);
        }
        g.dispose();
    }

    @Override
    public void paintThumb(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int inset = slider.isEnabled() ? 1 : 3;
        Color fill = slider.isEnabled() ? palette.accent() : palette.muted();
        g.setColor(fill);
        g.fillOval(thumbRect.x + inset, thumbRect.y + inset,
                thumbRect.width - inset * 2, thumbRect.height - inset * 2);
        g.setColor(ThemeManager.current() == AppTheme.CLASSIC
                ? Color.WHITE : palette.control());
        g.drawOval(thumbRect.x + inset, thumbRect.y + inset,
                thumbRect.width - inset * 2 - 1, thumbRect.height - inset * 2 - 1);
        g.dispose();
    }

    @Override
    public void paintFocus(Graphics graphics) {
        // La perilla ya ofrece una marca de foco clara sin el rectángulo de Metal.
    }

    @Override
    protected TrackListener createTrackListener(JSlider target) {
        return new TrackListener() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (!target.isEnabled()) {
                    return;
                }
                if (!thumbRect.contains(event.getPoint())) {
                    int value = target.getOrientation() == JSlider.HORIZONTAL
                            ? valueForXPosition(event.getX())
                            : valueForYPosition(event.getY());
                    target.setValue(value);
                    calculateThumbLocation();
                }
                // Tras mover la perilla debajo del puntero, BasicSliderUI conserva
                // el arrastre normal desde ese mismo clic.
                super.mousePressed(event);
            }
        };
    }
}
