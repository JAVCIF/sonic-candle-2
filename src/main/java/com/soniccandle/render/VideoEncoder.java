package com.soniccandle.render;

import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.ffmpeg.FFmpegLocator;
import com.soniccandle.ffmpeg.ProcessLog;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import javax.imageio.ImageIO;

public final class VideoEncoder {

    public void encode(Path audio, Path output, SpectrumData spectrum,
            RenderConfig config, IntConsumer progress,
            BooleanSupplier cancelled) throws IOException, InterruptedException {
        encode(audio, output, spectrum, config, ExportFormat.MP4, progress, cancelled);
    }

    public void encode(Path audio, Path output, SpectrumData spectrum,
            RenderConfig config, ExportFormat format, IntConsumer progress,
            BooleanSupplier cancelled) throws IOException, InterruptedException {
        ExportFormat safeFormat = format == null ? ExportFormat.MP4 : format;
        if (safeFormat == ExportFormat.PNG_SEQUENCE) {
            encodePngSequence(output, spectrum, config, progress, cancelled);
        } else {
            encodeVideo(audio, output, spectrum, config, safeFormat, progress, cancelled);
        }
    }

    private void encodeVideo(Path audio, Path output, SpectrumData spectrum,
            RenderConfig config, ExportFormat format, IntConsumer progress,
            BooleanSupplier cancelled) throws IOException, InterruptedException {
        Path ffmpeg = FFmpegLocator.findRequired("ffmpeg");
        List<String> command = command(ffmpeg, audio, output, config, format);
        Process process = new ProcessBuilder(command).start();
        ProcessLog log = ProcessLog.drain(process.getErrorStream());
        boolean videoBackground = format == ExportFormat.MP4
                && config.backgroundVideo() != null;
        FrameRenderer renderer = new FrameRenderer(config,
                format.transparentCanvas() || videoBackground);
        float[] loadBarLevels = config.visualizationMode() == VisualizationMode.LOAD_BAR
                ? LoadBarLevelProcessor.process(spectrum, config) : null;
        CardiogramTimeline cardiogramTimeline = config.visualizationMode()
                == VisualizationMode.CARDIOGRAM
                ? CardiogramSignalProcessor.timeline(spectrum,
                        config.cardiogramConfig()) : null;
        NeonWaveTimeline neonWaveTimeline = config.visualizationMode()
                == VisualizationMode.NEON_WAVE
                ? NeonWaveProcessor.process(spectrum,
                        config.neonWaveConfig()) : null;
        boolean wasCancelled = false;
        IOException writeFailure = null;

        int preRollFrames = preRollFrames(config);
        int totalFrames = spectrum.frameCount() + preRollFrames;
        float[] silence = new float[spectrum.bandCount()];
        try (OutputStream input = new BufferedOutputStream(
                process.getOutputStream(), 1024 * 1024)) {
            for (int frameIndex = 0; frameIndex < totalFrames; frameIndex++) {
                if (cancelled.getAsBoolean()) {
                    wasCancelled = true;
                    break;
                }
                BufferedImage frame = renderFrame(renderer, spectrum,
                        loadBarLevels, cardiogramTimeline, neonWaveTimeline,
                        silence, frameIndex,
                        preRollFrames);
                byte[] pixels = ((DataBufferByte) frame.getRaster()
                        .getDataBuffer()).getData();
                input.write(pixels);
                progress.accept((frameIndex + 1) * 100 / totalFrames);
            }
        } catch (IOException exception) {
            if (!cancelled.getAsBoolean()) {
                writeFailure = exception;
            } else {
                wasCancelled = true;
            }
        } catch (RuntimeException exception) {
            process.destroyForcibly();
            throw exception;
        }

        if (wasCancelled) {
            process.destroyForcibly();
            process.waitFor();
            Files.deleteIfExists(output);
            throw new CancellationException("Render cancelado.");
        }

        int exitCode = process.waitFor();
        log.await();
        if (exitCode != 0 || writeFailure != null) {
            Files.deleteIfExists(output);
            String detail = log.tail();
            if (detail.isBlank() && writeFailure != null) {
                detail = writeFailure.getMessage();
            }
            throw new IOException(localized("FFmpeg no pudo crear la exportación.\n",
                    "FFmpeg could not create the export.\n")
                    + detail, writeFailure);
        }
        progress.accept(100);
    }

