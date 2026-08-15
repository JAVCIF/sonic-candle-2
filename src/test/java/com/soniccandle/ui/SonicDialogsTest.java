package com.soniccandle.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/** Comprueba legibilidad y estilo de los avisos de completado/error. */
public final class SonicDialogsTest {

    private SonicDialogsTest() {
    }

    public static void main(String[] args) throws Exception {
        UiText.setLanguage(AppLanguage.SPANISH);
        ThemeManager.installDefaults(AppTheme.CLASSIC);
        JOptionPane completed = SonicDialogs.createPane(
                "Video creado y guardado correctamente:\nC:\\Videos\\resultado.mp4",
                JOptionPane.INFORMATION_MESSAGE);
        ThemeManager.apply(AppTheme.CLASSIC, completed);
        JButton button = SonicDialogs.findButton(completed);
        assertTrue(button != null, "El diálogo no creó el botón de confirmación.");
        SonicDialogs.stylePrimaryButton(button);
        assertTrue(button.getBackground().equals(ThemeManager.palette().accent()),
                "El botón de confirmación no usa el acento púrpura.");
        assertTrue("Aceptar".equals(button.getText()),
                "El botón no se localizó al español.");
        JTextArea text = find(completed, JTextArea.class);
        assertTrue(text != null && text.getForeground().equals(
                ThemeManager.palette().foreground()),
                "El mensaje no tiene contraste correcto.");
        JOptionPane compact = SonicDialogs.createPane(
                "Primero selecciona un archivo de audio.", JOptionPane.ERROR_MESSAGE);
        JTextArea compactText = find(compact, JTextArea.class);
        assertTrue(compactText != null && compactText.getPreferredSize().width < 280
                && compactText.getPreferredSize().height < 30,
                "Un mensaje corto sigue reservando un área demasiado grande.");

        completed.setSize(430, 155);
        layoutTree(completed);
        BufferedImage image = new BufferedImage(430, 155, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        completed.paint(graphics);
        graphics.dispose();
        assertTrue(countVeryBrightPixels(image) < image.getWidth() * image.getHeight() / 5,
                "El aviso clásico conserva superficies blancas excesivas.");

        JOptionPane error = SonicDialogs.createPane("Fallo de prueba",
                JOptionPane.ERROR_MESSAGE);
        ThemeManager.apply(AppTheme.CLASSIC, error);
        JButton errorButton = SonicDialogs.findButton(error);
        assertTrue(errorButton != null, "El aviso de error no creó su botón.");

        if (args.length == 1) {
            Path output = Path.of(args[0]);
            Files.createDirectories(output);
            ImageIO.write(image, "png", output.resolve("dialogo-completado-clasico.png").toFile());
        }
        System.out.println("Diálogos de completado y error correctamente tematizados.");
    }

    private static void layoutTree(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container nested) {
                layoutTree(nested);
            }
        }
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
