package com.trinova.scms.view;

import com.trinova.scms.model.Member;
import com.trinova.scms.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("SCMS - Smart Coworking Space");
        setSize(450, 380);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        gbc.insets = new Insets(8, 0, 8, 0);

        // Title
        JLabel titleLabel = new JLabel("SCMS — Sign In", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(16, 64, 110));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subLabel = new JLabel("Smart Coworking Space Management", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(subLabel, gbc);

        // Separator
        gbc.gridy = 2;
        mainPanel.add(new JSeparator(), gbc);

        // Email label
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 2, 10);
        mainPanel.add(new JLabel("Email:"), gbc);

        // Email field
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Password label
        gbc.gridy = 4;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Password:"), gbc);

        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 0, 4, 0);
        mainPanel.add(statusLabel, gbc);

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(16, 64, 110));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = 6;
        gbc.insets = new Insets(8, 0, 4, 0);
        mainPanel.add(loginBtn, gbc);

        // Register button
        JButton registerBtn = new JButton("Don't have an account? Register");
        registerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setForeground(new Color(16, 64, 110));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 4, 0);
        mainPanel.add(registerBtn, gbc);

        // Forgot password button
        JButton forgotBtn = new JButton("Forgot Password?");
        forgotBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setForeground(Color.GRAY);
        forgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8;
        mainPanel.add(forgotBtn, gbc);

        // Actions
        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> openRegister());
        forgotBtn.addActionListener(e -> openForgotPassword());

        add(mainPanel);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return;
        }

        statusLabel.setForeground(Color.GRAY);
        statusLabel.setText("Logging in...");

        // Use SwingWorker to prevent UI freeze
        SwingWorker<Member, Void> worker = new SwingWorker<>() {
            @Override
            protected Member doInBackground() throws Exception {
                AuthService auth = new AuthService();
                return auth.login(email, password);
            }

            @Override
            protected void done() {
                try {
                    Member member = get();
                    statusLabel.setForeground(new Color(0, 128, 0));
                    statusLabel.setText("Welcome, " + member.getFullName() + "!");

                    if (member.getRole().equals("ADMIN")) {
                        new AdminDashboard(member).setVisible(true);
                    } else {
                        new MemberDashboard(member).setVisible(true);
                    }
                    dispose();
                } catch (Exception ex) {
                    statusLabel.setForeground(Color.RED);
                    String msg = ex.getMessage();
                    if (msg != null && msg.contains("Exception: ")) {
                        msg = msg.substring(msg.indexOf("Exception: ") + 11);
                    }
                    statusLabel.setText(msg != null ? msg : "Login failed.");
                    passwordField.setText("");
                }
            }
        };
        worker.execute();
    }

    private void openRegister() {
        new RegisterFrame(this).setVisible(true);
        setVisible(false);
    }

    private void openForgotPassword() {
        new ForgotPasswordFrame(this).setVisible(true);
    }
}