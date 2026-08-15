package com.soniccandle.ui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

/** Verifica el seek por clic directo y el deslizador circular temático. */
public final class SonicSliderUITest {

    private SonicSliderUITest() {
    }

    public static void main(String[] args) throws Exception {
        AtomicInteger value = new AtomicInteger();
        AtomicInteger accentPixels = new AtomicInteger();
        SwingUtilities.invokeAndWait(() -> {
            ThemeManager.installDefaults(AppTheme.CLASSIC);
            JSlider slider = new JSlider(0, 1_000, 0);
            slider.setSize(420, 46);
            slider.setUI(new SonicSliderUI(slider, ThemeManager.palette()));
            BufferedImage image = new BufferedImage(420, 46,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            slider.paint(graphics);
            graphics.dispose();

            long now = System.currentTimeMillis();
            slider.dispatchEvent(new MouseEvent(slider, MouseEvent.MOUSE_PRESSED,
                    now, 0, 315, 23, 1, false, MouseEvent.BUTTON1));
            slider.dispatchEvent(new MouseEvent(slider, MouseEvent.MOUSE_RELEASED,
                    now + 1, 0, 315, 23, 1, false, MouseEvent.BUTTON1));
            value.set(slider.getValue());

            BufferedImage changed = new BufferedImage(420, 46,
                    BufferedImage.TYPE_INT_ARGB);
            graphics = changed.createGraphics();
            slider.paint(graphics);
            graphics.dispose();
            int accent = ThemeManager.palette().accent().getRGB() & 0xFFFFFF;
            int count = 0;
            for (int y = 0; y < changed.getHeight(); y++) {
                for (int x = 0; x < changed.getWidth(); x++) {
                    if ((changed.getRGB(x, y) & 0xFFFFFF) == accent) {
                        count++;
                    }
                }
            }
            accentPixels.set(count);
        });
        assertTrue(value.get() >= 700 && value.get() <= 800,
                "El clic directo no saltó a la sección esperada: " + value.get());
        assertTrue(accentPixels.get() > 250,
                "El slider clásico no pintó pista/perilla púrpura.");
        System.out.println("Slider correcto: clic directo y tema púrpura.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
