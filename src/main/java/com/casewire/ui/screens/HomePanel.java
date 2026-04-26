package com.casewire.ui.screens;

import com.casewire.model.PlayerProgress;
import com.casewire.model.User;
import com.casewire.service.CaseService;
import com.casewire.ui.MainFrame;
import com.casewire.utils.ThemeUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

public class HomePanel extends JPanel {

    private final MainFrame mainFrame;
    private final CaseService caseService = new CaseService();
    private JLabel welcomeLabel;
    private JLabel infoLabel;

    public HomePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(ThemeUtil.APP_BACKGROUND);
        buildUI();
    }

    private void buildUI() {
        JPanel mainCard = ThemeUtil.createSectionPanel();
        mainCard.setLayout(new BorderLayout(20, 0));
        mainCard.setPreferredSize(new Dimension(760, 360));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = ThemeUtil.createTitle("CASEWIRE", 36);
        JLabel subtitle = ThemeUtil.createMutedLabel("Simple police investigation game");
        welcomeLabel = ThemeUtil.createMutedLabel("Welcome");
        infoLabel = ThemeUtil.createMutedLabel("Start a new investigation from the cases screen.");

        JButton newGameButton = ThemeUtil.createButton("New Investigation", true);
        JButton historyButton = ThemeUtil.createButton("History");
        JButton logoutButton = ThemeUtil.createButton("Logout");
        JButton exitButton = ThemeUtil.createButton("Exit");

        newGameButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        historyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        exitButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        newGameButton.addActionListener(e -> mainFrame.showCases());
        historyButton.addActionListener(e -> mainFrame.showHistory());
        logoutButton.addActionListener(e -> mainFrame.logout());
        exitButton.addActionListener(e -> System.exit(0));

        left.add(title);
        left.add(Box.createVerticalStrut(10));
        left.add(subtitle);
        left.add(Box.createVerticalStrut(16));
        left.add(welcomeLabel);
        left.add(Box.createVerticalStrut(10));
        left.add(infoLabel);
        left.add(Box.createVerticalStrut(30));
        left.add(newGameButton);
        left.add(Box.createVerticalStrut(10));
        left.add(historyButton);
        left.add(Box.createVerticalStrut(10));
        left.add(logoutButton);
        left.add(Box.createVerticalStrut(10));
        left.add(exitButton);

        JPanel right = ThemeUtil.createCardPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(250, 0));

        JLabel notesTitle = new JLabel("Briefing");
        notesTitle.setFont(ThemeUtil.titleFont(20));
        notesTitle.setForeground(ThemeUtil.DARK_TEXT);
        notesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel note1 = createDarkLabel("Review suspects and evidence.");
        JLabel note2 = createDarkLabel("Select two clues to test a relation.");
        JLabel note3 = createDarkLabel("Solve the case when ready.");

        right.add(notesTitle);
        right.add(Box.createVerticalStrut(16));
        right.add(note1);
        right.add(Box.createVerticalStrut(10));
        right.add(note2);
        right.add(Box.createVerticalStrut(10));
        right.add(note3);

        mainCard.add(left, BorderLayout.CENTER);
        mainCard.add(right, BorderLayout.EAST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(mainCard, gbc);
    }

    public void refresh() {
        User currentUser = mainFrame.getCurrentUser();
        if (currentUser == null) {
            welcomeLabel.setText("Welcome");
            infoLabel.setText("Please log in to continue.");
            return;
        }

        String displayName = currentUser.getFullName() != null && !currentUser.getFullName().isBlank()
                ? currentUser.getFullName()
                : currentUser.getUsername();

        boolean hasSavedProgress = hasSavedProgress(currentUser.getId());
        welcomeLabel.setText("Welcome, " + displayName);
        infoLabel.setText(hasSavedProgress
                ? "This account has solved cases. Open History to review your results."
                : "This account is ready. Start a new investigation from the cases screen.");
    }

    private JLabel createDarkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeUtil.bodyFont(13));
        label.setForeground(ThemeUtil.DARK_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private boolean hasSavedProgress(int userId) {
        List<PlayerProgress> progress = caseService.getAllProgress(userId);
        for (PlayerProgress entry : progress) {
            if (entry.isSolved() || entry.getScore() > 0) {
                return true;
            }
        }
        return false;
    }
}
