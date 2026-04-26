package com.casewire.ui.screens;

import com.casewire.model.User;
import com.casewire.service.AuthService;
import com.casewire.ui.MainFrame;
import com.casewire.utils.ThemeUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthService authService = new AuthService();
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(ThemeUtil.APP_BACKGROUND);
        buildUI();
    }

    private void buildUI() {
        JPanel card = ThemeUtil.createSectionPanel();
        card.setLayout(new BorderLayout(0, 18));
        card.setPreferredSize(new Dimension(460, 340));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(ThemeUtil.createTitle("CaseWire Login", 26));
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(ThemeUtil.createMutedLabel("Sign in to continue your investigations."));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        styleField(usernameField);
        styleField(passwordField);
        passwordField.addActionListener(e -> attemptLogin());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFieldLabel("Username"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createFieldLabel("Password"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton loginButton = ThemeUtil.createButton("Login", true);
        JButton registerButton = ThemeUtil.createButton("Register");
        JButton exitButton = ThemeUtil.createButton("Exit");

        loginButton.addActionListener(e -> attemptLogin());
        registerButton.addActionListener(e -> mainFrame.showRegister());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        add(card);
    }

    public void resetForm() {
        if (usernameField != null) {
            usernameField.setText("");
        }
        if (passwordField != null) {
            passwordField.setText("");
        }
    }

    private void attemptLogin() {
        try {
            User user = authService.login(usernameField.getText(), new String(passwordField.getPassword()));
            mainFrame.setCurrentUser(user);
            resetForm();
            mainFrame.showHome();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    ex.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeUtil.bodyFont(13));
        label.setForeground(ThemeUtil.TEXT_PRIMARY);
        return label;
    }

    private void styleField(JComponent field) {
        field.setPreferredSize(new Dimension(220, 30));
        field.setMaximumSize(new Dimension(220, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        ThemeUtil.styleInputField(field);
    }
}
