package com.soniccandle.ui;

import com.soniccandle.analysis.AudioAnalyzer;
import com.soniccandle.analysis.FrequencyDistributionMode;
import com.soniccandle.analysis.MotionMode;
import com.soniccandle.analysis.SpectrumData;
import com.soniccandle.analysis.SpectrumMode;
import com.soniccandle.logging.AppLogger;
import com.soniccandle.render.BarStyle;
import com.soniccandle.render.CircleAlignment;
import com.soniccandle.render.CircleFillMode;
import com.soniccandle.render.CircularConfig;
import com.soniccandle.render.DualBarConfig;
import com.soniccandle.render.DualBarLayout;
import com.soniccandle.render.DualBarReach;
import com.soniccandle.render.DualBarVisibility;
import com.soniccandle.render.ExportFormat;
import com.soniccandle.render.HorizontalPlacement;
import com.soniccandle.render.IntroAnimationConfig;
import com.soniccandle.render.IntroAnimationDirection;
import com.soniccandle.render.IntroAnimationMode;
import com.soniccandle.render.LoadBarConfig;
import com.soniccandle.render.LoadBarAnimationMode;
import com.soniccandle.render.LoadBarBorderStyle;
import com.soniccandle.render.LoadBarFillStyle;
import com.soniccandle.render.LoadBarLevelProcessor;
import com.soniccandle.render.LoadBarOrientation;
import com.soniccandle.render.LoadBarResponseMode;
import com.soniccandle.render.LoadBarShape;
import com.soniccandle.render.PeakMode;
import com.soniccandle.render.RenderConfig;
import com.soniccandle.render.RestingLineMode;
import com.soniccandle.render.VideoEncoder;
import com.soniccandle.render.VerticalPlacement;
import com.soniccandle.render.VisualizationMode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private final SpectrumPanel preview = new SpectrumPanel();
    private final JLabel audioLabel = new JLabel();
    private final JLabel backgroundLabel = new JLabel();
    private final JLabel backgroundModeLabel = new JLabel();
    private final JLabel outputLabel = new JLabel();
    private final JLabel outputFormatNoteLabel = new JLabel();
    private final JComboBox<String> resolutionBox = new JComboBox<>(new String[]{"1280 × 720", "1920 × 1080"});
    private final JComboBox<Integer> fpsBox = new JComboBox<>(new Integer[]{30, 60});
    private final JSpinner bandsSpinner = new JSpinner(new SpinnerNumberModel(64, 16, 160, 8));
    private final JComboBox<MotionMode> motionBox = new JComboBox<>(MotionMode.values());
    private final JComboBox<SpectrumMode> spectrumModeBox = new JComboBox<>(SpectrumMode.values());
    private final JComboBox<FrequencyDistributionMode> distributionModeBox =
            new JComboBox<>(FrequencyDistributionMode.values());
    private final JComboBox<BarStyle> styleBox = new JComboBox<>(BarStyle.values());
    private final JComboBox<RestingLineMode> restingLineBox = new JComboBox<>(RestingLineMode.values());
    private final JComboBox<PeakMode> peakModeBox = new JComboBox<>(PeakMode.values());
    private final SliderNumberControl sensitivityControl =
            new SliderNumberControl(40, 250, 100, 1);
    private final JButton barColorButton = createColorButton(new Color(179, 127, 255));
    private final JCheckBox reverseBarsCheckBox = new JCheckBox();
    private final JComboBox<IntroAnimationMode> barsIntroModeBox =
            new JComboBox<>(IntroAnimationMode.values());
    private final JComboBox<IntroAnimationDirection> barsIntroDirectionBox =
            new JComboBox<>(IntroAnimationDirection.values());
    private final SliderNumberControl barsIntroDurationControl =
            new SliderNumberControl(IntroAnimationConfig.MIN_DURATION_MS,
                    IntroAnimationConfig.MAX_DURATION_MS,
                    IntroAnimationConfig.DEFAULT_DURATION_MS, 100);
    private final JTabbedPane compositionTabs = new JTabbedPane();
    private final JComboBox<BarStyle> circleStyleBox = new JComboBox<>(BarStyle.values());
    private final JComboBox<RestingLineMode> circleRestingLineBox =
            new JComboBox<>(RestingLineMode.values());
    private final JComboBox<PeakMode> circlePeakModeBox = new JComboBox<>(PeakMode.values());
    private final SliderNumberControl circleSensitivityControl =
            new SliderNumberControl(40, 250, 100, 1);
    private final JButton circleBarColorButton = createColorButton(new Color(179, 127, 255));
    private final JSpinner circleCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 2, 1));
    private final JComboBox<CircleAlignment> circleAlignmentBox =
            new JComboBox<>(CircleAlignment.values());
    private final JSpinner circleSizeSpinner = new JSpinner(new SpinnerNumberModel(
            62, CircularConfig.MIN_SIZE_PERCENT, CircularConfig.MAX_SIZE_PERCENT, 1));
    private final SliderNumberControl circleRotationControl =
            new SliderNumberControl(0, 359, 0, 1);
    private final JComboBox<CircleFillMode> circleFillBox =
            new JComboBox<>(CircleFillMode.values());
    private final JButton circleColorButton = createColorButton(new Color(28, 18, 51));
    private final JButton circleImageButton = new JButton();
    private final JLabel circleImageLabel = new JLabel();
    private final SliderNumberControl circleZoomControl =
            new SliderNumberControl(100, 300, 100, 1);
    private final SliderNumberControl circleOffsetXControl =
            new SliderNumberControl(-100, 100, 0, 1);
    private final SliderNumberControl circleOffsetYControl =
            new SliderNumberControl(-100, 100, 0, 1);
    private final JComboBox<BarStyle> dualStyleBox = new JComboBox<>(BarStyle.values());
    private final JComboBox<RestingLineMode> dualRestingLineBox =
            new JComboBox<>(RestingLineMode.values());
    private final SliderNumberControl dualSensitivityControl =
            new SliderNumberControl(40, 250, 100, 1);
    private final JButton dualBarColorButton = createColorButton(new Color(179, 127, 255));
    private final JComboBox<DualBarLayout> dualLayoutBox =
            new JComboBox<>(DualBarLayout.values());
    private final JComboBox<DualBarReach> dualReachBox =
            new JComboBox<>(DualBarReach.values());
    private final JComboBox<DualBarVisibility> dualVisibilityBox =
            new JComboBox<>(DualBarVisibility.values());
    private final JCheckBox dualReverseTopCheckBox = new JCheckBox();
    private final JCheckBox dualReverseBottomCheckBox = new JCheckBox();
    private final JComboBox<IntroAnimationMode> dualIntroModeBox =
            new JComboBox<>(IntroAnimationMode.values());
    private final JComboBox<IntroAnimationDirection> dualIntroDirectionBox =
            new JComboBox<>(IntroAnimationDirection.values());
    private final SliderNumberControl dualIntroDurationControl =
            new SliderNumberControl(IntroAnimationConfig.MIN_DURATION_MS,
                    IntroAnimationConfig.MAX_DURATION_MS,
                    IntroAnimationConfig.DEFAULT_DURATION_MS, 100);
    private final JSpinner loadBarCountSpinner =
            new JSpinner(new SpinnerNumberModel(1, 1, 2, 1));
    private final JComboBox<LoadBarOrientation> loadBarOrientationBox =
            new JComboBox<>(LoadBarOrientation.values());
    private final JComboBox<HorizontalPlacement> loadBarHorizontalPlacementBox =
            new JComboBox<>(HorizontalPlacement.values());
    private final JComboBox<VerticalPlacement> loadBarVerticalPlacementBox =
            new JComboBox<>(VerticalPlacement.values());
    private final JCheckBox loadBarReverseCheckBox = new JCheckBox();
    private final JComboBox<LoadBarShape> loadBarShapeBox =
            new JComboBox<>(LoadBarShape.values());
    private final JComboBox<LoadBarFillStyle> loadBarFillStyleBox =
            new JComboBox<>(LoadBarFillStyle.values());
    private final JComboBox<LoadBarBorderStyle> loadBarBorderStyleBox =
            new JComboBox<>(LoadBarBorderStyle.values());
    private final JComboBox<LoadBarResponseMode> loadBarResponseBox =
            new JComboBox<>(LoadBarResponseMode.values());
    private final JComboBox<LoadBarAnimationMode> loadBarAnimationBox =
            new JComboBox<>(LoadBarAnimationMode.values());
    private final SliderNumberControl loadBarSensitivityControl =
            new SliderNumberControl(40, 250, 100, 1);
    private final JButton loadBarColorButton = createColorButton(new Color(179, 127, 255));
    private final JButton loadBarBorderColorButton = createColorButton(Color.WHITE);
    private final JSlider timelineSlider = new JSlider();
    private final JButton backgroundColorButton = createColorButton(new Color(28, 18, 51));
    private final JButton audioButton = new JButton();
    private final JButton backgroundButton = new JButton();
    private final JComboBox<ExportFormat> outputFormatBox =
            new JComboBox<>(ExportFormat.values());
    private final JButton outputButton = new JButton();
    private final JButton analyzeButton = new JButton();
    private final JButton renderButton = new JButton();
    private final JButton cancelButton = new JButton();
    private final JButton playPauseButton = new JButton();
    private final JLabel timelineLabel = new JLabel("00:00.0 / 00:00.0");
    private final JComboBox<AppLanguage> languageBox =
            new JComboBox<>(AppLanguage.values());
    private final JComboBox<AppTheme> themeBox = new JComboBox<>(AppTheme.values());
    private final BrandHeaderPanel brandHeader = new BrandHeaderPanel(AppTheme.MODERN);
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel statusLabel = new JLabel();
    private final PreviewAudioPlayer previewAudioPlayer = new PreviewAudioPlayer();
    private final Timer playbackTimer = new Timer(15, event -> advancePreviewPlayback());

    private Path audioPath;
    private Path backgroundPath;
    private Path outputPath;
    private BufferedImage backgroundImage;
    private Path circleImagePath;
    private BufferedImage circleImage;
    private SpectrumData spectrum;
    private SpectrumData loadBarLevelSpectrum;
    private String loadBarLevelKey;
    private float[] loadBarLevels;
    private String analysisKey;
    private SwingWorker<Void, Integer> activeWorker;
    private final AtomicBoolean cancellationRequested = new AtomicBoolean();
    private boolean previewPlaying;
    private boolean previewAudioStarted;
    private boolean updatingTimelineFromPlayback;
    private int playbackStartFrame;
    private int playbackPreRollFrames;
    private long playbackStartNanos;
    private String statusKey = "status.ready";
    private Object[] statusArguments = new Object[0];

    public MainFrame() {
        super("Sonic Candle");
        UiText.setLanguage(AppLanguage.SPANISH);
        ThemeManager.installDefaults(AppTheme.MODERN);
        motionBox.setSelectedItem(MotionMode.AGILE);
        distributionModeBox.setSelectedItem(FrequencyDistributionMode.STANDARD);
        styleBox.setSelectedItem(BarStyle.ROUND_FILLED);
        restingLineBox.setSelectedItem(RestingLineMode.DOTTED);
        peakModeBox.setSelectedItem(PeakMode.FREE_OVERFLOW);
        circleStyleBox.setSelectedItem(BarStyle.FLUID_HALO);
        circleRestingLineBox.setSelectedItem(RestingLineMode.DOTTED);
        circlePeakModeBox.setSelectedItem(PeakMode.SOFT_LIMIT);
        circleAlignmentBox.setSelectedItem(CircleAlignment.CENTER);
        barsIntroModeBox.setSelectedItem(IntroAnimationMode.DISABLED);
        barsIntroDirectionBox.setSelectedItem(IntroAnimationDirection.OUTSIDE_IN);
        dualStyleBox.setSelectedItem(BarStyle.THIN);
        dualRestingLineBox.setSelectedItem(RestingLineMode.DOTTED);
        dualLayoutBox.setSelectedItem(DualBarLayout.EDGES);
        dualReachBox.setSelectedItem(DualBarReach.MEDIUM);
        dualVisibilityBox.setSelectedItem(DualBarVisibility.BOTH);
        dualIntroModeBox.setSelectedItem(IntroAnimationMode.DISABLED);
        dualIntroDirectionBox.setSelectedItem(IntroAnimationDirection.OUTSIDE_IN);
        loadBarOrientationBox.setSelectedItem(LoadBarOrientation.HORIZONTAL);
        loadBarHorizontalPlacementBox.setSelectedItem(HorizontalPlacement.CENTER);
        loadBarVerticalPlacementBox.setSelectedItem(VerticalPlacement.LEFT);
        loadBarShapeBox.setSelectedItem(LoadBarShape.ROUNDED);
        loadBarFillStyleBox.setSelectedItem(LoadBarFillStyle.DEFAULT);
        loadBarBorderStyleBox.setSelectedItem(LoadBarBorderStyle.DEFAULT);
        loadBarResponseBox.setSelectedItem(LoadBarResponseMode.NORMAL);
        loadBarAnimationBox.setSelectedItem(LoadBarAnimationMode.NORMAL);
        languageBox.setSelectedItem(AppLanguage.SPANISH);
        themeBox.setSelectedItem(AppTheme.MODERN);
        outputFormatBox.setSelectedItem(ExportFormat.MP4);
        configureLocalizationMetadata();
        configureComboRenderers();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1_100, 700));
        setSize(1_280, 800);
        setLocationRelativeTo(null);
        loadIcon();
        buildUi();
        bindActions();
        applyLanguage();
        ThemeManager.apply(AppTheme.MODERN, this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                stopPreviewPlayback(false);
                previewAudioPlayer.close();
                AppLogger.shutdown();
            }
        });
        AppLogger.info("Interfaz principal inicializada.");
        updatePreview();
    }

    private void configureLocalizationMetadata() {
        setTextKey(audioButton, "button.audio");
        setTextKey(backgroundButton, "button.backgroundImage");
        setTextKey(backgroundColorButton, "button.backgroundColor");
        setTextKey(outputButton, "button.output");
        setTextKey(analyzeButton, "button.analyze");
        setTextKey(renderButton, "button.render");
        setTextKey(cancelButton, "button.cancel");
        setTextKey(circleImageButton, "button.circleImage");
        setTextKey(reverseBarsCheckBox, "check.reverseBars");
        setTextKey(dualReverseTopCheckBox, "check.reverseTop");
        setTextKey(dualReverseBottomCheckBox, "check.reverseBottom");
        setTextKey(loadBarReverseCheckBox, "check.reverseLoad");

        setTooltipKey(motionBox, "tip.motion");
        setTooltipKey(spectrumModeBox, "tip.spectrum");
        setTooltipKey(distributionModeBox, "tip.distribution");
        setTooltipKey(restingLineBox, "tip.restingLine");
        setTooltipKey(peakModeBox, "tip.peaks");
        setTooltipKey(sensitivityControl, "tip.barSensitivity");
        setTooltipKey(reverseBarsCheckBox, "tip.reverseBars");
        setTooltipKey(barsIntroModeBox, "tip.introMode");
        setTooltipKey(barsIntroDurationControl, "tip.introDuration");

        setTooltipKey(circleRestingLineBox, "tip.circleRest");
        setTooltipKey(circlePeakModeBox, "tip.circlePeaks");
        setTooltipKey(circleSizeSpinner, "tip.circleSize");
        setTooltipKey(circleSensitivityControl, "tip.circleSensitivity");
        setTooltipKey(circleRotationControl, "tip.circleRotation");
        setTooltipKey(circleZoomControl, "tip.circleZoom");
        setTooltipKey(circleOffsetXControl, "tip.circleOffsetX");
        setTooltipKey(circleOffsetYControl, "tip.circleOffsetY");

        setTooltipKey(dualSensitivityControl, "tip.dualSensitivity");
        setTooltipKey(dualLayoutBox, "tip.dualLayout");
        setTooltipKey(dualReachBox, "tip.dualReach");
        setTooltipKey(dualVisibilityBox, "tip.dualVisibility");
        setTooltipKey(dualIntroModeBox, "tip.dualIntro");
        setTooltipKey(dualIntroDurationControl, "tip.introDuration");

        setTooltipKey(loadBarCountSpinner, "tip.loadCount");
        setTooltipKey(loadBarOrientationBox, "tip.loadOrientation");
        setTooltipKey(loadBarHorizontalPlacementBox, "tip.loadHorizontalPosition");
        setTooltipKey(loadBarVerticalPlacementBox, "tip.loadVerticalPosition");
        setTooltipKey(loadBarReverseCheckBox, "tip.loadReverse");
        setTooltipKey(loadBarShapeBox, "tip.loadShape");
        setTooltipKey(loadBarFillStyleBox, "tip.loadFillStyle");
        setTooltipKey(loadBarBorderStyleBox, "tip.loadBorderStyle");
        setTooltipKey(loadBarResponseBox, "tip.loadResponse");
        setTooltipKey(loadBarAnimationBox, "tip.loadAnimation");
        setTooltipKey(loadBarSensitivityControl, "tip.loadSensitivity");
        setTooltipKey(loadBarColorButton, "tip.loadFillColor");
        setTooltipKey(loadBarBorderColorButton, "tip.loadBorderColor");
        setTooltipKey(playPauseButton, "tip.play");
        setTooltipKey(timelineSlider, "tip.timeline");
        setTooltipKey(outputFormatBox, "tip.outputFormat");
    }

    private void configureComboRenderers() {
        LocalizedListCellRenderer renderer = new LocalizedListCellRenderer();
        JComboBox<?>[] boxes = {motionBox, spectrumModeBox, distributionModeBox,
            styleBox, restingLineBox, peakModeBox, barsIntroModeBox,
            barsIntroDirectionBox, circleStyleBox, circleRestingLineBox,
            circlePeakModeBox, circleAlignmentBox, circleFillBox, dualStyleBox,
            dualRestingLineBox, dualLayoutBox, dualReachBox, dualVisibilityBox,
            dualIntroModeBox, dualIntroDirectionBox, loadBarOrientationBox,
            loadBarHorizontalPlacementBox, loadBarVerticalPlacementBox,
            loadBarShapeBox, loadBarFillStyleBox, loadBarBorderStyleBox,
            loadBarResponseBox, loadBarAnimationBox, outputFormatBox,
            languageBox, themeBox};
        for (JComboBox<?> box : boxes) {
            box.setRenderer(renderer);
        }
    }

    private void applyLanguage() {
        AppLanguage selected = (AppLanguage) languageBox.getSelectedItem();
        UiText.setLanguage(selected);
        Locale locale = selected == AppLanguage.ENGLISH
                ? Locale.ENGLISH : Locale.forLanguageTag("es-CO");
        Locale.setDefault(locale);
        JComponent.setDefaultLocale(locale);
        applyLocalizedTree(getContentPane());
        compositionTabs.setTitleAt(0, UiText.text("tab.bars"));
        compositionTabs.setTitleAt(1, UiText.text("tab.circular"));
        compositionTabs.setTitleAt(2, UiText.text("tab.dual"));
        compositionTabs.setTitleAt(3, UiText.text("tab.load"));
        refreshDynamicLabels();
        setStatus(statusKey, statusArguments);
        updatePlayButtonText();
        updateTimelineLabel();
        preview.repaint();
        revalidate();
        repaint();
    }

    private static void applyLocalizedTree(Component component) {
        if (component instanceof JComponent swing) {
            Object textKey = swing.getClientProperty("sonic.text.key");
            if (textKey instanceof String key) {
                if (swing instanceof AbstractButton button) {
                    button.setText(UiText.text(key));
                } else if (swing instanceof JLabel label) {
                    label.setText(UiText.text(key));
                }
            }
            Object tooltipKey = swing.getClientProperty("sonic.tooltip.key");
            if (tooltipKey instanceof String key) {
                swing.setToolTipText(UiText.text(key));
            }
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyLocalizedTree(child);
            }
        }
    }

    private void refreshDynamicLabels() {
        audioLabel.setText(audioPath == null ? UiText.text("state.noAudio")
                : audioPath.getFileName().toString());
        audioLabel.setToolTipText(audioPath == null ? null : audioPath.toString());
        backgroundLabel.setText(backgroundPath == null
                ? UiText.text("state.noBackgroundImage")
                : backgroundPath.getFileName().toString());
        backgroundLabel.setToolTipText(backgroundPath == null ? null : backgroundPath.toString());
        backgroundModeLabel.setText(UiText.text(backgroundImage == null
                ? "state.solidBackground" : "state.imageBackground"));
        outputLabel.setText(outputPath == null ? UiText.text("state.noOutput")
                : outputPath.getFileName().toString());
        outputLabel.setToolTipText(outputPath == null ? null : outputPath.toString());
        ExportFormat outputFormat = selectedExportFormat();
        outputButton.setText(UiText.text(outputFormat.directoryOutput()
                ? "button.outputFolder" : "button.outputFile"));
        renderButton.setText(UiText.text(switch (outputFormat) {
            case MP4 -> "button.renderMp4";
            case PRORES_4444 -> "button.renderProRes";
            case WEBM_VP9 -> "button.renderWebm";
            case PNG_SEQUENCE -> "button.renderPng";
        }));
        outputFormatNoteLabel.setText(UiText.text(switch (outputFormat) {
            case MP4 -> "note.exportMp4";
            case PRORES_4444 -> "note.exportProRes";
            case WEBM_VP9 -> "note.exportWebm";
            case PNG_SEQUENCE -> "note.exportPng";
        }));
        circleImageLabel.setText(circleImagePath == null
                ? UiText.text("state.noCircleImage")
                : circleImagePath.getFileName().toString());
        circleImageLabel.setToolTipText(circleImagePath == null ? null : circleImagePath.toString());
    }

    private void buildUi() {
        JPanel settings = createSettingsPanel();
        JScrollPane settingsScroll = new JScrollPane(settings,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        settingsScroll.setBorder(null);
        settingsScroll.getVerticalScrollBar().setUnitIncrement(16);
        JPanel previewContainer = new JPanel(new BorderLayout(8, 8));
        previewContainer.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        previewContainer.add(preview, BorderLayout.CENTER);
        timelineSlider.setEnabled(false);
        playPauseButton.setEnabled(false);
        playPauseButton.putClientProperty("sonic.role", "play");
        JPanel playback = new JPanel(new BorderLayout(8, 0));
        playback.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        playback.add(playPauseButton, BorderLayout.WEST);
        playback.add(timelineSlider, BorderLayout.CENTER);
        playback.add(timelineLabel, BorderLayout.EAST);
        previewContainer.add(playback, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, settingsScroll, previewContainer);
        splitPane.setResizeWeight(0.28);
        splitPane.setDividerLocation(350);
        splitPane.setOneTouchExpandable(true);

        JPanel footer = new JPanel(new BorderLayout(10, 4));
        footer.setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));
        progressBar.setStringPainted(true);
        footer.add(statusLabel, BorderLayout.WEST);
        footer.add(progressBar, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.add(brandHeader, BorderLayout.CENTER);
        JPanel preferences = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        preferences.setBorder(BorderFactory.createEmptyBorder(0, 8, 2, 12));
        preferences.add(localizedLabel("settings.language"));
        preferences.add(languageBox);
        preferences.add(localizedLabel("settings.theme"));
        preferences.add(themeBox);
        header.add(preferences, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 6));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(4, 2, 4, 2);

        addSectionTitle(panel, constraints, "section.files");
        panel.add(audioButton, nextRow(constraints));
        panel.add(shortLabel(audioLabel), nextRow(constraints));

        panel.add(backgroundButton, nextRow(constraints));
        panel.add(shortLabel(backgroundLabel), nextRow(constraints));
        panel.add(backgroundColorButton, nextRow(constraints));
        panel.add(shortLabel(backgroundModeLabel), nextRow(constraints));

        addLabeled(panel, constraints, "label.outputFormat", outputFormatBox);
        panel.add(shortLabel(outputFormatNoteLabel), nextRow(constraints));
        panel.add(outputButton, nextRow(constraints));
        panel.add(shortLabel(outputLabel), nextRow(constraints));

        addSectionTitle(panel, nextRow(constraints), "section.videoSpectrum");
        addLabeled(panel, constraints, "label.resolution", resolutionBox);
        addLabeled(panel, constraints, "label.fps", fpsBox);
        addLabeled(panel, constraints, "label.bands", bandsSpinner);
        addLabeled(panel, constraints, "label.motion", motionBox);
        addLabeled(panel, constraints, "label.spectrum", spectrumModeBox);
        addLabeled(panel, constraints, "label.distribution", distributionModeBox);

        addSectionTitle(panel, nextRow(constraints), "section.visualizer");
        compositionTabs.addTab(UiText.text("tab.bars"), scrollableTab(createBarsTab()));
        compositionTabs.addTab(UiText.text("tab.circular"), scrollableTab(createCircularTab()));
        compositionTabs.addTab(UiText.text("tab.dual"), scrollableTab(createDualBarTab()));
        compositionTabs.addTab(UiText.text("tab.load"), scrollableTab(createLoadBarTab()));
        compositionTabs.setSelectedIndex(0);
        compositionTabs.setPreferredSize(new Dimension(315, 365));
        panel.add(compositionTabs, nextRow(constraints));

        JPanel actions = new JPanel(new GridBagLayout());
        GridBagConstraints actionConstraints = new GridBagConstraints();
        actionConstraints.gridx = 0;
        actionConstraints.gridy = 0;
        actionConstraints.fill = GridBagConstraints.HORIZONTAL;
        actionConstraints.weightx = 1;
        actionConstraints.insets = new Insets(3, 0, 3, 0);
        actions.add(analyzeButton, actionConstraints);
        actionConstraints.gridy++;
        actions.add(renderButton, actionConstraints);
        actionConstraints.gridy++;
        cancelButton.setEnabled(false);
        actions.add(cancelButton, actionConstraints);
        panel.add(actions, nextRow(constraints));

        constraints.gridy++;
        constraints.weighty = 1;
        panel.add(new JPanel(), constraints);

        audioButton.addActionListener(event -> selectAudio());
        backgroundButton.addActionListener(event -> selectBackground());
        circleImageButton.addActionListener(event -> selectCircleImage());
        outputButton.addActionListener(event -> selectOutput());
        updateCircularControls(false);
        updateDualControls(false);
        updateBarsControls(false);
        updateLoadBarControls(false);
        return panel;
    }

    private JPanel createBarsTab() {
        JPanel panel = createTabPanel();
        GridBagConstraints constraints = createTabConstraints();
        addLabeled(panel, constraints, "label.style", styleBox);
        addLabeled(panel, constraints, "label.restingLine", restingLineBox);
        addLabeled(panel, constraints, "label.peaks", peakModeBox);
        addLabeled(panel, constraints, "label.sensitivity", sensitivityControl);
        addLabeled(panel, constraints, "label.color", barColorButton);
        panel.add(reverseBarsCheckBox, nextRow(constraints));
        addSectionTitle(panel, nextRow(constraints), "section.dottedIntro");
        addLabeled(panel, constraints, "label.start", barsIntroModeBox);
        addLabeled(panel, constraints, "label.direction", barsIntroDirectionBox);
        addLabeled(panel, constraints, "label.durationMs", barsIntroDurationControl);
        finishTab(panel, constraints);
        return panel;
    }

    private JPanel createDualBarTab() {
        JPanel panel = createTabPanel();
        GridBagConstraints constraints = createTabConstraints();
        addSectionTitle(panel, constraints, "section.doubleFrequency");
        addLabeled(panel, constraints, "label.style", dualStyleBox);
        addLabeled(panel, constraints, "label.restingLine", dualRestingLineBox);
        addLabeled(panel, constraints, "label.sensitivity", dualSensitivityControl);
        addLabeled(panel, constraints, "label.color", dualBarColorButton);
        addSectionTitle(panel, nextRow(constraints), "section.layout");
        addLabeled(panel, constraints, "label.composition", dualLayoutBox);
        addLabeled(panel, constraints, "label.reach", dualReachBox);
        addLabeled(panel, constraints, "label.visibleBars", dualVisibilityBox);
        addLabeled(panel, constraints, "label.peaks", localizedLabel("label.alwaysContained"));
        panel.add(dualReverseTopCheckBox, nextRow(constraints));
        panel.add(dualReverseBottomCheckBox, nextRow(constraints));
        addSectionTitle(panel, nextRow(constraints), "section.dottedIntro");
        addLabeled(panel, constraints, "label.start", dualIntroModeBox);
        addLabeled(panel, constraints, "label.direction", dualIntroDirectionBox);
        addLabeled(panel, constraints, "label.durationMs", dualIntroDurationControl);
        finishTab(panel, constraints);
        return panel;
    }

    private JPanel createLoadBarTab() {
        JPanel panel = createTabPanel();
        GridBagConstraints constraints = createTabConstraints();
        addSectionTitle(panel, constraints, "section.composition");
        addLabeled(panel, constraints, "label.count", loadBarCountSpinner);
        addLabeled(panel, constraints, "label.layout", loadBarOrientationBox);
        addLabeled(panel, constraints, "label.horizontalPosition", loadBarHorizontalPlacementBox);
        addLabeled(panel, constraints, "label.verticalPosition", loadBarVerticalPlacementBox);
        panel.add(loadBarReverseCheckBox, nextRow(constraints));
        addSectionTitle(panel, nextRow(constraints), "section.appearance");
        addLabeled(panel, constraints, "label.shape", loadBarShapeBox);
        addLabeled(panel, constraints, "label.barStyle", loadBarFillStyleBox);
        addLabeled(panel, constraints, "label.borderStyle", loadBarBorderStyleBox);
        addLabeled(panel, constraints, "label.loadType", loadBarResponseBox);
        addLabeled(panel, constraints, "label.animation", loadBarAnimationBox);
        addLabeled(panel, constraints, "label.sensitivity", loadBarSensitivityControl);
        addLabeled(panel, constraints, "label.fillColor", loadBarColorButton);
        addLabeled(panel, constraints, "label.borderColor", loadBarBorderColorButton);
        JLabel note = localizedLabel("note.oppositeBars");
        note.putClientProperty("sonic.role", "secondary");
        panel.add(note, nextRow(constraints));
        finishTab(panel, constraints);
        return panel;
    }

    private JPanel createCircularTab() {
        JPanel panel = createTabPanel();
        GridBagConstraints constraints = createTabConstraints();
        addSectionTitle(panel, constraints, "section.radialFrequency");
        addLabeled(panel, constraints, "label.style", circleStyleBox);
        addLabeled(panel, constraints, "label.restingBorder", circleRestingLineBox);
        addLabeled(panel, constraints, "label.peaks", circlePeakModeBox);
        addLabeled(panel, constraints, "label.sensitivity", circleSensitivityControl);
        addLabeled(panel, constraints, "label.color", circleBarColorButton);
        addSectionTitle(panel, nextRow(constraints), "section.geometry");
        addLabeled(panel, constraints, "label.count", circleCountSpinner);
        addLabeled(panel, constraints, "label.alignment", circleAlignmentBox);
        addLabeled(panel, constraints, "label.sizePercent", circleSizeSpinner);
        addLabeled(panel, constraints, "label.rotation", circleRotationControl);
        addSectionTitle(panel, nextRow(constraints), "section.interior");
        addLabeled(panel, constraints, "label.fill", circleFillBox);
        addLabeled(panel, constraints, "label.innerColor", circleColorButton);
        addLabeled(panel, constraints, "label.image", circleImageButton);
        addLabeled(panel, constraints, "label.file", shortLabel(circleImageLabel));
        addLabeled(panel, constraints, "label.zoom", circleZoomControl);
        addLabeled(panel, constraints, "label.positionX", circleOffsetXControl);
        addLabeled(panel, constraints, "label.positionY", circleOffsetYControl);
        finishTab(panel, constraints);
        return panel;
    }

    private static JPanel createTabPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 8, 6));
        return panel;
    }

    private static JScrollPane scrollableTab(JPanel panel) {
        JScrollPane scroll = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        return scroll;
    }

    private static GridBagConstraints createTabConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(4, 2, 4, 2);
        return constraints;
    }

    private static void finishTab(JPanel panel, GridBagConstraints constraints) {
        constraints.gridy++;
        constraints.weighty = 1;
        panel.add(new JPanel(), constraints);
    }

    private void bindActions() {
        analyzeButton.addActionListener(event -> startWork(false));
        renderButton.addActionListener(event -> startWork(true));
        cancelButton.addActionListener(event -> {
            cancellationRequested.set(true);
            setStatus("status.cancelling");
            cancelButton.setEnabled(false);
        });
        playPauseButton.addActionListener(event -> togglePreviewPlayback());
        timelineSlider.addChangeListener(event -> {
            updatePreview();
            updateTimelineLabel();
            if (previewPlaying && !updatingTimelineFromPlayback
                    && !timelineSlider.getValueIsAdjusting()) {
                restartPreviewPlayback();
            }
        });
        languageBox.addActionListener(event -> applyLanguage());
        themeBox.addActionListener(event -> {
            AppTheme theme = (AppTheme) themeBox.getSelectedItem();
            ThemeManager.apply(theme, this);
            applyLanguage();
        });
        compositionTabs.addChangeListener(event -> {
            updateCircularControls(false);
            updateDualControls(false);
            updateBarsControls(false);
            updateLoadBarControls(false);
            refreshTimelineBounds();
            updatePreview();
        });
        resolutionBox.addActionListener(event -> updatePreview());
        fpsBox.addActionListener(event -> invalidateAnalysis());
        bandsSpinner.addChangeListener(event -> invalidateAnalysis());
        motionBox.addActionListener(event -> invalidateAnalysis());
        spectrumModeBox.addActionListener(event -> invalidateAnalysis());
        distributionModeBox.addActionListener(event -> invalidateAnalysis());
        styleBox.addActionListener(event -> updatePreview());
        restingLineBox.addActionListener(event -> {
            if (restingLineBox.getSelectedItem() == RestingLineMode.INVISIBLE) {
                barsIntroModeBox.setSelectedItem(IntroAnimationMode.DISABLED);
            }
            updateBarsControls(false);
            refreshTimelineBounds();
            updatePreview();
        });
        peakModeBox.addActionListener(event -> updatePreview());
        sensitivityControl.addChangeListener(event -> updatePreview());
        reverseBarsCheckBox.addActionListener(event -> updatePreview());
        barsIntroModeBox.addActionListener(event -> {
            if (barsIntroModeBox.getSelectedItem() != IntroAnimationMode.DISABLED) {
                restingLineBox.setSelectedItem(RestingLineMode.DOTTED);
            }
            updateBarsControls(false);
            refreshTimelineBounds();
            updatePreview();
        });
        barsIntroDirectionBox.addActionListener(event -> updatePreview());
        barsIntroDurationControl.addChangeListener(event -> {
            refreshTimelineBounds();
            updatePreview();
        });
        circleStyleBox.addActionListener(event -> updatePreview());
        circleRestingLineBox.addActionListener(event -> updatePreview());
        circlePeakModeBox.addActionListener(event -> updatePreview());
        circleSensitivityControl.addChangeListener(event -> updatePreview());
        circleCountSpinner.addChangeListener(event -> updatePreview());
        circleAlignmentBox.addActionListener(event -> updatePreview());
        circleSizeSpinner.addChangeListener(event -> updatePreview());
        circleRotationControl.addChangeListener(event -> updatePreview());
        circleFillBox.addActionListener(event -> {
            updateCircularControls(false);
            updatePreview();
        });
        circleZoomControl.addChangeListener(event -> updatePreview());
        circleOffsetXControl.addChangeListener(event -> updatePreview());
        circleOffsetYControl.addChangeListener(event -> updatePreview());
        dualStyleBox.addActionListener(event -> updatePreview());
        dualRestingLineBox.addActionListener(event -> {
            if (dualRestingLineBox.getSelectedItem() == RestingLineMode.INVISIBLE) {
                dualIntroModeBox.setSelectedItem(IntroAnimationMode.DISABLED);
            }
            updateDualControls(false);
            refreshTimelineBounds();
            updatePreview();
        });
        dualSensitivityControl.addChangeListener(event -> updatePreview());
        dualLayoutBox.addActionListener(event -> {
            updateDualControls(false);
            refreshTimelineBounds();
            updatePreview();
        });
        dualReachBox.addActionListener(event -> updatePreview());
        dualVisibilityBox.addActionListener(event -> {
            updateDualControls(false);
            updatePreview();
        });
        dualReverseTopCheckBox.addActionListener(event -> updatePreview());
        dualReverseBottomCheckBox.addActionListener(event -> updatePreview());
        dualIntroModeBox.addActionListener(event -> {
            if (dualIntroModeBox.getSelectedItem() != IntroAnimationMode.DISABLED) {
                dualRestingLineBox.setSelectedItem(RestingLineMode.DOTTED);
            }
            updateDualControls(false);
            refreshTimelineBounds();
            updatePreview();
        });
        dualIntroDirectionBox.addActionListener(event -> updatePreview());
        dualIntroDurationControl.addChangeListener(event -> {
            refreshTimelineBounds();
            updatePreview();
        });
        loadBarCountSpinner.addChangeListener(event -> {
            updateLoadBarControls(false);
            updatePreview();
        });
        loadBarOrientationBox.addActionListener(event -> {
            updateLoadBarControls(false);
            updatePreview();
        });
        loadBarHorizontalPlacementBox.addActionListener(event -> updatePreview());
        loadBarVerticalPlacementBox.addActionListener(event -> updatePreview());
        loadBarReverseCheckBox.addActionListener(event -> updatePreview());
        loadBarShapeBox.addActionListener(event -> updatePreview());
        loadBarFillStyleBox.addActionListener(event -> updatePreview());
        loadBarBorderStyleBox.addActionListener(event -> updatePreview());
        loadBarResponseBox.addActionListener(event -> updatePreview());
        loadBarAnimationBox.addActionListener(event -> updatePreview());
        loadBarSensitivityControl.addChangeListener(event -> updatePreview());
        barColorButton.addActionListener(event -> chooseColor(barColorButton, "dialog.barColor"));
        circleBarColorButton.addActionListener(event -> chooseColor(
                circleBarColorButton, "dialog.circleBarColor"));
        dualBarColorButton.addActionListener(event -> chooseColor(
                dualBarColorButton, "dialog.dualColor"));
        loadBarColorButton.addActionListener(event -> chooseColor(
                loadBarColorButton, "dialog.loadFillColor"));
        loadBarBorderColorButton.addActionListener(event -> chooseColor(
                loadBarBorderColorButton, "dialog.loadBorderColor"));
        outputFormatBox.addActionListener(event -> updateOutputFormat());
        backgroundColorButton.addActionListener(event -> chooseBackgroundColor());
        circleColorButton.addActionListener(event -> chooseColor(
                circleColorButton, "dialog.circleInnerColor"));
    }

    private void startWork(boolean renderAfterAnalysis) {
        if (audioPath == null) {
            showError(UiText.text("error.selectAudioFirst"));
            return;
        }
        if (renderAfterAnalysis && outputPath == null) {
            selectOutput();
            if (outputPath == null) {
                return;
            }
        }

        Path requestedAudio = audioPath;
        Path requestedOutput = outputPath;
        ExportFormat requestedExportFormat = selectedExportFormat();
        int requestedFps = selectedFps();
        int requestedBands = selectedBands();
        MotionMode requestedMotionMode = selectedMotionMode();
        SpectrumMode requestedSpectrumMode = selectedSpectrumMode();
        FrequencyDistributionMode requestedDistributionMode = selectedDistributionMode();
        RenderConfig requestedRenderConfig = currentRenderConfig();
        String requestedAnalysisKey = requestedAudio.toAbsolutePath().normalize()
                + "|" + requestedFps + "|" + requestedBands + "|"
                + requestedMotionMode.name() + "|" + requestedSpectrumMode.name()
                + "|" + requestedDistributionMode.name();

        AppLogger.info((renderAfterAnalysis
                ? "Exportación " + requestedExportFormat.name() + " solicitada: "
                : "Análisis solicitado: ")
                + requestedAudio.toAbsolutePath().normalize());

        stopPreviewPlayback(false);
        cancellationRequested.set(false);
        setBusy(true);
        progressBar.setValue(0);
        activeWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (spectrum == null || !requestedAnalysisKey.equals(analysisKey)) {
                    setStatusFromWorker("status.analyzing");
                    spectrum = new AudioAnalyzer().analyze(
                            requestedAudio, requestedFps, requestedBands,
                            requestedMotionMode, requestedSpectrumMode,
                            requestedDistributionMode, this::publish,
                            cancellationRequested::get);
                    analysisKey = requestedAnalysisKey;
                }
                if (cancellationRequested.get()) {
                    throw new CancellationException();
                }
                if (renderAfterAnalysis) {
                    setStatusFromWorker("status.exporting");
                    publish(0);
                    new VideoEncoder().encode(requestedAudio, requestedOutput,
                            spectrum, requestedRenderConfig, requestedExportFormat,
                            this::publish, cancellationRequested::get);
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                activeWorker = null;
                setBusy(false);
                try {
                    get();
                    configureTimeline();
                    updatePreview();
                    progressBar.setValue(100);
                    if (renderAfterAnalysis) {
                        setStatus("status.exportFinished", requestedOutput.getFileName());
                    } else {
                        setStatus("status.analysisFinished");
                    }
                    if (renderAfterAnalysis) {
                        showRenderCompleted(requestedOutput);
                    }
                    AppLogger.info(renderAfterAnalysis
                            ? "Render finalizado: " + requestedOutput.toAbsolutePath().normalize()
                            : "Análisis finalizado correctamente.");
                } catch (CancellationException exception) {
                    setStatus("status.cancelled");
                    progressBar.setValue(0);
                    AppLogger.info("Operación cancelada por el usuario.");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    showError(UiText.text("error.interrupted"), exception);
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    if (cause instanceof CancellationException) {
                        setStatus("status.cancelled");
                        progressBar.setValue(0);
                    } else {
                        Throwable report = cause == null ? exception : cause;
                        showError(report.getMessage(), report);
                        setStatus("status.error");
                    }
                }
            }
        };
        activeWorker.execute();
    }

    private void selectAudio() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(UiText.text("dialog.audio"));
        chooser.setFileFilter(new FileNameExtensionFilter(
                UiText.text("filter.audio"), "wav", "mp3", "flac", "ogg", "oga", "m4a", "aac", "wma", "opus"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            stopPreviewPlayback(false);
            audioPath = chooser.getSelectedFile().toPath();
            if (outputPath == null) {
                outputPath = suggestedOutputPath();
            }
            refreshDynamicLabels();
            invalidateAnalysis();
        }
    }

    private void selectBackground() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(UiText.text("dialog.background"));
        chooser.setFileFilter(new FileNameExtensionFilter(UiText.text("filter.images"),
                "png", "jpg", "jpeg", "bmp", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage selected = ImageIO.read(chooser.getSelectedFile());
                if (selected == null) {
                    throw new IOException(UiText.text("error.imageFormat"));
                }
                backgroundPath = chooser.getSelectedFile().toPath();
                backgroundImage = selected;
                refreshDynamicLabels();
                updatePreview();
            } catch (IOException exception) {
                showError(UiText.format("error.openBackground", exception.getMessage()),
                        exception);
            }
        }
    }

    private void selectCircleImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(UiText.text("dialog.circleImage"));
        chooser.setFileFilter(new FileNameExtensionFilter(
                UiText.text("filter.images"), "png", "jpg", "jpeg", "bmp", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage selected = ImageIO.read(chooser.getSelectedFile());
                if (selected == null) {
                    throw new IOException(UiText.text("error.imageFormat"));
                }
                circleImagePath = chooser.getSelectedFile().toPath();
                circleImage = selected;
                refreshDynamicLabels();
                circleFillBox.setSelectedItem(CircleFillMode.IMAGE);
                updateCircularControls(false);
                updatePreview();
            } catch (IOException exception) {
                showError(UiText.format("error.openCircle", exception.getMessage()),
                        exception);
            }
        }
    }

    private void selectOutput() {
        ExportFormat format = selectedExportFormat();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(UiText.text(format.directoryOutput()
                ? "dialog.outputFolder" : "dialog.outputFile"));
        if (format.directoryOutput()) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
        } else {
            chooser.setFileFilter(new FileNameExtensionFilter(
                    UiText.text(switch (format) {
                        case MP4 -> "filter.mp4";
                        case PRORES_4444 -> "filter.prores";
                        case WEBM_VP9 -> "filter.webm";
                        case PNG_SEQUENCE -> throw new IllegalStateException();
                    }), format.extension()));
        }
        if (outputPath != null) {
            chooser.setSelectedFile(outputPath.toFile());
        }
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            Path selected = chooser.getSelectedFile().toPath();
            if (!format.directoryOutput()) {
                String extension = "." + format.extension();
                if (!selected.getFileName().toString().toLowerCase(Locale.ROOT)
                        .endsWith(extension)) {
                    selected = selected.resolveSibling(selected.getFileName() + extension);
                }
            }
            outputPath = selected;
            refreshDynamicLabels();
        }
    }

    private void updateOutputFormat() {
        outputPath = suggestedOutputPath();
        refreshDynamicLabels();
    }

    private Path suggestedOutputPath() {
        if (audioPath == null) {
            return null;
        }
        String baseName = stripExtension(audioPath.getFileName().toString());
        return audioPath.resolveSibling(baseName + selectedExportFormat().suggestedSuffix());
    }

    private ExportFormat selectedExportFormat() {
        ExportFormat selected = (ExportFormat) outputFormatBox.getSelectedItem();
        return selected == null ? ExportFormat.MP4 : selected;
    }

    private RenderConfig currentRenderConfig() {
        return createRenderConfig(selectedVisualizationMode());
    }

    private RenderConfig createRenderConfig(VisualizationMode mode) {
        int[] dimensions = selectedDimensions();
        CircularConfig circularConfig = new CircularConfig(
                (Integer) circleCountSpinner.getValue(),
                (CircleAlignment) circleAlignmentBox.getSelectedItem(),
                (Integer) circleSizeSpinner.getValue(),
                circleRotationControl.getValue(),
                (CircleFillMode) circleFillBox.getSelectedItem(),
                circleColorButton.getBackground(), circleImage,
                circleZoomControl.getValue(), circleOffsetXControl.getValue(),
                circleOffsetYControl.getValue());
        DualBarConfig dualBarConfig = new DualBarConfig(
                (DualBarLayout) dualLayoutBox.getSelectedItem(),
                (DualBarReach) dualReachBox.getSelectedItem(),
                dualReverseTopCheckBox.isSelected(),
                dualReverseBottomCheckBox.isSelected(),
                (DualBarVisibility) dualVisibilityBox.getSelectedItem());
        LoadBarConfig loadBarConfig = new LoadBarConfig(
                (Integer) loadBarCountSpinner.getValue(),
                (LoadBarOrientation) loadBarOrientationBox.getSelectedItem(),
                (HorizontalPlacement) loadBarHorizontalPlacementBox.getSelectedItem(),
                (VerticalPlacement) loadBarVerticalPlacementBox.getSelectedItem(),
                loadBarReverseCheckBox.isSelected(),
                (LoadBarShape) loadBarShapeBox.getSelectedItem(),
                loadBarBorderColorButton.getBackground(),
                (LoadBarFillStyle) loadBarFillStyleBox.getSelectedItem(),
                (LoadBarBorderStyle) loadBarBorderStyleBox.getSelectedItem(),
                (LoadBarResponseMode) loadBarResponseBox.getSelectedItem(),
                (LoadBarAnimationMode) loadBarAnimationBox.getSelectedItem());
        IntroAnimationConfig introConfig = switch (mode) {
            case LINEAR -> new IntroAnimationConfig(
                    (IntroAnimationMode) barsIntroModeBox.getSelectedItem(),
                    (IntroAnimationDirection) barsIntroDirectionBox.getSelectedItem(),
                    barsIntroDurationControl.getValue());
            case DUAL_BAR -> new IntroAnimationConfig(
                    (IntroAnimationMode) dualIntroModeBox.getSelectedItem(),
                    (IntroAnimationDirection) dualIntroDirectionBox.getSelectedItem(),
                    dualIntroDurationControl.getValue());
            default -> IntroAnimationConfig.disabled();
        };
        Color visualizerColor;
        BarStyle style;
        float sensitivity;
        RestingLineMode restingLine;
        PeakMode peakMode;
        switch (mode) {
            case CIRCULAR -> {
                visualizerColor = circleBarColorButton.getBackground();
                style = (BarStyle) circleStyleBox.getSelectedItem();
                sensitivity = circleSensitivityControl.getValue() / 100f;
                restingLine = (RestingLineMode) circleRestingLineBox.getSelectedItem();
                peakMode = (PeakMode) circlePeakModeBox.getSelectedItem();
            }
            case DUAL_BAR -> {
                visualizerColor = dualBarColorButton.getBackground();
                style = (BarStyle) dualStyleBox.getSelectedItem();
                sensitivity = dualSensitivityControl.getValue() / 100f;
                restingLine = (RestingLineMode) dualRestingLineBox.getSelectedItem();
                peakMode = PeakMode.SOFT_LIMIT;
            }
            case LOAD_BAR -> {
                visualizerColor = loadBarColorButton.getBackground();
                style = BarStyle.ROUND_FILLED;
                sensitivity = loadBarSensitivityControl.getValue() / 100f;
                restingLine = RestingLineMode.INVISIBLE;
                peakMode = PeakMode.SOFT_LIMIT;
            }
            case LINEAR -> {
                visualizerColor = barColorButton.getBackground();
                style = (BarStyle) styleBox.getSelectedItem();
                sensitivity = sensitivityControl.getValue() / 100f;
                restingLine = (RestingLineMode) restingLineBox.getSelectedItem();
                peakMode = (PeakMode) peakModeBox.getSelectedItem();
            }
            default -> throw new IllegalStateException(
                    UiText.format("error.unsupportedVisualizer", mode));
        }
        return new RenderConfig(
                dimensions[0], dimensions[1], selectedFps(),
                visualizerColor,
                backgroundColorButton.getBackground(), backgroundImage,
                style, sensitivity, restingLine, peakMode,
                mode, circularConfig, reverseBarsCheckBox.isSelected(), dualBarConfig,
                loadBarConfig, introConfig);
    }

    private void updatePreview() {
        RenderConfig config = currentRenderConfig();
        int playbackFrame = timelineSlider.getValue();
        int preRoll = introPreRollFrames(config);
        int spectrumFrame = playbackFrame - preRoll;
        float[] frame = null;
        if (spectrum != null) {
            frame = spectrumFrame < 0
                    ? new float[spectrum.bandCount()]
                    : spectrum.frame(Math.min(spectrum.frameCount() - 1, spectrumFrame));
        }
        float loadLevel = Float.NaN;
        if (spectrum != null && config.visualizationMode() == VisualizationMode.LOAD_BAR
                && spectrumFrame >= 0) {
            float[] levels = currentLoadBarLevels(config);
            loadLevel = levels[Math.min(levels.length - 1, spectrumFrame)];
        }
        preview.showFrame(frame, config, playbackFrame, loadLevel);
    }

    private void togglePreviewPlayback() {
        if (previewPlaying) {
            stopPreviewPlayback(true);
        } else {
            startPreviewPlayback();
        }
    }

    private void startPreviewPlayback() {
        if (spectrum == null || spectrum.frameCount() == 0 || audioPath == null) {
            return;
        }
        if (timelineSlider.getValue() >= timelineSlider.getMaximum()) {
            updatingTimelineFromPlayback = true;
            timelineSlider.setValue(0);
            updatingTimelineFromPlayback = false;
        }
        RenderConfig config = currentRenderConfig();
        playbackStartFrame = timelineSlider.getValue();
        playbackPreRollFrames = introPreRollFrames(config);
        playbackStartNanos = System.nanoTime();
        previewAudioStarted = false;
        previewPlaying = true;
        if (PreviewTimeline.audioShouldBeActive(
                playbackStartFrame, playbackPreRollFrames)) {
            startPreviewAudio(PreviewTimeline.audioOffsetSeconds(
                    playbackStartFrame, playbackPreRollFrames, selectedFps()));
        }
        playbackTimer.start();
        updatePlayButtonText();
        setStatus("status.playing");
    }

    private void restartPreviewPlayback() {
        if (!previewPlaying) {
            return;
        }
        playbackTimer.stop();
        previewAudioPlayer.stop();
        previewPlaying = false;
        startPreviewPlayback();
    }

    private void startPreviewAudio(double offsetSeconds) {
        previewAudioStarted = true;
        previewAudioPlayer.play(audioPath, offsetSeconds, exception ->
                SwingUtilities.invokeLater(() -> {
                    if (previewPlaying) {
                        stopPreviewPlayback(false);
                        showError(UiText.format("error.previewAudio",
                                exception.getMessage() == null
                                ? exception.getClass().getSimpleName()
                                : exception.getMessage()), exception);
                    }
                }));
    }

    private void advancePreviewPlayback() {
        if (!previewPlaying || spectrum == null) {
            return;
        }
        int fps = selectedFps();
        double elapsed = (System.nanoTime() - playbackStartNanos) / 1_000_000_000.0;
        int target = PreviewTimeline.frameAfter(playbackStartFrame, elapsed,
                fps, timelineSlider.getMaximum());
        if (!previewAudioStarted && PreviewTimeline.audioShouldBeActive(
                target, playbackPreRollFrames)) {
            startPreviewAudio(PreviewTimeline.audioOffsetSeconds(
                    target, playbackPreRollFrames, fps));
        }
        if (previewAudioStarted && previewAudioPlayer.isReady()) {
            int audioTarget = PreviewTimeline.frameForAudioPosition(
                    previewAudioPlayer.positionSeconds(), playbackPreRollFrames,
                    fps, timelineSlider.getMaximum());
            target = Math.max(timelineSlider.getValue(), audioTarget);
        }
        updatingTimelineFromPlayback = true;
        timelineSlider.setValue(target);
        updatingTimelineFromPlayback = false;
        if (target >= timelineSlider.getMaximum()) {
            stopPreviewPlayback(false);
            setStatus("status.analysisFinished");
        }
    }

    private void stopPreviewPlayback(boolean userPause) {
        playbackTimer.stop();
        previewAudioPlayer.stop();
        previewPlaying = false;
        previewAudioStarted = false;
        updatePlayButtonText();
        if (userPause) {
            setStatus("status.paused");
        }
    }

    private void updatePlayButtonText() {
        playPauseButton.setText(UiText.text(previewPlaying
                ? "button.pause" : "button.play"));
    }

    private void updateTimelineLabel() {
        int fps = Math.max(1, selectedFps());
        double current = timelineSlider.getValue() / (double) fps;
        double total = timelineSlider.getMaximum() == 0 ? 0.0
                : (timelineSlider.getMaximum() + 1) / (double) fps;
        timelineLabel.setText(formatTime(current) + " / " + formatTime(total));
    }

    private static String formatTime(double seconds) {
        int minutes = (int) Math.floor(Math.max(0.0, seconds) / 60.0);
        double remaining = Math.max(0.0, seconds) - minutes * 60.0;
        return String.format(Locale.ROOT, "%02d:%04.1f", minutes, remaining);
    }

    private float[] currentLoadBarLevels(RenderConfig config) {
        String key = config.sensitivity() + "|"
                + config.loadBarConfig().responseMode().name() + "|"
                + config.loadBarConfig().animationMode().name() + "|"
                + config.framesPerSecond();
        if (loadBarLevels == null || loadBarLevelSpectrum != spectrum
                || !key.equals(loadBarLevelKey)) {
            loadBarLevels = LoadBarLevelProcessor.process(spectrum, config);
            loadBarLevelSpectrum = spectrum;
            loadBarLevelKey = key;
        }
        return loadBarLevels;
    }

    private boolean isCircularTabSelected() {
        return compositionTabs.getSelectedIndex() == 1;
    }

    private boolean isDualTabSelected() {
        return compositionTabs.getSelectedIndex() == 2;
    }

    private boolean isLoadBarTabSelected() {
        return compositionTabs.getSelectedIndex() == 3;
    }

    private VisualizationMode selectedVisualizationMode() {
        return switch (compositionTabs.getSelectedIndex()) {
            case 1 -> VisualizationMode.CIRCULAR;
            case 2 -> VisualizationMode.DUAL_BAR;
            case 3 -> VisualizationMode.LOAD_BAR;
            default -> VisualizationMode.LINEAR;
        };
    }

    private void configureTimeline() {
        refreshTimelineBounds();
        if (spectrum != null) {
            int preRoll = introPreRollFrames(currentRenderConfig());
            timelineSlider.setValue(Math.min(timelineSlider.getMaximum(),
                    preRoll + spectrum.frameCount() / 4));
        }
        playPauseButton.setEnabled(spectrum != null && spectrum.frameCount() > 0);
        updateTimelineLabel();
    }

    private void refreshTimelineBounds() {
        timelineSlider.setEnabled(spectrum != null && spectrum.frameCount() > 0);
        timelineSlider.setMinimum(0);
        if (spectrum == null) {
            timelineSlider.setMaximum(0);
            playPauseButton.setEnabled(false);
            updateTimelineLabel();
            return;
        }
        RenderConfig config = currentRenderConfig();
        int totalFrames = spectrum.frameCount() + introPreRollFrames(config);
        timelineSlider.setMaximum(Math.max(0, totalFrames - 1));
        playPauseButton.setEnabled(totalFrames > 0
                && (activeWorker == null || activeWorker.isDone()));
        updateTimelineLabel();
    }

    private static int introPreRollFrames(RenderConfig config) {
        return config.introAnimationApplies()
                && config.introAnimationConfig().mode() == IntroAnimationMode.BEFORE_AUDIO
                ? config.introFrameCount() : 0;
    }

    private void invalidateAnalysis() {
        stopPreviewPlayback(false);
        analysisKey = null;
        spectrum = null;
        loadBarLevels = null;
        loadBarLevelSpectrum = null;
        loadBarLevelKey = null;
        timelineSlider.setEnabled(false);
        playPauseButton.setEnabled(false);
        progressBar.setValue(0);
        setStatus("status.invalidated");
        updateTimelineLabel();
        updatePreview();
    }

    private int selectedFps() {
        return (Integer) fpsBox.getSelectedItem();
    }

    private int selectedBands() {
        return (Integer) bandsSpinner.getValue();
    }

    private MotionMode selectedMotionMode() {
        return (MotionMode) motionBox.getSelectedItem();
    }

    private SpectrumMode selectedSpectrumMode() {
        return (SpectrumMode) spectrumModeBox.getSelectedItem();
    }

    private FrequencyDistributionMode selectedDistributionMode() {
        return (FrequencyDistributionMode) distributionModeBox.getSelectedItem();
    }

    private int[] selectedDimensions() {
        return resolutionBox.getSelectedIndex() == 1 ? new int[]{1920, 1080} : new int[]{1280, 720};
    }

    private void setBusy(boolean busy) {
        if (busy) {
            stopPreviewPlayback(false);
        }
        analyzeButton.setEnabled(!busy);
        renderButton.setEnabled(!busy);
        cancelButton.setEnabled(busy);
        audioButton.setEnabled(!busy);
        backgroundButton.setEnabled(!busy);
        backgroundColorButton.setEnabled(!busy);
        outputButton.setEnabled(!busy);
        outputFormatBox.setEnabled(!busy);
        languageBox.setEnabled(!busy);
        themeBox.setEnabled(!busy);
        playPauseButton.setEnabled(!busy && spectrum != null && spectrum.frameCount() > 0);
        resolutionBox.setEnabled(!busy);
        fpsBox.setEnabled(!busy);
        bandsSpinner.setEnabled(!busy);
        motionBox.setEnabled(!busy);
        spectrumModeBox.setEnabled(!busy);
        distributionModeBox.setEnabled(!busy);
        compositionTabs.setEnabled(!busy);
        styleBox.setEnabled(!busy);
        restingLineBox.setEnabled(!busy);
        peakModeBox.setEnabled(!busy);
        sensitivityControl.setEnabled(!busy);
        barColorButton.setEnabled(!busy);
        reverseBarsCheckBox.setEnabled(!busy);
        updateCircularControls(busy);
        updateDualControls(busy);
        updateBarsControls(busy);
        updateLoadBarControls(busy);
    }

    private void updateBarsControls(boolean busy) {
        boolean bars = !busy && compositionTabs.getSelectedIndex() == 0;
        boolean dotted = restingLineBox.getSelectedItem() == RestingLineMode.DOTTED;
        boolean introSelected = barsIntroModeBox.getSelectedItem()
                != IntroAnimationMode.DISABLED;
        boolean intro = bars && dotted && introSelected;
        styleBox.setEnabled(bars);
        restingLineBox.setEnabled(bars && !intro);
        peakModeBox.setEnabled(bars);
        sensitivityControl.setEnabled(bars);
        barColorButton.setEnabled(bars);
        reverseBarsCheckBox.setEnabled(bars);
        barsIntroModeBox.setEnabled(bars && (dotted || introSelected));
        barsIntroDirectionBox.setEnabled(intro);
        barsIntroDurationControl.setEnabled(intro);
    }

    private void updateCircularControls(boolean busy) {
        boolean circular = !busy && isCircularTabSelected();
        CircleFillMode fillMode = (CircleFillMode) circleFillBox.getSelectedItem();
        circleStyleBox.setEnabled(circular);
        circleRestingLineBox.setEnabled(circular);
        circlePeakModeBox.setEnabled(circular);
        circleSensitivityControl.setEnabled(circular);
        circleBarColorButton.setEnabled(circular);
        circleCountSpinner.setEnabled(circular);
        circleAlignmentBox.setEnabled(circular);
        circleSizeSpinner.setEnabled(circular);
        circleRotationControl.setEnabled(circular);
        circleFillBox.setEnabled(circular);
        circleColorButton.setEnabled(circular && fillMode == CircleFillMode.SOLID_COLOR);
        circleImageButton.setEnabled(circular && fillMode == CircleFillMode.IMAGE);
        circleZoomControl.setEnabled(circular && fillMode == CircleFillMode.IMAGE);
        circleOffsetXControl.setEnabled(circular && fillMode == CircleFillMode.IMAGE);
        circleOffsetYControl.setEnabled(circular && fillMode == CircleFillMode.IMAGE);
    }

    private void updateDualControls(boolean busy) {
        boolean dual = !busy && isDualTabSelected();
        boolean joined = dualLayoutBox.getSelectedItem() == DualBarLayout.JOINED_CENTER;
        boolean dotted = dualRestingLineBox.getSelectedItem() == RestingLineMode.DOTTED;
        boolean introSelected = dualIntroModeBox.getSelectedItem()
                != IntroAnimationMode.DISABLED;
        boolean intro = dual && joined && dotted && introSelected;
        DualBarVisibility visibility = (DualBarVisibility) dualVisibilityBox.getSelectedItem();
        dualStyleBox.setEnabled(dual);
        dualRestingLineBox.setEnabled(dual && !intro);
        dualSensitivityControl.setEnabled(dual);
        dualBarColorButton.setEnabled(dual);
        dualLayoutBox.setEnabled(dual);
        dualReachBox.setEnabled(dual);
        dualVisibilityBox.setEnabled(dual);
        dualReverseTopCheckBox.setEnabled(dual && visibility.showsTop());
        dualReverseBottomCheckBox.setEnabled(dual && visibility.showsBottom());
        dualIntroModeBox.setEnabled(dual && joined && (dotted || introSelected));
        dualIntroDirectionBox.setEnabled(intro);
        dualIntroDurationControl.setEnabled(intro);
    }

    private void updateLoadBarControls(boolean busy) {
        boolean load = !busy && isLoadBarTabSelected();
        boolean horizontal = loadBarOrientationBox.getSelectedItem()
                == LoadBarOrientation.HORIZONTAL;
        boolean single = (Integer) loadBarCountSpinner.getValue() == 1;
        loadBarCountSpinner.setEnabled(load);
        loadBarOrientationBox.setEnabled(load);
        loadBarHorizontalPlacementBox.setEnabled(load && single && horizontal);
        loadBarVerticalPlacementBox.setEnabled(load && single && !horizontal);
        loadBarReverseCheckBox.setEnabled(load);
        loadBarShapeBox.setEnabled(load);
        loadBarFillStyleBox.setEnabled(load);
        loadBarBorderStyleBox.setEnabled(load);
        loadBarResponseBox.setEnabled(load);
        loadBarAnimationBox.setEnabled(load);
        loadBarSensitivityControl.setEnabled(load);
        loadBarColorButton.setEnabled(load);
        loadBarBorderColorButton.setEnabled(load);
    }

    private void setStatusFromWorker(String key, Object... arguments) {
        SwingUtilities.invokeLater(() -> setStatus(key, arguments));
    }

    private void setStatus(String key, Object... arguments) {
        statusKey = key;
        statusArguments = arguments == null ? new Object[0] : arguments.clone();
        statusLabel.setText(UiText.format(key, statusArguments));
    }

    private void chooseColor(JButton button, String titleKey) {
        Color selected = SonicColorChooser.showDialog(
                this, UiText.text(titleKey), button.getBackground());
        if (selected != null) {
            button.setBackground(selected);
            button.setForeground(ThemeManager.contrast(selected));
            updatePreview();
        }
    }

    private void chooseBackgroundColor() {
        Color selected = SonicColorChooser.showDialog(this,
                UiText.text("dialog.backgroundColor"),
                backgroundColorButton.getBackground());
        if (selected != null) {
            backgroundColorButton.setBackground(selected);
            backgroundColorButton.setForeground(ThemeManager.contrast(selected));
            backgroundImage = null;
            backgroundPath = null;
            refreshDynamicLabels();
            updatePreview();
        }
    }

    private void loadIcon() {
        try (InputStream input = MainFrame.class.getResourceAsStream("/sonic-candle-icon.png")) {
            if (input != null) {
                setIconImage(ImageIO.read(input));
            }
        } catch (IOException ignored) {
            AppLogger.warning("No se pudo cargar el icono de la aplicación.", ignored);
        }
    }

    private void showError(String message) {
        SonicDialogs.showMessage(this,
                UiText.text("dialog.errorTitle"),
                message == null ? UiText.text("error.unknown") : message,
                JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String message, Throwable error) {
        String safeMessage = message == null || message.isBlank()
                ? UiText.text("error.unknown") : message;
        AppLogger.error(safeMessage, error);
        Path log = AppLogger.currentLogHint();
        String detail = log == null ? safeMessage
                : safeMessage + "\n\n" + UiText.format("error.logLocation", log);
        showError(detail);
    }

    private void showRenderCompleted(Path renderedVideo) {
        SonicDialogs.showMessage(this, "Sonic Candle",
                UiText.format("dialog.exportComplete", renderedVideo),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static JButton createColorButton(Color color) {
        JButton button = new JButton();
        setTextKey(button, "button.choose");
        button.putClientProperty("sonic.role", "color");
        button.setBackground(color);
        button.setForeground(ThemeManager.contrast(color));
        button.setOpaque(true);
        return button;
    }

    private static JLabel shortLabel(JLabel label) {
        label.putClientProperty("sonic.role", "secondary");
        return label;
    }

    private static JLabel localizedLabel(String key) {
        JLabel label = new JLabel(UiText.text(key));
        setTextKey(label, key);
        return label;
    }

    private static void addSectionTitle(JPanel panel, GridBagConstraints constraints, String key) {
        JLabel label = localizedLabel(key);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
        label.putClientProperty("sonic.role", "section");
        GridBagConstraints copy = (GridBagConstraints) constraints.clone();
        copy.insets = new Insets(14, 2, 5, 2);
        panel.add(label, copy);
    }

    private static void addLabeled(JPanel panel, GridBagConstraints constraints,
            String key, Component component) {
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.weightx = 0.42;
        panel.add(localizedLabel(key), constraints);
        constraints.gridx = 1;
        constraints.weightx = 0.58;
        panel.add(component, constraints);
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
    }

    private static void setTextKey(JComponent component, String key) {
        component.putClientProperty("sonic.text.key", key);
    }

    private static void setTooltipKey(JComponent component, String key) {
        component.putClientProperty("sonic.tooltip.key", key);
    }

    private static GridBagConstraints nextRow(GridBagConstraints constraints) {
        constraints.gridy++;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        return constraints;
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
