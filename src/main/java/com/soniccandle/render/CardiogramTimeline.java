package com.soniccandle.render;

/** Señal cardiográfica y posición acumulada de su barrido temporal. */
public record CardiogramTimeline(float[] signal, float[] intensity,
        float[] sweepPosition) {
}
