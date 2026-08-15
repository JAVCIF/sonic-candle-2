package com.soniccandle.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.imageio.ImageIO;

/** Pruebas de orientación, posición, inversión y duplicación de Barra de carga. */
public final class LoadBarRendererTest {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 360;
    private static final Color FILL = new Color(255, 35, 174);
    private static final Color BORDER = new Color(235, 245, 255);

    private LoadBarRendererTest() {
    }

    public static void main(String[] args) throws Exception {
        testEveryArrangement();
        testEveryStyleCombination();
        testHorizontalInversion();
        testVerticalInversion();
        testAutomaticOppositeSides();
        testSafeMargins();
        if (args.length == 1) {
            writeReferences(Path.of(args[0]));
        }
        System.out.println("Barra de carga correcta: posiciones, inversión, bordes y duplicado.");
    }

    private static void testEveryStyleCombination() {
        for (LoadBarFillStyle fillStyle : LoadBarFillStyle.values()) {
            for (LoadBarBorderStyle borderStyle : LoadBarBorderStyle.values()) {
                LoadBarConfig load = styledConfig(fillStyle, borderStyle);
                BufferedImage silent = render(new float[64], load);
                BufferedImage active = render(activeSpectrum(), load);
                assertTrue(countChangedPixels(silent, active) > 350,
                        "El relleno no se distingue: " + fillStyle + " / " + borderStyle);
                assertTrue(countVisiblePixels(silent) > 500,
                        "El borde no se dibujó: " + fillStyle + " / " + borderStyle);
            }
        }
    }

    private static void testEveryArrangement() {
        float[] spectrum = activeSpectrum();
        for (LoadBarShape shape : LoadBarShape.values()) {
            for (HorizontalPlacement placement : HorizontalPlacement.values()) {
                BufferedImage image = render(spectrum, new LoadBarConfig(1,
                        LoadBarOrientation.HORIZONTAL, placement,
                        VerticalPlacement.LEFT, false, shape, BORDER));
                assertTrue(countBorder(image) > 500 && countFill(image) > 500,
                        "La barra horizontal no se dibujó: " + shape + " / " + placement);
            }
            for (VerticalPlacement placement : VerticalPlacement.values()) {
                BufferedImage image = render(spectrum, new LoadBarConfig(1,
                        LoadBarOrientation.VERTICAL, HorizontalPlacement.CENTER,
                        placement, false, shape, BORDER));
                assertTrue(countBorder(image) > 300 && countFill(image) > 300,
                        "La barra vertical no se dibujó: " + shape + " / " + placement);
            }
        }
    }

    private static void testHorizontalInversion() {
        LoadBarConfig normal = new LoadBarConfig(1, LoadBarOrientation.HORIZONTAL,
                HorizontalPlacement.CENTER, VerticalPlacement.LEFT,
                false, LoadBarShape.SQUARE, BORDER);
        LoadBarConfig reverse = new LoadBarConfig(1, LoadBarOrientation.HORIZONTAL,
                HorizontalPlacement.CENTER, VerticalPlacement.LEFT,
                true, LoadBarShape.SQUARE, BORDER);
        double normalCenter = fillCenterX(render(activeSpectrum(), normal));
        double reverseCenter = fillCenterX(render(activeSpectrum(), reverse));
        assertTrue(normalCenter < WIDTH / 2.0 && reverseCenter > WIDTH / 2.0,
                "La inversión horizontal no cambió el lado de llenado.");
    }

