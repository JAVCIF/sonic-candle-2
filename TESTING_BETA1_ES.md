# Guía de prueba — Sonic Candle 2.0 Beta 1.0

Beta 1.0 congela el conjunto completo de funciones construido hasta Alpha 20 y abre la etapa de pruebas comunitarias. No busca agregar más funciones por ahora: el objetivo es detectar errores reales, problemas de compatibilidad y ajustes de experiencia de uso.

## Inicio rápido

1. Descarga el JAR o EXE desde GitHub Releases.
2. El JAR requiere Java 17 o superior; el EXE incluye su propio runtime.
3. Asegúrate de que `ffmpeg` y `ffprobe` estén en `PATH` o disponibles junto al programa.
4. Selecciona audio o video, configura un visualizador y pulsa **Analizar y previsualizar**.
5. Prueba Play/Pausa, búsqueda en la línea de tiempo y al menos una exportación completa.

## Cobertura recomendada

- Probar Barras, Circular, Doble barra, Barra de carga, Electrocardiógrafo y Neon Wave.
- Cambiar entre Español/English y Azul moderno/Tema clásico.
- Usar fondo de color, imagen y video; probar también video con audio incrustado y video silencioso con canción externa.
- Exportar MP4 y, si se utiliza edición posterior, ProRes 4444, WebM VP9 o secuencia PNG transparente.
- Confirmar que vista previa y exportación conservan ritmo, geometría, color, fondos y transparencia.
- En Neon Wave, comprobar la línea de nodos durante silencios, los ecos, el halo, las partículas, la inversión y las tres posiciones.

## Reportes

Incluye versión de Sonic Candle, sistema operativo, Java, FFmpeg, formato de entrada/salida, visualizador, configuración utilizada y pasos exactos. Si existe, adjunta también el registro más reciente de `logs/`.
