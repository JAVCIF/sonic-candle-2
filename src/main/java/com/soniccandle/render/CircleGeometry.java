package com.soniccandle.render;

/** Geometría calculada de un círculo dentro del fotograma. */
public record CircleGeometry(
        double centerX,
        double centerY,
        double outerRadius,
        double circleRadius,
        double availableBarLength) {
}