    private static void testVerticalInversion() {
        LoadBarConfig normal = new LoadBarConfig(1, LoadBarOrientation.VERTICAL,
                HorizontalPlacement.CENTER, VerticalPlacement.CENTER,
                false, LoadBarShape.ROUNDED, BORDER);
        LoadBarConfig reverse = new LoadBarConfig(1, LoadBarOrientation.VERTICAL,
                HorizontalPlacement.CENTER, VerticalPlacement.CENTER,
                true, LoadBarShape.ROUNDED, BORDER);
        double normalCenter = fillCenterY(render(activeSpectrum(), normal));
        double reverseCenter = fillCenterY(render(activeSpectrum(), reverse));
        assertTrue(normalCenter > HEIGHT / 2.0 && reverseCenter < HEIGHT / 2.0,
                "La inversión vertical no cambió el sentido abajo/arriba.");
    }

    private static void testAutomaticOppositeSides() {
        BufferedImage horizontal = render(activeSpectrum(), new LoadBarConfig(2,
                LoadBarOrientation.HORIZONTAL, HorizontalPlacement.CENTER,
                VerticalPlacement.CENTER, false, LoadBarShape.ROUNDED, BORDER));
        assertTrue(countBorder(horizontal, 0, HEIGHT / 2) > 300
                && countBorder(horizontal, HEIGHT / 2, HEIGHT) > 300,
                "Las dos barras horizontales no quedaron arriba y abajo.");

        BufferedImage vertical = render(activeSpectrum(), new LoadBarConfig(2,
                LoadBarOrientation.VERTICAL, HorizontalPlacement.CENTER,
                VerticalPlacement.CENTER, false, LoadBarShape.ROUNDED, BORDER));
        assertTrue(countBorder(vertical, 0, WIDTH / 2, true) > 300
                && countBorder(vertical, WIDTH / 2, WIDTH, true) > 300,
                "Las dos barras verticales no quedaron a izquierda y derecha.");
    }

    private static void testSafeMargins() {
        for (LoadBarOrientation orientation : LoadBarOrientation.values()) {
            BufferedImage image = render(activeSpectrum(), new LoadBarConfig(2,
                    orientation, HorizontalPlacement.TOP, VerticalPlacement.LEFT,
                    false, LoadBarShape.SQUARE, BORDER));
            assertTrue(countNonBackgroundBorder(image) == 0,
                    "Una barra de carga quedó pegada al marco del video.");
        }
    }

    private static BufferedImage render(float[] spectrum, LoadBarConfig load) {
        RenderConfig config = new RenderConfig(WIDTH, HEIGHT, 30, FILL,
                Color.BLACK, null, BarStyle.ROUND_FILLED, 1.0f,
                RestingLineMode.INVISIBLE, PeakMode.SOFT_LIMIT,
                VisualizationMode.LOAD_BAR, CircularConfig.defaults(), false,
                DualBarConfig.defaults(), load, IntroAnimationConfig.disabled());
        return new FrameRenderer(config).render(spectrum);
    }

    private static float[] activeSpectrum() {
        float[] spectrum = new float[64];
        Arrays.fill(spectrum, 0.52f);
        spectrum[8] = 0.82f;
        return spectrum;
    }

    private static void writeReferences(Path directory) throws Exception {
        Files.createDirectories(directory);
        ImageIO.write(render(activeSpectrum(), new LoadBarConfig(1,
                LoadBarOrientation.HORIZONTAL, HorizontalPlacement.CENTER,
                VerticalPlacement.LEFT, false, LoadBarShape.ROUNDED, BORDER)),
                "png", directory.resolve("carga-horizontal.png").toFile());
        ImageIO.write(render(activeSpectrum(), new LoadBarConfig(2,
                LoadBarOrientation.VERTICAL, HorizontalPlacement.CENTER,
                VerticalPlacement.CENTER, false, LoadBarShape.SQUARE, BORDER)),
                "png", directory.resolve("carga-vertical-doble.png").toFile());
        ImageIO.write(render(activeSpectrum(), new LoadBarConfig(2,
                LoadBarOrientation.HORIZONTAL, HorizontalPlacement.CENTER,
                VerticalPlacement.CENTER, true, LoadBarShape.ROUNDED, BORDER)),
                "png", directory.resolve("carga-horizontal-doble-invertida.png").toFile());
        for (LoadBarFillStyle fillStyle : LoadBarFillStyle.values()) {
            ImageIO.write(render(activeSpectrum(), styledConfig(fillStyle,
                    LoadBarBorderStyle.ETCHED_BLOCK)), "png",
                    directory.resolve("carga-estilo-"
                            + fillStyle.name().toLowerCase().replace('_', '-')
                            + ".png").toFile());
        }
    }

