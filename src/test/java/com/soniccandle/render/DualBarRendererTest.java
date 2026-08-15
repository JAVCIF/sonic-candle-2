package com.soniccandle.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.imageio.ImageIO;

/** Regresiones de geometría, alcance e inversión del compositor de doble barra. */
public final class DualBarRendererTest {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 360;
    private static final Color BAR_COLOR = new Color(255, 35, 174);

    private DualBarRendererTest() {
    }

    public static void main(String[] args) throws Exception {
        testEveryStyleAndLayout();
        testReachNeverCrosses();
        testIndependentInversion();
        testVisibilityAndRecentering();
        testDottedRestAtTinyEnergy();
        testLinearInversion();
        if (args.length == 1) {
            writeReferenceImages(Path.of(args[0]));
        }
        System.out.println("Doble barra correcta: alcances, composiciones e inversión independiente.");
    }

    private static void testEveryStyleAndLayout() {
        float[] spectrum = sampleSpectrum();
        for (DualBarLayout layout : DualBarLayout.values()) {
            for (BarStyle style : BarStyle.values()) {
                BufferedImage image = render(spectrum, style, layout,
                        DualBarReach.MEDIUM, false, false);
                assertTrue(countBarPixels(image, 0, WIDTH, 0, HEIGHT) > 500,
                        "El estilo no se ve en " + layout + ": " + style);
            }
        }
    }

    private static void testReachNeverCrosses() {
        float[] oversized = new float[64];
        Arrays.fill(oversized, 25f);
        for (DualBarLayout layout : DualBarLayout.values()) {
            for (BarStyle style : BarStyle.values()) {
                BufferedImage high = render(oversized, style, layout,
                        DualBarReach.HIGH, false, false);
                if (layout == DualBarLayout.EDGES) {
                    assertTrue(countBarPixels(high, 0, WIDTH, HEIGHT / 2 - 3,
                            HEIGHT / 2 + 4) == 0,
                            "Golpe alto permitió que las barras se tocaran: " + style);
                } else {
                    assertTrue(countBarPixels(high, 0, WIDTH, 0, 3) == 0
                            && countBarPixels(high, 0, WIDTH, HEIGHT - 3, HEIGHT) == 0,
                            "Mitades unidas salió del marco: " + style);
                }
            }
        }

        int low = verticalSpan(render(oversized, BarStyle.THIN,
                DualBarLayout.EDGES, DualBarReach.LOW, false, false), true);
        int medium = verticalSpan(render(oversized, BarStyle.THIN,
                DualBarLayout.EDGES, DualBarReach.MEDIUM, false, false), true);
        int high = verticalSpan(render(oversized, BarStyle.THIN,
                DualBarLayout.EDGES, DualBarReach.HIGH, false, false), true);
        assertTrue(low < medium && medium < high,
                "Los tres alcances no producen zonas crecientes.");
    }

    private static void testIndependentInversion() {
        float[] impulse = new float[32];
        impulse[0] = 1.5f;
        BufferedImage image = render(impulse, BarStyle.THIN,
                DualBarLayout.EDGES, DualBarReach.MEDIUM, false, true);
        int topLeft = countBarPixels(image, 0, WIDTH / 3, 0, HEIGHT / 2);
        int topRight = countBarPixels(image, WIDTH * 2 / 3, WIDTH, 0, HEIGHT / 2);
        int bottomLeft = countBarPixels(image, 0, WIDTH / 3, HEIGHT / 2, HEIGHT);
        int bottomRight = countBarPixels(image, WIDTH * 2 / 3, WIDTH, HEIGHT / 2, HEIGHT);
        assertTrue(topLeft > topRight * 5,
                "La barra superior perdió su orden normal.");
        assertTrue(bottomRight > bottomLeft * 5,
                "La barra inferior no se invirtió de forma independiente.");
    }

