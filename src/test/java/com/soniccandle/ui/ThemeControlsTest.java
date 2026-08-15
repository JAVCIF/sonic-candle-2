package com.soniccandle.ui;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;

/** Confirma que los controles problemáticos reciben pintores propios. */
public final class ThemeControlsTest {

    private ThemeControlsTest() {
    }

    public static void main(String[] args) {
        JPanel root = new JPanel(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        JPanel controls = new JPanel();
        JComboBox<String> combo = new JComboBox<>(new String[]{"Estándar", "Ágil"});
        JSlider slider = new JSlider();
        JScrollPane scroll = new JScrollPane(new JPanel());
        controls.add(combo);
        controls.add(slider);
        controls.add(scroll);
        tabs.addTab("Barras", controls);
        tabs.addTab("Circular", new JPanel());
        root.add(tabs);

        ThemeManager.apply(AppTheme.CLASSIC, root);
        JScrollBar vertical = scroll.getVerticalScrollBar();
        assertTrue(tabs.getUI() instanceof SonicTabbedPaneUI,
                "Los tabs no usan el pintor Sonic.");
        assertTrue(combo.getUI() instanceof SonicComboBoxUI,
                "El selector conserva el pintor azul de Metal.");
        assertTrue(slider.getUI() instanceof SonicSliderUI,
                "El slider no usa el pintor Sonic.");
        assertTrue(vertical.getUI() instanceof SonicScrollBarUI,
                "La scrollbar conserva el pintor azul de Metal.");
        System.out.println("Controles del tema clásico correctamente sustituidos.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
