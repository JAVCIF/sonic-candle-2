# Changelog

All notable changes to Sonic Candle 2.0 are documented here.

[Historial completo en español](CHANGELOG_ES.md)

## 2.0.0-alpha.19 — 2026-08-15

- Added a fifth Cardiogram visualizer with music-driven P–QRS–T pulses.
- Added a default Heart rate mode: a continuous 58–190 BPM monitor accelerates and decelerates smoothly with song intensity.
- Retained the original per-attack behavior as Musical hits; manual sweep speed is available only in this mode and disabled for Heart rate.
- Added an optional, default-enabled adaptive Heart rate sweep with Normal, Fast, and Very fast advance. It moves the trace itself on a fixed scale instead of resizing the full history, preventing accordion-like deformation; disabling it fixes the speed at Normal.
- Heartbeats now use adaptive bass/onset peak detection; quiet spectrum variation cannot create unrelated pulses, and the R peak lands on the detected musical hit.
- New heartbeats enter from the right and old trace exits on the left; the sweep can be reversed.
- In Musical hits, Slow, Synchronized, and Fast change visible history without altering song or beat timing.
- Added Thin, Thick, Rounded, Segmented, and Fluid Halo line finishes plus independent color and sensitivity.
- The trace is softly contained inside a safe frame margin in preview and every export format.
- Preview seeking and final rendering share the same precomputed temporal signal for deterministic output.
- Added silence, pulse-shape, sweep-order, reversal, style, and containment regressions.
- Embedded cover art is no longer classified as real video, preventing MP3/M4A artwork from becoming an unintended or stalled background stream.
- Audio analysis and preview explicitly map the first audio stream and discard every attached picture.
- Packaged test logs are excluded from distributable ZIP files.

## 2.0.0-alpha.18 — 2026-08-15

- Added automatic FFprobe detection for audio-only, silent-video, and video-with-audio inputs.
- The main input selector now accepts a video with embedded audio and automatically uses both its sound and picture.
- Added independent background-video selection for combining a song with silent footage or footage whose audio must be ignored.
- Added Cover, Contain, and Stretch fitting plus Loop and Freeze-last-frame duration behavior.
- Background videos are decoded as a stream by FFmpeg instead of being stored in memory.
- MP4 composites the transparent Java visualizer over the streamed video while preserving the selected song as the only audio source.
- ProRes 4444, VP9, and PNG continue to omit every global background, including video.
- The audiovisual preview seeks and plays the background video from the same timeline position as the audio.
- Added command, media-probe, real preview-decoding, looping, freezing, and complete MP4 export regressions.

## 2.0.0-alpha.17 — 2026-08-15

- Added reproducible executable JAR and Windows EXE builds.
- Added a NetBeans-friendly Windows packaging script based on `jpackage` and WiX 3.14.
- The Windows installer includes a private Java runtime and can optionally bundle user-provided FFmpeg/FFprobe executables from `tools/`.
- Added GitHub Actions automation that publishes the JAR and EXE as workflow artifacts and creates the Alpha 17 pre-release.
- Added English and Spanish build guides, a multi-resolution Windows icon, and packaged-app FFmpeg discovery.
- Added an output selector for MP4, ProRes 4444, WebM VP9, and PNG sequences.
- MP4 preserves the configured background, images, fills, and complete composition.
- ProRes 4444 provides maximum-quality transparency using `prores_ks`, the 4444 profile, and PCM audio. The interface warns that files can be very large.
- WebM VP9 provides substantially lighter compressed transparency with Opus audio.
- Transparent formats omit the global background while preserving the visualizer and any configured Circular solid/image fill.
- Restored the original Sonic Candle workflow as a numbered transparent PNG sequence without audio.
- PNG export refuses to overwrite existing frames and only cleans up files created by a cancelled or failed attempt.
- Added bilingual labels, buttons, quality notes, filters, tooltips, and completion messages for every export format.
- Added regression coverage for alpha composition, Circular fills, codec commands, safe PNG output, and real FFmpeg/FFprobe exports.

## Earlier Alpha milestones

- **Alpha 16:** Compact theme-aware completion and error dialogs.
- **Alpha 15:** Fully themed and localized application dialogs.
- **Alpha 14:** Custom theme-aware color chooser.
- **Alpha 13:** Classic-theme controls, direct timeline seeking, and rotating diagnostic logs.
- **Alpha 12:** English/Spanish interface, two themes, and audiovisual preview playback.
- **Alpha 11:** Loading-bar styles, response modes, smoothing, and intro/idle-line validation.
- **Alpha 10:** Loading-bar visualizer, single-half Dual bar modes, and dotted intro animation.
- **Alpha 9:** Dual bar visualizer and independent inversion.
- **Alpha 8:** Frequency-distribution modes for improved high-frequency presence.
- **Alpha 7:** Circular visualizer with configurable fill, crop, alignment, and rotation.
- **Alpha 6:** Independent Bars/Circular tabs and live editing controls.
- **Alpha 5:** Resting-line and soft peak-normalization options.
- **Alpha 4:** Expanded classic bar styles and improved motion response.
- **Alpha 3:** Configurable motion speed and broader spectrum layout.
- **Alpha 2:** Initial visual-style expansion and segmented bars.
- **Alpha 1:** Java/FFmpeg reconstruction of the original Sonic Candle workflow.
