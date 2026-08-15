package com.soniccandle.ui;

import java.awt.Component;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;

/** Comprueba sincronización bidireccional y eventos del control slider + número. */
public final class SliderNumberControlTest {

    private SliderNumberControlTest() {
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            SliderNumberControl control = new SliderNumberControl(-100, 300, 0, 1);
            JSlider slider = find(control, JSlider.class);
            JSpinner number = find(control, JSpinner.class);
            AtomicInteger changes = new AtomicInteger();
            control.addChangeListener(event -> changes.incrementAndGet());

            slider.setValue(175);
            assertEquals(175, (Integer) number.getValue(),
                    "El número no siguió al slider.");
            number.setValue(-42);
            assertEquals(-42, slider.getValue(),
                    "El slider no siguió al número.");
            assertEquals(-42, control.getValue(),
                    "El control devolvió un valor desactualizado.");
            if (changes.get() < 2) {
                throw new AssertionError("Los cambios no notificaron a la vista previa.");
            }

            control.setEnabled(false);
            if (slider.isEnabled() || number.isEnabled()) {
                throw new AssertionError("Los componentes internos no se deshabilitaron.");
            }
        });
        System.out.println("Control slider + número sincronizado correctamente.");
    }

    private static <T extends Component> T find(
            SliderNumberControl control, Class<T> type) {
        for (Component component : control.getComponents()) {
            if (type.isInstance(component)) {
                return type.cast(component);
            }
        }
        throw new AssertionError("No se encontró " + type.getSimpleName());
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " " + actual + " != " + expected);
        }
    }
}
