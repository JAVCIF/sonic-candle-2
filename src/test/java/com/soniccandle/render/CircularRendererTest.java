package com.soniccandle.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;

/** Pruebas de geometría, relleno, recorte y halo del compositor circular. */
public final class CircularRendererTest {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 360;
    private static final Color BAR_COLOR = new Color(255, 35, 174);

    private CircularRendererTest() {
    }

    public static void main(String[] args) throws Exception {
        testSafeLayouts();
        testInteriorModes();
        testRadialStyles();
        testPeakModes();
        if (args.length == 1) {
            writeReferenceImages(Path.of(args[0]));
        }
        System.out.println("Compositor circular correcto: geometría, rellenos, recorte y halo.");
    }

    private static void testSafeLayouts() {
        for (int count = 1; count <= 2; count++) {
            for (CircleAlignment alignment : CircleAlignment.values()) {
                CircularConfig config = circular(count, alignment, 75,
                        CircleFillMode.TRANSPARENT, null, 100, 0, 0);
                List<CircleGeometry> circles = CircularLayoutCalculator.calculate(
                        WIDTH, HEIGHT, config);
                assertTrue(circles.size() == count, "Cantidad de círculos incorrecta.");
                for (CircleGeometry circle : circles) {
                    assertTrue(circle.centerY() == HEIGHT / 2.0,
                            "El círculo dejó de estar centrado verticalmente.");
                    assertTrue(circle.centerX() - circle.outerRadius() >= -0.01,
                            "El círculo salió por la izquierda.");
                    assertTrue(circle.centerX() + circle.outerRadius() <= WIDTH + 0.01,
                            "El círculo salió por la derecha.");
                    assertTrue(circle.centerY() - circle.outerRadius() >= -0.01,
                            "El círculo salió por arriba.");
                    assertTrue(circle.centerY() + circle.outerRadius() <= HEIGHT + 0.01,
                            "El círculo salió por abajo.");
                    assertTrue(circle.circleRadius() < circle.outerRadius(),
                            "El tamaño no reservó espacio para la frecuencia.");
                }
                if (count == 2) {
                    CircleGeometry left = circles.get(0);
                    CircleGeometry right = circles.get(1);
                    assertTrue(left.centerX() + left.outerRadius()
                            <= right.centerX() - right.outerRadius() + 0.01,
                            "Los dos círculos se superponen.");
                }
            }
        }
    }

    private static void testInteriorModes() {
        float[] silence = new float[64];
        CircularConfig transparent = circular(1, CircleAlignment.CENTER, 62,
                CircleFillMode.TRANSPARENT, null, 100, 0, 0);
        BufferedImage untouched = render(silence, BarStyle.ROUND_FILLED, transparent);

        Color fill = new Color(42, 188, 116);
        CircularConfig solid = new CircularConfig(1, CircleAlignment.CENTER, 62, 0,
                CircleFillMode.SOLID_COLOR, fill, null, 100, 0, 0);
        BufferedImage filled = render(silence, BarStyle.ROUND_FILLED, solid);
        int center = filled.getRGB(WIDTH / 2, HEIGHT / 2) & 0x00FFFFFF;
        assertTrue(center == (fill.getRGB() & 0x00FFFFFF),
                "El color sólido no rellenó el centro.");
        assertTrue(center != (untouched.getRGB(WIDTH / 2, HEIGHT / 2) & 0x00FFFFFF),
                "El modo transparente alteró el fondo.");

        BufferedImage sample = sampleImage();
        CircularConfig imageLeft = circular(1, CircleAlignment.CENTER, 62,
                CircleFillMode.IMAGE, sample, 150, -100, 0);
        CircularConfig imageRight = circular(1, CircleAlignment.CENTER, 62,
                CircleFillMode.IMAGE, sample, 150, 100, 0);
        BufferedImage leftCrop = render(silence, BarStyle.ROUND_FILLED, imageLeft);
        BufferedImage rightCrop = render(silence, BarStyle.ROUND_FILLED, imageRight);
        assertTrue(differentPixels(leftCrop, rightCrop) > 2_000,
                "Mover el encuadre no cambió el recorte de imagen.");

        List<CircleGeometry> geometry = CircularLayoutCalculator.calculate(WIDTH, HEIGHT, imageLeft);
        CircleGeometry circle = geometry.get(0);
        int outsideX = (int) Math.round(circle.centerX() + circle.circleRadius() + 5);
        assertTrue(leftCrop.getRGB(outsideX, (int) circle.centerY())
                == untouched.getRGB(outsideX, (int) circle.centerY()),
                "La imagen escapó del recorte circular.");
    }

    private static void testRadialStyles() {
        float[] spectrum = sampleSpectrum();
        CircularConfig circular = circular(2, CircleAlignment.CENTER, 64,
                CircleFillMode.TRANSPARENT, null, 100, 0, 0);
        BufferedImage blank = render(new float[64], BarStyle.ROUND_FILLED, circular);
        for (BarStyle style : BarStyle.values()) {
            BufferedImage rendered = render(spectrum, style, circular);
            assertTrue(differentPixels(blank, rendered) > 500,
                    "El estilo radial no produjo una forma visible: " + style);
        }
    }

