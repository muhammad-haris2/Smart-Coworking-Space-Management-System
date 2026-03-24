package com.trinova.scms.view;

import com.trinova.scms.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class ForgotPasswordFrame extends JFrame {

    private final JFrame parent;
    private JTextField emailField;
    private JLabel statusLabel;

    public ForgotPasswordFrame(JFrame parent) {
        this.parent = parent;
        setTitle("SCMS - Forgot Password");
        setSize(420, 280);
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
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel titleLabel = new JLabel("Reset Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel infoLabel = new JLabel(
            "<html><center>Enter your registered email.<br>" +
            "A reset link will be sent to you.</center></html>",
            SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(infoLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 10);
        mainPanel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 0, 4, 0);
        mainPanel.add(statusLabel, gbc);

        JButton sendBtn = new JButton("Send Reset Link");
        sendBtn.setBackground(new Color(16, 64, 110));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendBtn.setFocusPainted(false);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendBtn.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = 4;
        gbc.insets = new Insets(8, 0, 4, 0);
        mainPanel.add(sendBtn, gbc);

        JButton backBtn = new JButton("Back to Login");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setForeground(new Color(16, 64, 110));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 4, 0);
        mainPanel.add(backBtn, gbc);

        sendBtn.addActionListener(e -> doReset());
        backBtn.addActionListener(e -> dispose());

        add(mainPanel);
    }

    private void doReset() {
        String email = emailField.getText().trim();
        try {
            AuthService auth = new AuthService();
            auth.initiatePasswordReset(email);
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("If registered, a reset link has been sent.");
            JOptionPane.showMessageDialog(this,
                "If this email is registered, a reset link has been sent.\nCheck your email (simulated in console).",
                "Reset Link Sent", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText(ex.getMessage());
        }
    }
}