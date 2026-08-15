package com.soniccandle.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/** Tabs legibles en ambas paletas, incluso cuando Swing usa Metal. */
public final class SonicTabbedPaneUI extends BasicTabbedPaneUI {

    private final ThemeManager.Palette palette;

    public SonicTabbedPaneUI(ThemeManager.Palette palette) {
        this.palette = palette;
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        tabInsets = new Insets(7, 14, 7, 14);
        selectedTabPadInsets = new Insets(0, 0, 1, 0);
        tabAreaInsets = new Insets(3, 3, 0, 3);
        contentBorderInsets = new Insets(1, 1, 1, 1);
    }

    @Override
    protected void paintTabBackground(Graphics graphics, int tabPlacement,
            int tabIndex, int x, int y, int width, int height, boolean selected) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Color background;
        if (!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex)) {
            background = mix(palette.control(), palette.panel(), 0.55f);
        } else {
            background = selected ? palette.accent() : palette.control();
        }
        g.setColor(background);
        g.fillRoundRect(x + 1, y + 1, width - 2, height + 4, 8, 8);
        g.dispose();
    }

    @Override
    protected void paintTabBorder(Graphics graphics, int tabPlacement,
            int tabIndex, int x, int y, int width, int height, boolean selected) {
        graphics.setColor(selected ? palette.accent() : palette.border());
        graphics.drawRoundRect(x, y, width - 1, height + 3, 8, 8);
    }

    @Override
    protected void paintText(Graphics graphics, int tabPlacement, Font font,
            FontMetrics metrics, int tabIndex, String title, Rectangle textRect,
            boolean selected) {
        graphics.setFont(font);
        Color foreground;
        if (!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex)) {
            foreground = palette.muted();
        } else if (selected) {
            foreground = ThemeManager.contrast(palette.accent());
        } else {
            foreground = palette.foreground();
        }
        graphics.setColor(foreground);
        graphics.drawString(title, textRect.x, textRect.y + metrics.getAscent());
    }

    @Override
    protected void paintContentBorder(Graphics graphics, int tabPlacement,
            int selectedIndex) {
        graphics.setColor(palette.border());
        int y = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
        graphics.drawRect(0, y, tabPane.getWidth() - 1,
                Math.max(0, tabPane.getHeight() - y - 1));
    }

    @Override
    protected void paintFocusIndicator(Graphics graphics, int tabPlacement,
            Rectangle[] rectangles, int tabIndex, Rectangle iconRect,
            Rectangle textRect, boolean selected) {
        // El relleno púrpura del tab seleccionado es el indicador de foco.
    }

    private static Color mix(Color first, Color second, float secondWeight) {
        float w = Math.max(0f, Math.min(1f, secondWeight));
        return new Color(
                Math.round(first.getRed() * (1f - w) + second.getRed() * w),
                Math.round(first.getGreen() * (1f - w) + second.getGreen() * w),
                Math.round(first.getBlue() * (1f - w) + second.getBlue() * w));
    }
}
