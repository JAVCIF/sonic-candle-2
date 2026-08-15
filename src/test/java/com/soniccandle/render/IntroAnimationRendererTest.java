package com.soniccandle.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.imageio.ImageIO;

/** Valida el armado, parpadeo, punteado y exclusividad de la introducción. */
public final class IntroAnimationRendererTest {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 360;
    private static final Color BAR = new Color(255, 35, 174);

    private IntroAnimationRendererTest() {
    }

    public static void main(String[] args) throws Exception {
        testOutsideInAndInsideOut();
        testSpectrumWaitsForIntro();
        testFinalDottedLine();
        testDualJoinedSingleCentersLine();
        testDualEdgesRejectsIntro();
        testInvisibleRestRejectsIntro();
        if (args.length == 1) writeReferences(Path.of(args[0]));
        System.out.println("Introducción correcta: armado, parpadeo, punteado y sincronización visual.");
    }

    private static void testOutsideInAndInsideOut() {
        RenderConfig outside = linearIntro(IntroAnimationDirection.OUTSIDE_IN);
        RenderConfig inside = linearIntro(IntroAnimationDirection.INSIDE_OUT);
        BufferedImage outsideStart = new FrameRenderer(outside).render(activeSpectrum(), 0);
        BufferedImage insideStart = new FrameRenderer(inside).render(activeSpectrum(), 0);
        assertTrue(countBar(outsideStart, 0, WIDTH / 5, HEIGHT / 2 - 2, HEIGHT / 2 + 3)
                > countBar(outsideStart, WIDTH * 2 / 5, WIDTH * 3 / 5,
                        HEIGHT / 2 - 2, HEIGHT / 2 + 3),
                "Afuera hacia dentro no comenzó en los extremos.");
        assertTrue(countBar(insideStart, WIDTH * 2 / 5, WIDTH * 3 / 5,
                HEIGHT / 2 - 2, HEIGHT / 2 + 3)
                > countBar(insideStart, 0, WIDTH / 5, HEIGHT / 2 - 2, HEIGHT / 2 + 3),
                "Dentro hacia afuera no comenzó en el centro.");
    }

    private static void testSpectrumWaitsForIntro() {
        RenderConfig config = linearIntro(IntroAnimationDirection.OUTSIDE_IN);
        int introFrames = config.introFrameCount();
        BufferedImage during = new FrameRenderer(config).render(activeSpectrum(), 2);
        BufferedImage after = new FrameRenderer(config).render(activeSpectrum(), introFrames);
        assertTrue(countBar(during, 0, WIDTH, 0, HEIGHT / 2 - 5) == 0
                && countBar(during, 0, WIDTH, HEIGHT / 2 + 6, HEIGHT) == 0,
                "La frecuencia apareció antes de terminar la intro.");
        assertTrue(countBar(after, 0, WIDTH, 0, HEIGHT / 2 - 20) > 100,
                "La frecuencia no comenzó al terminar la intro.");
    }

    private static void testFinalDottedLine() {
        RenderConfig config = linearIntro(IntroAnimationDirection.OUTSIDE_IN);
        int settleFrame = Math.max(0, (int) Math.floor(config.introFrameCount() * 0.95) - 1);
        BufferedImage settled = new FrameRenderer(config).render(activeSpectrum(), settleFrame);
        int left = countBar(settled, 0, WIDTH / 4, HEIGHT / 2 - 2, HEIGHT / 2 + 3);
        int right = countBar(settled, WIDTH * 3 / 4, WIDTH,
                HEIGHT / 2 - 2, HEIGHT / 2 + 3);
        int total = countBar(settled, 0, WIDTH, HEIGHT / 2 - 2, HEIGHT / 2 + 3);
        assertTrue(left > 10 && right > 10 && total < WIDTH * 4,
                "La intro no terminó como línea punteada completa.");
    }

    private static void testDualJoinedSingleCentersLine() {
        RenderConfig config = dualIntro(DualBarLayout.JOINED_CENTER,
                DualBarVisibility.BOTTOM_ONLY);
        BufferedImage image = new FrameRenderer(config).render(activeSpectrum(), 0);
        assertTrue(countBar(image, 0, WIDTH, HEIGHT / 2 - 2, HEIGHT / 2 + 3) > 10,
                "La intro de una sola mitad no quedó centrada.");
        assertTrue(config.introAnimationApplies(),
                "Mitades unidas no aceptó la introducción.");
    }

