package com.casewire.app;

import javax.swing.SwingUtilities;

import com.casewire.ui.MainFrame;
import com.casewire.utils.ThemeUtil;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ThemeUtil.applyTheme();
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
