package com.casewire.ui.components;

import com.casewire.model.Evidence;
import com.casewire.utils.ThemeUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.util.function.Consumer;

public class EvidenceCard extends JPanel {

    public enum SelectionState {
        NONE,
        SOURCE,
        TARGET
    }

    private final Evidence evidence;
    private final Consumer<Evidence> onClickCallback;
    private final JLabel titleLabel;
    private final JLabel typeLabel;
    private final JLabel locationLabel;

    private SelectionState selectionState = SelectionState.NONE;

    public EvidenceCard(Evidence evidence, Consumer<Evidence> onClickCallback) {
        this.evidence = evidence;
        this.onClickCallback = onClickCallback;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(260, 168));
        setMinimumSize(new Dimension(240, 160));
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel headerLabel = new JLabel("Evidence");
        headerLabel.setFont(ThemeUtil.smallFont());
        headerLabel.setForeground(ThemeUtil.DARK_TEXT);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleLabel = new JLabel(evidence.getTitle());
        titleLabel.setFont(ThemeUtil.titleFont(16));
        titleLabel.setForeground(ThemeUtil.DARK_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        typeLabel = new JLabel("Type: " + evidence.getType());
        typeLabel.setFont(ThemeUtil.smallFont());
        typeLabel.setForeground(ThemeUtil.DARK_TEXT);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        locationLabel = new JLabel("Location: " + evidence.getLocation());
        locationLabel.setFont(ThemeUtil.smallFont());
        locationLabel.setForeground(ThemeUtil.DARK_TEXT);
        locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeUtil.BORDER, 2),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        add(headerLabel);
        add(Box.createVerticalStrut(6));
        add(titleLabel);
        add(Box.createVerticalStrut(10));
        add(typeLabel);
        add(Box.createVerticalStrut(6));
        add(locationLabel);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                EvidenceCard.this.onClickCallback.accept(EvidenceCard.this.evidence);
            }
        });

        updateStyle();
    }

    public Evidence getEvidence() {
        return evidence;
    }

    public void setSelectionState(SelectionState selectionState) {
        this.selectionState = selectionState == null ? SelectionState.NONE : selectionState;
        updateStyle();
    }

    public Point getAnchorPoint() {
        return new Point(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }

    private void updateStyle() {
        Color background = ThemeUtil.CARD_BACKGROUND;
        Color borderColor = ThemeUtil.BORDER;

        if (selectionState == SelectionState.SOURCE) {
            background = ThemeUtil.SELECTED_SOURCE;
            borderColor = ThemeUtil.WARNING;
        } else if (selectionState == SelectionState.TARGET) {
            background = ThemeUtil.SELECTED_TARGET;
            borderColor = ThemeUtil.ACCENT.brighter();
        }

        setBackground(background);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, selectionState == SelectionState.NONE ? 2 : 4),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        repaint();
    }
}
