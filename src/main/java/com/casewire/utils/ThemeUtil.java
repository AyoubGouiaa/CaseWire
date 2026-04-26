package com.casewire.utils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;

public final class ThemeUtil {

    public static final Color APP_BACKGROUND = new Color(8, 11, 18);
    public static final Color PANEL_BACKGROUND = new Color(15, 22, 34);
    public static final Color CARD_BACKGROUND = new Color(20, 30, 46);
    public static final Color SIDEBAR_BACKGROUND = new Color(17, 25, 39);
    public static final Color BOARD_BACKGROUND = new Color(7, 14, 24);
    public static final Color BOARD_GRID = new Color(20, 33, 50);
    public static final Color BOARD_LINE = new Color(140, 24, 38);
    public static final Color BOARD_LINE_PREVIEW = new Color(214, 164, 57);
    public static final Color TEXT_PRIMARY = new Color(242, 245, 248);
    public static final Color TEXT_MUTED = new Color(171, 181, 196);
    public static final Color DARK_TEXT = new Color(232, 237, 243);
    public static final Color ACCENT = new Color(162, 27, 43);
    public static final Color ACCENT_ALT = new Color(28, 44, 72);
    public static final Color SUCCESS = new Color(66, 156, 104);
    public static final Color WARNING = new Color(214, 164, 57);
    public static final Color BORDER = new Color(116, 28, 41);
    public static final Color INPUT_BACKGROUND = new Color(12, 18, 28);
    public static final Color INPUT_TEXT = new Color(240, 242, 245);
    public static final Color LOCKED_CARD_BACKGROUND = new Color(14, 19, 29);
    public static final Color TABLE_HEADER_BACKGROUND = new Color(34, 16, 24);
    public static final Color SELECTED_SOURCE = new Color(71, 56, 19);
    public static final Color SELECTED_TARGET = new Color(96, 25, 36);

    private ThemeUtil() {
    }

    public static void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("Panel.background", APP_BACKGROUND);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("OptionPane.background", APP_BACKGROUND);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("OptionPane.foreground", TEXT_PRIMARY);
    }

    public static Font titleFont(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    public static Font bodyFont(int size) {
        return new Font("SansSerif", Font.PLAIN, size);
    }

    public static Font smallFont() {
        return bodyFont(12);
    }

    public static JLabel createTitle(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(titleFont(size));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    public static JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(bodyFont(12));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    public static JButton createButton(String text) {
        return createButton(text, false);
    }

    public static JButton createButton(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(bodyFont(13).deriveFont(Font.BOLD));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBackground(primary ? ACCENT : ACCENT_ALT);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? ACCENT.brighter() : BORDER, 2),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        return button;
    }

    public static JPanel createSectionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(paddedBorder(12));
        return panel;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    public static JTextArea createReadOnlyTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(bodyFont(13));
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        area.setForeground(DARK_TEXT);
        area.setBackground(CARD_BACKGROUND);
        area.setCaretColor(TEXT_PRIMARY);
        return area;
    }

    public static void styleScrollPane(JScrollPane scrollPane, Color background) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(background);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
    }

    public static void styleInputField(JComponent field) {
        field.setFont(bodyFont(13));
        field.setBackground(INPUT_BACKGROUND);
        field.setForeground(INPUT_TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        if (field instanceof JTextField textField) {
            textField.setCaretColor(INPUT_TEXT);
        }
    }

    public static void styleTable(JTable table) {
        table.setBackground(CARD_BACKGROUND);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(ACCENT_ALT);
        table.setSelectionBackground(SELECTED_TARGET.darker());
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFont(bodyFont(13));
        table.setRowHeight(28);
        table.getTableHeader().setBackground(TABLE_HEADER_BACKGROUND);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(bodyFont(13).deriveFont(Font.BOLD));
    }

    public static Border paddedBorder(int padding) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        );
    }

    public static Border paddedBorder(Insets insets) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right)
        );
    }
}
