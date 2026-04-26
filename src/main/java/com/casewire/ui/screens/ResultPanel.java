package com.casewire.ui.screens;

import com.casewire.model.CaseFile;
import com.casewire.service.CaseService;
import com.casewire.ui.MainFrame;
import com.casewire.utils.ThemeUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class ResultPanel extends JPanel {

    private final MainFrame mainFrame;
    private int lastCaseId;

    public ResultPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(ThemeUtil.APP_BACKGROUND);
    }

    public void showResult(int caseId, int score, String verdict,
                           String correctSuspect, String chosenSuspect,
                           String explanation) {
        lastCaseId = caseId;
        removeAll();

        JPanel content = ThemeUtil.createSectionPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setPreferredSize(new Dimension(620, 420));

        JLabel verdictLabel = ThemeUtil.createTitle(verdict, 26);
        verdictLabel.setForeground(scoreColor(score));
        verdictLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = ThemeUtil.createTitle("Score: " + score + " / 100", 18);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel chosenLabel = ThemeUtil.createMutedLabel("Your accusation: " + chosenSuspect);
        chosenLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel correctLabel = ThemeUtil.createMutedLabel("Correct suspect: " + correctSuspect);
        correctLabel.setForeground(ThemeUtil.SUCCESS);
        correctLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel explanationTitle = ThemeUtil.createTitle("Explanation", 16);
        explanationTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea explanationArea = ThemeUtil.createReadOnlyTextArea(explanation);
        JScrollPane scrollPane = new JScrollPane(explanationArea);
        ThemeUtil.styleScrollPane(scrollPane, ThemeUtil.CARD_BACKGROUND);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton casesButton = ThemeUtil.createButton("Back to Cases");
        JButton retryButton = ThemeUtil.createButton("Retry");

        casesButton.addActionListener(e -> mainFrame.showCases());
        retryButton.addActionListener(e -> {
            CaseService caseService = new CaseService();
            CaseFile caseFile = caseService.getCaseById(lastCaseId);
            if (caseFile != null) {
                mainFrame.beginInvestigation(caseFile.getId());
                mainFrame.showInvestigation(caseFile);
            }
        });

        buttonPanel.add(casesButton);
        buttonPanel.add(retryButton);

        content.add(verdictLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(scoreLabel);
        content.add(Box.createVerticalStrut(18));
        content.add(chosenLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(correctLabel);
        content.add(Box.createVerticalStrut(16));
        content.add(explanationTitle);
        content.add(Box.createVerticalStrut(8));
        content.add(scrollPane);
        content.add(Box.createVerticalStrut(16));
        content.add(buttonPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(content, gbc);

        revalidate();
        repaint();
    }

    private Color scoreColor(int score) {
        if (score >= 90) {
            return ThemeUtil.SUCCESS;
        }
        if (score >= 70) {
            return ThemeUtil.WARNING;
        }
        return ThemeUtil.SELECTED_TARGET.darker();
    }
}
