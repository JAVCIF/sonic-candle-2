# Sonic Candle 2.0

An approachable Java remake and community continuation of **Sonic Candle**, built for creating customizable music visualizers without needing a full video editor.

> **Current status:** Alpha 18. The core workflow is functional, but the project is still being tested and refined.

[Leer en español](README_ES.md)

## Download

Download the latest Windows installer or executable JAR from [GitHub Releases](https://github.com/JAVCIF/sonic-candle-2/releases).

- The **Windows EXE** includes its own Java runtime but requires FFmpeg/FFprobe in `PATH`.
- The **executable JAR** works across platforms with Java 17 or newer and FFmpeg/FFprobe.

## Why this project exists

Sonic Candle was useful because it did one job quickly: turn a song into a visualizer video. Sonic Candle 2.0 keeps that simple spirit. It is not trying to replace After Effects, Vegas, or a complete motion-graphics editor. It is meant to be the comfortable tool you open when you want a polished audio visualizer without following a long tutorial.

## Highlights

- Desktop interface built with Java Swing.
- Spanish and English interface with instant language switching.
- Modern blue and Classic dark/purple themes.
- Real-time visual preview with synchronized audio, Play/Pause, and timeline seeking.
- Audio or video input through FFmpeg. A video with embedded audio becomes both the spectrum source and background automatically.
- Silent or audible background videos can be combined with a separately selected song without loading the video into memory.
- Background-video Cover, Contain, Stretch, Loop, and Freeze-last-frame controls.
- 720p or 1080p output at 30 or 60 FPS.
- 16 to 160 logarithmic frequency bands.
- Standard and Classic interleaved spectrum modes.
- Standard, Balanced, and Proportional frequency distribution.
- Normal, Agile, and Fast motion response without changing song speed.
- Free-overflow or softly normalized peaks.
- Invisible or dotted idle line.
- Optional dotted intro animation with configurable direction and timing.
- Rotating diagnostic logs for easier bug reports.

## Visualizer modes

### Bars

Classic horizontal spectrum with left/right reversal, adjustable sensitivity, idle line, peak behavior, colors, and eleven rendering styles.

### Circular

One or two circular visualizers with configurable size, alignment, rotation, sensitivity, and inner content. The circle can remain transparent, use a solid color, or crop and reposition an image.

### Dual bar

Top/bottom edge bars or joined center halves. Each half can be reversed independently, hidden, or limited to low, medium, or high reach.

### Loading bar

Horizontal or vertical single-level visualizer with one or two bars, reversible direction, several fill and border styles, independent colors, response modes, and temporal smoothing.

## Export formats

| Format | Background | Transparency | Audio | Best use |
|---|---|---|---|---|
| MP4 (H.264) | Color, image, or streaming video preserved | No | AAC | Ready-to-share complete videos |
| ProRes 4444 | Removed | Maximum quality | PCM | Professional editing and archival masters |
| WebM VP9 | Removed | Compressed | Opus | Lightweight transparent overlays |
| PNG sequence | Removed | Lossless per frame | No | Legacy workflows and maximum editor compatibility |

Transparent exports keep only the visualizer. In Circular mode, a configured solid or image fill is preserved; a transparent interior remains transparent.

## Requirements

- JDK 17 or newer.
- FFmpeg and FFprobe.
- Apache NetBeans with Maven support, Maven itself, or another Java IDE.

Sonic Candle searches for FFmpeg in this order:

1. The project's `tools` directory.
2. `SONIC_CANDLE_FFMPEG` and `SONIC_CANDLE_FFPROBE` environment variables.
3. The system `PATH`.

On Windows, the easiest portable development setup is to place `ffmpeg.exe` and `ffprobe.exe` inside `tools/`.

## Run from source

Clone the repository and open it as a Maven project in NetBeans, or build it from a terminal:

```bash
mvn clean package
java -cp target/classes com.soniccandle.App
```

The main class is `com.soniccandle.App`. NetBeans launch settings are included in `nbactions.xml`.

Detailed NetBeans instructions for producing both the executable JAR and Windows EXE are available in [BUILDING.md](BUILDING.md) and [BUILDING_ES.md](BUILDING_ES.md).

## Project structure

```text
src/main/java/com/soniccandle/
├── analysis/   Audio decoding, FFT, spectrum response and distribution
├── ffmpeg/     FFmpeg/FFprobe discovery and process handling
├── logging/    Rotating diagnostic log
├── render/     Visualizer composition and video/PNG encoders
└── ui/         Swing interface, themes, dialogs and audiovisual preview
```

Dependency-free regression programs live in `src/test/java`. The GitHub Actions workflow compiles the project and runs the core headless suite.

## Contributing and bug reports

Bug reports and focused feature proposals are welcome. Please include:

- Sonic Candle version and operating system.
- Java and FFmpeg versions.
- Export format and visualizer mode.
- Exact reproduction steps.
- The relevant file from `logs/`, when available.

See [CONTRIBUTING.md](CONTRIBUTING.md) for the development workflow.

## Attribution

Sonic Candle 2.0 is based on and inspired by the original [Sonic Candle](https://github.com/ryan-schroeder/sonic-candle), created by **Ryan Schroeder and Chris Soderquist**.

This community continuation was rebuilt in Java with a new FFT/audio-analysis pipeline, FFmpeg-based encoding, audiovisual preview, additional visualizer modes, bilingual UI, and expanded customization.

The application header preserves the historical credit as **by JavCif & Candle**.

## License

Licensed under the [Apache License 2.0](LICENSE). See [NOTICE](NOTICE) for attribution details.
