package com.soniccandle.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

/** Slider visual sincronizado con un campo numérico editable. */
public final class SliderNumberControl extends JPanel {

    private static final long serialVersionUID = 1L;
    private final JSlider slider;
    private final JSpinner number;
    private boolean synchronizing;

    public SliderNumberControl(int minimum, int maximum, int value, int step) {
        super(new BorderLayout(6, 0));
        slider = new JSlider(minimum, maximum, value);
        number = new JSpinner(new SpinnerNumberModel(value, minimum, maximum, step));
        number.setEditor(new JSpinner.NumberEditor(number, "0"));
        Dimension preferred = number.getPreferredSize();
        number.setPreferredSize(new Dimension(Math.max(64, preferred.width), preferred.height));

        slider.addChangeListener(event -> syncNumberFromSlider());
        number.addChangeListener(event -> syncSliderFromNumber());
        add(slider, BorderLayout.CENTER);
        add(number, BorderLayout.EAST);
    }

    public int getValue() {
        return slider.getValue();
    }

    public void addChangeListener(ChangeListener listener) {
        slider.addChangeListener(listener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (slider != null) {
            slider.setEnabled(enabled);
        }
        if (number != null) {
            number.setEnabled(enabled);
        }
    }

    @Override
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        if (slider != null) {
            slider.setToolTipText(text);
        }
        if (number != null) {
            number.setToolTipText(text);
        }
    }

    private void syncNumberFromSlider() {
        if (synchronizing) {
            return;
        }
        synchronizing = true;
        number.setValue(slider.getValue());
        synchronizing = false;
    }

    private void syncSliderFromNumber() {
        if (synchronizing) {
            return;
        }
        synchronizing = true;
        slider.setValue((Integer) number.getValue());
        synchronizing = false;
    }
}
