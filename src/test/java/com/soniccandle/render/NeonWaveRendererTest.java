package com.soniccandle.render;

import com.soniccandle.analysis.SpectrumData;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.imageio.ImageIO;

public final class NeonWaveRendererTest {

    private NeonWaveRendererTest() {
    }

    public static void main(String[] args) {
        silentWaveKeepsItsIdleLine();
        stylesRenderInsideTheFrame();
        inversionChangesPeakDirection();
        echoesAndParticlesAreDeterministic();
        writeReference();
        System.out.println("NeonWaveRendererTest OK");
    }

    private static void silentWaveKeepsItsIdleLine() {
        float[][] frames = new float[20][64];
        SpectrumData silence = new SpectrumData(frames, 30, 44_100);
        NeonWaveConfig neon = new NeonWaveConfig(12,
                NeonWaveLineStyle.ANGULAR, 7, 2, 45, 100,
                NeonWavePlacement.CENTER, false,
                NeonWaveParticleMode.SUBTLE);
        BufferedImage image = render(silence, neon, 12);
        check(coloredPixels(image) > 100,
                "Absolute silence removed the Neon Wave idle line");
        check(!edgeHasColor(image),
                "Silent Neon Wave idle line escaped the frame");
    }

    private static void stylesRenderInsideTheFrame() {
        SpectrumData spectrum = sampleSpectrum();
        for (NeonWaveLineStyle style : NeonWaveLineStyle.values()) {
            NeonWaveConfig neon = new NeonWaveConfig(12, style, 4, 2, 45,
                    100, NeonWavePlacement.CENTER, false,
                    NeonWaveParticleMode.SUBTLE);
            BufferedImage image = render(spectrum, neon, 32);
            check(coloredPixels(image) > 250,
                    style + " did not render a visible Neon Wave");
            check(!edgeHasColor(image), style + " escaped the video frame");
        }
    }

    private static void inversionChangesPeakDirection() {
        SpectrumData spectrum = sampleSpectrum();
        NeonWaveConfig normalConfig = new NeonWaveConfig(12,
                NeonWaveLineStyle.ANGULAR, 0, 2, 45, 0,
                NeonWavePlacement.CENTER, false,
                NeonWaveParticleMode.DISABLED);
        NeonWaveConfig invertedConfig = new NeonWaveConfig(12,
                NeonWaveLineStyle.ANGULAR, 0, 2, 45, 0,
                NeonWavePlacement.CENTER, true,
                NeonWaveParticleMode.DISABLED);
        BufferedImage normal = render(spectrum, normalConfig, 32);
        BufferedImage inverted = render(spectrum, invertedConfig, 32);
        check(coloredCentroidY(normal) > coloredCentroidY(inverted) + 12,
                "Inversion did not move the peaks to the opposite side");
    }

    private static void echoesAndParticlesAreDeterministic() {
        SpectrumData spectrum = sampleSpectrum();
        NeonWaveConfig rich = new NeonWaveConfig(14,
                NeonWaveLineStyle.ROUNDED, 8, 2, 62, 140,
                NeonWavePlacement.CENTER, false,
                NeonWaveParticleMode.INTENSE);
        BufferedImage first = render(spectrum, rich, 36);
        BufferedImage second = render(spectrum, rich, 36);
        check(Arrays.equals(pixels(first), pixels(second)),
                "Neon Wave particles changed between identical renders");

        NeonWaveConfig plain = new NeonWaveConfig(14,
                NeonWaveLineStyle.ROUNDED, 0, 2, 62, 0,
                NeonWavePlacement.CENTER, false,
                NeonWaveParticleMode.DISABLED);
        check(coloredPixels(first) > coloredPixels(render(spectrum, plain, 36)),
                "Echoes and particles did not add a visible trail");
    }

    private static BufferedImage render(SpectrumData spectrum,
            NeonWaveConfig neon, int frame) {
        NeonWaveTimeline timeline = NeonWaveProcessor.process(spectrum, neon);
        RenderConfig config = new RenderConfig(640, 360, 30,
                new Color(255, 91, 18), Color.BLACK, null, null,
                BackgroundFitMode.COVER, VideoEndMode.LOOP, BarStyle.THIN, 1f,
                RestingLineMode.INVISIBLE, PeakMode.SOFT_LIMIT,
                VisualizationMode.NEON_WAVE, CircularConfig.defaults(), false,
                DualBarConfig.defaults(), LoadBarConfig.defaults(),
                IntroAnimationConfig.disabled(), CardiogramConfig.defaults(), neon);
        return new FrameRenderer(config).render(spectrum.frame(frame), frame,
                Float.NaN, null, -1, null, timeline, frame);
    }

    private static SpectrumData sampleSpectrum() {
        float[][] frames = new float[48][64];
        for (int frame = 0; frame < frames.length; frame++) {
            float pulse = frame % 12 < 4 ? 0.48f : 0.19f;
            for (int band = 0; band < frames[frame].length; band++) {
                double firstPeak = Math.exp(-Math.pow((band
                        - (13 + frame % 9)) / 3.2, 2));
                double secondPeak = Math.exp(-Math.pow((band
                        - (43 - frame % 7)) / 2.4, 2));
                double shimmer = Math.abs(Math.sin(band * 0.39
                        + frame * 0.17)) * 0.06;
                frames[frame][band] = (float) (0.025 + shimmer
                        + pulse * (firstPeak * 0.75 + secondPeak));
            }
        }
        return new SpectrumData(frames, 30, 44_100);
    }

    private static int coloredPixels(BufferedImage image) {
        int count = 0;
        for (int value : pixels(image)) {
            if ((value & 0x00ffffff) != 0) count++;
        }
        return count;
    }

    private static int[] pixels(BufferedImage image) {
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(),
                null, 0, image.getWidth());
    }

    private static boolean edgeHasColor(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            if ((image.getRGB(x, 0) & 0x00ffffff) != 0
                    || (image.getRGB(x, image.getHeight() - 1)
                    & 0x00ffffff) != 0) return true;
        }
        return false;
    }

    private static double coloredCentroidY(BufferedImage image) {
        long weighted = 0;
        long count = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) & 0x00ffffff) != 0) {
                    weighted += y;
                    count++;
                }
            }
        }
        return count == 0 ? -1 : weighted / (double) count;
    }

    private static void writeReference() {
        try {
            Path directory = Path.of("target", "test-output");
            Files.createDirectories(directory);
            NeonWaveConfig neon = new NeonWaveConfig(12,
                    NeonWaveLineStyle.ANGULAR, 8, 2, 58, 140,
                    NeonWavePlacement.CENTER, false,
                    NeonWaveParticleMode.INTENSE);
            ImageIO.write(render(sampleSpectrum(), neon, 36), "png",
                    directory.resolve("neon-wave.png").toFile());
        } catch (Exception exception) {
            throw new AssertionError("Could not write Neon Wave reference", exception);
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
