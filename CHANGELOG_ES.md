# Changelog

## 2.0.0-alpha.18 — 2026-08-15

- Detección automática mediante FFprobe de entradas con solo audio, video silencioso o video con audio.
- El selector principal admite un video con pista incrustada y utiliza automáticamente tanto su sonido como su imagen.
- Selector independiente de video de fondo para combinar una canción con material silencioso o descartar el audio del fondo cuando ya existe otra fuente.
- Ajustes Cubrir, Contener y Estirar, además de los finales Repetir y Congelar último fotograma.
- Los fondos de video se decodifican progresivamente mediante FFmpeg y nunca se almacenan completos en memoria.
- MP4 compone el visualizador Java transparente sobre el video y conserva únicamente la fuente de audio elegida.
- ProRes 4444, VP9 y PNG continúan omitiendo todo fondo global, incluido el video.
- La vista previa audiovisual busca y reproduce el fondo desde la misma posición temporal que el audio.
- Pruebas nuevas para comandos, detección de pistas, decodificación real de preview, repetición, congelado y exportación MP4 completa.

## 2.0.0-alpha.17 — 2026-08-15

- Compilaciones reproducibles para JAR ejecutable e instalador EXE de Windows.
- Nuevo script de empaquetado amigable con NetBeans basado en `jpackage` y WiX 3.14.
- El instalador incluye su propio runtime de Java y puede integrar opcionalmente los ejecutables FFmpeg/FFprobe aportados por el usuario en `tools/`.
- Automatización de GitHub Actions que publica JAR y EXE como artefactos y crea la prerelease de Alpha 17.
- Guías de compilación en inglés y español, icono multirresolución de Windows y detección de FFmpeg dentro de aplicaciones empaquetadas.
- Nuevo selector de salida con MP4, ProRes 4444, WebM VP9 y secuencia PNG.
- MP4 conserva el fondo de color o imagen, los rellenos y toda la composición configurada.
- ProRes 4444 exporta transparencia de máxima calidad con video `prores_ks`, perfil 4444 y audio PCM; se advierte que sus archivos pueden ser muy grandes.
- WebM VP9 ofrece transparencia comprimida y más ligera con audio Opus para composición y publicación habitual.
- ProRes, VP9 y PNG omiten el fondo global y conservan solamente el visualizador; en Circular mantienen el color o la imagen interior cuando fueron configurados y respetan el interior transparente.
- Recuperada la exportación clásica como secuencia PNG con alfa, nombres ordenados y sin audio.
- La secuencia PNG no sobrescribe fotogramas existentes y elimina de forma segura únicamente los creados por un intento cancelado o fallido.
- Selector, botones, avisos de tamaño/calidad, filtros y diálogos disponibles en español e inglés.
- Pruebas nuevas para composición alfa, rellenos circulares, comandos de códec, secuencias seguras y exportaciones reales verificadas con FFmpeg/FFprobe.

## 2.0.0-alpha.16 — 2026-08-15

- Diálogos de completado y error compactados para evitar ventanas sobredimensionadas en mensajes breves.
- Tamaño mínimo reducido y cálculo dinámico basado en el ancho real del texto, no en una cantidad fija de columnas.
- Mensajes cortos permanecen en una sola línea; rutas y trazas largas crecen o se ajustan únicamente cuando es necesario.
- Se conservan el botón púrpura, el contraste temático, la localización y el centrado de Alpha 15.
- Prueba adicional que impide que un error breve vuelva a reservar un área de texto excesiva.

## 2.0.0-alpha.15 — 2026-08-15

- Ventanas de completado y error integradas con las paletas Moderna y Clásica.
- Botón Aceptar resaltado con el color de acento; se elimina el texto claro sobre botón claro del tema oscuro.
- Mensajes con ajuste automático de línea para rutas largas y detalles técnicos del registro.
- Texto, fondo, bordes e iconos conservan contraste correcto sin depender de los colores predeterminados de Swing/Metal.
- Diálogos modales centrados sobre Sonic Candle y con un tamaño mínimo cómodo.
- Botón localizado como Aceptar u OK según el idioma activo.
- Nueva prueba visual de completado y prueba estructural equivalente para errores.

