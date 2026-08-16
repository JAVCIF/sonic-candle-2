package com.soniccandle.ui;

import com.soniccandle.render.BackgroundFitMode;
import com.soniccandle.render.VideoEndMode;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Decodifica una captura y varios fotogramas reales mediante FFmpeg. */
public final class PreviewVideoSmokeTest {

    private PreviewVideoSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("PreviewVideoSmokeTest <video>");
        }
        Path video = Path.of(args[0]);
        PreviewVideoPlayer player = new PreviewVideoPlayer();
        AtomicReference<Exception> failure = new AtomicReference<>();
        AtomicReference<BufferedImage> still = new AtomicReference<>();
        CountDownLatch stillReady = new CountDownLatch(1);
        player.requestFrame(video, 0.25, 160, 90, BackgroundFitMode.COVER,
                Color.BLACK, frame -> {
                    still.set(frame);
                    stillReady.countDown();
                }, exception -> {
                    failure.set(exception);
                    stillReady.countDown();
                });
        assertTrue(stillReady.await(10, TimeUnit.SECONDS),
                "La captura del video agotó el tiempo.");
        assertTrue(failure.get() == null && still.get() != null
                && still.get().getWidth() == 160 && still.get().getHeight() == 90,
                "La captura del video no produjo una imagen válida.");

        AtomicInteger frames = new AtomicInteger();
        CountDownLatch playbackReady = new CountDownLatch(3);
        player.play(video, 0.0, 160, 90, 30, BackgroundFitMode.CONTAIN,
                Color.BLACK, VideoEndMode.LOOP, frame -> {
                    frames.incrementAndGet();
                    playbackReady.countDown();
                }, failure::set);
        assertTrue(playbackReady.await(10, TimeUnit.SECONDS),
                "La reproducción del video no entregó fotogramas.");
        player.close();
        assertTrue(failure.get() == null && frames.get() >= 3,
                "La reproducción real del fondo falló.");
        System.out.println("Vista previa real correcta: captura y reproducción por streaming.");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
