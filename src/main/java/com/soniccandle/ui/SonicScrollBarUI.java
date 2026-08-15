package com.soniccandle.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** Scrollbar sin los azules heredados de Metal. */
public final class SonicScrollBarUI extends BasicScrollBarUI {

    private final ThemeManager.Palette palette;

    public SonicScrollBarUI(ThemeManager.Palette palette) {
        this.palette = palette;
    }

    @Override
    protected void configureScrollBarColors() {
        trackColor = palette.panel();
        thumbColor = palette.accent();
        thumbDarkShadowColor = palette.accent().darker();
        thumbHighlightColor = palette.accent().brighter();
        thumbLightShadowColor = palette.accent();
    }

    @Override
    protected void paintTrack(Graphics graphics, JComponent component,
            Rectangle bounds) {
        graphics.setColor(palette.panel());
        graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    protected void paintThumb(Graphics graphics, JComponent component,
            Rectangle bounds) {
        if (!component.isEnabled() || bounds.isEmpty()) {
            return;
        }
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(palette.accent());
        g.fillRoundRect(bounds.x + 2, bounds.y + 2,
                Math.max(0, bounds.width - 4), Math.max(0, bounds.height - 4), 8, 8);
        g.dispose();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new ArrowButton(orientation);
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new ArrowButton(orientation);
    }

    private final class ArrowButton extends JButton {

        private static final long serialVersionUID = 1L;
        private final int direction;

        ArrowButton(int direction) {
            this.direction = direction;
            setFocusable(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setPreferredSize(new Dimension(15, 15));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(palette.control());
            g.fillRect(0, 0, getWidth(), getHeight());
            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            Polygon arrow = switch (direction) {
                case SwingConstants.NORTH -> new Polygon(
                    new int[]{cx - 4, cx + 4, cx}, new int[]{cy + 2, cy + 2, cy - 3}, 3);
                case SwingConstants.SOUTH -> new Polygon(
                    new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                case SwingConstants.WEST -> new Polygon(
                    new int[]{cx + 2, cx + 2, cx - 3}, new int[]{cy - 4, cy + 4, cy}, 3);
                default -> new Polygon(
                    new int[]{cx - 2, cx - 2, cx + 3}, new int[]{cy - 4, cy + 4, cy}, 3);
            };
            g.setColor(palette.foreground());
            g.fillPolygon(arrow);
            g.dispose();
        }
    }
}
