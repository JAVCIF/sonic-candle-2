# Changelog

All notable changes to Sonic Candle 2.0 are documented here.

[Historial completo en español](CHANGELOG_ES.md)

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
