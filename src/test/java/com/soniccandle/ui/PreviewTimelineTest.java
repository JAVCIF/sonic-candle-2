package com.soniccandle.ui;

/** Valida búsqueda, preroll y sincronía temporal del reproductor. */
public final class PreviewTimelineTest {

    private PreviewTimelineTest() {
    }

    public static void main(String[] args) {
        assertEquals(75, PreviewTimeline.frameAfter(30, 1.5, 30, 300),
                "El reloj no avanzó a 30 FPS.");
        assertEquals(300, PreviewTimeline.frameAfter(290, 2.0, 30, 300),
                "El reloj superó el final.");
        assertTrue(!PreviewTimeline.audioShouldBeActive(29, 30),
                "El audio comenzó dentro de la intro previa.");
        assertTrue(PreviewTimeline.audioShouldBeActive(30, 30),
                "El audio no comenzó al terminar la intro.");
        assertNear(2.0, PreviewTimeline.audioOffsetSeconds(90, 30, 30),
                "El seek no descontó el preroll.");
        assertEquals(90, PreviewTimeline.frameForAudioPosition(2.0, 30, 30, 300),
                "La posición de audio no regresó al fotograma correcto.");
        System.out.println("Línea de tiempo correcta: play, seek, preroll y límites.");
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) throw new AssertionError(message
                + " Esperado=" + expected + ", actual=" + actual);
    }

    private static void assertNear(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.0001) throw new AssertionError(message);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
