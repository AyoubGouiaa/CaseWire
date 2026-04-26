package com.casewire.ui.screens;

import com.casewire.model.CaseFile;
import com.casewire.model.Level;
import com.casewire.model.PlayerProgress;
import com.casewire.model.User;
import com.casewire.service.CaseService;
import com.casewire.ui.MainFrame;
import com.casewire.utils.ThemeUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CaseSelectionPanel extends JPanel {

    private final MainFrame mainFrame;
    private final CaseService caseService = new CaseService();
    private JPanel contentPanel;

    public CaseSelectionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ThemeUtil.APP_BACKGROUND);
        buildUI();
    }

    private void buildUI() {
        JPanel header = ThemeUtil.createSectionPanel();
        header.setLayout(new BorderLayout());

        JButton backButton = ThemeUtil.createButton("<- Home");
        backButton.addActionListener(e -> mainFrame.showHome());

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(ThemeUtil.createTitle("Cases", 24));
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(ThemeUtil.createMutedLabel("Open an unlocked case file to start investigating."));

        header.add(backButton, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setBackground(ThemeUtil.APP_BACKGROUND);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        ThemeUtil.styleScrollPane(scrollPane, ThemeUtil.APP_BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refresh() {
        User currentUser = mainFrame.getCurrentUser();
        if (currentUser == null) {
            mainFrame.showLogin();
            return;
        }

        contentPanel.removeAll();

        List<Level> levels = caseService.getAllLevels();
        List<CaseFile> allCases = caseService.getAllCases();
        List<PlayerProgress> progress = caseService.getAllProgress(currentUser.getId());
        Map<Integer, PlayerProgress> progressMap = progress.stream()
                .collect(Collectors.toMap(PlayerProgress::getCaseId, value -> value));

        for (Level level : levels) {
            JLabel levelLabel = ThemeUtil.createTitle(level.getName() + " Cases", 18);
            levelLabel.setBorder(BorderFactory.createEmptyBorder(16, 12, 8, 12));
            contentPanel.add(levelLabel);

            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
            rowPanel.setBackground(ThemeUtil.APP_BACKGROUND);

            for (CaseFile caseFile : allCases) {
                if (caseFile.getLevelId() == level.getId()) {
                    rowPanel.add(buildCaseCard(caseFile, progressMap.get(caseFile.getId())));
                }
            }

            contentPanel.add(rowPanel);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildCaseCard(CaseFile caseFile, PlayerProgress progress) {
        boolean unlocked = progress != null && progress.isUnlocked();
        boolean solved = progress != null && progress.isSolved();
        int score = progress != null ? progress.getScore() : 0;

        JPanel card = ThemeUtil.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(290, 170));

        JLabel titleLabel = new JLabel(caseFile.getTitle());
        titleLabel.setFont(ThemeUtil.titleFont(16));
        titleLabel.setForeground(ThemeUtil.DARK_TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String statusText = solved ? "Solved - score " + score : unlocked ? "Unlocked" : "Locked";
        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(ThemeUtil.smallFont());
        statusLabel.setForeground(solved ? ThemeUtil.SUCCESS : unlocked ? ThemeUtil.WARNING : ThemeUtil.TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea description = ThemeUtil.createReadOnlyTextArea(caseFile.getDescription());
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        description.setMaximumSize(new Dimension(260, 80));

        if (!unlocked) {
            card.setBackground(ThemeUtil.LOCKED_CARD_BACKGROUND);
            description.setBackground(card.getBackground());
        }

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(description);

        if (unlocked) {
            java.awt.event.MouseAdapter listener = new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showIntroAndOpen(caseFile);
                }
            };
            card.addMouseListener(listener);
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            for (Component component : card.getComponents()) {
                component.addMouseListener(listener);
                component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }

        return card;
    }

    private void showIntroAndOpen(CaseFile caseFile) {
        JTextArea introArea = ThemeUtil.createReadOnlyTextArea(caseFile.getIntro());
        introArea.setPreferredSize(new Dimension(420, 150));

        int choice = JOptionPane.showConfirmDialog(
                mainFrame,
                new Object[]{ThemeUtil.createTitle("Case Briefing", 16), introArea},
                caseFile.getTitle(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice == JOptionPane.OK_OPTION) {
            mainFrame.beginInvestigation(caseFile.getId());
            mainFrame.showInvestigation(caseFile);
        }
    }
}
