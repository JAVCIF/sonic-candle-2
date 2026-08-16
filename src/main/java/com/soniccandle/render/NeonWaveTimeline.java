package com.soniccandle.render;

/** Puntos y energía precalculados para preview y exportación deterministas. */
public record NeonWaveTimeline(float[][] points, float[] energy,
        float[] onset) {

    public int frameCount() {
        return points.length;
    }

    public float[] frame(int index) {
        if (points.length == 0) {
            return new float[0];
        }
        return points[Math.max(0, Math.min(points.length - 1, index))];
    }
}
