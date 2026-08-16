# Prueba rápida — Alpha 18 (fondos de video)

Esta iteración requiere Java 17 o superior, `ffmpeg` y `ffprobe`. Puedes colocar
`ffmpeg.exe` y `ffprobe.exe` dentro de `tools\` o instalarlos en el `PATH`.

## Caso 1: video con audio incrustado

1. Pulsa **Seleccionar audio o video…** y abre un MP4, MOV, MKV, WebM, AVI, MPEG u OGV con audio.
2. Comprueba que el mismo archivo aparezca como fuente y que el estado indique **Video de fondo activo**.
3. Pulsa **Analizar y previsualizar**.
4. Mueve la línea de tiempo y usa **Reproducir/Pausar**. Video, audio y visualizador deben buscar el mismo instante.
5. Exporta MP4. El resultado debe conservar el video y su audio original junto con el visualizador.

## Caso 2: canción y video silencioso separados

1. Selecciona primero una canción.
2. Pulsa **Seleccionar video de fondo…** y elige un video sin audio.
3. Prueba **Cubrir**, **Contener** y **Estirar**.
4. Si el fondo es más corto, compara **Repetir** con **Congelar último fotograma**.
5. Exporta MP4. Debe escucharse únicamente la canción seleccionada.

El mismo flujo también acepta un video de fondo que posea audio: cuando ya existe
una canción seleccionada, Sonic Candle descarta automáticamente la pista del fondo.

## Transparencia

Exporta ProRes 4444, WebM VP9 o PNG después de configurar un fondo de video. Estas
tres salidas deben omitir el fondo global y conservar solamente el visualizador y,
en Circular, el relleno interior configurado.

Si algo falla, adjunta el archivo más reciente de `logs\` e indica el formato,
duración y resolución del video utilizado.