    private void encodePngSequence(Path outputDirectory, SpectrumData spectrum,
            RenderConfig config, IntConsumer progress,
            BooleanSupplier cancelled) throws IOException {
        boolean directoryExisted = Files.exists(outputDirectory);
        Files.createDirectories(outputDirectory);
        FrameRenderer renderer = new FrameRenderer(config, true);
        float[] loadBarLevels = config.visualizationMode() == VisualizationMode.LOAD_BAR
                ? LoadBarLevelProcessor.process(spectrum, config) : null;
        CardiogramTimeline cardiogramTimeline = config.visualizationMode()
                == VisualizationMode.CARDIOGRAM
                ? CardiogramSignalProcessor.timeline(spectrum,
                        config.cardiogramConfig()) : null;
        NeonWaveTimeline neonWaveTimeline = config.visualizationMode()
                == VisualizationMode.NEON_WAVE
                ? NeonWaveProcessor.process(spectrum,
                        config.neonWaveConfig()) : null;
        int preRollFrames = preRollFrames(config);
        int totalFrames = spectrum.frameCount() + preRollFrames;
        float[] silence = new float[spectrum.bandCount()];
        List<Path> createdFrames = new ArrayList<>();
        try {
            for (int frameIndex = 0; frameIndex < totalFrames; frameIndex++) {
                if (cancelled.getAsBoolean()) {
                    throw new CancellationException("Render cancelado.");
                }
                BufferedImage frame = renderFrame(renderer, spectrum,
                        loadBarLevels, cardiogramTimeline, neonWaveTimeline,
                        silence, frameIndex,
                        preRollFrames);
                Path framePath = outputDirectory.resolve(String.format(
                        Locale.ROOT, "sonic-candle_%06d.png", frameIndex + 1));
                if (Files.exists(framePath)) {
                    throw new IOException(localized("La carpeta ya contiene ",
                            "The folder already contains ")
                            + framePath.getFileName()
                            + localized(". Elige una carpeta nueva o vacía.",
                                    ". Choose a new or empty folder."));
                }
                if (!ImageIO.write(frame, "png", framePath.toFile())) {
                    throw new IOException(localized(
                            "No hay un codificador PNG disponible.",
                            "No PNG encoder is available."));
                }
                createdFrames.add(framePath);
                progress.accept((frameIndex + 1) * 100 / totalFrames);
            }
        } catch (IOException | RuntimeException exception) {
            for (Path frame : createdFrames) {
                try {
                    Files.deleteIfExists(frame);
                } catch (IOException ignored) {
                    // Se conserva el error original.
                }
            }
            if (!directoryExisted) {
                try {
                    Files.deleteIfExists(outputDirectory);
                } catch (IOException ignored) {
                    // Puede contener archivos ajenos creados al mismo tiempo.
                }
            }
            throw exception;
        }
        progress.accept(100);
    }

    private static BufferedImage renderFrame(FrameRenderer renderer,
            SpectrumData spectrum, float[] loadBarLevels,
            CardiogramTimeline cardiogramTimeline,
            NeonWaveTimeline neonWaveTimeline, float[] silence,
            int frameIndex, int preRollFrames) {
        int spectrumIndex = frameIndex - preRollFrames;
        float[] values = spectrumIndex < 0
                ? silence : spectrum.frame(spectrumIndex);
        float loadBarLevel = loadBarLevels == null || spectrumIndex < 0
                ? Float.NaN : loadBarLevels[spectrumIndex];
        float[] cardiogramSignal = cardiogramTimeline == null
                ? null : cardiogramTimeline.signal();
        float[] sweepPositions = cardiogramTimeline == null
                ? null : cardiogramTimeline.sweepPosition();
        return renderer.render(values, frameIndex, loadBarLevel,
                cardiogramSignal, spectrumIndex, sweepPositions,
                neonWaveTimeline, spectrumIndex);
    }

