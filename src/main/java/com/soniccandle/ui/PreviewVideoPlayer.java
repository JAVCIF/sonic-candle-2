package com.soniccandle.ui;

import com.soniccandle.ffmpeg.FFmpegLocator;
import com.soniccandle.ffmpeg.ProcessLog;
import com.soniccandle.render.BackgroundFitMode;
import com.soniccandle.render.VideoEndMode;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/** Decodifica únicamente los fotogramas visibles de la vista previa. */
public final class PreviewVideoPlayer implements AutoCloseable {

    private final AtomicLong generation = new AtomicLong();
    private volatile Process process;

    public void requestFrame(Path video, double offsetSeconds, int width, int height,
            BackgroundFitMode fitMode, Color paddingColor,
            Consumer<BufferedImage> onFrame, Consumer<Exception> onFailure) {
        stop();
        long token = generation.get();
        Thread thread = new Thread(() -> run(video, offsetSeconds, width, height,
                1, fitMode, paddingColor, VideoEndMode.FREEZE, true,
                token, onFrame, onFailure), "sonic-candle-preview-video-frame");
        thread.setDaemon(true);
        thread.start();
    }

    public void play(Path video, double offsetSeconds, int width, int height,
            int framesPerSecond, BackgroundFitMode fitMode, Color paddingColor,
            VideoEndMode endMode, Consumer<BufferedImage> onFrame,
            Consumer<Exception> onFailure) {
        stop();
        long token = generation.get();
        Thread thread = new Thread(() -> run(video, offsetSeconds, width, height,
                framesPerSecond, fitMode, paddingColor, endMode, false,
                token, onFrame, onFailure), "sonic-candle-preview-video");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        generation.incrementAndGet();
        Process current = process;
        process = null;
        if (current != null) {
            current.destroy();
        }
    }

    @Override
    public void close() {
        stop();
    }

    private void run(Path video, double offsetSeconds, int width, int height,
            int framesPerSecond, BackgroundFitMode fitMode, Color paddingColor,
            VideoEndMode endMode, boolean singleFrame, long token,
            Consumer<BufferedImage> onFrame, Consumer<Exception> onFailure) {
        Process localProcess = null;
        try {
            Path ffmpeg = FFmpegLocator.findRequired("ffmpeg");
            List<String> command = command(ffmpeg, video, offsetSeconds,
                    Math.max(2, width), Math.max(2, height), framesPerSecond,
                    fitMode, paddingColor, endMode, singleFrame);
            localProcess = new ProcessBuilder(command).start();
            if (generation.get() != token) {
                localProcess.destroy();
                return;
            }
            process = localProcess;
            ProcessLog log = ProcessLog.drain(localProcess.getErrorStream());
            int frameBytes = Math.max(2, width) * Math.max(2, height) * 3;
            byte[] pixels = new byte[frameBytes];
            try (InputStream input = new BufferedInputStream(
                    localProcess.getInputStream(), Math.min(frameBytes, 1024 * 1024))) {
                while (generation.get() == token && readFrame(input, pixels)) {
                    BufferedImage image = new BufferedImage(Math.max(2, width),
                            Math.max(2, height), BufferedImage.TYPE_3BYTE_BGR);
                    byte[] target = ((DataBufferByte) image.getRaster()
                            .getDataBuffer()).getData();
                    System.arraycopy(pixels, 0, target, 0, pixels.length);
                    if (onFrame != null) {
                        onFrame.accept(image);
                    }
                    if (singleFrame) {
                        break;
                    }
                }
            }
            int exit = localProcess.waitFor();
            log.await();
            if (exit != 0 && generation.get() == token) {
                throw new IOException(log.tail());
            }
        } catch (Exception exception) {
            if (generation.get() == token && onFailure != null) {
                onFailure.accept(exception);
            }
        } finally {
            if (generation.get() == token) {
                process = null;
            }
            if (localProcess != null) {
                localProcess.destroy();
            }
        }
    }

    static List<String> command(Path ffmpeg, Path video, double offsetSeconds,
            int width, int height, int framesPerSecond,
            BackgroundFitMode fitMode, Color paddingColor,
            VideoEndMode endMode, boolean singleFrame) {
        List<String> command = new ArrayList<>();
        command.addAll(List.of(ffmpeg.toString(), "-v", "error"));
        if (!singleFrame && endMode == VideoEndMode.LOOP) {
            command.addAll(List.of("-stream_loop", "-1"));
        }
        if (!singleFrame) {
            command.add("-re");
        }
        command.addAll(List.of("-ss", String.format(Locale.ROOT, "%.6f",
                Math.max(0.0, offsetSeconds)), "-i", video.toString(), "-an"));
        String filter = fitFilter(width, height, fitMode, paddingColor);
        if (!singleFrame) {
            filter += ",fps=" + Math.max(1, framesPerSecond);
        }
        command.addAll(List.of("-vf", filter));
        if (singleFrame) {
            command.addAll(List.of("-frames:v", "1"));
        }
        command.addAll(List.of("-pix_fmt", "bgr24", "-f", "rawvideo", "pipe:1"));
        return command;
    }

    static String fitFilter(int width, int height, BackgroundFitMode fitMode,
            Color paddingColor) {
        BackgroundFitMode safeMode = fitMode == null ? BackgroundFitMode.COVER : fitMode;
        return switch (safeMode) {
            case COVER -> "scale=" + width + ":" + height
                    + ":force_original_aspect_ratio=increase:flags=lanczos,"
                    + "crop=" + width + ":" + height + ",setsar=1";
            case CONTAIN -> "scale=" + width + ":" + height
                    + ":force_original_aspect_ratio=decrease:flags=lanczos,"
                    + "pad=" + width + ":" + height
                    + ":(ow-iw)/2:(oh-ih)/2:color=" + colorHex(paddingColor)
                    + ",setsar=1";
            case STRETCH -> "scale=" + width + ":" + height
                    + ":flags=lanczos,setsar=1";
        };
    }

    private static boolean readFrame(InputStream input, byte[] target) throws IOException {
        int offset = 0;
        while (offset < target.length) {
            int read = input.read(target, offset, target.length - offset);
            if (read < 0) {
                if (offset == 0) {
                    return false;
                }
                throw new EOFException("Fotograma de video incompleto.");
            }
            offset += read;
        }
        return true;
    }

    private static String colorHex(Color color) {
        Color safe = color == null ? Color.BLACK : color;
        return String.format(Locale.ROOT, "0x%02X%02X%02X",
                safe.getRed(), safe.getGreen(), safe.getBlue());
    }
}
