package com.soniccandle.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public final class CardiogramRendererTest {

    private CardiogramRendererTest() {
    }

    public static void main(String[] args) throws Exception {
        allStylesRenderAndStayContained();
        reverseMirrorsTheSweep();
        writeReference();
        System.out.println("CardiogramRendererTest OK");
    }

    private static void allStylesRenderAndStayContained() {
        float[] signal = sampleSignal();
        for (CardiogramStyle style : CardiogramStyle.values()) {
            BufferedImage image = renderer(style, false).render(new float[32],
                    90, Float.NaN, signal, 90);
            int colored = coloredPixels(image);
            check(colored > 250, style + " did not render a visible trace");
            check(!edgeHasColor(image), style + " escaped the safe video area");
        }
    }

    private static void reverseMirrorsTheSweep() {
        float[] signal = new float[160];
        signal[80] = 1f;
        BufferedImage normal = renderer(CardiogramStyle.THIN, false)
                .render(new float[32], 100, Float.NaN, signal, 100);
        BufferedImage reverse = renderer(CardiogramStyle.THIN, true)
                .render(new float[32], 100, Float.NaN, signal, 100);
        double normalX = offCenterCentroid(normal);
        double reverseX = offCenterCentroid(reverse);
        check(Math.abs((normal.getWidth() - 1 - normalX) - reverseX) < 4.5,
                "Reverse sweep is not the horizontal mirror of normal sweep");
    }

    private static FrameRenderer renderer(CardiogramStyle style, boolean reverse) {
        RenderConfig config = new RenderConfig(640, 360, 30,
                new Color(202, 120, 255), Color.BLACK, null, null,
                BackgroundFitMode.COVER, VideoEndMode.LOOP, BarStyle.THIN, 1f,
                RestingLineMode.DOTTED, PeakMode.SOFT_LIMIT,
                VisualizationMode.CARDIOGRAM, CircularConfig.defaults(), false,
                DualBarConfig.defaults(), LoadBarConfig.defaults(),
                IntroAnimationConfig.disabled(), new CardiogramConfig(
                        CardiogramSpeedMode.SYNCHRONIZED, style, reverse));
        return new FrameRenderer(config);
    }

    private static float[] sampleSignal() {
        float[] signal = new float[180];
        signal[45] = 0.2f;
        signal[48] = -0.2f;
        signal[50] = 1f;
        signal[53] = -0.45f;
        signal[60] = 0.32f;
        signal[110] = 0.18f;
        signal[114] = -0.2f;
        signal[116] = 0.9f;
        signal[119] = -0.4f;
        signal[128] = 0.28f;
        return signal;
    }

    private static int coloredPixels(BufferedImage image) {
        int count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) & 0x00ffffff) != 0) count++;
            }
        }
        return count;
    }

    private static boolean edgeHasColor(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            if ((image.getRGB(x, 0) & 0x00ffffff) != 0
                    || (image.getRGB(x, image.getHeight() - 1) & 0x00ffffff) != 0) {
                return true;
            }
        }
        return false;
    }

    private static double offCenterCentroid(BufferedImage image) {
        double weighted = 0.0;
        int count = 0;
        int center = image.getHeight() / 2;
        for (int y = 0; y < image.getHeight(); y++) {
            if (Math.abs(y - center) < 8) continue;
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) & 0x00ffffff) != 0) {
                    weighted += x;
                    count++;
                }
            }
        }
        return count == 0 ? -1.0 : weighted / count;
    }

    private static void writeReference() throws Exception {
        Path directory = Path.of("target", "test-output");
        Files.createDirectories(directory);
        ImageIO.write(renderer(CardiogramStyle.FLUID_HALO, false)
                .render(new float[32], 150, Float.NaN, sampleSignal(), 150),
                "png", directory.resolve("cardiogram-halo.png").toFile());
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
