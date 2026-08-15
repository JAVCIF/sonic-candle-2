package com.soniccandle.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.JPanel;

/** Cabecera vectorial compartida por ambos temas. */
public final class BrandHeaderPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private AppTheme theme;

    public BrandHeaderPanel(AppTheme theme) {
        this.theme = theme == null ? AppTheme.MODERN : theme;
        setOpaque(false);
        setPreferredSize(new Dimension(900, 86));
    }

    public void setTheme(AppTheme theme) {
        this.theme = theme == null ? AppTheme.MODERN : theme;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        ThemeManager.Palette p = ThemeManager.palette(theme);
        g.setPaint(new GradientPaint(0, 0, p.headerStart(),
                getWidth(), 0, p.headerEnd()));
        g.fillRect(0, 0, getWidth(), getHeight());

        int left = 18;
        int baseline = 54;
        Font brandFont = new Font(Font.SANS_SERIF, Font.BOLD, 31);
        g.setFont(brandFont);
        g.setColor(p.headerText());
        String brand = "Sonic Candle";
        g.drawString(brand, left, baseline);
        FontMetrics metrics = g.getFontMetrics();
        int candleX = left + metrics.stringWidth(brand) + 12;
        drawCandleAndLine(g, candleX, baseline, p.headerText(), p.accent());

        String version = "Version 2.0-J";
        String author = "by JavCif & Candle";
        int right = getWidth() - 18;
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g.setColor(p.headerText());
        g.drawString(version, right - g.getFontMetrics().stringWidth(version), 32);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        g.setColor(p.headerMuted());
        g.drawString(author, right - g.getFontMetrics().stringWidth(author), 55);
        g.dispose();
    }

    private static void drawCandleAndLine(Graphics2D g, int x, int baseline,
            Color light, Color accent) {
        int bodyTop = baseline - 25;
        g.setColor(light);
        g.fillRoundRect(x, bodyTop, 9, 26, 2, 2);
        Path2D flame = new Path2D.Double();
        flame.moveTo(x + 4.5, bodyTop - 5);
        flame.curveTo(x - 1, bodyTop - 12, x + 8, bodyTop - 16, x + 6, bodyTop - 23);
        flame.curveTo(x + 14, bodyTop - 14, x + 12, bodyTop - 7, x + 4.5, bodyTop - 5);
        g.fill(flame);

        int lineY = baseline - 1;
        int start = x + 16;
        g.setColor(accent);
        g.fillRect(start, lineY - 7, 11, 8);
        g.fillRect(start + 16, lineY - 4, 9, 5);
        g.fillRect(start + 30, lineY - 2, 7, 3);
        g.setStroke(new BasicStroke(2f));
        g.drawLine(start + 43, lineY, start + 150, lineY);
    }
}