## 2.0.0-alpha.14 — 2026-08-15

- Selector de color integrado por completo con los temas de Sonic Candle; el diálogo ya no conserva paneles, tabs ni botones claros incompatibles con el tema oscuro.
- Tabs Muestras, HSV, HSL, RGB y CMYK con contraste correcto y nombres localizados en Español/English.
- Vista previa simplificada con muestra grande, valor hexadecimal y componentes RGB legibles.
- Botón Aceptar resaltado con el color de acento y botones Cancelar/Restablecer coherentes con la paleta activa.
- El diálogo conserva el color anterior al cancelar y continúa actualizando la vista principal en tiempo real únicamente al aceptar.
- Nueva prueba visual y funcional dedicada al selector clásico, incluida la actualización de HEX/RGB.

## 2.0.0-alpha.13 — 2026-08-15

- Tema clásico corregido para eliminar los azules heredados de Swing/Metal en selectores, flechas, bordes y barras de desplazamiento.
- Pestañas rediseñadas con estados activo, inactivo y bloqueado de alto contraste; ya no aparece texto blanco sobre fondo blanco.
- Línea de tiempo con pista clara, progreso púrpura y perilla circular púrpura en el tema clásico.
- El clic directo sobre cualquier punto de la línea de tiempo salta ahora al instante exacto y permite continuar arrastrando desde el mismo gesto.
- Nuevo registro técnico rotativo en `logs/sonic-candle-0.log`, con cinco archivos de hasta 2 MiB y destino alternativo si la carpeta del programa no es escribible.
- Captura de excepciones no controladas, errores de análisis/render, imágenes, audio de vista previa e inicialización gráfica, incluyendo trazas completas.
- Los diálogos de fallos técnicos indican la ruta del registro para facilitar reportes futuros en GitHub.
- La cabecera conserva únicamente `Version 2.0-J` y `by JavCif & Candle`, sin notas de cambios añadidas.
- Nuevas pruebas automatizadas de seek por clic, pintores temáticos y escritura de trazas en el log.

## 2.0.0-alpha.12 — 2026-08-15

- Nueva interfaz bilingüe Español/English con cambio inmediato y sin perder la configuración actual.
- Traducción de pestañas, etiquetas, botones, estados, diálogos, tooltips y todas las opciones de los combobox.
- Tooltips completos para cada control de Barra de carga.
- Nuevo tema Azul moderno predeterminado, con fondos claros y cabecera azul.
- Nuevo Tema clásico inspirado en la interfaz carbón/púrpura de Sonic Candle 1.1.11.
- Cabecera vectorial compartida: Sonic Candle, vela, línea, Version 2.0-J y by JavCif & Candle.
- La selección de fondo separa el nombre del recurso de imagen y el estado de color sólido.
- Elegir un color de fondo desactiva explícitamente la imagen y actualiza la vista en tiempo real.
- Reproductor de vista previa con Play/Pausa, audio decodificado por FFmpeg y reloj de reproducción.
- Búsqueda mediante slider durante la reproducción con reinicio limpio del audio en el instante elegido.
- Audio como reloj maestro una vez abierto el dispositivo para evitar deriva visual.
- Soporte correcto del silencio previo cuando la introducción ocurre antes de la canción.
- Pruebas de diccionario bilingüe, traducción de enums, paletas, cabecera, comandos PCM, seek, preroll y límites temporales.

## 2.0.0-alpha.11 — 2026-08-15

