package com.soniccandle.ui;

import java.util.HashMap;
import java.util.Map;

/** Diccionario ligero para cambiar toda la interfaz sin reiniciar el programa. */
public final class UiText {

    private static final Map<String, String> ES = new HashMap<>();
    private static final Map<String, String> EN = new HashMap<>();
    private static final Map<String, String> ENUM_EN = new HashMap<>();
    private static AppLanguage language = AppLanguage.SPANISH;

    static {
        add("settings.language", "Idioma", "Language");
        add("settings.theme", "Tema", "Theme");
        add("section.files", "Archivos", "Files");
        add("section.videoSpectrum", "Video y espectro", "Video and spectrum");
        add("section.visualizer", "Visualizador", "Visualizer");
        add("section.dottedIntro", "Introducción punteada", "Dotted intro");
        add("section.doubleFrequency", "Frecuencia doble", "Dual frequency");
        add("section.layout", "Disposición", "Layout");
        add("section.composition", "Composición", "Composition");
        add("section.appearance", "Apariencia", "Appearance");
        add("section.radialFrequency", "Frecuencia radial", "Radial frequency");
        add("section.geometry", "Geometría", "Geometry");
        add("section.interior", "Interior", "Interior");

        add("button.audio", "Seleccionar audio…", "Select audio…");
        add("button.backgroundImage", "Seleccionar imagen de fondo…", "Select background image…");
        add("button.backgroundColor", "Usar color como fondo…", "Use color as background…");
        add("button.output", "Elegir video de salida…", "Choose output video…");
        add("button.analyze", "Analizar y previsualizar", "Analyze and preview");
        add("button.render", "Renderizar MP4", "Render MP4");
        add("button.outputFile", "Elegir archivo de salida…", "Choose output file…");
        add("button.outputFolder", "Elegir carpeta de salida…", "Choose output folder…");
        add("button.renderMp4", "Renderizar MP4", "Render MP4");
        add("button.renderProRes", "Renderizar ProRes 4444", "Render ProRes 4444");
        add("button.renderWebm", "Renderizar WebM VP9", "Render WebM VP9");
        add("button.renderPng", "Exportar secuencia PNG", "Export PNG sequence");
        add("button.cancel", "Cancelar", "Cancel");
        add("button.choose", "Elegir…", "Choose…");
        add("button.circleImage", "Seleccionar imagen…", "Select image…");
        add("button.play", "▶ Reproducir", "▶ Play");
        add("button.pause", "⏸ Pausar", "⏸ Pause");

        add("state.noAudio", "Ningún audio seleccionado", "No audio selected");
        add("state.noBackgroundImage", "Ningún recurso de imagen seleccionado", "No image resource selected");
        add("state.solidBackground", "Color sólido activo", "Solid color active");
        add("state.imageBackground", "Imagen de fondo activa", "Background image active");
        add("state.noOutput", "Ninguna salida seleccionada", "No output selected");
        add("state.noCircleImage", "Ninguna", "None");

        add("tab.bars", "Barras", "Bars");
        add("tab.circular", "Circular", "Circular");
        add("tab.dual", "Doble barra", "Dual bar");
        add("tab.load", "Barra de carga", "Loading bar");

        add("label.resolution", "Resolución", "Resolution");
        add("label.fps", "FPS", "FPS");
        add("label.bands", "Bandas", "Bands");
        add("label.motion", "Movimiento", "Motion");
        add("label.spectrum", "Espectro", "Spectrum");
        add("label.distribution", "Distribución", "Distribution");
        add("label.outputFormat", "Formato de salida", "Output format");
        add("label.style", "Estilo", "Style");
        add("label.restingLine", "Línea en reposo", "Idle line");
        add("label.restingBorder", "Borde en reposo", "Idle edge");
        add("label.peaks", "Picos", "Peaks");
        add("label.sensitivity", "Sensibilidad", "Sensitivity");
        add("label.color", "Color", "Color");
        add("label.start", "Inicio", "Start");
        add("label.direction", "Dirección", "Direction");
        add("label.durationMs", "Duración (ms)", "Duration (ms)");
        add("label.composition", "Composición", "Composition");
        add("label.reach", "Alcance", "Reach");
        add("label.visibleBars", "Barras visibles", "Visible bars");
        add("label.alwaysContained", "Siempre contenidos", "Always contained");
        add("label.count", "Cantidad", "Count");
        add("label.layout", "Disposición", "Orientation");
        add("label.horizontalPosition", "Posición horizontal", "Horizontal position");
        add("label.verticalPosition", "Posición vertical", "Vertical position");
        add("label.shape", "Forma", "Shape");
        add("label.barStyle", "Estilo de barra", "Bar style");
        add("label.borderStyle", "Estilo de borde", "Border style");
        add("label.loadType", "Tipo de carga", "Loading response");
        add("label.animation", "Animación", "Animation");
        add("label.fillColor", "Color de llenado", "Fill color");
        add("label.borderColor", "Color del borde", "Border color");
        add("label.alignment", "Alineación", "Alignment");
        add("label.sizePercent", "Tamaño (%)", "Size (%)");
        add("label.rotation", "Rotación (°)", "Rotation (°)");
        add("label.fill", "Relleno", "Fill");
        add("label.innerColor", "Color interior", "Interior color");
        add("label.image", "Imagen", "Image");
        add("label.file", "Archivo", "File");
        add("label.zoom", "Zoom", "Zoom");
        add("label.positionX", "Posición X", "X position");
        add("label.positionY", "Posición Y", "Y position");

        add("check.reverseBars", "Invertir izquierda/derecha", "Reverse left/right");
        add("check.reverseTop", "Invertir barra superior", "Reverse top bar");
        add("check.reverseBottom", "Invertir barra inferior", "Reverse bottom bar");
        add("check.reverseLoad", "Invertir llenado", "Reverse fill");
        add("note.oppositeBars", "Dos barras se ubican automáticamente en lados opuestos.",
                "Two bars are placed automatically on opposite sides.");
        add("note.exportMp4", "Incluye el fondo y todas las imágenes configuradas.",
                "Includes the background and every configured image.");
        add("note.exportProRes", "Alfa de máxima calidad; genera archivos muy grandes.",
                "Maximum-quality alpha; creates very large files.");
        add("note.exportWebm", "Alfa comprimido y mucho más liviano que ProRes.",
                "Compressed alpha and much lighter than ProRes.");
        add("note.exportPng", "Fotogramas PNG con alfa; la secuencia no contiene audio.",
                "PNG frames with alpha; the sequence contains no audio.");

        add("tip.motion", "Normal conserva más inercia; Ágil reacciona antes; Rápido sigue golpes breves.",
                "Normal keeps more inertia; Agile reacts sooner; Fast follows short hits.");
        add("tip.spectrum", "Intercalado clásico recrea el indexado irregular del Sonic Candle original.",
                "Classic interleaving recreates the irregular indexing of the original Sonic Candle.");
        add("tip.distribution", "Estándar no altera las bandas; Equilibrado refuerza la derecha; Proporcional aprovecha toda la canción.",
                "Standard leaves bands unchanged; Balanced strengthens the right; Proportional uses the whole song.");
        add("tip.restingLine", "Elige si las barras sin energía dejan puntos visibles en el centro.",
                "Choose whether silent bars leave visible dots at the center.");
        add("tip.peaks", "Normalizar picos los acerca al borde con una curva suave sin desbordarlos.",
                "Normalize brings peaks near the edge with a soft curve without overflowing.");
        add("tip.circleRest", "Elige si el borde deja una marca mínima durante el reposo.",
                "Choose whether the edge leaves a minimum mark while idle.");
        add("tip.circlePeaks", "Normalizar mantiene los picos dentro del radio seguro.",
                "Normalize keeps peaks inside the safe radius.");
        add("tip.circleSize", "El máximo de 75 % reserva espacio real para la frecuencia.",
                "The 75% maximum reserves actual room for the spectrum.");
        add("tip.barSensitivity", "Sensibilidad porcentual de las barras.", "Percentage sensitivity of the bars.");
        add("tip.circleSensitivity", "Sensibilidad porcentual de la frecuencia circular.", "Percentage sensitivity of the circular spectrum.");
        add("tip.circleRotation", "0° arriba, 90° derecha, 180° abajo y 270° izquierda.",
                "0° top, 90° right, 180° bottom and 270° left.");
        add("tip.circleZoom", "Amplía la imagen dentro del recorte circular.", "Zooms the image inside the circular crop.");
        add("tip.circleOffsetX", "Desplaza horizontalmente el encuadre de la imagen.", "Moves the image framing horizontally.");
        add("tip.circleOffsetY", "Desplaza verticalmente el encuadre de la imagen.", "Moves the image framing vertically.");
        add("tip.reverseBars", "Intercambia el orden horizontal de graves y agudos.", "Swaps the horizontal order of bass and treble.");
        add("tip.introMode", "Puede montarse sobre el inicio del audio o terminar antes de que este comience.",
                "It can overlap the start of the audio or finish before the audio begins.");
        add("tip.introDuration", "Duración exacta de armado, parpadeo y asentamiento de la línea punteada.",
                "Exact duration of the dotted line build, blink and settling stages.");
        add("tip.dualSensitivity", "Sensibilidad porcentual de las dos frecuencias.", "Percentage sensitivity of both spectra.");
        add("tip.dualLayout", "En los bordes apunta al centro; Mitades unidas nace en el centro.",
                "Edges points toward the center; Joined halves starts at the center.");
        add("tip.dualReach", "Define cuánto puede avanzar cada mitad sin tocar a su contraparte.",
                "Defines how far each half may travel without touching its counterpart.");
        add("tip.dualVisibility", "Permite conservar las dos mitades o mostrar solo una.",
                "Keeps both halves or displays only one.");
        add("tip.dualIntro", "Disponible únicamente en la composición Mitades unidas.",
                "Available only for the Joined halves composition.");

        add("tip.loadCount", "Elige una o dos barras; dos unidades se colocan automáticamente en lados opuestos.",
                "Choose one or two bars; two units are placed automatically on opposite sides.");
        add("tip.loadOrientation", "Horizontal carga de izquierda a derecha; Vertical carga de abajo hacia arriba.",
                "Horizontal fills left to right; Vertical fills bottom to top.");
        add("tip.loadHorizontalPosition", "Con una barra horizontal, elige arriba, centro o abajo sin tocar el marco.",
                "For one horizontal bar, choose top, center or bottom without touching the frame.");
        add("tip.loadVerticalPosition", "Con una barra vertical, elige izquierda, centro o derecha sin tocar el marco.",
                "For one vertical bar, choose left, center or right without touching the frame.");
        add("tip.loadReverse", "Invierte el sentido de llenado: derecha a izquierda o arriba a abajo.",
                "Reverses the filling direction: right to left or top to bottom.");
        add("tip.loadShape", "Usa esquinas cuadradas o extremos redondeados.",
                "Uses square corners or rounded ends.");
        add("tip.loadFillStyle", "Cambia el acabado del relleno sin modificar el estilo independiente del borde.",
                "Changes the fill finish without altering the independent border style.");
        add("tip.loadBorderStyle", "Cambia únicamente el marco; puede combinarse con cualquier estilo de barra.",
                "Changes only the frame; it can be combined with any bar style.");
        add("tip.loadResponse", "Alta favorece llenados grandes; Proporcional amplía el contraste de toda la canción.",
                "High favors large fills; Proportional expands contrast across the whole song.");
        add("tip.loadAnimation", "Equilibrada añade inercia moderada; Suavizada conserva más continuidad.",
                "Balanced adds moderate inertia; Smoothed preserves more continuity.");
        add("tip.loadSensitivity", "Ajusta cuánto influye el nivel musical conjunto en el llenado.",
                "Adjusts how strongly the combined musical level affects the fill.");
        add("tip.loadFillColor", "Selecciona el color del relleno de la barra.", "Selects the loading bar fill color.");
        add("tip.loadBorderColor", "Selecciona el color del borde, independiente del relleno.",
                "Selects the border color independently from the fill.");
        add("tip.play", "Reproduce o pausa la vista previa con audio desde la posición del deslizador.",
                "Plays or pauses the preview with audio from the slider position.");
        add("tip.timeline", "Haz clic o arrastra para buscar otro instante; audio e imagen se resincronizan.",
                "Click or drag to seek; audio and image resynchronize.");
        add("tip.outputFormat", "MP4 conserva el fondo. ProRes, VP9 y PNG exportan transparencia y conservan únicamente el visualizador y el relleno circular configurado.",
                "MP4 keeps the background. ProRes, VP9 and PNG export transparency and keep only the visualizer and configured circular fill.");

        add("status.ready", "Listo", "Ready");
        add("status.cancelling", "Cancelando…", "Cancelling…");
        add("status.analyzing", "Analizando audio…", "Analyzing audio…");
        add("status.rendering", "Renderizando video…", "Rendering video…");
        add("status.exporting", "Creando exportación…", "Creating export…");
        add("status.videoFinished", "Video terminado: %s", "Video finished: %s");
        add("status.exportFinished", "Exportación terminada: %s", "Export finished: %s");
        add("status.analysisFinished", "Análisis terminado. Usa Reproducir o mueve la línea de tiempo.",
                "Analysis finished. Press Play or move the timeline.");
        add("status.cancelled", "Operación cancelada.", "Operation cancelled.");
        add("status.error", "Ocurrió un error.", "An error occurred.");
        add("status.invalidated", "La configuración cambió; vuelve a analizar el audio.",
                "The analysis settings changed; analyze the audio again.");
        add("status.playing", "Reproduciendo vista previa…", "Playing preview…");
        add("status.paused", "Vista previa pausada.", "Preview paused.");

        add("error.selectAudioFirst", "Primero selecciona un archivo de audio.", "Select an audio file first.");
        add("error.interrupted", "La operación fue interrumpida.", "The operation was interrupted.");
        add("error.unknown", "Error desconocido.", "Unknown error.");
        add("error.imageFormat", "Formato de imagen no reconocido.", "Unrecognized image format.");
        add("error.openBackground", "No se pudo abrir la imagen: %s", "Could not open the image: %s");
        add("error.openCircle", "No se pudo abrir la imagen interior: %s", "Could not open the interior image: %s");
        add("error.previewAudio", "No se pudo reproducir el audio de la vista previa: %s",
                "Could not play preview audio: %s");
        add("error.logLocation", "Registro técnico: %s", "Technical log: %s");
        add("error.unsupportedVisualizer", "Visualizador no compatible: %s", "Unsupported visualizer: %s");
        add("dialog.errorTitle", "Sonic Candle — Error", "Sonic Candle — Error");
        add("dialog.audio", "Seleccionar audio", "Select audio");
        add("dialog.background", "Seleccionar imagen de fondo", "Select background image");
        add("dialog.circleImage", "Seleccionar imagen para el interior del círculo", "Select image for the circle interior");
        add("dialog.output", "Guardar video MP4", "Save MP4 video");
        add("dialog.outputFile", "Guardar exportación", "Save export");
        add("dialog.outputFolder", "Elegir carpeta para la secuencia PNG", "Choose folder for PNG sequence");
        add("dialog.barColor", "Color de las barras", "Bar color");
        add("dialog.circleBarColor", "Color de la frecuencia circular", "Circular spectrum color");
        add("dialog.dualColor", "Color de la doble barra", "Dual bar color");
        add("dialog.loadFillColor", "Color de llenado", "Fill color");
        add("dialog.loadBorderColor", "Color del borde", "Border color");
        add("dialog.backgroundColor", "Color del fondo", "Background color");
        add("dialog.circleInnerColor", "Color interior del círculo", "Circle interior color");
        add("dialog.renderComplete", "Video creado y guardado correctamente:\n%s",
                "Video created and saved successfully:\n%s");
        add("dialog.exportComplete", "Exportación creada y guardada correctamente:\n%s",
                "Export created and saved successfully:\n%s");
        add("dialog.accept", "Aceptar", "OK");
        add("colorChooser.swatches", "Muestras", "Swatches");
        add("colorChooser.recent", "Recientes", "Recent");
        add("colorChooser.hsv", "HSV", "HSV");
        add("colorChooser.hsl", "HSL", "HSL");
        add("colorChooser.rgb", "RGB", "RGB");
        add("colorChooser.cmyk", "CMYK", "CMYK");
        add("colorChooser.preview", "Vista previa", "Preview");
        add("colorChooser.accept", "Aceptar", "OK");
        add("colorChooser.cancel", "Cancelar", "Cancel");
        add("colorChooser.reset", "Restablecer", "Reset");
        add("filter.audio", "Audio compatible", "Compatible audio");
        add("filter.images", "Imágenes", "Images");
        add("filter.video", "Video MP4", "MP4 video");
        add("filter.mp4", "Video MP4", "MP4 video");
        add("filter.prores", "Apple ProRes 4444", "Apple ProRes 4444");
        add("filter.webm", "Video WebM VP9", "WebM VP9 video");
        add("preview.welcomeTitle", "Sonic Candle", "Sonic Candle");
        add("preview.welcomeSubtitle", "Carga un audio para encender el espectro",
                "Load an audio file to light up the spectrum");

        enumEn("AppLanguage", "SPANISH", "Spanish");
        enumEn("AppLanguage", "ENGLISH", "English");
        enumEn("AppTheme", "MODERN", "Modern blue");
        enumEn("AppTheme", "CLASSIC", "Classic theme");
        enumEn("ExportFormat", "MP4", "MP4 — Full video");
        enumEn("ExportFormat", "PRORES_4444", "ProRes 4444 — Maximum transparency quality");
        enumEn("ExportFormat", "WEBM_VP9", "WebM VP9 — Moderate transparency quality");
        enumEn("ExportFormat", "PNG_SEQUENCE", "PNG — Transparent sequence");
        enumEn("FrequencyDistributionMode", "STANDARD", "Standard");
        enumEn("FrequencyDistributionMode", "BALANCED", "Balanced — more presence on the right");
        enumEn("FrequencyDistributionMode", "PROPORTIONAL", "Proportional — uses every band");
        enumEn("MotionMode", "NORMAL", "Normal");
        enumEn("MotionMode", "AGILE", "Agile");
        enumEn("MotionMode", "FAST", "Fast");
        enumEn("SpectrumMode", "STANDARD", "Standard");
        enumEn("SpectrumMode", "CLASSIC_INTERLEAVED", "Classic interleaved");
        enumEn("BarStyle", "THICK_BLOCK", "01 Thick Block");
        enumEn("BarStyle", "OUTLINE_BLOCK", "02 Outline Block");
        enumEn("BarStyle", "THIN", "03 Thin");
        enumEn("BarStyle", "ROUND_FILLED", "04 Round Filled");
        enumEn("BarStyle", "ROUND_OUTLINE", "05 Round Outline");
        enumEn("BarStyle", "POP_UP_BLOCK", "06 Pop Up Block");
        enumEn("BarStyle", "ETCHED_BLOCK", "07 Etched Block");
        enumEn("BarStyle", "OVAL_FILLED", "08 Oval Filled");
        enumEn("BarStyle", "OVAL_OUTLINE", "09 Oval Outline");
        enumEn("BarStyle", "SEGMENTED_BLOCKS", "Extra: Segmented Blocks");
        enumEn("BarStyle", "FLUID_HALO", "Extra: Fluid Halo");
        enumEn("CircleAlignment", "LEFT", "Left");
        enumEn("CircleAlignment", "CENTER", "Center");
        enumEn("CircleAlignment", "RIGHT", "Right");
        enumEn("CircleFillMode", "TRANSPARENT", "Transparent");
        enumEn("CircleFillMode", "SOLID_COLOR", "Solid color");
        enumEn("CircleFillMode", "IMAGE", "Image");
        enumEn("DualBarLayout", "EDGES", "At the edges");
        enumEn("DualBarLayout", "JOINED_CENTER", "Joined halves");
        enumEn("DualBarReach", "LOW", "Low impact");
        enumEn("DualBarReach", "MEDIUM", "Medium impact");
        enumEn("DualBarReach", "HIGH", "High impact");
        enumEn("DualBarVisibility", "BOTH", "Top and bottom");
        enumEn("DualBarVisibility", "TOP_ONLY", "Top only");
        enumEn("DualBarVisibility", "BOTTOM_ONLY", "Bottom only");
        enumEn("HorizontalPlacement", "TOP", "Top");
        enumEn("HorizontalPlacement", "CENTER", "Center");
        enumEn("HorizontalPlacement", "BOTTOM", "Bottom");
        enumEn("VerticalPlacement", "LEFT", "Left");
        enumEn("VerticalPlacement", "CENTER", "Center");
        enumEn("VerticalPlacement", "RIGHT", "Right");
        enumEn("IntroAnimationDirection", "OUTSIDE_IN", "Outside in");
        enumEn("IntroAnimationDirection", "INSIDE_OUT", "Inside out");
        enumEn("IntroAnimationMode", "DISABLED", "No animation");
        enumEn("IntroAnimationMode", "SYNCHRONIZED", "Along with the song");
        enumEn("IntroAnimationMode", "BEFORE_AUDIO", "Before the song");
        enumEn("LoadBarAnimationMode", "NORMAL", "Normal");
        enumEn("LoadBarAnimationMode", "BALANCED", "Balanced");
        enumEn("LoadBarAnimationMode", "SMOOTHED", "Smoothed");
        enumEn("LoadBarBorderStyle", "DEFAULT", "Default");
        enumEn("LoadBarBorderStyle", "THICK_BLOCK", "Thick Block");
        enumEn("LoadBarBorderStyle", "POP_UP_BLOCK", "Pop Up Block");
        enumEn("LoadBarBorderStyle", "ETCHED_BLOCK", "Etched Block");
        enumEn("LoadBarBorderStyle", "SEGMENTED_BLOCKS", "Segmented Block");
        enumEn("LoadBarFillStyle", "DEFAULT", "Default");
        enumEn("LoadBarFillStyle", "THICK_BLOCK", "Thick Block");
        enumEn("LoadBarFillStyle", "POP_UP_BLOCK", "Pop Up Block");
        enumEn("LoadBarFillStyle", "ETCHED_BLOCK", "Etched Block");
        enumEn("LoadBarFillStyle", "SEGMENTED_BLOCKS", "Segmented Block");
        enumEn("LoadBarFillStyle", "FLUID_HALO", "Fluid Halo");
        enumEn("LoadBarOrientation", "HORIZONTAL", "Horizontal");
        enumEn("LoadBarOrientation", "VERTICAL", "Vertical");
        enumEn("LoadBarResponseMode", "NORMAL", "Normal");
        enumEn("LoadBarResponseMode", "HIGH", "High");
        enumEn("LoadBarResponseMode", "PROPORTIONAL", "Proportional");
        enumEn("LoadBarShape", "SQUARE", "Square");
        enumEn("LoadBarShape", "ROUNDED", "Rounded");
        enumEn("PeakMode", "FREE_OVERFLOW", "Free overflow");
        enumEn("PeakMode", "SOFT_LIMIT", "Normalize peaks");
        enumEn("RestingLineMode", "INVISIBLE", "Invisible");
        enumEn("RestingLineMode", "DOTTED", "Dotted");
        enumEn("VisualizationMode", "LINEAR", "Linear");
        enumEn("VisualizationMode", "CIRCULAR", "Circular");
        enumEn("VisualizationMode", "DUAL_BAR", "Dual bar");
        enumEn("VisualizationMode", "LOAD_BAR", "Loading bar");
    }

