# Building Sonic Candle 2.0

[Leer en español](BUILDING_ES.md)

## Requirements

- Apache NetBeans with Maven support.
- A full JDK 17 or newer. A JRE alone does not include `jpackage`.
- FFmpeg and FFprobe for running and testing audio features.
- Windows and WiX Toolset 3.14 when creating the `.exe` installer.

Native packages must be created on their target operating system. Therefore, the Windows EXE must be built on Windows.

## Executable JAR from NetBeans

1. Open the directory containing `pom.xml` through **File > Open Project**.
2. Open **Project Properties > Libraries** and confirm that the Java Platform is JDK 17 or newer.
3. Select **Run > Clean and Build Project**, or press **Shift+F11**.
4. NetBeans runs Maven and creates:

   ```text
   target/sonic-candle-2.0.0-alpha.18.jar
   ```

5. Test it from a terminal opened in the project directory:

   ```text
   java -jar target\sonic-candle-2.0.0-alpha.18.jar
   ```

The Maven JAR plugin writes `com.soniccandle.App` into the manifest, so the package can be launched with `java -jar` or by double-clicking it when `.jar` files are associated with Java.

## Windows EXE from NetBeans

The generated EXE is a graphical per-user installer. It includes a private Java runtime, creates optional Start Menu and desktop shortcuts, and does not require the final user to install Java.

1. Install a full JDK 17 or newer and configure it as the project's Java Platform in NetBeans.
2. Install **WiX Toolset 3.14**. `jpackage` uses WiX to create Windows EXE/MSI installers.
3. Optional: copy `ffmpeg.exe` and `ffprobe.exe` into `tools\` before packaging. When both are present, the script bundles them inside the installed application. Only redistribute FFmpeg builds whose license terms you comply with.
4. Run **Clean and Build Project** in NetBeans.
5. Open a terminal in the project directory and run:

   ```text
   package-windows.bat -SkipBuild
   ```

   To let the script run Maven itself, use `package-windows.bat` without `-SkipBuild` and make sure `mvn` is available in `PATH`.

6. The results are written to:

   ```text
   dist\sonic-candle-2.0.0-alpha.18.jar
   dist\windows\sonic-candle-2.0.0-alpha.18-windows-x64.exe
   ```

The installer is not code-signed, so Windows SmartScreen can show an unknown-publisher warning. A public code-signing certificate is required to remove that warning reliably.

## Automated GitHub build

`.github/workflows/windows-release.yml` repeats the Windows build on GitHub Actions. It publishes the JAR and EXE as workflow artifacts and creates the first `v2.0.0-alpha.17` pre-release when that release does not already exist.

The automated EXE includes Java but not FFmpeg. Install FFmpeg separately or place it in `PATH`. Local Windows builds can bundle FFmpeg by placing both executables in `tools\` first.
