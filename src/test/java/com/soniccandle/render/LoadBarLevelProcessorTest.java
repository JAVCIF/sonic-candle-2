package com.soniccandle.render;

import com.soniccandle.analysis.SpectrumData;
import java.awt.Color;
import java.util.Arrays;

/** Pruebas de respuesta dinámica y suavizado temporal de Barra de carga. */
public final class LoadBarLevelProcessorTest {

    private LoadBarLevelProcessorTest() {
    }

    public static void main(String[] args) {
        testHighResponseApproachesTheEnd();
        testProportionalUsesTheAvailableRange();
        testAnimationAttackAndRelease();
        testSilenceRemainsSilent();
        System.out.println("Respuesta de carga correcta: Normal, Alta, Proporcional y suavizados.");
    }

    private static void testHighResponseApproachesTheEnd() {
        SpectrumData data = spectrum(0.58f);
        float normal = process(data, LoadBarResponseMode.NORMAL,
                LoadBarAnimationMode.NORMAL)[0];
        float high = process(data, LoadBarResponseMode.HIGH,
                LoadBarAnimationMode.NORMAL)[0];
        assertTrue(high > normal + 0.18f,
                "La respuesta Alta no dio una ganancia perceptible.");
        assertTrue(high > 0.78f && high <= 0.985f,
                "La respuesta Alta no se acercó al borde seguro.");
    }

    private static void testProportionalUsesTheAvailableRange() {
        float[][] frames = new float[10][48];
        float[] amplitudes = {0f, 0.05f, 0.07f, 0.10f, 0.14f,
            0.20f, 0.29f, 0.39f, 0.52f, 0.70f};
        for (int frame = 0; frame < frames.length; frame++) {
            Arrays.fill(frames[frame], amplitudes[frame]);
        }
        SpectrumData data = new SpectrumData(frames, 30, 44_100);
        float[] normal = process(data, LoadBarResponseMode.NORMAL,
                LoadBarAnimationMode.NORMAL);
        float[] proportional = process(data, LoadBarResponseMode.PROPORTIONAL,
                LoadBarAnimationMode.NORMAL);
        float normalRange = normal[normal.length - 1] - normal[1];
        float proportionalRange = proportional[proportional.length - 1]
                - proportional[1];
        assertTrue(proportional[0] == 0f,
                "Proporcional inventó actividad durante el silencio.");
        assertTrue(proportionalRange > normalRange + 0.10f,
                "Proporcional no aprovechó más recorrido de la barra.");
        for (int index = 2; index < proportional.length; index++) {
            assertTrue(proportional[index] >= proportional[index - 1],
                    "Proporcional alteró el orden del ritmo.");
        }
    }

    private static void testAnimationAttackAndRelease() {
        float[][] frames = new float[12][48];
        for (int frame = 3; frame <= 5; frame++) {
            Arrays.fill(frames[frame], 0.72f);
        }
        SpectrumData data = new SpectrumData(frames, 30, 44_100);
        float[] normal = process(data, LoadBarResponseMode.NORMAL,
                LoadBarAnimationMode.NORMAL);
        float[] balanced = process(data, LoadBarResponseMode.NORMAL,
                LoadBarAnimationMode.BALANCED);
        float[] smoothed = process(data, LoadBarResponseMode.NORMAL,
                LoadBarAnimationMode.SMOOTHED);
        assertTrue(normal[3] > balanced[3] && balanced[3] > smoothed[3],
                "El ataque no respeta Normal > Equilibrada > Suavizada.");
        assertTrue(smoothed[7] > balanced[7] && balanced[7] > normal[7],
                "La caída no respeta Suavizada > Equilibrada > Normal.");
        assertTrue(normal[0] == 0f && balanced[0] == 0f && smoothed[0] == 0f,
                "El suavizado inventó señal antes del ataque.");
    }

    private static void testSilenceRemainsSilent() {
        SpectrumData data = new SpectrumData(new float[20][64], 60, 48_000);
        for (LoadBarResponseMode response : LoadBarResponseMode.values()) {
            for (LoadBarAnimationMode animation : LoadBarAnimationMode.values()) {
                for (float level : process(data, response, animation)) {
                    assertTrue(level == 0f,
                            "Un modo produjo carga durante silencio absoluto.");
                }
            }
        }
    }

    private static SpectrumData spectrum(float amplitude) {
        float[][] frames = new float[1][64];
        Arrays.fill(frames[0], amplitude);
        return new SpectrumData(frames, 30, 44_100);
    }

    private static float[] process(SpectrumData data,
            LoadBarResponseMode response, LoadBarAnimationMode animation) {
        LoadBarConfig load = new LoadBarConfig(1, LoadBarOrientation.HORIZONTAL,
                HorizontalPlacement.CENTER, VerticalPlacement.LEFT, false,
                LoadBarShape.ROUNDED, Color.WHITE, LoadBarFillStyle.DEFAULT,
                LoadBarBorderStyle.DEFAULT, response, animation);
        RenderConfig config = new RenderConfig(640, 360, data.framesPerSecond(),
                Color.PINK, Color.BLACK, null, BarStyle.ROUND_FILLED, 1f,
                RestingLineMode.INVISIBLE, PeakMode.SOFT_LIMIT,
                VisualizationMode.LOAD_BAR, CircularConfig.defaults(), false,
                DualBarConfig.defaults(), load, IntroAnimationConfig.disabled());
        return LoadBarLevelProcessor.process(data, config);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
