package com.trinova.scms.view;

import com.trinova.scms.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RegisterFrame extends JFrame {

    private final JFrame parent;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;

    public RegisterFrame(JFrame parent) {
        this.parent = parent;
        setTitle("SCMS - Register");
        setSize(520, 680);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bg = UITheme.gradientBackground();

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 4, getHeight() - 4, 24, 24));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 580));
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 28, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridwidth = 2;

        // Title
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 2, 0);
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        card.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel subLabel = new JLabel("Join the smart coworking community", SwingConstants.CENTER);
        subLabel.setFont(UITheme.FONT_SMALL);
        subLabel.setForeground(UITheme.TEXT_MUTED);
        card.add(subLabel, gbc);

        // Full Name
        gbc.gridy = 2; gbc.insets = new Insets(4, 0, 4, 0);
        card.add(UITheme.fieldLabel("FULL NAME"), gbc);
        gbc.gridy = 3;
        nameField = UITheme.styledField(20);
        card.add(nameField, gbc);

        // Email
        gbc.gridy = 4; gbc.insets = new Insets(10, 0, 4, 0);
        card.add(UITheme.fieldLabel("EMAIL"), gbc);
        gbc.gridy = 5; gbc.insets = new Insets(4, 0, 4, 0);
        emailField = UITheme.styledField(20);
        card.add(emailField, gbc);

        // Password
        gbc.gridy = 6; gbc.insets = new Insets(10, 0, 4, 0);
        card.add(UITheme.fieldLabel("PASSWORD"), gbc);
        gbc.gridy = 7; gbc.insets = new Insets(4, 0, 4, 0);
        passwordField = UITheme.styledPasswordField(20);
        card.add(passwordField, gbc);

        // Confirm Password
        gbc.gridy = 8; gbc.insets = new Insets(10, 0, 4, 0);
        card.add(UITheme.fieldLabel("CONFIRM PASSWORD"), gbc);
        gbc.gridy = 9; gbc.insets = new Insets(4, 0, 4, 0);
        confirmPasswordField = UITheme.styledPasswordField(20);
        card.add(confirmPasswordField, gbc);

        // Password hint
        gbc.gridy = 10;
        gbc.insets = new Insets(2, 0, 4, 0);
        JLabel hintLabel = new JLabel("Min 8 chars, 1 uppercase, 1 digit, 1 special char");
        hintLabel.setFont(UITheme.FONT_TINY);
        hintLabel.setForeground(UITheme.TEXT_MUTED);
        card.add(hintLabel, gbc);

        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setFont(UITheme.FONT_SMALL);
        gbc.gridy = 11;
        gbc.insets = new Insets(6, 0, 6, 0);
        card.add(statusLabel, gbc);

        // Register button
        JButton registerBtn = UITheme.primaryButton("Create Account");
        gbc.gridy = 12;
        gbc.insets = new Insets(4, 0, 8, 0);
        card.add(registerBtn, gbc);

        // Back to login
        JButton backBtn = UITheme.ghostButton(
            "Already have an account? Sign In", UITheme.ACCENT);
        gbc.gridy = 13;
        gbc.insets = new Insets(2, 0, 0, 0);
        card.add(backBtn, gbc);

        // Actions (UNCHANGED)
        registerBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> goBack());

        bg.add(card);
        add(bg);
    }

    private void doRegister() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmPasswordField.getPassword());

        try {
            AuthService auth = new AuthService();
            auth.register(name, email, password, confirm);
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Account created! Please login.");
            JOptionPane.showMessageDialog(this,
                "Registration successful!\nA verification email has been sent to: " + email,
                "Success", JOptionPane.INFORMATION_MESSAGE);
            goBack();
        } catch (Exception ex) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText(ex.getMessage());
        }
    }

    private void goBack() {
        parent.setVisible(true);
        dispose();
    }
}