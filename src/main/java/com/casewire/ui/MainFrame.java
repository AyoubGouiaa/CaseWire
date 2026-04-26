package com.casewire.ui;

import com.casewire.model.CaseFile;
import com.casewire.model.User;
import com.casewire.ui.screens.CaseSelectionPanel;
import com.casewire.ui.screens.HistoryPanel;
import com.casewire.ui.screens.HomePanel;
import com.casewire.ui.screens.InvestigationPanel;
import com.casewire.ui.screens.LoginPanel;
import com.casewire.ui.screens.RegisterPanel;
import com.casewire.ui.screens.ResultPanel;
import com.casewire.ui.screens.SolveCasePanel;
import com.casewire.utils.ThemeUtil;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private static final String LOGIN = "LOGIN";
    private static final String REGISTER = "REGISTER";
    private static final String HOME = "HOME";
    private static final String HISTORY = "HISTORY";
    private static final String CASES = "CASES";
    private static final String INVESTIGATE = "INVESTIGATE";
    private static final String SOLVE = "SOLVE";
    private static final String RESULT = "RESULT";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);

    private User currentUser;
    private final Map<Integer, Long> investigationStartTimes = new HashMap<>();
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private HomePanel homePanel;
    private HistoryPanel historyPanel;
    private CaseSelectionPanel caseSelectionPanel;
    private InvestigationPanel investigationPanel;
    private SolveCasePanel solveCasePanel;
    private ResultPanel resultPanel;

    public MainFrame() {
        setTitle("CaseWire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(ThemeUtil.APP_BACKGROUND);

        buildScreens();
        add(container);
        showLogin();
    }

    private void buildScreens() {
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        homePanel = new HomePanel(this);
        historyPanel = new HistoryPanel(this);
        caseSelectionPanel = new CaseSelectionPanel(this);
        investigationPanel = new InvestigationPanel(this);
        solveCasePanel = new SolveCasePanel(this);
        resultPanel = new ResultPanel(this);

        container.add(loginPanel, LOGIN);
        container.add(registerPanel, REGISTER);
        container.add(homePanel, HOME);
        container.add(historyPanel, HISTORY);
        container.add(caseSelectionPanel, CASES);
        container.add(investigationPanel, INVESTIGATE);
        container.add(solveCasePanel, SOLVE);
        container.add(resultPanel, RESULT);
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
        investigationStartTimes.clear();
        showLogin();
    }

    public void showLogin() {
        loginPanel.resetForm();
        cardLayout.show(container, LOGIN);
    }

    public void showRegister() {
        registerPanel.resetForm();
        cardLayout.show(container, REGISTER);
    }

    public void showHome() {
        if (!ensureLoggedIn()) {
            return;
        }
        homePanel.refresh();
        cardLayout.show(container, HOME);
    }

    public void showHistory() {
        if (!ensureLoggedIn()) {
            return;
        }
        historyPanel.refresh();
        cardLayout.show(container, HISTORY);
    }

    public void showCases() {
        if (!ensureLoggedIn()) {
            return;
        }
        caseSelectionPanel.refresh();
        cardLayout.show(container, CASES);
    }

    public void showInvestigation(CaseFile caseFile) {
        if (!ensureLoggedIn()) {
            return;
        }
        if (caseFile != null) {
            investigationStartTimes.putIfAbsent(caseFile.getId(), System.currentTimeMillis());
        }
        investigationPanel.loadCase(caseFile);
        cardLayout.show(container, INVESTIGATE);
    }

    public void showSolve(CaseFile caseFile) {
        if (!ensureLoggedIn()) {
            return;
        }
        solveCasePanel.loadCase(caseFile);
        cardLayout.show(container, SOLVE);
    }

    public void showResult(int caseId, int score, String verdict,
                           String correctSuspectName, String chosenSuspectName,
                           String explanation) {
        if (!ensureLoggedIn()) {
            return;
        }
        resultPanel.showResult(caseId, score, verdict, correctSuspectName, chosenSuspectName, explanation);
        cardLayout.show(container, RESULT);
    }

    public void beginInvestigation(int caseId) {
        investigationStartTimes.put(caseId, System.currentTimeMillis());
    }

    public int getInvestigationTimeSeconds(int caseId) {
        Long startedAt = investigationStartTimes.get(caseId);
        if (startedAt == null) {
            return 0;
        }

        long elapsedMillis = System.currentTimeMillis() - startedAt;
        return Math.max(0, (int) (elapsedMillis / 1000L));
    }

    public void clearInvestigationTimer(int caseId) {
        investigationStartTimes.remove(caseId);
    }

    private boolean ensureLoggedIn() {
        if (currentUser == null) {
            showLogin();
            return false;
        }
        return true;
    }
}
