package com.soniccandle.ui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

final class LocalizedListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean selected, boolean focus) {
        return super.getListCellRendererComponent(list, UiText.enumText(value),
                index, selected, focus);
    }
}
