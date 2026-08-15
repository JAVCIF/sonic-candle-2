package com.soniccandle.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** Valida el lienzo alfa y las reglas especiales del relleno circular. */
public final class TransparentRendererTest {

    private TransparentRendererTest() {
    }

    public static void main(String[] args) {
        BufferedImage background = solidImage(80, 45, new Color(230, 25, 35));
        float[] silence = new float[24];
        RenderConfig linear = config(VisualizationMode.LINEAR,
                CircularConfig.defaults(), background);

        BufferedImage transparent = new FrameRenderer(linear, true).render(silence);
        assertTrue(alphaAt(transparent, 0, 0) == 0,
                "La exportación alfa conservó la imagen de fondo global.");
        assertTrue(maxAlpha(transparent) == 0,
                "Un visualizador silencioso e invisible dejó píxeles opacos.");
        BufferedImage complete = new FrameRenderer(linear).render(silence);
        assertTrue(alphaAt(complete, 0, 0) == 255,
                "El MP4 perdió su fondo completo.");

        CircularConfig solidCircle = new CircularConfig(1, CircleAlignment.CENTER,
                55, 0, CircleFillMode.SOLID_COLOR, new Color(30, 190, 110),
                null, 100, 0, 0);
        BufferedImage solid = new FrameRenderer(config(VisualizationMode.CIRCULAR,
                solidCircle, background), true).render(silence);
        assertTrue(alphaAt(solid, solid.getWidth() / 2, solid.getHeight() / 2) == 255,
                "El relleno sólido del círculo no sobrevivió al lienzo alfa.");
        assertTrue(alphaAt(solid, 0, 0) == 0,
                "El fondo exterior del círculo no es transparente.");

        CircularConfig transparentCircle = new CircularConfig(1, CircleAlignment.CENTER,
                55, 0, CircleFillMode.TRANSPARENT, Color.BLACK,
                null, 100, 0, 0);
        BufferedImage hollow = new FrameRenderer(config(VisualizationMode.CIRCULAR,
                transparentCircle, background), true).render(silence);
        assertTrue(maxAlpha(hollow) == 0,
                "El círculo configurado como transparente recibió relleno.");

        BufferedImage innerImage = solidImage(40, 80, new Color(45, 95, 220));
        CircularConfig imageCircle = new CircularConfig(1, CircleAlignment.CENTER,
                55, 0, CircleFillMode.IMAGE, Color.BLACK,
                innerImage, 100, 0, 0);
        BufferedImage withImage = new FrameRenderer(config(VisualizationMode.CIRCULAR,
                imageCircle, background), true).render(silence);
        int center = withImage.getRGB(withImage.getWidth() / 2, withImage.getHeight() / 2);
        assertTrue((center >>> 24) == 255 && (center & 255) > 180,
                "La imagen interior circular no se conservó.");
        System.out.println("Transparencia correcta: fondo omitido y relleno circular preservado.");
    }

    private static RenderConfig config(VisualizationMode mode,
            CircularConfig circular, BufferedImage background) {
        return new RenderConfig(320, 180, 30, new Color(180, 90, 255),
                new Color(28, 18, 51), background, BarStyle.ROUND_FILLED,
                1f, RestingLineMode.INVISIBLE, PeakMode.SOFT_LIMIT,
                mode, circular);
    }

    private static BufferedImage solidImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();
        return image;
    }

    private static int alphaAt(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) >>> 24;
    }

    private static int maxAlpha(BufferedImage image) {
        int maximum = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                maximum = Math.max(maximum, alphaAt(image, x, y));
            }
        }
        return maximum;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
