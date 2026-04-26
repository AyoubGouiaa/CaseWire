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

public class RegisterPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthService authService = new AuthService();
    private JTextField fullNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(ThemeUtil.APP_BACKGROUND);
        buildUI();
    }

    private void buildUI() {
        JPanel card = ThemeUtil.createSectionPanel();
        card.setLayout(new BorderLayout(0, 18));
        card.setPreferredSize(new Dimension(520, 420));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(ThemeUtil.createTitle("Create Account", 26));
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(ThemeUtil.createMutedLabel("Register once, then your progress stays tied to your account."));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        fullNameField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        styleField(fullNameField);
        styleField(usernameField);
        styleField(passwordField);
        styleField(confirmPasswordField);
        confirmPasswordField.addActionListener(e -> attemptRegister());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFieldLabel("Full Name"), gbc);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createFieldLabel("Username"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createFieldLabel("Password"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createFieldLabel("Confirm Password"), gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        JButton createButton = ThemeUtil.createButton("Create Account", true);
        JButton backButton = ThemeUtil.createButton("Back to Login");

        createButton.addActionListener(e -> attemptRegister());
        backButton.addActionListener(e -> mainFrame.showLogin());

        buttonPanel.add(createButton);
        buttonPanel.add(backButton);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        add(card);
    }

    public void resetForm() {
        if (fullNameField != null) {
            fullNameField.setText("");
        }
        if (usernameField != null) {
            usernameField.setText("");
        }
        if (passwordField != null) {
            passwordField.setText("");
        }
        if (confirmPasswordField != null) {
            confirmPasswordField.setText("");
        }
    }

    private void attemptRegister() {
        try {
            User user = authService.register(
                    fullNameField.getText(),
                    usernameField.getText(),
                    new String(passwordField.getPassword()),
                    new String(confirmPasswordField.getPassword())
            );
            mainFrame.setCurrentUser(user);
            resetForm();
            mainFrame.showHome();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    ex.getMessage(),
                    "Registration Failed",
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
        field.setPreferredSize(new Dimension(240, 30));
        field.setMaximumSize(new Dimension(240, 30));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        ThemeUtil.styleInputField(field);
    }
}
