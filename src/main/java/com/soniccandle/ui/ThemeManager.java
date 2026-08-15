package com.soniccandle.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

/** Paletas propias: azul clara por defecto y gris/púrpura inspirada en 1.1.11. */
public final class ThemeManager {

    public record Palette(Color window, Color panel, Color control,
            Color foreground, Color muted, Color accent, Color selection,
            Color border, Color headerStart, Color headerEnd,
            Color headerText, Color headerMuted) {
    }

    private static final Palette MODERN = new Palette(
            new Color(232, 242, 252), new Color(244, 249, 255), Color.WHITE,
            new Color(22, 48, 74), new Color(88, 112, 137),
            new Color(35, 118, 208), new Color(196, 224, 250),
            new Color(178, 204, 229), new Color(18, 77, 132),
            new Color(39, 132, 208), Color.WHITE, new Color(215, 235, 252));
    private static final Palette CLASSIC = new Palette(
            new Color(20, 20, 20), new Color(35, 35, 35), new Color(47, 47, 47),
            new Color(244, 244, 244), new Color(174, 174, 174),
            new Color(133, 36, 202), new Color(82, 35, 112),
            new Color(73, 73, 73), new Color(10, 10, 10),
            new Color(26, 26, 26), Color.WHITE, new Color(202, 202, 202));

    private static AppTheme current = AppTheme.MODERN;

    private ThemeManager() {
    }

    public static Palette palette() {
        return palette(current);
    }

    public static Palette palette(AppTheme theme) {
        return theme == AppTheme.CLASSIC ? CLASSIC : MODERN;
    }

    public static AppTheme current() {
        return current;
    }

