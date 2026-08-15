package com.soniccandle;

import com.soniccandle.logging.AppLogger;
import com.soniccandle.ui.MainFrame;
import com.soniccandle.ui.AppTheme;
import com.soniccandle.ui.ThemeManager;
import java.awt.EventQueue;
import java.util.Locale;
import javax.swing.UIManager;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        AppLogger.initialize();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) ->
                AppLogger.error("Excepción no controlada en " + thread.getName(), error));
        AppLogger.info("Iniciando Sonic Candle 2.0-J; Java "
                + System.getProperty("java.version") + "; "
                + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        Locale.setDefault(Locale.forLanguageTag("es-CO"));
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignored) {
                // Swing conserva su apariencia multiplataforma si el tema del sistema falla.
                AppLogger.warning("No se pudo activar la apariencia multiplataforma.", ignored);
            }
            ThemeManager.installDefaults(AppTheme.MODERN);
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