    private static void testDualEdgesRejectsIntro() {
        RenderConfig config = dualIntro(DualBarLayout.EDGES, DualBarVisibility.BOTH);
        BufferedImage image = new FrameRenderer(config).render(activeSpectrum(), 0);
        assertTrue(!config.introAnimationApplies() && config.introFrameCount() == 0,
                "La introducción se activó fuera de Mitades unidas.");
        assertTrue(countBar(image, 0, WIDTH, 0, HEIGHT / 3) > 100,
                "El modo Bordes bloqueó la frecuencia por una intro no aplicable.");
    }

    private static void testInvisibleRestRejectsIntro() {
        RenderConfig config = new RenderConfig(WIDTH, HEIGHT, 30, BAR,
                Color.BLACK, null, BarStyle.THIN, 1.0f,
                RestingLineMode.INVISIBLE, PeakMode.SOFT_LIMIT,
                VisualizationMode.LINEAR, CircularConfig.defaults(), false,
                DualBarConfig.defaults(), LoadBarConfig.defaults(),
                new IntroAnimationConfig(IntroAnimationMode.SYNCHRONIZED,
                        IntroAnimationDirection.OUTSIDE_IN, 1_000));
        assertTrue(!config.introAnimationApplies() && config.introFrameCount() == 0,
                "La línea invisible permitió activar la introducción.");
    }

    private static RenderConfig linearIntro(IntroAnimationDirection direction) {
        return config(VisualizationMode.LINEAR, DualBarConfig.defaults(), direction);
    }

    private static RenderConfig dualIntro(DualBarLayout layout, DualBarVisibility visibility) {
        return config(VisualizationMode.DUAL_BAR,
                new DualBarConfig(layout, DualBarReach.MEDIUM,
                        false, false, visibility),
                IntroAnimationDirection.OUTSIDE_IN);
    }

    private static RenderConfig config(VisualizationMode mode, DualBarConfig dual,
            IntroAnimationDirection direction) {
        return new RenderConfig(WIDTH, HEIGHT, 30, BAR, Color.BLACK, null,
                BarStyle.THIN, 1.0f, RestingLineMode.DOTTED,
                PeakMode.SOFT_LIMIT, mode, CircularConfig.defaults(), false, dual,
                LoadBarConfig.defaults(), new IntroAnimationConfig(
                        IntroAnimationMode.SYNCHRONIZED, direction, 1_000));
    }

    private static float[] activeSpectrum() {
        float[] spectrum = new float[64];
        Arrays.fill(spectrum, 0.65f);
        return spectrum;
    }

    private static void writeReferences(Path directory) throws Exception {
        Files.createDirectories(directory);
        RenderConfig outside = linearIntro(IntroAnimationDirection.OUTSIDE_IN);
        FrameRenderer renderer = new FrameRenderer(outside);
        ImageIO.write(renderer.render(activeSpectrum(), 0), "png",
                directory.resolve("intro-afuera-inicio.png").toFile());
        ImageIO.write(renderer.render(activeSpectrum(), outside.introFrameCount() / 2), "png",
                directory.resolve("intro-afuera-mitad.png").toFile());
        ImageIO.write(renderer.render(activeSpectrum(),
                Math.max(0, (int) (outside.introFrameCount() * 0.95) - 1)), "png",
                directory.resolve("intro-punteada.png").toFile());
        RenderConfig joined = dualIntro(DualBarLayout.JOINED_CENTER,
                DualBarVisibility.BOTH);
        ImageIO.write(new FrameRenderer(joined).render(activeSpectrum(),
                Math.max(0, joined.introFrameCount() / 2)), "png",
                directory.resolve("intro-doble-unida.png").toFile());
    }

    private static int countBar(BufferedImage image,
            int x0, int x1, int y0, int y1) {
        int count = 0;
        for (int y = Math.max(0, y0); y < Math.min(HEIGHT, y1); y++) {
            for (int x = Math.max(0, x0); x < Math.min(WIDTH, x1); x++) {
                Color pixel = new Color(image.getRGB(x, y));
                if (pixel.getRed() > 180 && pixel.getBlue() > 100
                        && pixel.getGreen() < 150) count++;
            }
        }
        return count;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
