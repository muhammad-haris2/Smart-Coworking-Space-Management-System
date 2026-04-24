package com.trinova.scms.view;

import com.trinova.scms.model.Member;
import com.trinova.scms.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("SCMS - Smart Coworking Space");
        setSize(520, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        // ── Full-frame gradient background ───────────────────
        JPanel bg = UITheme.gradientBackground();

        // ── Floating card ────────────────────────────────────
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // card shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Float(4, 4, getWidth() - 4, getHeight() - 4, 24, 24));
                // card body
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 500));
        card.setBorder(BorderFactory.createEmptyBorder(36, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 0, 4, 0);

        // ── Avatar circle ────────────────────────────────────
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel avatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight());
                int x = (getWidth() - d) / 2;
                g2.setColor(UITheme.ACCENT);
                g2.fillOval(x, 0, d, d);
                // key icon
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
                String icon = "🔑";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(icon,
                    x + (d - fm.stringWidth(icon)) / 2,
                    (d + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(64, 64));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(avatar, gbc);

        // ── Title ────────────────────────────────────────────
        gbc.gridy = 1;
        gbc.insets = new Insets(12, 0, 2, 0);
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        card.add(titleLabel, gbc);

        // ── Subtitle ─────────────────────────────────────────
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel subLabel = new JLabel("Sign in to your coworking account", SwingConstants.CENTER);
        subLabel.setFont(UITheme.FONT_SMALL);
        subLabel.setForeground(UITheme.TEXT_MUTED);
        card.add(subLabel, gbc);

        // ── Email label ──────────────────────────────────────
        gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 0, 4, 0);
        card.add(UITheme.fieldLabel("EMAIL"), gbc);

        // ── Email field ──────────────────────────────────────
        gbc.gridy = 4;
        emailField = UITheme.styledField(20);
        card.add(emailField, gbc);

        // ── Password label ───────────────────────────────────
        gbc.gridy = 5;
        gbc.insets = new Insets(12, 0, 4, 0);
        card.add(UITheme.fieldLabel("PASSWORD"), gbc);

        // ── Password field ───────────────────────────────────
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 0, 4, 0);
        passwordField = UITheme.styledPasswordField(20);
        card.add(passwordField, gbc);

        // ── Status label ─────────────────────────────────────
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setFont(UITheme.FONT_SMALL);
        gbc.gridy = 7;
        gbc.insets = new Insets(6, 0, 6, 0);
        card.add(statusLabel, gbc);

        // ── Login button ─────────────────────────────────────
        JButton loginBtn = UITheme.primaryButton("Sign In");
        gbc.gridy = 8;
        gbc.insets = new Insets(4, 0, 8, 0);
        card.add(loginBtn, gbc);

        // ── Register link ────────────────────────────────────
        JButton registerBtn = UITheme.ghostButton(
            "Don't have an account? Create one", UITheme.ACCENT);
        gbc.gridy = 9;
        gbc.insets = new Insets(2, 0, 2, 0);
        card.add(registerBtn, gbc);

        // ── Forgot password ──────────────────────────────────
        JButton forgotBtn = UITheme.ghostButton(
            "Forgot Password?", UITheme.TEXT_MUTED);
        gbc.gridy = 10;
        card.add(forgotBtn, gbc);

        // ── Actions (UNCHANGED) ──────────────────────────────
        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> openRegister());
        forgotBtn.addActionListener(e -> openForgotPassword());

        bg.add(card);
        add(bg);
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return;
        }

        try {
            AuthService auth = new AuthService();
            Member member = auth.login(email, password);
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Welcome, " + member.getFullName() + "!");

            if (member.getRole().equals("ADMIN")) {
                new AdminDashboard(member).setVisible(true);
            } else {
                new MemberDashboard(member).setVisible(true);
            }
            dispose();

        } catch (Exception ex) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText(ex.getMessage());
            passwordField.setText("");
        }
    }

    private void openRegister() {
        new RegisterFrame(this).setVisible(true);
        setVisible(false);
    }

    private void openForgotPassword() {
        new ForgotPasswordFrame(this).setVisible(true);
    }
}