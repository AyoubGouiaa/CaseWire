package com.casewire.ui.screens;

import com.casewire.model.CaseFile;
import com.casewire.model.ClueConnection;
import com.casewire.model.Evidence;
import com.casewire.model.Solution;
import com.casewire.model.Suspect;
import com.casewire.model.User;
import com.casewire.service.CaseService;
import com.casewire.service.HistoryService;
import com.casewire.service.InvestigationService;
import com.casewire.service.ProgressService;
import com.casewire.service.SolveService;
import com.casewire.ui.MainFrame;
import com.casewire.utils.ThemeUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SolveCasePanel extends JPanel {

    private final MainFrame mainFrame;
    private final CaseService caseService = new CaseService();
    private final SolveService solveService = new SolveService();
    private final ProgressService progressService = new ProgressService();
    private final InvestigationService investigationService = new InvestigationService();
    private final HistoryService historyService = new HistoryService();

    private CaseFile currentCase;
    private List<Suspect> suspects;
    private List<Evidence> evidenceList;
    private Solution solution;

    private final List<JRadioButton> suspectRadios = new ArrayList<>();
    private final List<JCheckBox> evidenceCheckboxes = new ArrayList<>();
    private JComboBox<String> motiveCombo;

    public SolveCasePanel(MainFrame mainFrame) {
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

        currentCase = caseFile;
        suspects = caseService.getSuspects(caseFile.getId());
        evidenceList = caseService.getEvidence(caseFile.getId());
        solution = caseService.getSolution(caseFile.getId());

        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        JPanel header = ThemeUtil.createSectionPanel();
        header.setLayout(new BorderLayout());

        JButton backButton = ThemeUtil.createButton("<- Investigation");
        backButton.addActionListener(e -> mainFrame.showInvestigation(currentCase));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(ThemeUtil.createTitle("Solve Case", 22));
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(ThemeUtil.createMutedLabel(currentCase.getTitle()));

        header.add(backButton, BorderLayout.WEST);
        header.add(titlePanel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(ThemeUtil.APP_BACKGROUND);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        content.add(buildSuspectPanel());
        content.add(Box.createVerticalStrut(12));
        content.add(buildMotivePanel());
        content.add(Box.createVerticalStrut(12));
        content.add(buildEvidencePanel());
        content.add(Box.createVerticalStrut(18));

        JButton submitButton = ThemeUtil.createButton("Submit Accusation", true);
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.addActionListener(e -> submitAccusation());
        content.add(submitButton);

        JScrollPane scrollPane = new JScrollPane(content);
        ThemeUtil.styleScrollPane(scrollPane, ThemeUtil.APP_BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buildSuspectPanel() {
        JPanel panel = ThemeUtil.createSectionPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = ThemeUtil.createTitle("Primary Suspect", 16);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));

        ButtonGroup group = new ButtonGroup();
        suspectRadios.clear();
        for (Suspect suspect : suspects) {
            JRadioButton radio = new JRadioButton(suspect.getName() + " - " + suspect.getRole());
            radio.setOpaque(false);
            radio.setForeground(ThemeUtil.TEXT_PRIMARY);
            radio.setFont(ThemeUtil.bodyFont(13));
            radio.putClientProperty("suspectId", suspect.getId());
            group.add(radio);
            suspectRadios.add(radio);
            panel.add(radio);
            panel.add(Box.createVerticalStrut(4));
        }

        return panel;
    }

    private JPanel buildMotivePanel() {
        JPanel panel = ThemeUtil.createSectionPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = ThemeUtil.createTitle("Motive", 16);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));

        motiveCombo = new JComboBox<>(buildMotiveOptions());
        motiveCombo.setMaximumSize(new Dimension(500, 28));
        motiveCombo.setBackground(ThemeUtil.INPUT_BACKGROUND);
        motiveCombo.setForeground(ThemeUtil.INPUT_TEXT);
        motiveCombo.setFont(ThemeUtil.bodyFont(13));
        panel.add(motiveCombo);
        return panel;
    }

    private String[] buildMotiveOptions() {
        List<String> options = new ArrayList<>();
        if (solution != null) {
            options.add(solution.getCorrectMotive());
        }
        options.add("Financial gain");
        options.add("Jealousy and rivalry");
        options.add("Personal revenge");
        options.add("Self-preservation");
        options.add("Theft of property");
        options.add("Silencing a witness");
        return options.stream().distinct().toArray(String[]::new);
    }

    private JPanel buildEvidencePanel() {
        JPanel panel = ThemeUtil.createSectionPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = ThemeUtil.createTitle("Supporting Evidence", 16);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));

        evidenceCheckboxes.clear();
        for (Evidence evidence : evidenceList) {
            JCheckBox checkBox = new JCheckBox(evidence.getTitle() + " [" + evidence.getType() + "]");
            checkBox.setOpaque(false);
            checkBox.setForeground(ThemeUtil.TEXT_PRIMARY);
            checkBox.setFont(ThemeUtil.bodyFont(13));
            checkBox.putClientProperty("evidenceId", evidence.getId());
            evidenceCheckboxes.add(checkBox);
            panel.add(checkBox);
            panel.add(Box.createVerticalStrut(4));
        }

        JLabel helper = ThemeUtil.createMutedLabel("Select up to three supporting pieces of evidence.");
        helper.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(8));
        panel.add(helper);
        return panel;
    }

    private void submitAccusation() {
        User currentUser = mainFrame.getCurrentUser();
        if (currentUser == null) {
            mainFrame.showLogin();
            return;
        }

        int suspectId = -1;
        String chosenSuspectName = "None";
        for (JRadioButton radio : suspectRadios) {
            if (radio.isSelected()) {
                suspectId = (int) radio.getClientProperty("suspectId");
                for (Suspect suspect : suspects) {
                    if (suspect.getId() == suspectId) {
                        chosenSuspectName = suspect.getName();
                        break;
                    }
                }
            }
        }

        if (suspectId == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please choose a suspect first.", "Missing Choice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String chosenMotive = (String) motiveCombo.getSelectedItem();
        List<Integer> chosenEvidenceIds = new ArrayList<>();
        for (JCheckBox checkBox : evidenceCheckboxes) {
            if (checkBox.isSelected()) {
                chosenEvidenceIds.add((int) checkBox.getClientProperty("evidenceId"));
            }
        }

        Set<Integer> discoveredIds = investigationService.getDiscoveredConnectionIds(currentUser.getId());
        List<ClueConnection> caseConnections = caseService.getClueConnections(currentCase.getId());
        boolean discoveredAny = caseConnections.stream().anyMatch(connection -> discoveredIds.contains(connection.getId()));

        int score = solveService.calculateScore(suspectId, chosenMotive, chosenEvidenceIds, discoveredAny, solution);
        String verdict = solveService.getVerdict(score);
        progressService.saveResult(currentUser.getId(), currentCase.getId(), score);
        if (score >= 50) {
            int timeTakenSeconds = mainFrame.getInvestigationTimeSeconds(currentCase.getId());
            historyService.saveCaseHistory(currentUser.getId(), currentCase.getId(), score, timeTakenSeconds);
            mainFrame.clearInvestigationTimer(currentCase.getId());
        }

        String correctSuspectName = "Unknown";
        for (Suspect suspect : suspects) {
            if (suspect.getId() == solution.getCorrectSuspectId()) {
                correctSuspectName = suspect.getName();
                break;
            }
        }

        mainFrame.showResult(currentCase.getId(), score, verdict, correctSuspectName, chosenSuspectName, solution.getExplanation());
    }
}
