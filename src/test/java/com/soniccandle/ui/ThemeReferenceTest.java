package com.soniccandle.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;

/** Referencias visuales reproducibles de los temas Moderno y Clásico. */
public final class ThemeReferenceTest {

    private ThemeReferenceTest() {
    }

    public static void main(String[] args) throws Exception {
        BufferedImage modern = render(AppTheme.MODERN);
        BufferedImage classic = render(AppTheme.CLASSIC);
        assertTrue(colorDistance(modern.getRGB(20, 30), classic.getRGB(20, 30)) > 80,
                "Los dos temas no tienen paletas diferenciadas.");
        assertTrue(modern.getWidth() == 900 && classic.getHeight() == 500,
                "La referencia temática cambió de tamaño.");
        if (args.length == 1) {
            Path output = Path.of(args[0]);
            Files.createDirectories(output);
            ImageIO.write(modern, "png", output.resolve("tema-azul-moderno.png").toFile());
            ImageIO.write(classic, "png", output.resolve("tema-clasico.png").toFile());
        }
        System.out.println("Temas correctos: Azul moderno y Clásico.");
    }

    private static BufferedImage render(AppTheme theme) {
        ThemeManager.installDefaults(theme);
        JPanel root = new JPanel(new BorderLayout());
        root.setSize(new Dimension(900, 500));
        root.add(new BrandHeaderPanel(theme), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(10, 10));
        JPanel controls = new JPanel(new GridLayout(5, 2, 10, 10));
        controls.add(new JLabel("Video y espectro"));
        controls.add(new JComboBox<>(new String[]{"1280 × 720", "1920 × 1080"}));
        controls.add(new JLabel("Movimiento"));
        controls.add(new JComboBox<>(new String[]{"Ágil", "Rápido"}));
        controls.add(new JLabel("Sensibilidad"));
        controls.add(new JProgressBar(40, 250));
        controls.add(new JCheckBox("Invertir izquierda/derecha"));
        controls.add(new JButton("Elegir…"));
        controls.add(new JLabel("Vista previa con audio"));
        controls.add(new JButton("▶ Reproducir"));
        controls.add(new JLabel("Línea de tiempo"));
        JSlider timeline = new JSlider(0, 100, 63);
        controls.add(timeline);
        body.add(controls, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        JPanel tab = new JPanel(new GridLayout(6, 1, 4, 4));
        tab.add(new JLabel("Estilo"));
        tab.add(new JComboBox<>(new String[]{"Bloque redondo", "Halo fluido"}));
        tab.add(new JLabel("Sensibilidad"));
        tab.add(new JSlider(40, 250, 100));
        tab.add(new JCheckBox("Invertir izquierda/derecha"));
        tab.add(new JButton("Elegir color…"));
        tabs.addTab("Barras", new JScrollPane(tab));
        tabs.addTab("Circular", new JPanel());
        tabs.addTab("Doble barra", new JPanel());
        tabs.addTab("Barra de carga", new JPanel());
        body.add(tabs, BorderLayout.CENTER);
        body.setBorder(ThemeManager.cardBorder(12, 20, 14, 20));
        root.add(body, BorderLayout.CENTER);
        ThemeManager.apply(theme, root);
        root.doLayout();
        layoutTree(root);
        BufferedImage image = new BufferedImage(900, 500, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        root.paint(graphics);
        graphics.dispose();
        return image;
    }

    private static void layoutTree(java.awt.Container container) {
        container.doLayout();
        for (java.awt.Component child : container.getComponents()) {
            if (child instanceof java.awt.Container nested) layoutTree(nested);
        }
    }

    private static int colorDistance(int first, int second) {
        int red = Math.abs((first >> 16 & 255) - (second >> 16 & 255));
        int green = Math.abs((first >> 8 & 255) - (second >> 8 & 255));
        int blue = Math.abs((first & 255) - (second & 255));
        return red + green + blue;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
