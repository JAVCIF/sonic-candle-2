package com.soniccandle.render;

/** Confirma que una amplitud mayor de 1.0 no se recorte al alto del lienzo. */
public final class RendererOverflowTest {

    private RendererOverflowTest() {
    }

    public static void main(String[] args) {
        int height = FrameRenderer.safeHeight(1.50f, 360);
        if (height != 540) {
            throw new AssertionError("La amplitud volvió a quedar limitada: " + height);
        }
        System.out.println("Desborde correcto: 1.50 produce 540 px desde el centro.");
    }
}
