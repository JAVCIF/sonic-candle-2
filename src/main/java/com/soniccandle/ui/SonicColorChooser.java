package com.soniccandle.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/** Selector de color integrado con los temas e idiomas de Sonic Candle. */
public final class SonicColorChooser {

    private SonicColorChooser() {
    }

    public static Color showDialog(Component parent, String title, Color initialColor) {
        JColorChooser chooser = createChooser(initialColor);
        AtomicReference<Color> result = new AtomicReference<>();
        JDialog dialog = JColorChooser.createDialog(parent, title, true, chooser,
                event -> result.set(chooser.getColor()), event -> result.set(null));
        JButton accept = dialog.getRootPane().getDefaultButton();
        if (accept != null) {
            accept.putClientProperty("sonic.role", "play");
        }
        ThemeManager.apply(ThemeManager.current(), dialog);
        dialog.setMinimumSize(new Dimension(690, 500));
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        dialog.dispose();
        return result.get();
    }

    static JColorChooser createChooser(Color initialColor) {
        installLocalizedStrings();
        JColorChooser chooser = new JColorChooser(
                initialColor == null ? Color.WHITE : initialColor);
        chooser.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        chooser.setPreviewPanel(new ColorPreviewPanel(chooser));
        ThemeManager.apply(ThemeManager.current(), chooser);
        return chooser;
    }

    private static void installLocalizedStrings() {
        putText("ColorChooser.swatchesNameText", "colorChooser.swatches");
        putText("ColorChooser.swatchesRecentText", "colorChooser.recent");
        putText("ColorChooser.hsvNameText", "colorChooser.hsv");
        putText("ColorChooser.hslNameText", "colorChooser.hsl");
        putText("ColorChooser.rgbNameText", "colorChooser.rgb");
        putText("ColorChooser.cmykNameText", "colorChooser.cmyk");
        putText("ColorChooser.previewText", "colorChooser.preview");
        putText("ColorChooser.okText", "colorChooser.accept");
        putText("ColorChooser.cancelText", "colorChooser.cancel");
        putText("ColorChooser.resetText", "colorChooser.reset");
    }

    private static void putText(String uiKey, String textKey) {
        UIManager.put(uiKey, UiText.text(textKey));
    }

    private static final class ColorPreviewPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private final JColorChooser chooser;
        private final ColorSample sample = new ColorSample();
        private final JLabel value = new JLabel();

        ColorPreviewPanel(JColorChooser chooser) {
            super(new BorderLayout(12, 0));
            this.chooser = chooser;
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            setPreferredSize(new Dimension(460, 70));
            sample.setPreferredSize(new Dimension(96, 52));
            value.setFont(value.getFont().deriveFont(Font.BOLD, 14f));
            value.setHorizontalAlignment(SwingConstants.LEFT);
            add(sample, BorderLayout.WEST);
            add(value, BorderLayout.CENTER);
            chooser.getSelectionModel().addChangeListener(event -> updateColor());
            updateColor();
        }

        private void updateColor() {
            Color color = chooser.getColor();
            sample.setColor(color);
            value.setText(String.format("#%02X%02X%02X  ·  %d, %d, %d",
                    color.getRed(), color.getGreen(), color.getBlue(),
                    color.getRed(), color.getGreen(), color.getBlue()));
        }
    }

    private static final class ColorSample extends JPanel {

        private static final long serialVersionUID = 1L;
        private Color color = Color.WHITE;

        ColorSample() {
            setOpaque(false);
        }

        void setColor(Color color) {
            this.color = color == null ? Color.WHITE : color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(ThemeManager.palette().border());
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g.setColor(color);
            g.fillRoundRect(2, 2, Math.max(0, getWidth() - 4),
                    Math.max(0, getHeight() - 4), 10, 10);
            g.dispose();
        }
    }
}
