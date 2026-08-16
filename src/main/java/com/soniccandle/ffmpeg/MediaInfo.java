package com.soniccandle.ffmpeg;

/** Información mínima necesaria para decidir cómo usar un archivo multimedia. */
public record MediaInfo(boolean hasAudio, boolean hasVideo, double durationSeconds) {
}
