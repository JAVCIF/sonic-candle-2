package com.soniccandle.render;

import java.util.ArrayList;
import java.util.List;

/** Calcula posiciones seguras y limita automáticamente uno o dos círculos. */
public final class CircularLayoutCalculator {

    private CircularLayoutCalculator() {
    }

    public static List<CircleGeometry> calculate(int width, int height, CircularConfig config) {
        int count = config.circleCount();
        double margin = Math.max(8.0, Math.min(width, height) * 0.025);
        double byHeight = height / 2.0 - margin;
        double byWidth = (width - margin * 2.0) / (2.0 * count);
        if (count == 2) {
            byWidth *= 0.88;
        }
        double outerRadius = Math.max(24.0, Math.min(byHeight, byWidth));
        double groupWidth = outerRadius * 2.0 * count;
        double startX = switch (config.alignment()) {
            case LEFT -> margin;
            case CENTER -> (width - groupWidth) / 2.0;
            case RIGHT -> width - margin - groupWidth;
        };
        startX = Math.max(margin, Math.min(width - margin - groupWidth, startX));

        double circleRadius = outerRadius * config.sizePercent() / 100.0;
        double availableBarLength = Math.max(8.0, outerRadius - circleRadius - 4.0);
        List<CircleGeometry> result = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            result.add(new CircleGeometry(
                    startX + outerRadius * (index * 2.0 + 1.0),
                    height / 2.0,
                    outerRadius,
                    circleRadius,
                    availableBarLength));
        }
        return List.copyOf(result);
    }
}