    private UiText() {
    }

    public static void setLanguage(AppLanguage selected) {
        language = selected == null ? AppLanguage.SPANISH : selected;
    }

    public static AppLanguage language() {
        return language;
    }

    public static String text(String key) {
        Map<String, String> selected = language == AppLanguage.ENGLISH ? EN : ES;
        return selected.getOrDefault(key, ES.getOrDefault(key, "!" + key + "!"));
    }

    public static String format(String key, Object... values) {
        return String.format(text(key), values);
    }

    public static String enumText(Object value) {
        if (!(value instanceof Enum<?> item) || language == AppLanguage.SPANISH) {
            return String.valueOf(value);
        }
        String key = item.getClass().getSimpleName() + "." + item.name();
        return ENUM_EN.getOrDefault(key, String.valueOf(value));
    }

    static boolean hasText(String key) {
        return ES.containsKey(key) && EN.containsKey(key);
    }

    static boolean hasEnglishEnum(Enum<?> value) {
        return ENUM_EN.containsKey(value.getClass().getSimpleName() + "." + value.name());
    }

    private static void add(String key, String spanish, String english) {
        ES.put(key, spanish);
        EN.put(key, english);
    }

    private static void enumEn(String type, String value, String english) {
        ENUM_EN.put(type + "." + value, english);
    }
}
