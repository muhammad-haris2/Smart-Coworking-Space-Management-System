package com.trinova.scms.view;

import com.trinova.scms.model.Member;

import javax.swing.*;
import java.awt.*;

public class MemberDashboard extends JFrame {

    private final Member member;

    public MemberDashboard(Member member) {
        this.member = member;
        setTitle("SCMS - Member Dashboard");
        setSize(1050, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ── Top bar ────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(com.trinova.scms.util.UIConfig.NAVY_DARK);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        JLabel titleLabel = new JLabel("Smart Coworking Space Management System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        String planText = member.hasActivePlan() ? member.getPlanType() + " Plan" : "No Plan";
        JLabel welcomeLabel = new JLabel("Welcome, " + member.getFullName() + " | " + planText);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.putClientProperty("FlatLaf.style", "background: #ffffff; foreground: #10406E; arc: 5; font: 12");
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        rightPanel.add(welcomeLabel);
        rightPanel.add(logoutBtn);
        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Content panels ─────────────────────────────────
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.add(buildWelcomePanel(),              "Dashboard");
        contentPanel.add(new FloorMapPanel(member),        "Space Map");
        contentPanel.add(new SpaceBrowserPanel(member),    "Browse Spaces");
        contentPanel.add(new BookingHistoryPanel(member),  "My Bookings");
        contentPanel.add(new SubscriptionPanel(member),    "Subscription");
        contentPanel.add(new InvoicePanel(member),         "My Invoices");
        contentPanel.add(buildProfilePanel(),              "My Profile");

        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();

        // ── Sidebar ────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 245, 250));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {
            "Dashboard", "Space Map", "Browse Spaces", "My Bookings", "Subscription", "My Invoices", "My Profile"
        };

        for (String item : menuItems) {
            JButton btn = sidebarButton(item);
            btn.addActionListener(e -> {
                cardLayout.show(contentPanel, item);
                updateSidebarActive(sidebar, btn);
            });
            sidebar.add(btn);
        }

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void updateSidebarActive(JPanel sidebar, JButton activeBtn) {
        for (Component c : sidebar.getComponents()) {
            if (c instanceof JButton) {
                JButton b = (JButton) c;
                if (b == activeBtn) {
                    b.setBackground(new Color(220, 235, 250));
                    b.setForeground(com.trinova.scms.util.UIConfig.NAVY_DARK);
                    b.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    b.setOpaque(true);
                } else {
                    b.setBackground(new Color(240, 245, 250));
                    b.setForeground(com.trinova.scms.util.UIConfig.TEXT_DARK);
                    b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    b.setOpaque(false);
                }
            }
        }
    }

    private JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        return btn;
    }

    private JPanel buildWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel welcome = new JLabel("Welcome back, " + member.getFullName() + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(com.trinova.scms.util.UIConfig.NAVY_DARK);
        header.add(welcome, gbc);

        gbc.gridy = 1;
        JLabel role = new JLabel("Role: MEMBER | Email: " + member.getEmail());
        role.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        role.setForeground(Color.GRAY);
        header.add(role, gbc);

        // Plan Banner
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 0, 15, 0);
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        banner.setBackground(new Color(230, 250, 240));
        banner.setBorder(BorderFactory.createLineBorder(new Color(180, 230, 200), 1));
        banner.putClientProperty("FlatLaf.style", "arc: 20");
        
        JLabel bannerTxt = new JLabel("✔ Active Plan: " + (member.hasActivePlan() ? member.getPlanType() : "PREMIUM") + " — Expires: 2026-05-20");
        bannerTxt.setForeground(new Color(0, 100, 50));
        bannerTxt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        banner.add(bannerTxt);
        header.add(banner, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel subNote = new JLabel("Use the sidebar to browse spaces, manage bookings, and view your invoices.");
        subNote.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subNote.setForeground(Color.GRAY);
        header.add(subNote, gbc);

        panel.add(header, BorderLayout.NORTH);

        // Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        statsPanel.add(statCard("Upcoming Bookings", "3", new Color(16, 64, 110)));
        statsPanel.add(statCard("Current Plan", member.hasActivePlan() ? member.getPlanType() : "PREMIUM", new Color(0, 128, 0)));
        statsPanel.add(statCard("Total Invoices", "12", new Color(120, 80, 200)));

        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel statCard(String label, String value, Color color) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        card.putClientProperty("FlatLaf.style", "borderWidth: 1; borderColor: #E0E5EA");

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.weightx = 1.0;

        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valLabel.setForeground(color);
        card.add(valLabel, g);

        g.gridy = 1;
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLabel.setForeground(Color.GRAY);
        card.add(lblLabel, g);

        return card;
    }

    private JPanel buildProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 10);

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(
            new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        JTextField nameField = new JTextField(
            member.getFullName(), 20);
        JTextField emailField = new JTextField(
            member.getEmail(), 20);
        emailField.setEditable(false);
        emailField.setBackground(new Color(240, 240, 240));
        JTextField phoneField = new JTextField(
            member.getPhone() != null ?
            member.getPhone() : "", 20);
        JTextField bioField = new JTextField(
            member.getBio() != null ?
            member.getBio() : "", 20);

        String[] labels = {
            "Full Name:", "Email:", "Phone:", "Bio:"};
        JTextField[] fields = {
            nameField, emailField, phoneField, bioField};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i + 1; gbc.gridx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        JLabel statusLabel = new JLabel(
            " ", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(0, 128, 0));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(16, 64, 110));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        panel.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                com.trinova.scms.dao.MemberDAO dao =
                    new com.trinova.scms.dao.MemberDAO();
                member.setFullName(
                    nameField.getText().trim());
                member.setPhone(
                    phoneField.getText().trim());
                member.setBio(
                    bioField.getText().trim());
                dao.updateProfile(member);
                statusLabel.setForeground(
                    new Color(0, 128, 0));
                statusLabel.setText(
                    "Profile updated successfully!");
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText(
                    "Error: " + ex.getMessage());
            }
        });

        return panel;
    }
}