    static List<String> command(Path ffmpeg, Path audio, Path output,
            RenderConfig config, ExportFormat format) {
        boolean videoBackground = format == ExportFormat.MP4
                && config.backgroundVideo() != null;
        String inputPixelFormat = format.transparentCanvas() || videoBackground
                ? "abgr" : "bgr24";
        List<String> command = new ArrayList<>(List.of(
                ffmpeg.toString(), "-y",
                "-f", "rawvideo", "-pixel_format", inputPixelFormat,
                "-video_size", config.width() + "x" + config.height(),
                "-framerate", Integer.toString(config.framesPerSecond()),
                "-i", "pipe:0"));
        int preRollFrames = preRollFrames(config);
        double delaySeconds = preRollFrames / (double) config.framesPerSecond();
        int audioInputIndex = 1;
        if (videoBackground) {
            if (config.videoEndMode() == VideoEndMode.LOOP) {
                command.addAll(List.of("-stream_loop", "-1"));
            }
            command.addAll(List.of("-i", config.backgroundVideo().toString()));
            audioInputIndex = 2;
        }
        if (preRollFrames > 0) {
            command.add("-itsoffset");
            command.add(String.format(Locale.ROOT, "%.6f", delaySeconds));
        }
        command.addAll(List.of("-i", audio.toString()));
        if (videoBackground) {
            command.addAll(List.of("-filter_complex",
                    backgroundVideoFilter(config, delaySeconds),
                    "-map", "[composed]", "-map", audioInputIndex + ":a:0"));
        } else {
            command.addAll(List.of("-map", "0:v:0", "-map", "1:a:0"));
        }
        switch (format) {
            case MP4 -> command.addAll(List.of(
                    "-c:v", "libx264", "-preset", "medium", "-crf", "18",
                    "-pix_fmt", "yuv420p", "-c:a", "aac", "-b:a", "192k",
                    "-shortest", "-movflags", "+faststart"));
            case PRORES_4444 -> command.addAll(List.of(
                    "-c:v", "prores_ks", "-profile:v", "4",
                    "-pix_fmt", "yuva444p10le", "-c:a", "pcm_s16le",
                    "-shortest", "-movflags", "+faststart"));
            case WEBM_VP9 -> command.addAll(List.of(
                    "-c:v", "libvpx-vp9", "-pix_fmt", "yuva420p",
                    "-crf", "24", "-b:v", "0", "-deadline", "good",
                    "-cpu-used", "2", "-auto-alt-ref", "0",
                    "-metadata:s:v:0", "alpha_mode=1",
                    "-c:a", "libopus", "-b:a", "192k", "-shortest"));
            case PNG_SEQUENCE -> throw new IllegalArgumentException(
                    "La secuencia PNG no usa FFmpeg.");
        }
        command.add(output.toString());
        return command;
    }

    static String backgroundVideoFilter(RenderConfig config, double delaySeconds) {
        int width = config.width();
        int height = config.height();
        String scale = switch (config.backgroundFitMode()) {
            case COVER -> "scale=" + width + ":" + height
                    + ":force_original_aspect_ratio=increase:flags=lanczos,"
                    + "crop=" + width + ":" + height;
            case CONTAIN -> "scale=" + width + ":" + height
                    + ":force_original_aspect_ratio=decrease:flags=lanczos,"
                    + "pad=" + width + ":" + height
                    + ":(ow-iw)/2:(oh-ih)/2:color="
                    + colorHex(config.backgroundColor());
            case STRETCH -> "scale=" + width + ":" + height + ":flags=lanczos";
        };
        StringBuilder filter = new StringBuilder("[1:v]")
                .append(scale)
                .append(",setsar=1,fps=").append(config.framesPerSecond());
        if (delaySeconds > 0.0) {
            filter.append(",tpad=start_mode=clone:start_duration=")
                    .append(String.format(Locale.ROOT, "%.6f", delaySeconds));
        }
        if (config.videoEndMode() == VideoEndMode.FREEZE) {
            filter.append(",tpad=stop_mode=clone:stop_duration=86400");
        }
        filter.append(",drawbox=color=black@0.22:t=fill,format=rgba[background];")
                .append("[background][0:v]overlay=0:0:format=auto:shortest=1[composed]");
        return filter.toString();
    }

    private static String colorHex(java.awt.Color color) {
        java.awt.Color safe = color == null ? java.awt.Color.BLACK : color;
        return String.format(Locale.ROOT, "0x%02X%02X%02X",
                safe.getRed(), safe.getGreen(), safe.getBlue());
    }

    private static int preRollFrames(RenderConfig config) {
        boolean introBeforeAudio = config.introAnimationApplies()
                && config.introAnimationConfig().mode() == IntroAnimationMode.BEFORE_AUDIO;
        return introBeforeAudio ? config.introFrameCount() : 0;
    }

    private static String localized(String spanish, String english) {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("en")
                ? english : spanish;
    }
}
