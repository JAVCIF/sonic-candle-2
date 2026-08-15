package com.soniccandle.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Renderiza todos los estilos lineales para detectar regresiones. */
public final class BarStyleRenderTest {

    private BarStyleRenderTest() {
    }

    public static void main(String[] args) throws Exception {
        float[] spectrum = new float[48];
        for (int index = 0; index < spectrum.length; index++) {
            spectrum[index] = 0.15f + (float) Math.abs(Math.sin(index * 0.31)) * 0.95f;
        }
        Path outputDirectory = args.length == 1 ? Path.of(args[0]) : null;
        if (outputDirectory != null) {
            Files.createDirectories(outputDirectory);
        }

        for (BarStyle style : BarStyle.values()) {
            RenderConfig config = new RenderConfig(
                    640, 360, 30, new Color(255, 30, 175), Color.BLACK,
                    null, style, 1.0f, RestingLineMode.DOTTED, PeakMode.FREE_OVERFLOW,
                    VisualizationMode.LINEAR, CircularConfig.defaults());
            BufferedImage image = new FrameRenderer(config).render(spectrum);
            if (!containsBarPixels(image)) {
                throw new AssertionError("El estilo no dibujó barras: " + style);
            }
            if (outputDirectory != null) {
                ImageIO.write(image, "png", outputDirectory.resolve(style.name() + ".png").toFile());
            }
        }
        System.out.println("Estilos renderizados correctamente: " + BarStyle.values().length);
    }

    private static boolean containsBarPixels(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color pixel = new Color(image.getRGB(x, y));
                if (pixel.getRed() > 180 && pixel.getBlue() > 100) {
                    return true;
                }
            }
        }
        return false;
    }
}
