# Compilar Sonic Candle 2.0

[Read in English](BUILDING.md)

## Requisitos

- Apache NetBeans con soporte Maven.
- Un JDK completo 17 o superior. Un JRE por sí solo no incluye `jpackage`.
- FFmpeg y FFprobe para ejecutar y probar las funciones de audio.
- Windows y WiX Toolset 3.14 para crear el instalador `.exe`.

Los paquetes nativos deben generarse en su sistema operativo de destino. Por eso el EXE de Windows debe compilarse desde Windows.

## Crear el JAR ejecutable desde NetBeans

1. En NetBeans abre mediante **File > Open Project** o **Archivo > Abrir proyecto** la carpeta que contiene `pom.xml`.
2. En **Project Properties > Libraries** comprueba que la plataforma sea JDK 17 o superior.
3. Selecciona **Run > Clean and Build Project** o presiona **Shift+F11**.
4. NetBeans ejecutará Maven y creará:

   ```text
   target\sonic-candle-2.0.0-alpha.18.jar
   ```

5. Pruébalo desde una terminal abierta en la carpeta del proyecto:

   ```text
   java -jar target\sonic-candle-2.0.0-alpha.18.jar
   ```

El plugin JAR de Maven escribe `com.soniccandle.App` en el manifiesto. Por eso puede abrirse con `java -jar` o mediante doble clic cuando Windows tenga los `.jar` asociados con Java.

## Crear el EXE desde NetBeans

El EXE generado es un instalador gráfico por usuario. Incluye su propio runtime de Java, puede crear accesos directos y no obliga al usuario final a instalar Java.

1. Instala un JDK completo 17 o superior y selecciónalo como plataforma del proyecto en NetBeans.
2. Instala **WiX Toolset 3.14**. `jpackage` necesita WiX para producir instaladores EXE/MSI en Windows.
3. Opcional: copia `ffmpeg.exe` y `ffprobe.exe` dentro de `tools\` antes de empaquetar. Si encuentra ambos, el script los incluirá dentro de la aplicación instalada. Redistribuye solamente compilaciones de FFmpeg cuyas condiciones de licencia puedas cumplir.
4. Ejecuta **Clean and Build Project** en NetBeans.
5. Abre una terminal dentro de la carpeta del proyecto y ejecuta:

   ```text
   package-windows.bat -SkipBuild
   ```

   Si prefieres que el script también ejecute Maven, usa `package-windows.bat` sin `-SkipBuild` y asegúrate de tener `mvn` disponible en `PATH`.

6. Encontrarás los resultados en:

   ```text
   dist\sonic-candle-2.0.0-alpha.18.jar
   dist\windows\sonic-candle-2.0.0-alpha.18-windows-x64.exe
   ```

El instalador no está firmado digitalmente, por lo que Windows SmartScreen puede advertir que su editor es desconocido. Para eliminar esa advertencia de manera confiable hace falta un certificado público de firma de código.

## Compilación automática en GitHub

`.github/workflows/windows-release.yml` repite la compilación en GitHub Actions. Publica el JAR y el EXE como artefactos y crea la primera prerelease `v2.0.0-alpha.17` si todavía no existe.

El EXE automático incluye Java, pero no FFmpeg. Debe instalarse aparte o estar en `PATH`. Una compilación local sí puede incluir FFmpeg colocando antes ambos ejecutables dentro de `tools\`.