    private static LoadBarConfig styledConfig(LoadBarFillStyle fillStyle,
            LoadBarBorderStyle borderStyle) {
        return new LoadBarConfig(1, LoadBarOrientation.HORIZONTAL,
                HorizontalPlacement.CENTER, VerticalPlacement.LEFT,
                false, LoadBarShape.ROUNDED, BORDER, fillStyle, borderStyle,
                LoadBarResponseMode.NORMAL, LoadBarAnimationMode.NORMAL);
    }

    private static int countChangedPixels(BufferedImage first, BufferedImage second) {
        int count = 0;
        for (int y = 0; y < first.getHeight(); y++) {
            for (int x = 0; x < first.getWidth(); x++) {
                if (first.getRGB(x, y) != second.getRGB(x, y)) count++;
            }
        }
        return count;
    }

    private static int countVisiblePixels(BufferedImage image) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) & 0x00ffffff) != 0) count++;
            }
        }
        return count;
    }

    private static int countFill(BufferedImage image) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (isFill(image.getRGB(x, y))) count++;
            }
        }
        return count;
    }

    private static int countBorder(BufferedImage image) {
        return countBorder(image, 0, HEIGHT);
    }

    private static int countBorder(BufferedImage image, int from, int to) {
        int count = 0;
        for (int y = from; y < to; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (isBorder(image.getRGB(x, y))) count++;
            }
        }
        return count;
    }

    private static int countBorder(BufferedImage image, int from, int to, boolean xRange) {
        int count = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = from; x < to; x++) {
                if (isBorder(image.getRGB(x, y))) count++;
            }
        }
        return count;
    }

    private static double fillCenterX(BufferedImage image) {
        long sum = 0;
        int count = 0;
        for (int y = 0; y < HEIGHT; y++) for (int x = 0; x < WIDTH; x++) {
            if (isFill(image.getRGB(x, y))) { sum += x; count++; }
        }
        return sum / (double) Math.max(1, count);
    }

    private static double fillCenterY(BufferedImage image) {
        long sum = 0;
        int count = 0;
        for (int y = 0; y < HEIGHT; y++) for (int x = 0; x < WIDTH; x++) {
            if (isFill(image.getRGB(x, y))) { sum += y; count++; }
        }
        return sum / (double) Math.max(1, count);
    }

    private static int countNonBackgroundBorder(BufferedImage image) {
        int count = 0;
        for (int x = 0; x < WIDTH; x++) {
            if (isFill(image.getRGB(x, 0)) || isBorder(image.getRGB(x, 0))) count++;
            if (isFill(image.getRGB(x, HEIGHT - 1)) || isBorder(image.getRGB(x, HEIGHT - 1))) count++;
        }
        for (int y = 0; y < HEIGHT; y++) {
            if (isFill(image.getRGB(0, y)) || isBorder(image.getRGB(0, y))) count++;
            if (isFill(image.getRGB(WIDTH - 1, y)) || isBorder(image.getRGB(WIDTH - 1, y))) count++;
        }
        return count;
    }

    private static boolean isFill(int rgb) {
        Color pixel = new Color(rgb);
        return pixel.getRed() > 190 && pixel.getBlue() > 100
                && pixel.getGreen() < 130;
    }

    private static boolean isBorder(int rgb) {
        Color pixel = new Color(rgb);
        return pixel.getRed() > 180 && pixel.getGreen() > 180
                && pixel.getBlue() > 180;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
