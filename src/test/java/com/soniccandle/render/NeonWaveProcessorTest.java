package com.soniccandle.render;

import com.soniccandle.analysis.SpectrumData;
import java.util.Arrays;

public final class NeonWaveProcessorTest {

    private NeonWaveProcessorTest() {
    }

    public static void main(String[] args) {
        silenceStaysSilent();
        pointCountAndEnergyFollowSpectrum();
        processingIsDeterministic();
        System.out.println("NeonWaveProcessorTest OK");
    }

    private static void silenceStaysSilent() {
        NeonWaveTimeline timeline = NeonWaveProcessor.process(
                new SpectrumData(new float[5][32], 30, 44_100),
                NeonWaveConfig.defaults());
        for (int frame = 0; frame < timeline.frameCount(); frame++) {
            check(timeline.frame(frame).length == 12,
                    "Default point count changed");
            check(timeline.energy()[frame] == 0f,
                    "Silence generated Neon Wave energy");
            check(timeline.onset()[frame] == 0f,
                    "Silence generated a false onset");
        }
    }

    private static void pointCountAndEnergyFollowSpectrum() {
        float[][] frames = new float[3][64];
        Arrays.fill(frames[1], 0.22f);
        for (int band = 0; band < frames[2].length; band++) {
            frames[2][band] = band % 9 == 0 ? 0.9f : 0.08f;
        }
        NeonWaveConfig config = new NeonWaveConfig(18,
                NeonWaveLineStyle.ANGULAR, 7, 2, 45, 100,
                NeonWavePlacement.CENTER, false,
                NeonWaveParticleMode.SUBTLE);
        NeonWaveTimeline timeline = NeonWaveProcessor.process(
                new SpectrumData(frames, 30, 44_100), config);
        check(timeline.frame(1).length == 18,
                "Configured point count was ignored");
        check(timeline.energy()[1] > 0.1f,
                "Musical energy was not measured");
        check(timeline.onset()[1] > 0f,
                "Energy rise did not produce an onset");
        check(timeline.energy()[2] > 0f,
                "Irregular spectrum collapsed to silence");
    }

    private static void processingIsDeterministic() {
        float[][] frames = new float[24][48];
        for (int frame = 0; frame < frames.length; frame++) {
            for (int band = 0; band < frames[frame].length; band++) {
                frames[frame][band] = (float) (0.15
                        + Math.abs(Math.sin(frame * 0.17 + band * 0.31)) * 0.5);
            }
        }
        SpectrumData spectrum = new SpectrumData(frames, 30, 48_000);
        NeonWaveTimeline first = NeonWaveProcessor.process(spectrum,
                NeonWaveConfig.defaults());
        NeonWaveTimeline second = NeonWaveProcessor.process(spectrum,
                NeonWaveConfig.defaults());
        check(Arrays.deepEquals(first.points(), second.points()),
                "Point timelines are not deterministic");
        check(Arrays.equals(first.energy(), second.energy()),
                "Energy timelines are not deterministic");
        check(Arrays.equals(first.onset(), second.onset()),
                "Onset timelines are not deterministic");
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