- Doble barra conserva ahora una marca punteada visible aun con silencio o energía positiva casi nula.
- La introducción y la línea de reposo aplican exclusión mutua: Invisible desactiva y bloquea la introducción; una introducción activa fija Punteada.
- Barra de carga incorpora seis estilos de llenado: Predeterminado, Bloque grueso, Bloque elevado, Bloque grabado, Bloque segmentado y Halo fluido.
- El borde de Barra de carga dispone de cinco estilos independientes y combinables con cualquier relleno.
- Nuevo Tipo de carga Normal, Alta y Proporcional, siempre limitado al área segura del video.
- Nueva Animación Normal, Equilibrada y Suavizada con coeficientes temporales ajustados a 30 o 60 FPS.
- El procesamiento temporal se comparte entre vista previa y exportación para que el resultado final coincida con lo editado.
- Pruebas de las 30 combinaciones de estilos, silencio, rango proporcional, ataque/caída, reposo punteado e introducciones incompatibles.

## 2.0.0-alpha.10 — 2026-08-15

- Doble barra permite mostrar ambas mitades, solo la superior o solo la inferior.
- Una mitad única en Mitades unidas se centra automáticamente y conserva su dirección vertical.
- Nuevo cuarto tab Barra de carga.
- Barra de carga horizontal con posiciones arriba, centro y abajo e inversión derecha/izquierda.
- Barra de carga vertical con posiciones izquierda, centro y derecha e inversión arriba/abajo.
- Una o dos barras; al duplicarlas se colocan automáticamente en lados opuestos.
- Formas cuadrada y redondeada, con colores independientes de llenado y borde.
- Medición robusta del nivel conjunto mediante bandas activas y pico del fotograma.
- Circular ahora inicia con alineación Centro seleccionada explícitamente.
- Nueva introducción punteada en Barras y Mitades unidas.
- Armado de afuera hacia dentro o de dentro hacia afuera, seguido de parpadeo y reposo punteado.
- Modos Junto con la canción y Antes de la canción con duración editable.
- El modo previo extiende el video y retrasa realmente el audio; el sincronizado conserva la duración original.
- Línea de tiempo ampliada automáticamente cuando la introducción ocurre antes del audio.
- Pruebas visuales, geométricas y de exportación MP4 para todas las funciones nuevas.

## 2.0.0-alpha.9 — 2026-08-15

- Nueva pestaña Doble barra junto a Barras y Circular.
- Composición En los bordes: espectros anclados arriba y abajo que crecen hacia el centro.
- Composición Mitades unidas: dos espectros que nacen junto al eje central y crecen hacia afuera.
- Alcances Golpe bajo, Golpe medio y Golpe alto con separación geométrica garantizada.
- Contención suave obligatoria de picos en Doble barra, independiente de la sensibilidad.
- Inversión izquierda/derecha separada para la barra superior y la inferior.
- Nueva opción Invertir izquierda/derecha en la pestaña Barras.
- Los once estilos existentes, incluido Halo fluido, funcionan en ambas composiciones dobles.
- Vista previa en tiempo real para todos los controles nuevos.
- Pruebas automáticas de estilos, límites, alcances e inversión independiente.

## 2.0.0-alpha.8 — 2026-08-15

- Nuevo selector Distribución en Video y espectro.
- Modo Estándar completamente compatible con el resultado de Alpha 7.
- Modo Equilibrado con ganancia progresiva moderada hacia las bandas derechas/agudas.
- Modo Proporcional con referencia estadística por banda calculada sobre toda la canción.
- Ganancias temporales constantes para preservar ataque, caída y cadencia.
- Suavizado espacial de ganancias proporcionales para evitar saltos bruscos entre bandas vecinas.
- Compensación robusta de energía en Proporcional para evitar inflar innecesariamente los picos globales.
- Los silencios y bandas realmente vacías permanecen en cero.
- Pruebas sintéticas y con audio real de presencia derecha y conservación del ritmo.

## 2.0.0-alpha.7 — 2026-08-15

- Restauración de la edición visual en tiempo real sin recalcular el espectro.
- El fondo global vuelve a actualizarse inmediatamente en Barras y Circular.
- Imagen interior, encuadre, geometría, rotación, estilo y colores circulares en vivo.
- Nuevo control combinado slider + número para ambas sensibilidades, rotación, zoom y posición X/Y.
- Rotación circular convertida de spinner aislado a slider con valor numérico exacto.
- Analizar y previsualizar queda reservado para generar los fotogramas musicales.
- Diálogo de video terminado centrado explícitamente sobre la ventana principal.
- Prueba automática de sincronización bidireccional de los controles numéricos.

