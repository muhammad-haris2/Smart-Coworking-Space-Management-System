package com.trinova.scms.view;

import com.trinova.scms.service.AuthService;

import javax.swing.*;
import java.awt.*;

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
        setSize(480, 480);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(16, 64, 110));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        mainPanel.add(new JSeparator(), gbc);

        // Full Name
        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 2, 10);
        mainPanel.add(new JLabel("Full Name:"), gbc);
        nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        // Email
        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Password
        gbc.gridy = 4; gbc.gridx = 0;
        mainPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridy = 5; gbc.gridx = 0;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // Password hint
        JLabel hintLabel = new JLabel("Min 8 chars, 1 uppercase, 1 digit, 1 special char");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 4, 0);
        mainPanel.add(hintLabel, gbc);

        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 7;
        mainPanel.add(statusLabel, gbc);

        // Register button
        JButton registerBtn = new JButton("Create Account");
        registerBtn.setBackground(new Color(16, 64, 110));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = 8;
        gbc.insets = new Insets(8, 0, 4, 0);
        mainPanel.add(registerBtn, gbc);

        // Back to login
        JButton backBtn = new JButton("Already have an account? Login");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(16, 64, 110));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 4, 0);
        mainPanel.add(backBtn, gbc);

        registerBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> goBack());

        add(mainPanel);
    }

    private void doRegister() {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmPasswordField.getPassword());

        statusLabel.setForeground(Color.GRAY);
        statusLabel.setText("Registering...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                AuthService auth = new AuthService();
                auth.register(name, email, password, confirm);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText("Account created! Please login.");
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                        "Registration successful!\nA verification email has been sent to: " + email,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    goBack();
                } catch (Exception ex) {
                    statusLabel.setForeground(Color.RED);
                    String msg = ex.getMessage();
                    if (msg != null && msg.contains("Exception: ")) {
                        msg = msg.substring(msg.indexOf("Exception: ") + 11);
                    }
                    statusLabel.setText(msg != null ? msg : "Registration failed.");
                }
            }
        };
        worker.execute();
    }

    private void goBack() {
        parent.setVisible(true);
        dispose();
    }
}