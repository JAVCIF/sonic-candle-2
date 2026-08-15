package com.soniccandle.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

/** Flecha de combobox neutra/púrpura, independiente del tema azul de Metal. */
public final class SonicComboBoxUI extends BasicComboBoxUI {

    private final ThemeManager.Palette palette;

    public SonicComboBoxUI(ThemeManager.Palette palette) {
        this.palette = palette;
    }

    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(isEnabled() ? palette.control() : palette.panel());
                g.fillRect(0, 0, getWidth(), getHeight());
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                Polygon arrow = new Polygon(
                        new int[]{cx - 5, cx + 5, cx},
                        new int[]{cy - 3, cy - 3, cy + 3}, 3);
                g.setColor(isEnabled() ? palette.accent() : palette.muted());
                g.fillPolygon(arrow);
                g.dispose();
            }
        };
        button.setName("ComboBox.arrowButton");
        button.setFocusable(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(24, 20));
        return button;
    }
}
