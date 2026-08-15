package com.soniccandle.ffmpeg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

public final class ProcessLog {

    private static final int MAX_LINES = 40;
    private final Deque<String> lines = new ArrayDeque<>();
    private final Thread thread;

    private ProcessLog(InputStream input) {
        thread = new Thread(() -> read(input), "ffmpeg-log-reader");
        thread.setDaemon(true);
        thread.start();
    }

    public static ProcessLog drain(InputStream input) {
        return new ProcessLog(input);
    }

    private void read(InputStream input) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                synchronized (lines) {
                    if (lines.size() == MAX_LINES) {
                        lines.removeFirst();
                    }
                    lines.addLast(line);
                }
            }
        } catch (IOException ignored) {
            // Al cancelar FFmpeg es normal que su stream se cierre abruptamente.
        }
    }

    public void await() throws InterruptedException {
        thread.join();
    }

    public String tail() {
        synchronized (lines) {
            return String.join(System.lineSeparator(), lines);
        }
    }
}
