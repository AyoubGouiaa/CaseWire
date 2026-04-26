package com.casewire.ui.screens;

import com.casewire.model.CaseFile;
import com.casewire.model.ClueConnection;
import com.casewire.model.Evidence;
import com.casewire.model.Suspect;
import com.casewire.model.User;
import com.casewire.service.CaseService;
import com.casewire.service.InvestigationService;
import com.casewire.ui.MainFrame;
import com.casewire.ui.components.BoardPanel;
import com.casewire.ui.components.EvidenceCard;
import com.casewire.ui.components.SuspectCard;
import com.casewire.utils.ThemeUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InvestigationPanel extends JPanel {

    private final MainFrame mainFrame;
    private final CaseService caseService = new CaseService();
    private final InvestigationService investigationService = new InvestigationService();

    private CaseFile currentCase;
    private List<Suspect> suspects;
    private List<Evidence> evidenceList;
    private List<ClueConnection> connections;

    private Evidence selectedEvidenceA;
    private Evidence selectedEvidenceB;
    private final List<EvidenceCard> evidenceCards = new ArrayList<>();
    private final Map<Integer, EvidenceCard> evidenceCardMap = new LinkedHashMap<>();
    private final Map<Integer, SuspectCard> suspectCardMap = new LinkedHashMap<>();
    private final List<ClueConnection> discoveredConnections = new ArrayList<>();

    private JLabel detailTitle;
    private JLabel detailMeta;
    private JTextArea detailBody;
    private JLabel selectionLabel;
    private JLabel statusLabel;
    private JButton connectButton;
    private BoardPanel boardPanel;
    private JProgressBar progressBar;
    private int wrongAttempts;
    private int cooldownSecondsRemaining;
    private Timer cooldownTimer;

    public InvestigationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ThemeUtil.APP_BACKGROUND);
    }

    public void loadCase(CaseFile caseFile) {
        User currentUser = mainFrame.getCurrentUser();
        if (currentUser == null) {
            mainFrame.showLogin();
            return;
        }
        if (caseFile == null) {
            return;
        }

        currentCase = caseFile;
        suspects = caseService.getSuspects(caseFile.getId());
        evidenceList = caseService.getEvidence(caseFile.getId());
        connections = caseService.getClueConnections(caseFile.getId());
        selectedEvidenceA = null;
        selectedEvidenceB = null;
        wrongAttempts = 0;
        stopCooldownTimer();
        cooldownSecondsRemaining = 0;
        refreshDiscoveredConnections();

        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setBackground(ThemeUtil.APP_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        centerPanel.add(buildSuspectPanel(), BorderLayout.WEST);
        centerPanel.add(buildBoardArea(), BorderLayout.CENTER);
        centerPanel.add(buildDetailPanel(), BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        add(buildFooter(), BorderLayout.SOUTH);
        updateSelectionFeedback("Select one clue, then a second clue, then click Connect.");
    }

    private JPanel buildHeader() {
        JPanel header = ThemeUtil.createSectionPanel();
        header.setLayout(new BorderLayout());

        JButton backButton = ThemeUtil.createButton("<- Cases");
        backButton.addActionListener(e -> mainFrame.showCases());

        JButton solveButton = ThemeUtil.createButton("Solve Case", true);
        solveButton.addActionListener(e -> mainFrame.showSolve(currentCase));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(ThemeUtil.createTitle(currentCase.getTitle(), 22));
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(ThemeUtil.createMutedLabel("Open suspects, inspect evidence, and connect related clues."));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(solveButton);

        header.add(backButton, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSuspectPanel() {
        JPanel panel = ThemeUtil.createSectionPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(220, 0));

        JLabel title = ThemeUtil.createTitle("Suspects", 16);
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(ThemeUtil.SIDEBAR_BACKGROUND);
        suspectCardMap.clear();

        for (Suspect suspect : suspects) {
            SuspectCard card = new SuspectCard(suspect, this::onSuspectClicked);
            suspectCardMap.put(suspect.getId(), card);
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        ThemeUtil.styleScrollPane(scrollPane, ThemeUtil.SIDEBAR_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBoardArea() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setBackground(ThemeUtil.APP_BACKGROUND);

        JLabel title = ThemeUtil.createTitle("Evidence Board", 16);
        JLabel hint = ThemeUtil.createMutedLabel("Amber = first clue. Red = second clue. Red lines = discovered relations.");

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(hint);

        boardPanel = new BoardPanel();
        evidenceCards.clear();
        evidenceCardMap.clear();

        for (Evidence evidence : evidenceList) {
            EvidenceCard card = new EvidenceCard(evidence, this::onEvidenceClicked);
            evidenceCards.add(card);
            evidenceCardMap.put(evidence.getId(), card);
        }

        boardPanel.setEvidenceCards(evidenceCardMap);
        boardPanel.setDiscoveredConnections(discoveredConnections);

        JScrollPane scrollPane = new JScrollPane(boardPanel);
        ThemeUtil.styleScrollPane(scrollPane, ThemeUtil.BOARD_BACKGROUND);

        wrapper.add(titlePanel, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildDetailPanel() {
        JPanel panel = ThemeUtil.createSectionPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(290, 0));

        JLabel progressTitle = ThemeUtil.createTitle("Progress", 16);
        progressTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        progressBar = new JProgressBar(0, Math.max(connections.size(), 1));
        progressBar.setValue(discoveredConnections.size());
        progressBar.setStringPainted(true);
        progressBar.setString(discoveredConnections.size() + " / " + connections.size() + " links found");
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressBar.setBackground(ThemeUtil.INPUT_BACKGROUND);
        progressBar.setForeground(ThemeUtil.ACCENT);

        JLabel notesTitle = ThemeUtil.createTitle("Details", 16);
        notesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailTitle = new JLabel("Select a suspect or evidence card");
        detailTitle.setFont(ThemeUtil.titleFont(16));
        detailTitle.setForeground(ThemeUtil.TEXT_PRIMARY);
        detailTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailMeta = ThemeUtil.createMutedLabel("Information appears here.");
        detailMeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        detailBody = ThemeUtil.createReadOnlyTextArea("Click a suspect or evidence card to inspect it.");
        JScrollPane bodyScroll = new JScrollPane(detailBody);
        ThemeUtil.styleScrollPane(bodyScroll, ThemeUtil.CARD_BACKGROUND);
        bodyScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(progressTitle);
        panel.add(Box.createVerticalStrut(8));
        panel.add(progressBar);
        panel.add(Box.createVerticalStrut(18));
        panel.add(notesTitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(detailTitle);
        panel.add(Box.createVerticalStrut(6));
        panel.add(detailMeta);
        panel.add(Box.createVerticalStrut(8));
        panel.add(bodyScroll);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = ThemeUtil.createSectionPanel();
        footer.setLayout(new FlowLayout(FlowLayout.LEFT));

        connectButton = ThemeUtil.createButton("Connect", true);
        connectButton.addActionListener(e -> attemptConnection());

        JButton clearButton = ThemeUtil.createButton("Clear Selection");
        clearButton.addActionListener(e -> clearSelection());

        selectionLabel = ThemeUtil.createMutedLabel("No clues selected");
        statusLabel = ThemeUtil.createMutedLabel("");

        footer.add(connectButton);
        footer.add(clearButton);
        footer.add(selectionLabel);
        footer.add(statusLabel);
        return footer;
    }

    private void onSuspectClicked(Suspect suspect) {
        for (SuspectCard card : suspectCardMap.values()) {
            card.setSelected(card.getSuspect().getId() == suspect.getId());
        }

        detailTitle.setText(suspect.getName());
        detailMeta.setText(suspect.getRole());
        detailBody.setText("Description:\n" + suspect.getDescription() + "\n\nAlibi:\n" + suspect.getAlibi());
        detailBody.setCaretPosition(0);
    }

    private void onEvidenceClicked(Evidence evidence) {
        detailTitle.setText(evidence.getTitle());
        detailMeta.setText("Type: " + evidence.getType() + " | Location: " + evidence.getLocation());
        detailBody.setText("Description:\n" + evidence.getDescription() + "\n\nWhy it matters:\n" + evidence.getWhyMatters());
        detailBody.setCaretPosition(0);

        if (selectedEvidenceA == null) {
            selectedEvidenceA = evidence;
        } else if (selectedEvidenceA.getId() == evidence.getId()) {
            selectedEvidenceA = null;
            selectedEvidenceB = null;
        } else if (selectedEvidenceB == null || selectedEvidenceB.getId() == evidence.getId()) {
            selectedEvidenceB = selectedEvidenceB != null && selectedEvidenceB.getId() == evidence.getId() ? null : evidence;
        } else {
            selectedEvidenceB = evidence;
        }

        applyEvidenceSelectionStyles();
        updateSelectionFeedback("Use Connect to test whether the two selected clues belong together.");
    }

    private void attemptConnection() {
        if (cooldownSecondsRemaining > 0) {
            updateSelectionFeedback("Too many wrong attempts. Please wait " + cooldownSecondsRemaining + " seconds.");
            return;
        }

        if (selectedEvidenceA == null || selectedEvidenceB == null) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please select two evidence cards first.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClueConnection found = investigationService.checkConnection(selectedEvidenceA.getId(), selectedEvidenceB.getId(), connections);
        if (found == null) {
            wrongAttempts++;
            handleWrongAttempt();
            JOptionPane.showMessageDialog(mainFrame,
                    "No stored relation was found between these clues.",
                    "No Relation",
                    JOptionPane.INFORMATION_MESSAGE);
            selectedEvidenceB = null;
            applyEvidenceSelectionStyles();
            return;
        }

        User currentUser = mainFrame.getCurrentUser();
        if (currentUser == null) {
            mainFrame.showLogin();
            return;
        }

        investigationService.saveDiscoveredConnection(currentUser.getId(), found.getId());
        wrongAttempts = 0;
        cooldownSecondsRemaining = 0;
        stopCooldownTimer();
        updateConnectButtonState();
        refreshDiscoveredConnections();
        boardPanel.setDiscoveredConnections(discoveredConnections);
        updateProgressBar();

        JOptionPane.showMessageDialog(mainFrame,
                found.getRelationType() + "\n\n" + found.getExplanation(),
                "Relation Found",
                JOptionPane.INFORMATION_MESSAGE);

        clearSelection();
        updateSelectionFeedback("Relation saved. Select two more clues to continue.");
    }

    private void clearSelection() {
        selectedEvidenceA = null;
        selectedEvidenceB = null;
        applyEvidenceSelectionStyles();
        updateSelectionFeedback("Selection cleared.");
    }

    private void refreshDiscoveredConnections() {
        discoveredConnections.clear();
        User currentUser = mainFrame.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        Set<Integer> discoveredIds = new HashSet<>(investigationService.getDiscoveredConnectionIds(currentUser.getId()));
        for (ClueConnection connection : connections) {
            if (discoveredIds.contains(connection.getId())) {
                discoveredConnections.add(connection);
            }
        }
    }

    private void applyEvidenceSelectionStyles() {
        for (EvidenceCard card : evidenceCards) {
            if (selectedEvidenceA != null && card.getEvidence().getId() == selectedEvidenceA.getId()) {
                card.setSelectionState(EvidenceCard.SelectionState.SOURCE);
            } else if (selectedEvidenceB != null && card.getEvidence().getId() == selectedEvidenceB.getId()) {
                card.setSelectionState(EvidenceCard.SelectionState.TARGET);
            } else {
                card.setSelectionState(EvidenceCard.SelectionState.NONE);
            }
        }

        if (boardPanel != null) {
            boardPanel.setSelection(
                    selectedEvidenceA == null ? null : selectedEvidenceA.getId(),
                    selectedEvidenceB == null ? null : selectedEvidenceB.getId()
            );
        }
    }

    private void updateSelectionFeedback(String message) {
        if (selectionLabel != null) {
            if (selectedEvidenceA == null && selectedEvidenceB == null) {
                selectionLabel.setText("No clues selected");
            } else if (selectedEvidenceA != null && selectedEvidenceB == null) {
                selectionLabel.setText("1 clue selected");
            } else {
                selectionLabel.setText("2 clues selected");
            }
        }

        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void handleWrongAttempt() {
        if (wrongAttempts <= 3) {
            updateSelectionFeedback("Wrong attempt " + wrongAttempts + " / 3. Read the evidence before guessing.");
            return;
        }

        startCooldown();
        updateSelectionFeedback("Too many wrong attempts. Please read the evidence before trying again.");
    }

    private void startCooldown() {
        cooldownSecondsRemaining = 10;
        updateConnectButtonState();
        stopCooldownTimer();

        cooldownTimer = new Timer(1000, e -> {
            cooldownSecondsRemaining--;
            if (cooldownSecondsRemaining > 0) {
                updateSelectionFeedback("Too many wrong attempts. Please wait " + cooldownSecondsRemaining + " seconds.");
                updateConnectButtonState();
                return;
            }

            wrongAttempts = 0;
            cooldownSecondsRemaining = 0;
            stopCooldownTimer();
            updateConnectButtonState();
            updateSelectionFeedback("Cooldown finished. You can connect clues again.");
        });
        cooldownTimer.setRepeats(true);
        cooldownTimer.start();
        updateSelectionFeedback("Too many wrong attempts. Please wait " + cooldownSecondsRemaining + " seconds.");
    }

    private void stopCooldownTimer() {
        if (cooldownTimer != null) {
            cooldownTimer.stop();
            cooldownTimer = null;
        }
    }

    private void updateConnectButtonState() {
        if (connectButton != null) {
            connectButton.setEnabled(cooldownSecondsRemaining <= 0);
            if (cooldownSecondsRemaining > 0) {
                connectButton.setText("Connect (" + cooldownSecondsRemaining + "s)");
            } else {
                connectButton.setText("Connect");
            }
        }
    }

    private void updateProgressBar() {
        if (progressBar == null) {
            return;
        }
        progressBar.setMaximum(Math.max(connections.size(), 1));
        progressBar.setValue(discoveredConnections.size());
        progressBar.setString(discoveredConnections.size() + " / " + connections.size() + " links found");
    }
}
