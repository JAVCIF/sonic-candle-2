package com.soniccandle.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/** Referencia visual y funcional del selector de color temático. */
public final class SonicColorChooserTest {

    private SonicColorChooserTest() {
    }

    public static void main(String[] args) throws Exception {
        UiText.setLanguage(AppLanguage.SPANISH);
        ThemeManager.installDefaults(AppTheme.CLASSIC);
        JColorChooser chooser = SonicColorChooser.createChooser(new Color(92, 35, 153));
        chooser.setSize(700, 470);
        layoutTree(chooser);

        JTabbedPane tabs = find(chooser, JTabbedPane.class);
        assertTrue(tabs != null && tabs.getUI() instanceof SonicTabbedPaneUI,
                "Las pestañas del selector no recibieron el tema oscuro.");
        chooser.setColor(new Color(171, 42, 221));
        assertTrue(hasLabel(chooser, "#AB2ADD"),
                "La vista previa no siguió el color seleccionado.");

        BufferedImage image = new BufferedImage(700, 470, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        chooser.paint(graphics);
        graphics.dispose();
        assertTrue(countVeryBrightPixels(image) < image.getWidth() * image.getHeight() / 3,
                "El selector clásico conserva paneles blancos excesivos.");
        if (args.length == 1) {
            Path output = Path.of(args[0]);
            Files.createDirectories(output);
            ImageIO.write(image, "png", output.resolve("selector-color-clasico.png").toFile());
        }
        System.out.println("Selector de color clásico correcto y legible.");
    }

    private static void layoutTree(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container nested) {
                layoutTree(nested);
            }
        }
    }

    private static boolean hasLabel(Container root, String fragment) {
        for (Component child : root.getComponents()) {
            if (child instanceof JLabel label && label.getText() != null
                    && label.getText().contains(fragment)) {
                return true;
            }
            if (child instanceof Container nested && hasLabel(nested, fragment)) {
                return true;
            }
        }
        return false;
    }

    private static <T extends Component> T find(Container root, Class<T> type) {
        for (Component child : root.getComponents()) {
            if (type.isInstance(child)) {
                return type.cast(child);
            }
            if (child instanceof Container nested) {
                T found = find(nested, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static int countVeryBrightPixels(BufferedImage image) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                if ((rgb >> 16 & 255) > 245 && (rgb >> 8 & 255) > 245
                        && (rgb & 255) > 245) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
