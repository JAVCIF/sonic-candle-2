package com.soniccandle.ui;

import com.soniccandle.analysis.FrequencyDistributionMode;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumMode;
import com.soniccandle.render.BarStyle;
import com.soniccandle.render.CircleAlignment;
import com.soniccandle.render.CircleFillMode;
import com.soniccandle.render.DualBarLayout;
import com.soniccandle.render.DualBarReach;
import com.soniccandle.render.DualBarVisibility;
import com.soniccandle.render.ExportFormat;
import com.soniccandle.render.HorizontalPlacement;
import com.soniccandle.render.IntroAnimationDirection;
import com.soniccandle.render.IntroAnimationMode;
import com.soniccandle.render.LoadBarAnimationMode;
import com.soniccandle.render.LoadBarBorderStyle;
import com.soniccandle.render.LoadBarFillStyle;
import com.soniccandle.render.LoadBarOrientation;
import com.soniccandle.render.LoadBarResponseMode;
import com.soniccandle.render.LoadBarShape;
import com.soniccandle.render.PeakMode;
import com.soniccandle.render.RestingLineMode;
import com.soniccandle.render.VerticalPlacement;
import com.soniccandle.render.VisualizationMode;

/** Confirma que todos los selectores tienen equivalente en inglés. */
public final class UiTextTest {

    private UiTextTest() {
    }

    public static void main(String[] args) {
        Enum<?>[][] groups = {AppLanguage.values(), AppTheme.values(),
            FrequencyDistributionMode.values(), MotionMode.values(),
            SpectrumMode.values(), BarStyle.values(), CircleAlignment.values(),
            CircleFillMode.values(), DualBarLayout.values(), DualBarReach.values(),
            DualBarVisibility.values(), HorizontalPlacement.values(),
            ExportFormat.values(),
            VerticalPlacement.values(), IntroAnimationDirection.values(),
            IntroAnimationMode.values(), LoadBarAnimationMode.values(),
            LoadBarBorderStyle.values(), LoadBarFillStyle.values(),
            LoadBarOrientation.values(), LoadBarResponseMode.values(),
            LoadBarShape.values(), PeakMode.values(), RestingLineMode.values(),
            VisualizationMode.values()};
        for (Enum<?>[] group : groups) {
            for (Enum<?> value : group) {
                assertTrue(UiText.hasEnglishEnum(value),
                        "Falta traducción para " + value.getClass().getSimpleName()
                                + "." + value.name());
            }
        }
        String[] keys = {"button.play", "button.pause", "tab.load",
            "tip.loadCount", "tip.loadOrientation", "tip.loadHorizontalPosition",
            "tip.loadVerticalPosition", "tip.loadReverse", "tip.loadShape",
            "tip.loadFillStyle", "tip.loadBorderStyle", "tip.loadResponse",
            "tip.loadAnimation", "tip.loadSensitivity", "tip.loadFillColor",
            "tip.loadBorderColor", "state.noBackgroundImage",
            "state.solidBackground", "status.analysisFinished",
            "label.outputFormat", "tip.outputFormat", "note.exportMp4",
            "note.exportProRes", "note.exportWebm", "note.exportPng",
            "button.renderMp4", "button.renderProRes", "button.renderWebm",
            "button.renderPng", "dialog.exportComplete"};
        for (String key : keys) {
            assertTrue(UiText.hasText(key), "Falta una cadena bilingüe: " + key);
        }
        UiText.setLanguage(AppLanguage.ENGLISH);
        assertTrue("Classic interleaved".equals(
                UiText.enumText(SpectrumMode.CLASSIC_INTERLEAVED)),
                "El selector de espectro no cambió al inglés.");
        assertTrue(UiText.text("button.analyze").equals("Analyze and preview"),
                "El botón principal no cambió al inglés.");
        assertTrue(UiText.enumText(ExportFormat.PRORES_4444).contains(
                "Maximum transparency quality"),
                "El formato ProRes no cambió al inglés.");
        UiText.setLanguage(AppLanguage.SPANISH);
        assertTrue(UiText.text("button.analyze").equals("Analizar y previsualizar"),
                "No se recuperó el español.");
        System.out.println("Interfaz bilingüe correcta: opciones, textos y tooltips.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