## 2.0.0-alpha.6 — 2026-08-15

- Sustitución del selector Lineal/Circular por pestañas Barras y Circular.
- Pestaña Barras seleccionada de forma predeterminada.
- Activación automática de todos los controles circulares al entrar en su pestaña.
- Configuración visual independiente por pestaña: estilo, reposo, picos, sensibilidad y color.
- Cambio de pestaña con actualización inmediata de la vista previa.
- Los controles internos quedan pendientes hasta pulsar Analizar y previsualizar.
- La línea de tiempo conserva la última configuración aplicada y no filtra cambios pendientes.
- Panel circular desplazable para reducir la altura ocupada por la configuración.

## 2.0.0-alpha.5 — 2026-08-15

- Nuevo selector de composición Lineal o Circular.
- Uno o dos círculos con alineación horizontal y centrado vertical automático.
- Tamaño seguro de 20 % a 75 % y geometría reducida automáticamente al usar dos círculos.
- Interior transparente, de color sólido o con imagen recortada.
- Controles de zoom y desplazamiento X/Y para reencuadrar imágenes rectangulares.
- Rotación radial de 0° a 359°.
- Los diez estilos previos adaptados al crecimiento desde el borde exterior del círculo.
- Nuevo estilo Halo fluido, continuo, suavizado y con brillo exterior.
- Pruebas automáticas de límites, solapes, rellenos, recorte y estilos radiales.

## 2.0.0-alpha.4 — 2026-08-15

- Línea central seleccionable entre Invisible y Punteada.
- Picos seleccionables entre Desborde libre y Normalizar picos.
- Nuevo limitador suave y monótono que conserva diferencias entre picos sin tocar los bordes.
- Nuevo espectro Intercalado clásico basado en el recorrido FFT del Sonic Candle original.
- Corrección de la segunda barra integrada automáticamente en el modo intercalado.
- Panel de configuración desplazable para alojar las nuevas opciones.
- Pruebas automáticas de silencio, contención de picos e intercalado.

## 2.0.0-alpha.3 — 2026-08-15

- Nuevo selector de movimiento Normal, Ágil y Rápido.
- Modo Ágil seleccionado de forma predeterminada.
- Ventana FFT y ataque/caída adaptados a cada modo sin alterar el audio.
- Recuperación de los nueve estilos de barras del Sonic Candle original.
- Conservación de los bloques segmentados de Alpha 2 como estilo adicional.
- Pruebas automáticas de respuesta temporal y renderizado de todos los estilos.

## 2.0.0-alpha.2 — 2026-08-14

- Sustitución de la compresión `log1p` por amplitud FFT calibrada.
- Ventana FFT ampliada a 4.096 muestras para separar mejor las bandas graves.
- Bandas logarítmicas sin reutilizar el mismo bin de frecuencia.
- Piso de ruido individual y referencia robusta para conservar la cadencia entre pasajes suaves y fuertes.
- Eliminación del límite artificial de amplitud: los picos pueden salir del fotograma.
- Espectro horizontal extendido prácticamente de borde a borde.
- Sensibilidad predeterminada reajustada al nuevo motor.

## 2.0.0-alpha.1 — 2026-08-14

- Primera base de la resurrección de Sonic Candle.
- Migración de Gradle antiguo a un proyecto Maven para Java 17.
- Nueva interfaz Swing compatible con NetBeans.
- Sustitución conceptual de Xuggle por FFmpeg y FFprobe.
- Decodificación multiformato a PCM flotante.
- Nueva FFT radix-2 implementada en Java.
- Ventana Hann, bandas logarítmicas y suavizado temporal.
- Previsualización navegable.
- Fondos de color o imagen.
- Cuatro estilos iniciales de barras.
- Exportación a MP4 H.264/AAC.
- Progreso y cancelación de tareas.
