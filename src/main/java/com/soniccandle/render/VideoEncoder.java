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
        FrameRenderer renderer = new FrameRenderer(config, format.transparentCanvas());
        float[] loadBarLevels = config.visualizationMode() == VisualizationMode.LOAD_BAR
                ? LoadBarLevelProcessor.process(spectrum, config) : null;
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
                        loadBarLevels, silence, frameIndex, preRollFrames);
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
                        loadBarLevels, silence, frameIndex, preRollFrames);
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
            SpectrumData spectrum, float[] loadBarLevels, float[] silence,
            int frameIndex, int preRollFrames) {
        int spectrumIndex = frameIndex - preRollFrames;
        float[] values = spectrumIndex < 0
                ? silence : spectrum.frame(spectrumIndex);
        float loadBarLevel = loadBarLevels == null || spectrumIndex < 0
                ? Float.NaN : loadBarLevels[spectrumIndex];
        return renderer.render(values, frameIndex, loadBarLevel);
    }

    static List<String> command(Path ffmpeg, Path audio, Path output,
            RenderConfig config, ExportFormat format) {
        String inputPixelFormat = format.transparentCanvas() ? "abgr" : "bgr24";
        List<String> command = new ArrayList<>(List.of(
                ffmpeg.toString(), "-y",
                "-f", "rawvideo", "-pixel_format", inputPixelFormat,
                "-video_size", config.width() + "x" + config.height(),
                "-framerate", Integer.toString(config.framesPerSecond()),
                "-i", "pipe:0"));
        int preRollFrames = preRollFrames(config);
        if (preRollFrames > 0) {
            double delaySeconds = preRollFrames / (double) config.framesPerSecond();
            command.add("-itsoffset");
            command.add(String.format(Locale.ROOT, "%.6f", delaySeconds));
        }
        command.addAll(List.of("-i", audio.toString(),
                "-map", "0:v:0", "-map", "1:a:0"));
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
