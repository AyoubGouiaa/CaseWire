package com.casewire.ui.components;

import com.casewire.model.Suspect;
import com.casewire.utils.ThemeUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.function.Consumer;

public class SuspectCard extends JPanel {

    private final Suspect suspect;

    public SuspectCard(Suspect suspect, Consumer<Suspect> onClickCallback) {
        this.suspect = suspect;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
        setBackground(ThemeUtil.CARD_BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeUtil.BORDER, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setPreferredSize(new Dimension(180, 110));
        setMaximumSize(new Dimension(220, 120));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(suspect.getName());
        nameLabel.setFont(ThemeUtil.titleFont(15));
        nameLabel.setForeground(ThemeUtil.DARK_TEXT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(suspect.getRole());
        roleLabel.setFont(ThemeUtil.bodyFont(12));
        roleLabel.setForeground(ThemeUtil.DARK_TEXT);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hintLabel = new JLabel("Click to inspect");
        hintLabel.setFont(ThemeUtil.smallFont());
        hintLabel.setForeground(ThemeUtil.DARK_TEXT);
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(nameLabel);
        add(Box.createVerticalStrut(6));
        add(roleLabel);
        add(Box.createVerticalStrut(10));
        add(hintLabel);

        java.awt.event.MouseAdapter listener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onClickCallback.accept(suspect);
            }
        };
        addMouseListener(listener);
        nameLabel.addMouseListener(listener);
        roleLabel.addMouseListener(listener);
        hintLabel.addMouseListener(listener);
    }

    public Suspect getSuspect() {
        return suspect;
    }

    public void setSelected(boolean selected) {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(selected ? ThemeUtil.ACCENT : ThemeUtil.BORDER, selected ? 3 : 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        repaint();
    }
}
