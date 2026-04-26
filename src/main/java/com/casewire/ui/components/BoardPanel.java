package com.casewire.ui.components;

import com.casewire.model.ClueConnection;
import com.casewire.utils.ThemeUtil;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BoardPanel extends JPanel implements Scrollable {

    private final Map<Integer, EvidenceCard> evidenceCards = new LinkedHashMap<>();
    private final List<ClueConnection> discoveredConnections = new ArrayList<>();

    private Integer sourceEvidenceId;
    private Integer targetEvidenceId;
    private int columnCount = 2;

    public BoardPanel() {
        setOpaque(true);
        setBackground(ThemeUtil.BOARD_BACKGROUND);
        setLayout(new GridLayout(0, columnCount, 26, 26));
        setBorder(BorderFactory.createEmptyBorder(26, 26, 26, 26));
    }

    public void setEvidenceCards(Map<Integer, EvidenceCard> cards) {
        evidenceCards.clear();
        evidenceCards.putAll(cards);
        updateLayoutForCardCount();

        removeAll();
        for (EvidenceCard card : evidenceCards.values()) {
            add(card);
        }

        revalidate();
        repaint();
    }

    public void setDiscoveredConnections(List<ClueConnection> connections) {
        discoveredConnections.clear();
        discoveredConnections.addAll(connections);
        repaint();
    }

    public void setSelection(Integer sourceEvidenceId, Integer targetEvidenceId) {
        this.sourceEvidenceId = sourceEvidenceId;
        this.targetEvidenceId = targetEvidenceId;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(ThemeUtil.BOARD_GRID);
        for (int y = 0; y < getHeight(); y += 72) {
            g2.drawLine(0, y, getWidth(), y);
        }
        for (int x = 0; x < getWidth(); x += 72) {
            g2.drawLine(x, 0, x, getHeight());
        }

        for (ClueConnection connection : discoveredConnections) {
            drawLineBetween(g2, connection.getEvidenceIdA(), connection.getEvidenceIdB(), ThemeUtil.BOARD_LINE, 5f);
        }

        if (sourceEvidenceId != null && targetEvidenceId != null) {
            drawLineBetween(g2, sourceEvidenceId, targetEvidenceId, ThemeUtil.BOARD_LINE_PREVIEW, 4f);
        }

        g2.dispose();
    }

    private void drawLineBetween(Graphics2D g2, Integer firstId, Integer secondId, Color color, float width) {
        EvidenceCard firstCard = evidenceCards.get(firstId);
        EvidenceCard secondCard = evidenceCards.get(secondId);
        if (firstCard == null || secondCard == null || firstCard.getWidth() == 0 || secondCard.getWidth() == 0) {
            return;
        }

        Point firstPoint = firstCard.getAnchorPoint();
        Point secondPoint = secondCard.getAnchorPoint();
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);
        g2.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
        g2.fillOval(firstPoint.x - 5, firstPoint.y - 5, 10, 10);
        g2.fillOval(secondPoint.x - 5, secondPoint.y - 5, 10, 10);
    }

    private void updateLayoutForCardCount() {
        int cardCount = Math.max(1, evidenceCards.size());
        if (cardCount == 1) {
            columnCount = 1;
        } else if (cardCount <= 4) {
            columnCount = 2;
        } else {
            columnCount = 3;
        }
        setLayout(new GridLayout(0, columnCount, 26, 26));
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 80;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public Dimension getPreferredSize() {
        int cardCount = Math.max(1, evidenceCards.size());
        int rows = (int) Math.ceil(cardCount / (double) columnCount);
        int cardWidth = 260;
        int cardHeight = 168;
        int width = columnCount * cardWidth + Math.max(0, columnCount - 1) * 26 + 52;
        int height = Math.max(480, rows * cardHeight + Math.max(0, rows - 1) * 26 + 52);
        return new Dimension(width, height);
    }
}
