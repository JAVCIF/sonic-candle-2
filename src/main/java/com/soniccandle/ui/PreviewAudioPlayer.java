package com.soniccandle.ui;

import com.soniccandle.ffmpeg.FFmpegLocator;
import com.soniccandle.ffmpeg.ProcessLog;
import java.io.BufferedInputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/** Reproduce cualquier formato admitido por FFmpeg mediante PCM transmitido a Java Sound. */
public final class PreviewAudioPlayer implements AutoCloseable {

    static final int SAMPLE_RATE = 44_100;
    private final AtomicLong generation = new AtomicLong();
    private volatile Process process;
    private volatile SourceDataLine line;
    private volatile double requestedOffsetSeconds;
    private volatile boolean ready;

    public void play(Path audio, double offsetSeconds, Consumer<Exception> onFailure) {
        stop();
        long token = generation.get();
        requestedOffsetSeconds = Math.max(0.0, offsetSeconds);
        ready = false;
        Thread thread = new Thread(() -> run(audio, requestedOffsetSeconds,
                token, onFailure), "sonic-candle-preview-audio");
        thread.setDaemon(true);
        thread.start();
    }

    public double positionSeconds() {
        SourceDataLine currentLine = line;
        if (!ready || currentLine == null) {
            return requestedOffsetSeconds;
        }
        return requestedOffsetSeconds
                + currentLine.getLongFramePosition() / (double) SAMPLE_RATE;
    }

    public boolean isReady() {
        return ready;
    }

    public void stop() {
        generation.incrementAndGet();
        ready = false;
        Process currentProcess = process;
        process = null;
        if (currentProcess != null) {
            currentProcess.destroy();
        }
        SourceDataLine currentLine = line;
        line = null;
        if (currentLine != null) {
            try {
                currentLine.stop();
                currentLine.flush();
            } finally {
                currentLine.close();
            }
        }
    }

    @Override
    public void close() {
        stop();
    }

    static List<String> command(Path ffmpeg, Path audio, double offsetSeconds) {
        return List.of(ffmpeg.toString(), "-v", "error", "-ss",
                String.format(Locale.ROOT, "%.6f", Math.max(0.0, offsetSeconds)),
                "-i", audio.toString(), "-vn", "-ac", "2", "-ar",
                Integer.toString(SAMPLE_RATE), "-f", "s16le", "pipe:1");
    }

    private void run(Path audio, double offsetSeconds, long token,
            Consumer<Exception> onFailure) {
        Process localProcess = null;
        SourceDataLine localLine = null;
        try {
            Path ffmpeg = FFmpegLocator.findRequired("ffmpeg");
            localProcess = new ProcessBuilder(command(ffmpeg, audio, offsetSeconds)).start();
            if (generation.get() != token) {
                localProcess.destroy();
                return;
            }
            process = localProcess;
            ProcessLog log = ProcessLog.drain(localProcess.getErrorStream());
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            localLine = (SourceDataLine) AudioSystem.getLine(info);
            localLine.open(format, SAMPLE_RATE * format.getFrameSize() / 4);
            if (generation.get() != token) {
                return;
            }
            line = localLine;
            localLine.start();
            ready = true;
            byte[] buffer = new byte[16 * 1024];
            try (BufferedInputStream input = new BufferedInputStream(
                    localProcess.getInputStream(), buffer.length)) {
                int read;
                while (generation.get() == token && (read = input.read(buffer)) >= 0) {
                    if (read > 0) {
                        localLine.write(buffer, 0, read);
                    }
                }
            }
            if (generation.get() == token) {
                localLine.drain();
                int exit = localProcess.waitFor();
                log.await();
                if (exit != 0 && generation.get() == token) {
                    throw new java.io.IOException(log.tail());
                }
            }
        } catch (Exception exception) {
            if (generation.get() == token && onFailure != null) {
                onFailure.accept(exception);
            }
        } finally {
            if (generation.get() == token) {
                ready = false;
                process = null;
                line = null;
            }
            if (localLine != null) {
                localLine.close();
            }
            if (localProcess != null) {
                localProcess.destroy();
            }
        }
    }
}