    private static void testPeakModes() {
        float[] oversized = new float[64];
        java.util.Arrays.fill(oversized, 5.0f);
        CircularConfig circular = circular(1, CircleAlignment.CENTER, 62,
                CircleFillMode.TRANSPARENT, null, 100, 0, 0);
        BufferedImage contained = render(oversized, BarStyle.FLUID_HALO,
                circular, PeakMode.SOFT_LIMIT);
        BufferedImage overflow = render(oversized, BarStyle.FLUID_HALO,
                circular, PeakMode.FREE_OVERFLOW);
        BufferedImage blank = render(new float[64], BarStyle.FLUID_HALO,
                circular, PeakMode.SOFT_LIMIT);
        assertTrue(changedBorderPixels(contained, blank) == 0,
                "El halo normalizado alcanzó el borde del video.");
        assertTrue(changedBorderPixels(overflow, blank) > 100,
                "El desborde libre no pudo salir del fotograma.");
    }

    private static void writeReferenceImages(Path directory) throws Exception {
        Files.createDirectories(directory);
        float[] spectrum = sampleSpectrum();
        BufferedImage sample = sampleImage();
        ImageIO.write(render(spectrum, BarStyle.ROUND_FILLED,
                circular(1, CircleAlignment.CENTER, 64,
                        CircleFillMode.IMAGE, sample, 125, 35, -15)),
                "png", directory.resolve("circular-imagen.png").toFile());
        ImageIO.write(render(spectrum, BarStyle.THIN,
                circular(2, CircleAlignment.CENTER, 66,
                        CircleFillMode.SOLID_COLOR, null, 100, 0, 0)),
                "png", directory.resolve("circular-doble.png").toFile());
        ImageIO.write(render(spectrum, BarStyle.FLUID_HALO,
                circular(1, CircleAlignment.RIGHT, 58,
                        CircleFillMode.TRANSPARENT, null, 100, 0, 0)),
                "png", directory.resolve("circular-halo.png").toFile());
    }

    private static BufferedImage render(float[] spectrum, BarStyle style,
            CircularConfig circular) {
        return render(spectrum, style, circular, PeakMode.SOFT_LIMIT);
    }

    private static BufferedImage render(float[] spectrum, BarStyle style,
            CircularConfig circular, PeakMode peakMode) {
        RenderConfig config = new RenderConfig(WIDTH, HEIGHT, 30, BAR_COLOR,
                new Color(17, 20, 30), null, style, 1.0f,
                RestingLineMode.INVISIBLE, peakMode,
                VisualizationMode.CIRCULAR, circular);
        return new FrameRenderer(config).render(spectrum);
    }

    private static CircularConfig circular(int count, CircleAlignment alignment,
            int size, CircleFillMode fillMode, BufferedImage image,
            int zoom, int offsetX, int offsetY) {
        return new CircularConfig(count, alignment, size, 0, fillMode,
                new Color(30, 38, 58), image, zoom, offsetX, offsetY);
    }

    private static float[] sampleSpectrum() {
        float[] spectrum = new float[64];
        for (int index = 0; index < spectrum.length; index++) {
            double pulse = 0.20 + Math.pow(Math.sin(index * 0.37), 4.0) * 0.63;
            double envelope = 0.65 + Math.sin(index * 0.11) * 0.25;
            spectrum[index] = (float) (pulse * envelope);
        }
        spectrum[0] = 1.35f;
        return spectrum;
    }

    private static BufferedImage sampleImage() {
        BufferedImage image = new BufferedImage(500, 220, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(220, 55, 65));
        graphics.fillRect(0, 0, 170, image.getHeight());
        graphics.setColor(new Color(245, 202, 62));
        graphics.fillRect(170, 0, 160, image.getHeight());
        graphics.setColor(new Color(46, 132, 219));
        graphics.fillRect(330, 0, 170, image.getHeight());
        graphics.dispose();
        return image;
    }

    private static long differentPixels(BufferedImage first, BufferedImage second) {
        long count = 0;
        for (int y = 0; y < first.getHeight(); y++) {
            for (int x = 0; x < first.getWidth(); x++) {
                if (first.getRGB(x, y) != second.getRGB(x, y)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static long changedBorderPixels(BufferedImage image, BufferedImage reference) {
        long changed = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            if (image.getRGB(x, 0) != reference.getRGB(x, 0)) changed++;
            if (image.getRGB(x, image.getHeight() - 1)
                    != reference.getRGB(x, reference.getHeight() - 1)) changed++;
        }
        for (int y = 1; y < image.getHeight() - 1; y++) {
            if (image.getRGB(0, y) != reference.getRGB(0, y)) changed++;
            if (image.getRGB(image.getWidth() - 1, y)
                    != reference.getRGB(reference.getWidth() - 1, y)) changed++;
        }
        return changed;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
