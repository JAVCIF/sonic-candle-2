package com.soniccandle.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Verifica línea en reposo y contención suave de picos. */
public final class VisualOptionsTest {

    private static final Color BAR_COLOR = new Color(255, 30, 175);

    private VisualOptionsTest() {
    }

    public static void main(String[] args) throws Exception {
        testRestingLine();
        testSoftLimiterCurve();
        testContainedRendering();
        if (args.length == 1) {
            writeReferenceImages(Path.of(args[0]));
        }
        System.out.println("Opciones visuales correctas: reposo y picos contenidos.");
    }

    private static void writeReferenceImages(Path outputDirectory) throws Exception {
        Files.createDirectories(outputDirectory);
        float[] silence = new float[32];
        float[] peaks = new float[32];
        for (int index = 0; index < peaks.length; index++) {
            peaks[index] = 0.20f + (float) Math.abs(Math.sin(index * 0.42)) * 2.20f;
        }
        ImageIO.write(render(silence, BarStyle.ROUND_FILLED,
                RestingLineMode.INVISIBLE, PeakMode.FREE_OVERFLOW), "png",
                outputDirectory.resolve("reposo-invisible.png").toFile());
        ImageIO.write(render(silence, BarStyle.ROUND_FILLED,
                RestingLineMode.DOTTED, PeakMode.FREE_OVERFLOW), "png",
                outputDirectory.resolve("reposo-punteado.png").toFile());
        ImageIO.write(render(peaks, BarStyle.ROUND_FILLED,
                RestingLineMode.INVISIBLE, PeakMode.FREE_OVERFLOW), "png",
                outputDirectory.resolve("picos-desborde.png").toFile());
        ImageIO.write(render(peaks, BarStyle.ROUND_FILLED,
                RestingLineMode.INVISIBLE, PeakMode.SOFT_LIMIT), "png",
                outputDirectory.resolve("picos-normalizados.png").toFile());
    }

    private static void testRestingLine() {
        float[] silence = new float[32];
        BufferedImage invisible = render(silence, BarStyle.ROUND_FILLED,
                RestingLineMode.INVISIBLE, PeakMode.FREE_OVERFLOW);
        BufferedImage dotted = render(silence, BarStyle.ROUND_FILLED,
                RestingLineMode.DOTTED, PeakMode.FREE_OVERFLOW);
        if (countBarPixels(invisible) != 0) {
            throw new AssertionError("La línea invisible dejó píxeles de barras.");
        }
        if (countBarPixels(dotted) == 0) {
            throw new AssertionError("La línea punteada no apareció.");
        }
    }

    private static void testSoftLimiterCurve() {
        float low = FrameRenderer.softLimitAmplitude(0.50f, 0.98f);
        float one = FrameRenderer.softLimitAmplitude(1.00f, 0.98f);
        float two = FrameRenderer.softLimitAmplitude(2.00f, 0.98f);
        float huge = FrameRenderer.softLimitAmplitude(100.0f, 0.98f);
        if (Math.abs(low - 0.50f) > 0.0001f || !(low < one && one < two && two < huge && huge <= 0.98f)) {
            throw new AssertionError("La curva de picos no es suave y monótona: "
                    + low + ", " + one + ", " + two + ", " + huge);
        }
    }

    private static void testContainedRendering() {
        float[] extreme = new float[16];
        java.util.Arrays.fill(extreme, 20f);
        for (BarStyle style : BarStyle.values()) {
            BufferedImage image = render(extreme, style,
                    RestingLineMode.INVISIBLE, PeakMode.SOFT_LIMIT);
            for (int x = 0; x < image.getWidth(); x++) {
                if (isBarPixel(image.getRGB(x, 0))
                        || isBarPixel(image.getRGB(x, image.getHeight() - 1))) {
                    throw new AssertionError("El estilo normalizado tocó el borde: " + style);
                }
            }
        }
    }

    private static BufferedImage render(float[] spectrum, BarStyle style,
            RestingLineMode restingLineMode, PeakMode peakMode) {
        RenderConfig config = new RenderConfig(
                640, 360, 30, BAR_COLOR, Color.BLACK, null, style, 1.0f,
                restingLineMode, peakMode,
                VisualizationMode.LINEAR, CircularConfig.defaults());
        return new FrameRenderer(config).render(spectrum);
    }

    private static int countBarPixels(BufferedImage image) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (isBarPixel(image.getRGB(x, y))) {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean isBarPixel(int rgb) {
        Color pixel = new Color(rgb);
        return pixel.getRed() > 180 && pixel.getBlue() > 100;
    }
}
