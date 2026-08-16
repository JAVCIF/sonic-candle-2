# Sonic Candle 2.0 — Alpha 18

Continuación experimental de **Sonic Candle**, el generador de videos de espectro musical creado originalmente por Ryan Schroeder y Chris Soderquist.

Esta versión reemplaza la antigua integración con Xuggle por un pipeline basado en FFmpeg y reconstruye el análisis de audio con una FFT propia en Java. La rama 2.0 ofrece edición bilingüe, dos temas visuales y una vista previa audiovisual sincronizada.

[Read in English](README.md)

## Descargar

Descarga el instalador de Windows o el JAR ejecutable más reciente desde [GitHub Releases](https://github.com/JAVCIF/sonic-candle-2/releases).

- El **EXE de Windows** incluye su propio runtime de Java, pero necesita FFmpeg/FFprobe disponibles en `PATH`.
- El **JAR ejecutable** funciona en diferentes sistemas con Java 17 o superior y FFmpeg/FFprobe.

## Funciones incluidas

- Interfaz de escritorio Swing en español e inglés.
- Cambio inmediato entre español e inglés, incluidas opciones, pestañas, mensajes y ayudas emergentes.
- Tema Azul moderno predeterminado y Tema clásico inspirado en la paleta carbón/púrpura de Sonic Candle 1.1.11.
- Cabecera vectorial común con el nombre Sonic Candle, vela, línea, Version 2.0-J y la firma by JavCif & Candle.
- Proyecto Maven listo para abrir en Apache NetBeans.
- Entrada de audio o video mediante FFmpeg. Un video con audio se usa automáticamente como fuente del espectro y como fondo.
- Fondo de color, imagen PNG/JPG/BMP/GIF o video MP4/MOV/MKV/WebM/AVI/MPEG/OGV.
- Un video silencioso puede combinarse con una canción externa; si contiene audio, su pista se ignora cuando ya existe otra fuente seleccionada.
- Ajustes de video Cubrir, Contener o Estirar y final Repetir o Congelar último fotograma.
- Previsualización navegable por la línea de tiempo.
- Reproductor de vista previa con Play/Pausa, audio real, búsqueda mediante slider y reloj visible.
- Sincronización dirigida por el audio, compatible con introducciones simultáneas o previas a la canción.
- 16 a 160 bandas logarítmicas.
- Selector de movimiento Normal, Ágil o Rápido sin alterar la velocidad del audio.
- Espectro Estándar o Intercalado clásico con corrección automática de la segunda barra.
- Distribución Estándar, Equilibrada o Proporcional mediante un selector ampliable.
- Ventana Hann de 2.048 o 4.096 muestras según el modo elegido.
- Piso de ruido por banda y suavizado de ataque y caída.
- Amplitud sin techo artificial: un pico puede superar los bordes del video.
- Picos con desborde libre o contenidos mediante un limitador suave.
- Línea central invisible o punteada durante el reposo.
- Pestañas independientes **Barras**, **Circular**, **Doble barra** y **Barra de carga**, con Barras seleccionada al iniciar.
- Cada pestaña conserva su configuración visual; las opciones no se pisan al cambiar de composición.
- Fondo, estilos, colores, sensibilidad, geometría, rotación e imagen circular actualizados en tiempo real.
- Todos los sliders visuales combinan arrastre y campo numérico editable; la línea de tiempo queda separada.
- Uno o dos círculos, siempre centrados verticalmente y alineables a izquierda, centro o derecha.
- Tamaño circular entre 20 % y 75 %, con reducción automática al usar dos círculos.
- Interior transparente, de color sólido o con imagen recortada mediante zoom y desplazamiento X/Y.
- Rotación de la distribución radial: 0° arriba, 90° derecha, 180° abajo y 270° izquierda.
- Espectro extendido prácticamente de borde a borde.
- Los nueve estilos clásicos: bloque grueso, bloque contorno, línea fina,
  redondeado relleno/contorno, bloque elevado/grabado y óvalo relleno/contorno.
- Bloques segmentados de Alpha 2 conservados como décimo estilo adicional.
- Halo fluido como undécimo estilo, con una silueta continua y redondeada.
- Inversión izquierda/derecha en Barras sin alterar el análisis de audio.
- Doble barra con composiciones En los bordes y Mitades unidas.
- Alcances Golpe bajo, medio y alto, siempre contenidos y separados.
- Inversión independiente de las mitades superior e inferior.
- Doble barra puede mostrar ambas mitades, solo la superior o solo la inferior; una mitad unida se recentra automáticamente.
- Barra de carga horizontal o vertical, normal o invertida, con una o dos unidades.
- Forma cuadrada o redondeada, color de llenado y color de borde independientes.
- Seis estilos de llenado y cinco estilos de borde combinables libremente.
- Respuesta de carga Normal, Alta o Proporcional.
- Animación Normal, Equilibrada o Suavizada con ataque y caída temporales reales.
- Ayudas emergentes en cada control de Barra de carga: cantidad, orientación, posiciones, inversión, forma, estilos, respuesta, animación, sensibilidad y colores.
- Introducción punteada opcional de afuera hacia dentro o de dentro hacia afuera.
- La introducción sólo puede activarse con línea punteada; ambos controles se bloquean mutuamente para impedir configuraciones incompatibles.
- Introducción simultánea con la canción o previa con retraso real del audio.
- Selector de exportación MP4, ProRes 4444, WebM VP9 o secuencia PNG.
- MP4 H.264/AAC conserva el fondo completo configurado.
- ProRes 4444 y WebM VP9 exportan el visualizador con transparencia y audio; omiten el fondo global, pero conservan el relleno o la imagen interior de los círculos.
- Secuencia PNG transparente sin audio, con numeración ordenada y protección contra sobrescrituras.
- 720p o 1080p, a 30 o 60 FPS.
- Progreso y cancelación real del análisis o render.

## Requisitos de desarrollo

- JDK 17 o superior.
- Apache NetBeans con soporte Maven.
- FFmpeg y FFprobe.

## Abrir en NetBeans

1. Descomprime el ZIP completo.
2. En NetBeans selecciona **Archivo > Abrir proyecto**.
3. Selecciona la carpeta `sonic-candle-2.0-alpha18`, que contiene `pom.xml`.
4. Comprueba en las propiedades del proyecto que la plataforma Java sea JDK 17 o superior.
5. Ejecuta el proyecto con el botón **Run**.

La clase principal es `com.soniccandle.App`. El archivo `nbactions.xml` ya incluye la acción necesaria para ejecutarla desde NetBeans.

Las pruebas sin dependencias externas están en `src/test/java`: `SmokeTest`,
`SpectrumResponseTest`, `MotionModeTest`, `InterleaveModeTest`,
`RendererOverflowTest`, `VisualOptionsTest`, `BarStyleRenderTest` y
`CircularRendererTest`, `SliderNumberControlTest`,
`FrequencyDistributionProcessorTest`, `FrequencyDistributionAudioTest` y
`DualBarRendererTest`, `LoadBarRendererTest`, `LoadBarLevelProcessorTest`,
`IntroAnimationRendererTest`, `IntroVideoSmokeTest`, `UiTextTest`,
`PreviewTimelineTest`, `PreviewAudioPlayerTest`, `ThemeReferenceTest`,
`TransparentRendererTest`, `ExportFormatTest`, `MediaProbeTest`,
`PreviewVideoPlayerTest` y `PreviewVideoSmokeTest`.

Después de ejecutar **Clean and Build Project**, NetBeans crea el JAR en `target/sonic-candle-2.0.0-alpha.18.jar`. Puede iniciarse desde una terminal situada en la raíz del proyecto con:

```text
java -jar target/sonic-candle-2.0.0-alpha.18.jar
```

El JAR necesita Java 17 y FFmpeg. El instalador EXE incluye su propio runtime de Java.

Consulta [BUILDING_ES.md](BUILDING_ES.md) para generar desde NetBeans tanto el JAR ejecutable como el instalador EXE de Windows.

## Instalar FFmpeg en Windows

Sonic Candle busca `ffmpeg.exe` y `ffprobe.exe` de las siguientes maneras:

1. Dentro de la carpeta `tools` del proyecto.
2. Mediante las variables `SONIC_CANDLE_FFMPEG` y `SONIC_CANDLE_FFPROBE`.
3. En el `PATH` del sistema.

La opción más sencilla para desarrollar es copiar ambos ejecutables desde la carpeta `bin` de una distribución de FFmpeg hacia:

```text
sonic-candle-2.0-alpha18/
└── tools/
    ├── ffmpeg.exe
    └── ffprobe.exe
```

Los archivos DLL exigidos por la distribución de FFmpeg deben copiarse también si se utiliza una compilación compartida. Una compilación estática normalmente solo necesita los dos ejecutables.

## Flujo interno

1. FFprobe detecta automáticamente si la entrada contiene audio, video o ambas pistas.
2. FFmpeg decodifica la pista de audio compatible a PCM mono de 44.100 Hz.
3. Java aplica una ventana Hann y una FFT radix-2 de 2.048 o 4.096 muestras.
4. El modo Estándar agrupa bandas logarítmicas entre 45 Hz y 16 kHz; el modo Intercalado reproduce el recorrido de datos FFT del programa original.
5. Se descarta el piso de ruido y se calibra con un percentil robusto.
6. El modo de movimiento determina la resolución temporal y la velocidad de ataque/caída sin modificar el audio.
7. Java2D genera fotogramas BGR para fondos estáticos o ABGR con alfa para fondos de video y exportaciones transparentes.
8. Los fotogramas se envían por tubería a FFmpeg. El fondo de video se decodifica y descarta progresivamente, sin almacenarlo completo en RAM.
9. FFmpeg agrega el audio original y produce MP4, ProRes 4444 o WebM VP9; la secuencia PNG se escribe directamente sin audio.

## Modos de movimiento

- **Normal:** conserva la respuesta de Alpha 2, con más continuidad e inercia.
- **Ágil:** opción predeterminada; reacciona y cae más deprisa sin volverse brusca.
- **Rápido:** respuesta casi inmediata para canciones intensas o frenéticas.

Cambiar el modo exige volver a analizar porque modifica la ventana temporal del
espectro. No cambia los FPS, el tono, la duración ni la velocidad de la canción.

## Distribución de frecuencias

- **Estándar:** conserva exactamente el resultado de Alpha 7 y sirve como referencia sin correcciones adicionales.
- **Equilibrado:** mantiene intacto el inicio del espectro y aplica una ganancia progresiva moderada hacia las bandas derechas/agudas. Es la opción recomendada cuando el extremo derecho se percibe demasiado quieto.
- **Proporcional:** analiza los momentos fuertes de cada banda durante toda la canción y calcula una ganancia propia para cada una. Reduce la diferencia entre bandas dominantes y débiles para aprovechar mucho más el ancho disponible.

Las ganancias de Equilibrado y Proporcional son constantes durante toda la canción. Por eso preservan el instante, ataque y caída de cada banda: solo cambia su presencia visual. Un valor cero sigue siendo cero y el programa no inventa frecuencia durante silencios. Como esta opción sí modifica los datos musicales, cambiarla requiere pulsar **Analizar y previsualizar**.

## Edición visual en tiempo real

- **Barras:** es la pestaña predeterminada y contiene únicamente las opciones del visualizador lineal.
- **Circular:** activa el compositor radial y muestra sus opciones de frecuencia, geometría e interior. Ya no depende de un selector escondido ni presenta sus controles bloqueados al entrar.
- **Doble barra:** ofrece dos espectros independientes en vertical; sus ajustes se actualizan en vivo igual que los demás visualizadores.
- **Barra de carga:** resume el nivel musical en un medidor continuo personalizable.
- La imagen, el color o el video de fondo son globales y se actualizan en las cuatro pestañas.
- Cambiar de pestaña, estilo, sensibilidad, color, picos, geometría, relleno, imagen, rotación o encuadre redibuja la vista previa inmediatamente.
- Sensibilidad, rotación, zoom y posición X/Y combinan slider y número editable para recuperar valores exactos.
- **Analizar y previsualizar** se reserva para generar o recalcular los fotogramas musicales. Los ajustes puramente visuales no repiten la FFT.
- FPS, cantidad de bandas, Movimiento y modo de Espectro sí requieren volver a analizar porque modifican los datos musicales.
- Las cuatro pestañas conservan configuraciones visuales independientes.

## Idioma, apariencia y fondo

- **Idioma:** Español es el valor inicial; English traduce en vivo toda la interfaz y las opciones de los selectores, sin reiniciar ni perder ajustes.
- **Azul moderno:** tema predeterminado de fondos claros, cabecera azul y alto contraste para una edición prolongada.
- **Tema clásico:** recrea el ambiente oscuro del Sonic Candle original con carbón, blanco y acentos púrpura.
- Ambos temas comparten el mismo logotipo vectorial y la identificación **Version 2.0-J — by JavCif & Candle**.
- La sección Archivos muestra el nombre real del recurso elegido bajo **Seleccionar imagen de fondo**; ya no confunde una imagen ausente con el texto Color sólido.
- **Usar color como fondo** selecciona un color y desactiva la imagen o video actual. El estado inferior distingue **Color sólido**, **Imagen** y **Video de fondo activo**.
- **Seleccionar audio o video** acepta un videoclip con pista incrustada sin exigir una extracción manual. Si la entrada posee ambas pistas, el video se activa también como fondo.
- **Seleccionar video de fondo** admite material silencioso o con audio. Una canción seleccionada por separado siempre tiene prioridad y la pista del fondo se descarta.
- Los fondos de video se previsualizan desde el punto actual del slider y se reproducen por streaming junto con la canción.

## Vista previa audiovisual

- Tras **Analizar y previsualizar**, el botón **Reproducir** inicia imagen y audio desde el punto actual del slider.
- **Pausar** conserva la posición; al volver a reproducir, FFmpeg busca ese instante sin modificar el archivo original.
- Arrastrar el slider durante la reproducción realiza una nueva búsqueda y resincroniza ambos medios.
- Una vez abierto el dispositivo de sonido, su posición se convierte en el reloj maestro para evitar que la animación se adelante al audio.
- En una introducción **Antes de la canción**, la línea se anima primero en silencio y el audio entra exactamente al terminar el preroll. En **Junto con la canción**, ambos comienzan en el segundo cero.
- La previsualización utiliza el mismo fotograma, configuración visual y procesamiento temporal que la exportación MP4.

## Composición Doble barra

- **En los bordes:** la barra superior nace en el marco y apunta hacia abajo; la inferior nace en el marco y apunta hacia arriba.
- **Mitades unidas:** ambas nacen junto a la línea central y crecen en direcciones opuestas, como una onda partida por su eje.
- **Golpe bajo:** mantiene cada espectro en una franja reducida.
- **Golpe medio:** ocupa una zona intermedia sin dominar el fondo.
- **Golpe alto:** permite acercarse al centro o a los bordes, según la composición, conservando siempre un margen de seguridad.
- **Invertir barra superior/inferior:** cambia graves por agudos de manera independiente; se pueden invertir ninguna, una o ambas.
- **Barras visibles:** permite dejar ambas, solo la superior o solo la inferior. En Mitades unidas, la mitad superviviente nace exactamente en el centro y conserva su dirección de golpe.
- Los picos usan contención suave obligatoria en esta composición, incluso con sensibilidad elevada.

## Barra de carga

- **Horizontal:** llena de izquierda a derecha y puede invertirse; una barra se alinea arriba, al centro o abajo sin pegarse al marco.
- **Vertical:** llena de abajo hacia arriba y puede invertirse; una barra se alinea a izquierda, centro o derecha.
- **Cantidad:** con dos barras, la posición pasa a ser automática. Horizontal usa arriba/abajo y Vertical usa izquierda/derecha.
- **Forma:** cuadrada o redondeada.
- **Estilo de barra:** Predeterminado, Bloque grueso, Bloque elevado, Bloque grabado, Bloque segmentado o Halo fluido.
- **Estilo de borde:** Predeterminado, Bloque grueso, Bloque elevado, Bloque grabado o Bloque segmentado. Se combina de forma independiente con cualquier estilo de barra.
- **Colores:** el llenado y el borde son independientes; el interior todavía vacío deja ver el fondo.
- **Tipo de carga — Normal:** conserva la respuesta de Alpha 10.
- **Tipo de carga — Alta:** da más recorrido a los golpes fuertes y los acerca al borde seguro sin desbordar.
- **Tipo de carga — Proporcional:** usa el rango dinámico de la canción para repartir mejor el recorrido disponible, sin inventar actividad durante el silencio.
- **Animación — Normal:** sigue cada fotograma analizado sin suavizado adicional.
- **Animación — Equilibrada:** suaviza ataque y caída de forma moderada.
- **Animación — Suavizada:** añade más inercia para un movimiento pausado y fluido, manteniendo los mismos golpes musicales.
- El nivel combina las bandas más activas con el pico del fotograma, respeta Movimiento, Espectro y Distribución, y permanece vacío durante el silencio.

## Introducción punteada

Disponible en Barras y en Doble barra cuando la composición es **Mitades unidas**. Al activarla, el reposo punteado se vuelve obligatorio. Si la línea en reposo está en Invisible, el selector de introducción queda bloqueado; al habilitar una introducción, el selector de reposo queda bloqueado en Punteada.

- **De afuera hacia dentro:** dos trazos parten de los extremos y se encuentran en el centro.
- **De dentro hacia afuera:** el proceso inverso parte del centro y alcanza los extremos.
- Tras armarse, la línea parpadea y se asienta como línea punteada antes de liberar la frecuencia.
- **Junto con la canción:** el audio comienza en el segundo cero, pero la frecuencia espera hasta terminar la animación.
- **Antes de la canción:** primero se reproduce la animación; luego comienza el audio y la frecuencia desde su fotograma inicial. El video se alarga exactamente lo necesario.
- La duración se controla en milisegundos con slider y campo numérico.

## Composición circular

- **Cantidad:** uno o dos círculos. Ambos reutilizan el mismo espectro y la misma configuración interior.
- **Alineación:** mueve el grupo a izquierda, centro o derecha sin cambiar su centro vertical. Centro es ahora el valor predeterminado.
- **Tamaño:** ajusta el disco interior; el límite de 75 % conserva una corona útil para la frecuencia. Al elegir dos círculos, el cálculo reduce su radio exterior para evitar solapes y desbordes.
- **Interior transparente:** deja visible el fondo del video dentro del círculo.
- **Interior de color:** rellena el disco con un color independiente.
- **Interior con imagen:** aplica un recorte circular tipo “cubrir”. Zoom y Posición X/Y permiten reencuadrar imágenes rectangulares sin deformarlas.
- **Rotación:** decide dónde comienza el recorrido de frecuencias alrededor del borde.
- **Halo fluido:** convierte las bandas en una membrana continua suavizada, con un brillo tenue exterior.

El selector **Picos** también rige el modo circular: Normalizar mantiene la frecuencia dentro del radio seguro; Desborde libre permite que los picos salgan del fotograma.

## Opciones visuales heredadas de Alpha 4

- **Línea en reposo — Invisible:** no dibuja nada hasta que una banda recibe energía.
- **Línea en reposo — Punteada:** cada banda deja una marca mínima en el centro.
- **Picos — Desborde libre:** conserva la amplitud completa y permite salir del fotograma.
- **Picos — Normalizar picos:** no modifica amplitudes pequeñas; comprime progresivamente las grandes para aproximarlas al borde sin tocarlo ni formar una meseta plana.
- **Espectro — Intercalado clásico:** reproduce el muestreo peculiar del Sonic Candle original, concentrado en frecuencias bajas y con una distribución más irregular. Requiere volver a analizar.

## Limitaciones conocidas de Alpha 18

- El análisis completo se guarda en memoria; canciones extremadamente largas consumirán más RAM.
- La reproducción previa necesita un dispositivo de salida de audio disponible en el sistema.
- Por ahora FFmpeg debe instalarse o copiarse manualmente.
- ProRes 4444 prioriza fidelidad y puede generar archivos considerablemente más grandes que MP4 o WebM.
- Las secuencias PNG no contienen audio; deben sincronizarse con la pista original en el editor.
- No existe todavía instalador ni ejecutable portable con runtime Java incluido.

## Licencia y atribución

Distribuido bajo Apache License 2.0. Consulta `LICENSE` y `NOTICE`.

Sonic Candle original: Ryan Schroeder y Chris Soderquist. Esta continuación conserva el nombre, la atribución histórica y el espíritu del proyecto original.
