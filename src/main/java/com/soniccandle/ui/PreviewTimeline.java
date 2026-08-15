package com.soniccandle.ui;

/** Cálculos puros usados por la reproducción y por sus pruebas de sincronía. */
public final class PreviewTimeline {

    private PreviewTimeline() {
    }

    public static int frameAfter(int startFrame, double elapsedSeconds,
            int framesPerSecond, int maximumFrame) {
        long advance = Math.max(0L, (long) Math.floor(Math.max(0.0, elapsedSeconds)
                * Math.max(1, framesPerSecond)));
        return (int) Math.min(Math.max(0, maximumFrame),
                Math.max(0L, startFrame + advance));
    }

    public static boolean audioShouldBeActive(int timelineFrame, int preRollFrames) {
        return timelineFrame >= Math.max(0, preRollFrames);
    }

    public static double audioOffsetSeconds(int timelineFrame,
            int preRollFrames, int framesPerSecond) {
        return Math.max(0, timelineFrame - Math.max(0, preRollFrames))
                / (double) Math.max(1, framesPerSecond);
    }

    public static int frameForAudioPosition(double audioPositionSeconds,
            int preRollFrames, int framesPerSecond, int maximumFrame) {
        long frame = Math.max(0, preRollFrames) + Math.round(
                Math.max(0.0, audioPositionSeconds) * Math.max(1, framesPerSecond));
        return (int) Math.min(Math.max(0, maximumFrame), frame);
    }
}