    public static void installDefaults(AppTheme theme) {
        current = theme == null ? AppTheme.MODERN : theme;
        Palette p = palette();
        put("control", p.control());
        put("info", p.control());
        put("primaryControl", p.accent());
        put("primaryControlShadow", p.accent().darker());
        put("primaryControlDarkShadow", p.accent().darker().darker());
        put("primaryControlHighlight", p.accent().brighter());
        put("controlShadow", p.border());
        put("controlDkShadow", p.border().darker());
        put("controlHighlight", p.control().brighter());
        put("controlLtHighlight", p.control().brighter());
        put("focus", p.accent());
        put("textHighlight", p.selection());
        put("textHighlightText", p.foreground());
        put("nimbusBase", p.accent());
        put("nimbusBlueGrey", p.panel());
        put("nimbusLightBackground", p.control());
        put("nimbusSelectionBackground", p.accent());
        put("nimbusSelectedText", contrast(p.accent()));
        put("text", p.foreground());
        put("Panel.background", p.panel());
        put("Viewport.background", p.panel());
        put("Label.foreground", p.foreground());
        put("Button.background", p.control());
        put("Button.foreground", p.foreground());
        put("Button.select", p.selection());
        put("ToggleButton.background", p.control());
        put("ToggleButton.foreground", p.foreground());
        put("CheckBox.background", p.panel());
        put("CheckBox.foreground", p.foreground());
        put("ComboBox.background", p.control());
        put("ComboBox.foreground", p.foreground());
        put("ComboBox.selectionBackground", p.selection());
        put("ComboBox.selectionForeground", p.foreground());
        put("ComboBox.disabledBackground", p.panel());
        put("ComboBox.disabledForeground", p.muted());
        put("List.background", p.control());
        put("List.foreground", p.foreground());
        put("List.selectionBackground", p.selection());
        put("List.selectionForeground", p.foreground());
        put("TextField.background", p.control());
        put("TextField.foreground", p.foreground());
        put("TextField.caretForeground", p.accent());
        put("FormattedTextField.background", p.control());
        put("FormattedTextField.foreground", p.foreground());
        put("Spinner.background", p.control());
        put("TabbedPane.background", p.panel());
        put("TabbedPane.foreground", p.foreground());
        put("TabbedPane.selected", p.control());
        put("TabbedPane.disabledForeground", p.muted());
        put("Slider.background", p.panel());
        put("Slider.foreground", p.accent());
        put("ScrollPane.background", p.panel());
        put("ScrollBar.background", p.panel());
        put("ScrollBar.track", p.panel());
        put("ScrollBar.thumb", p.accent());
        put("ScrollBar.thumbDarkShadow", p.accent().darker());
        put("ScrollBar.thumbHighlight", p.accent().brighter());
        put("ProgressBar.background", p.control());
        put("ProgressBar.foreground", p.accent());
        put("ProgressBar.selectionForeground", contrast(p.accent()));
        put("ProgressBar.selectionBackground", p.foreground());
        put("OptionPane.background", p.panel());
        put("OptionPane.messageForeground", p.foreground());
        put("ColorChooser.background", p.panel());
        put("ColorChooser.foreground", p.foreground());
        put("ColorChooser.swatchesDefaultRecentColor", p.control());
        put("TitledBorder.titleColor", p.foreground());
        UIManager.put("ToolTip.background", new ColorUIResource(p.control()));
        UIManager.put("ToolTip.foreground", new ColorUIResource(p.foreground()));
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(p.border()));
        FontUIResource font = new FontUIResource("SansSerif", java.awt.Font.PLAIN, 13);
        FontUIResource small = new FontUIResource("SansSerif", java.awt.Font.PLAIN, 12);
        for (String key : new String[]{"Label.font", "Button.font", "CheckBox.font",
            "ComboBox.font", "Spinner.font", "TextField.font", "FormattedTextField.font",
            "TabbedPane.font", "Slider.font", "ProgressBar.font"}) {
            UIManager.put(key, font);
        }
        UIManager.put("ToolTip.font", small);
        UIManager.put("ScrollBar.width", 15);
    }

    public static void apply(AppTheme theme, Component root) {
        installDefaults(theme);
        SwingUtilities.updateComponentTreeUI(root);
        styleTree(root);
        root.invalidate();
        root.repaint();
    }

    private static void styleTree(Component component) {
        Palette p = palette();
        if (component instanceof BrandHeaderPanel header) {
            header.setTheme(current);
        } else if (component instanceof JComponent swing) {
            Object role = swing.getClientProperty("sonic.role");
            if ("section".equals(role) && swing instanceof JLabel label) {
                label.setForeground(p.accent());
            } else if ("secondary".equals(role) && swing instanceof JLabel label) {
                label.setForeground(p.muted());
            } else if ("play".equals(role) && swing instanceof AbstractButton button) {
                button.setBackground(p.accent());
                button.setForeground(contrast(p.accent()));
                button.setOpaque(true);
            } else if ("color".equals(role) && swing instanceof AbstractButton button) {
                button.setForeground(contrast(button.getBackground()));
                button.setOpaque(true);
                button.setBorder(BorderFactory.createLineBorder(p.border()));
            } else if (swing instanceof AbstractButton button
                    && !(button instanceof JCheckBox)) {
                button.setBackground(p.control());
                button.setForeground(p.foreground());
                button.setOpaque(true);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(p.border()),
                        BorderFactory.createEmptyBorder(3, 7, 3, 7)));
            }
            if (component instanceof JProgressBar progress) {
                progress.setForeground(p.accent());
            } else if (component instanceof JTabbedPane tabs) {
                tabs.setUI(new SonicTabbedPaneUI(p));
                tabs.setBackground(p.panel());
                tabs.setForeground(p.foreground());
                tabs.setBorder(BorderFactory.createLineBorder(p.border()));
            } else if (component instanceof JComboBox<?> combo) {
                combo.setUI(new SonicComboBoxUI(p));
                combo.setBackground(p.control());
                combo.setForeground(p.foreground());
                combo.setBorder(BorderFactory.createLineBorder(p.border()));
            } else if (component instanceof JSlider slider) {
                slider.setUI(new SonicSliderUI(slider, p));
                slider.setOpaque(false);
            } else if (component instanceof JScrollBar scrollBar) {
                scrollBar.setUI(new SonicScrollBarUI(p));
                scrollBar.setBackground(p.panel());
            } else if (component instanceof JScrollPane scroll) {
                scroll.getViewport().setBackground(p.panel());
                scroll.setBorder(BorderFactory.createLineBorder(p.border()));
            } else if (component instanceof JSplitPane split) {
                split.setDividerSize(8);
                split.setBorder(BorderFactory.createLineBorder(p.border()));
            } else if (component instanceof JSpinner spinner) {
                spinner.setBorder(BorderFactory.createLineBorder(p.border()));
            } else if (component instanceof JTextField field) {
                field.setBackground(p.control());
                field.setForeground(p.foreground());
                field.setCaretColor(p.accent());
                field.setBorder(BorderFactory.createLineBorder(p.border()));
            }
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                styleTree(child);
            }
        }
    }

    public static Border cardBorder(int top, int left, int bottom, int right) {
        Palette p = palette();
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(p.border()),
                BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    static Color contrast(Color color) {
        double luminance = color.getRed() * 0.299
                + color.getGreen() * 0.587 + color.getBlue() * 0.114;
        return luminance > 155 ? new Color(20, 30, 42) : Color.WHITE;
    }

    private static void put(String key, Color color) {
        UIManager.put(key, new ColorUIResource(color));
    }
}
