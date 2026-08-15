package com.soniccandle.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/** Diálogos informativos y de error coherentes con el tema activo. */
public final class SonicDialogs {

    private SonicDialogs() {
    }

    public static void showMessage(Component parent, String title,
            String message, int messageType) {
        JOptionPane pane = createPane(message, messageType);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.setModal(true);
        ThemeManager.apply(ThemeManager.current(), dialog);
        JButton action = findButton(dialog);
        if (action != null) {
            stylePrimaryButton(action);
            dialog.getRootPane().setDefaultButton(action);
        }
        dialog.pack();
        Dimension size = dialog.getSize();
        dialog.setSize(Math.max(350, size.width), Math.max(140, size.height));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        dialog.dispose();
    }

    static JOptionPane createPane(String message, int messageType) {
        JTextArea text = new JTextArea(message == null ? "" : message);
        text.setEditable(false);
        text.setFocusable(false);
        text.setOpaque(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        Font labelFont = javax.swing.UIManager.getFont("Label.font");
        if (labelFont != null) {
            text.setFont(labelFont);
        }
        text.setPreferredSize(messageSize(text));
        ThemeManager.Palette palette = ThemeManager.palette();
        text.setForeground(palette.foreground());

        String accept = UiText.text("dialog.accept");
        return new JOptionPane(text, messageType, JOptionPane.DEFAULT_OPTION,
                null, new Object[]{accept}, accept);
    }

    static JButton findButton(Container root) {
        for (Component child : root.getComponents()) {
            if (child instanceof JButton button) {
                return button;
            }
            if (child instanceof Container nested) {
                JButton found = findButton(nested);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    static void stylePrimaryButton(JButton button) {
        ThemeManager.Palette palette = ThemeManager.palette();
        Color accent = palette.accent();
        button.putClientProperty("sonic.role", "play");
        button.setBackground(accent);
        button.setForeground(ThemeManager.contrast(accent));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent.brighter()),
                BorderFactory.createEmptyBorder(5, 18, 5, 18)));
        button.setFocusPainted(false);
    }

    static Dimension messageSize(JTextArea text) {
        FontMetrics metrics = text.getFontMetrics(text.getFont());
        int maximumWidth = 360;
        int widest = 0;
        int rows = 0;
        String[] lines = text.getText().split("\\R", -1);
        for (String line : lines) {
            int lineWidth = metrics.stringWidth(line);
            widest = Math.max(widest, Math.min(maximumWidth, lineWidth));
            rows += Math.max(1, (lineWidth + maximumWidth - 1) / maximumWidth);
        }
        int width = Math.max(205, Math.min(maximumWidth, widest + 4));
        int visibleRows = Math.max(1, Math.min(7, rows));
        int height = visibleRows * metrics.getHeight() + 4;
        return new Dimension(width, height);
    }
}
