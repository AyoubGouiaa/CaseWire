package com.casewire.ui.screens;

import com.casewire.model.CaseHistory;
import com.casewire.model.User;
import com.casewire.service.HistoryService;
import com.casewire.ui.MainFrame;
import com.casewire.utils.ThemeUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryPanel extends JPanel {

    private final MainFrame mainFrame;
    private final HistoryService historyService = new HistoryService();
    private final DefaultTableModel tableModel;
    private final JTable historyTable;
    private final JLabel emptyLabel;

    public HistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(12, 12));
        setBackground(ThemeUtil.APP_BACKGROUND);

        JPanel header = ThemeUtil.createSectionPanel();
        header.setLayout(new BorderLayout());

        JButton backButton = ThemeUtil.createButton("<- Home");
        backButton.addActionListener(e -> mainFrame.showHome());

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(ThemeUtil.createTitle("Case History", 22));
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(ThemeUtil.createMutedLabel("Review your solved cases, score, time, and solved date."));

        header.add(backButton, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Case Title", "Score", "Time Taken", "Solved At"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        ThemeUtil.styleTable(historyTable);
        historyTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        ThemeUtil.styleScrollPane(scrollPane, ThemeUtil.CARD_BACKGROUND);
        scrollPane.setBorder(ThemeUtil.paddedBorder(10));

        emptyLabel = ThemeUtil.createMutedLabel("No solved cases yet.");
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel centerPanel = ThemeUtil.createSectionPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(ThemeUtil.paddedBorder(14));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(emptyLabel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void refresh() {
        User currentUser = mainFrame.getCurrentUser();
        if (currentUser == null) {
            mainFrame.showLogin();
            return;
        }

        tableModel.setRowCount(0);
        List<CaseHistory> historyEntries = historyService.getUserHistory(currentUser.getId());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (CaseHistory entry : historyEntries) {
            tableModel.addRow(new Object[]{
                    entry.getCaseTitle(),
                    entry.getScore() + " / 100",
                    formatDuration(entry.getTimeTakenSeconds()),
                    formatter.format(entry.getSolvedAt())
            });
        }

        emptyLabel.setVisible(historyEntries.isEmpty());
        historyTable.setPreferredScrollableViewportSize(new Dimension(760, 360));
    }

    private String formatDuration(int timeTakenSeconds) {
        int minutes = timeTakenSeconds / 60;
        int seconds = timeTakenSeconds % 60;
        return String.format("%d min %02d sec", minutes, seconds);
    }
}
