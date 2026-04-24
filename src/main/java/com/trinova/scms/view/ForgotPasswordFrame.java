package com.trinova.scms.view;

import com.trinova.scms.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ForgotPasswordFrame extends JFrame {

    private final JFrame parent;
    private JTextField emailField;
    private JLabel statusLabel;

    public ForgotPasswordFrame(JFrame parent) {
        this.parent = parent;
        setTitle("SCMS - Forgot Password");
        setSize(480, 420);
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
        card.setPreferredSize(new Dimension(400, 340));
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 28, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridwidth = 2;

        // Icon
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel icon = new JLabel("🔒", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        card.add(icon, gbc);

        // Title
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 2, 0);
        JLabel titleLabel = new JLabel("Reset Password", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        card.add(titleLabel, gbc);

        // Info
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel infoLabel = new JLabel(
            "<html><center>Enter your registered email.<br>" +
            "A reset link will be sent to you.</center></html>",
            SwingConstants.CENTER);
        infoLabel.setFont(UITheme.FONT_SMALL);
        infoLabel.setForeground(UITheme.TEXT_MUTED);
        card.add(infoLabel, gbc);

        // Email label
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 0, 4, 0);
        card.add(UITheme.fieldLabel("EMAIL ADDRESS"), gbc);

        // Email field
        gbc.gridy = 4;
        emailField = UITheme.styledField(20);
        card.add(emailField, gbc);

        // Status
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setFont(UITheme.FONT_SMALL);
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 8, 0);
        card.add(statusLabel, gbc);

        // Send button
        JButton sendBtn = UITheme.primaryButton("Send Reset Link");
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 0, 8, 0);
        card.add(sendBtn, gbc);

        // Back
        JButton backBtn = UITheme.ghostButton("Back to Sign In", UITheme.ACCENT);
        gbc.gridy = 7;
        gbc.insets = new Insets(2, 0, 0, 0);
        card.add(backBtn, gbc);

        // Actions (UNCHANGED)
        sendBtn.addActionListener(e -> doReset());
        backBtn.addActionListener(e -> dispose());

        bg.add(card);
        add(bg);
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
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText(ex.getMessage());
        }
    }
}