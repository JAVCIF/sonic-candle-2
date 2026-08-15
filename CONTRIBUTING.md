# Contributing to Sonic Candle 2.0

Thank you for helping improve Sonic Candle 2.0. The project values focused changes that preserve its main goal: making music visualizers quick and approachable.

## Before opening a change

1. Search existing issues to avoid duplicates.
2. Keep proposals within the scope of an automated visualizer creator rather than a full video editor.
3. For behavior changes, explain how preview and export should remain consistent.

## Development setup

- Install JDK 17 or newer.
- Install FFmpeg and FFprobe, or place both executables in `tools/`.
- Open the project as Maven in NetBeans or run `mvn clean package`.

## Pull requests

- Keep each pull request focused on one feature or fix.
- Preserve English and Spanish localization for every user-facing string.
- Add or update a dependency-free regression program in `src/test/java` when practical.
- Confirm that MP4 rendering and transparent rendering still use the intended canvas rules.
- Do not commit generated videos, logs, `target/`, `dist/`, or FFmpeg binaries.

## Bug reports

Include your operating system, Java version, FFmpeg version, visualizer mode, export format, reproduction steps, and relevant diagnostic log. Avoid uploading copyrighted songs; use a short generated or freely licensed test clip whenever possible.