    private static void testLinearInversion() {
        float[] impulse = new float[32];
        impulse[0] = 1.2f;
        BufferedImage normal = renderLinear(impulse, false);
        BufferedImage reverse = renderLinear(impulse, true);
        assertTrue(countBarPixels(normal, 0, WIDTH / 3, 0, HEIGHT)
                > countBarPixels(normal, WIDTH * 2 / 3, WIDTH, 0, HEIGHT) * 5,
                "La barra lineal normal cambió de orientación.");
        assertTrue(countBarPixels(reverse, WIDTH * 2 / 3, WIDTH, 0, HEIGHT)
                > countBarPixels(reverse, 0, WIDTH / 3, 0, HEIGHT) * 5,
                "La inversión lineal no movió el espectro a la derecha.");
    }

    private static void testDottedRestAtTinyEnergy() {
        float[] almostSilent = new float[64];
        java.util.Arrays.fill(almostSilent, 0.000_001f);
        for (BarStyle style : BarStyle.values()) {
            RenderConfig config = new RenderConfig(WIDTH, HEIGHT, 30, BAR_COLOR,
                    new Color(17, 20, 30), null, style, 1.0f,
                    RestingLineMode.DOTTED, PeakMode.SOFT_LIMIT,
                    VisualizationMode.DUAL_BAR, CircularConfig.defaults(), false,
                    new DualBarConfig(DualBarLayout.EDGES, DualBarReach.MEDIUM,
                            false, false, DualBarVisibility.BOTH));
            BufferedImage image = new FrameRenderer(config).render(almostSilent);
            assertTrue(countBarPixels(image, 0, WIDTH, 0, 7) > 20,
                    "El punteado superior desapareció con energía mínima: " + style);
            assertTrue(countBarPixels(image, 0, WIDTH, HEIGHT - 7, HEIGHT) > 20,
                    "El punteado inferior desapareció con energía mínima: " + style);
        }
    }

    private static void testVisibilityAndRecentering() {
        float[] spectrum = sampleSpectrum();
        BufferedImage topEdge = render(spectrum, BarStyle.THIN,
                DualBarLayout.EDGES, DualBarReach.LOW, false, false,
                DualBarVisibility.TOP_ONLY);
        assertTrue(countBarPixels(topEdge, 0, WIDTH, HEIGHT / 2, HEIGHT) == 0,
                "Solo superior dibujó una frecuencia inferior.");

        BufferedImage bottomEdge = render(spectrum, BarStyle.THIN,
                DualBarLayout.EDGES, DualBarReach.LOW, false, false,
                DualBarVisibility.BOTTOM_ONLY);
        assertTrue(countBarPixels(bottomEdge, 0, WIDTH, 0, HEIGHT / 2) == 0,
                "Solo inferior dibujó una frecuencia superior.");

        BufferedImage topJoined = render(spectrum, BarStyle.THIN,
                DualBarLayout.JOINED_CENTER, DualBarReach.MEDIUM, false, false,
                DualBarVisibility.TOP_ONLY);
        assertTrue(countBarPixels(topJoined, 0, WIDTH,
                HEIGHT / 2 - 2, HEIGHT / 2 + 2) > 0,
                "La mitad superior única no se centró.");
        assertTrue(countBarPixels(topJoined, 0, WIDTH, HEIGHT / 2 + 4, HEIGHT) == 0,
                "La mitad superior centrada creció hacia abajo.");

        BufferedImage bottomJoined = render(spectrum, BarStyle.FLUID_HALO,
                DualBarLayout.JOINED_CENTER, DualBarReach.MEDIUM, false, false,
                DualBarVisibility.BOTTOM_ONLY);
        assertTrue(countBarPixels(bottomJoined, 0, WIDTH, HEIGHT / 2, HEIGHT / 2 + 6) > 0,
                "La mitad inferior única no se centró.");
        assertTrue(countBarPixels(bottomJoined, 0, WIDTH, 0, HEIGHT / 2 - 4) == 0,
                "La mitad inferior centrada creció hacia arriba.");
    }

    private static BufferedImage render(float[] spectrum, BarStyle style,
            DualBarLayout layout, DualBarReach reach,
            boolean reverseTop, boolean reverseBottom) {
        return render(spectrum, style, layout, reach, reverseTop, reverseBottom,
                DualBarVisibility.BOTH);
    }

    private static BufferedImage render(float[] spectrum, BarStyle style,
            DualBarLayout layout, DualBarReach reach,
            boolean reverseTop, boolean reverseBottom, DualBarVisibility visibility) {
        RenderConfig config = new RenderConfig(WIDTH, HEIGHT, 30, BAR_COLOR,
                Color.BLACK, null, style, 1.0f, RestingLineMode.INVISIBLE,
                PeakMode.SOFT_LIMIT, VisualizationMode.DUAL_BAR,
                CircularConfig.defaults(), false,
                new DualBarConfig(layout, reach, reverseTop, reverseBottom, visibility));
        return new FrameRenderer(config).render(spectrum);
    }

    private static BufferedImage renderLinear(float[] spectrum, boolean reverse) {
        RenderConfig config = new RenderConfig(WIDTH, HEIGHT, 30, BAR_COLOR,
                Color.BLACK, null, BarStyle.THIN, 1.0f,
                RestingLineMode.INVISIBLE, PeakMode.SOFT_LIMIT,
                VisualizationMode.LINEAR, CircularConfig.defaults(), reverse,
                DualBarConfig.defaults());
        return new FrameRenderer(config).render(spectrum);
    }

    private static void writeReferenceImages(Path directory) throws Exception {
        Files.createDirectories(directory);
        float[] spectrum = sampleSpectrum();
        for (DualBarReach reach : DualBarReach.values()) {
            ImageIO.write(render(spectrum, BarStyle.THIN, DualBarLayout.EDGES,
                    reach, false, true), "png",
                    directory.resolve("doble-bordes-" + reach.name().toLowerCase() + ".png").toFile());
        }
        ImageIO.write(render(spectrum, BarStyle.THIN, DualBarLayout.JOINED_CENTER,
                DualBarReach.MEDIUM, true, false), "png",
                directory.resolve("doble-mitades-unidas.png").toFile());
        ImageIO.write(render(spectrum, BarStyle.FLUID_HALO, DualBarLayout.EDGES,
                DualBarReach.MEDIUM, false, false), "png",
                directory.resolve("doble-halo.png").toFile());
        RenderConfig dotted = new RenderConfig(WIDTH, HEIGHT, 30, BAR_COLOR,
                new Color(17, 20, 30), null, BarStyle.ROUND_FILLED, 1.0f,
                RestingLineMode.DOTTED, PeakMode.SOFT_LIMIT,
                VisualizationMode.DUAL_BAR, CircularConfig.defaults(), false,
                new DualBarConfig(DualBarLayout.EDGES, DualBarReach.MEDIUM,
                        false, false, DualBarVisibility.BOTH));
        ImageIO.write(new FrameRenderer(dotted).render(new float[64]), "png",
                directory.resolve("doble-bordes-punteada-reposo.png").toFile());
    }

    private static float[] sampleSpectrum() {
        float[] spectrum = new float[64];
        for (int index = 0; index < spectrum.length; index++) {
            double pulse = 0.12 + Math.pow(Math.sin(index * 0.31), 4.0) * 0.78;
            double envelope = 0.72 + Math.sin(index * 0.10) * 0.22;
            spectrum[index] = (float) (pulse * envelope);
        }
        spectrum[4] = 1.45f;
        spectrum[42] = 1.20f;
        return spectrum;
    }

    private static int verticalSpan(BufferedImage image, boolean topHalf) {
        int start = topHalf ? 0 : HEIGHT / 2;
        int end = topHalf ? HEIGHT / 2 : HEIGHT;
        int first = -1;
        int last = -1;
        for (int y = start; y < end; y++) {
            if (countBarPixels(image, 0, WIDTH, y, y + 1) > 0) {
                if (first < 0) {
                    first = y;
                }
                last = y;
            }
        }
        return first < 0 ? 0 : last - first + 1;
    }

    private static int countBarPixels(BufferedImage image,
            int x0, int x1, int y0, int y1) {
        int count = 0;
        for (int y = Math.max(0, y0); y < Math.min(image.getHeight(), y1); y++) {
            for (int x = Math.max(0, x0); x < Math.min(image.getWidth(), x1); x++) {
                Color pixel = new Color(image.getRGB(x, y));
                if (pixel.getRed() > 180 && pixel.getBlue() > 100) {
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
