package com.trinova.scms.view;

import com.trinova.scms.model.Member;

import javax.swing.*;
import java.awt.*;

public class MemberDashboard extends JFrame {

    private final Member member;

    public MemberDashboard(Member member) {
        this.member = member;
        setTitle("SCMS - Member Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(16, 64, 110));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("Smart Coworking Space Management System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topBar.add(titleLabel, BorderLayout.WEST);

        JLabel welcomeLabel = new JLabel("Welcome, " + member.getFullName() + "  |  Member");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setForeground(new Color(16, 64, 110));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(new Color(16, 64, 110));
        rightPanel.add(welcomeLabel);
        rightPanel.add(logoutBtn);
        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 244, 248));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.setPreferredSize(new Dimension(180, 0));

        String[] menuItems = {"Dashboard", "Browse Spaces",
                              "My Bookings", "Subscription", "My Profile"};

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.add(buildWelcomePanel(), "Dashboard");
        contentPanel.add(new JLabel("  Browse Spaces — coming in Sprint 2",
            SwingConstants.CENTER), "Browse Spaces");
        contentPanel.add(new JLabel("  My Bookings — coming in Sprint 2",
            SwingConstants.CENTER), "My Bookings");
        contentPanel.add(new JLabel("  Subscription — coming in Sprint 3",
            SwingConstants.CENTER), "Subscription");
        contentPanel.add(buildProfilePanel(), "My Profile");

        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(240, 244, 248));
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.addActionListener(e -> cardLayout.show(contentPanel, item));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel buildWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel welcome = new JLabel("Welcome back, " + member.getFullName() + "!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setForeground(new Color(16, 64, 110));
        panel.add(welcome, gbc);

        gbc.gridy = 1;
        JLabel role = new JLabel("Role: " + member.getRole() +
                                 "   |   Email: " + member.getEmail());
        role.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        role.setForeground(Color.GRAY);
        panel.add(role, gbc);

        gbc.gridy = 2;
        JLabel info = new JLabel("Use the sidebar to navigate the system.");
        info.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        info.setForeground(Color.GRAY);
        panel.add(info, gbc);

        return panel;
    }

    private JPanel buildProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 10);

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        String[] labels = {"Full Name:", "Email:", "Phone:", "Bio:"};
        JTextField nameField  = new JTextField(member.getFullName(), 20);
        JTextField emailField = new JTextField(member.getEmail(), 20);
        emailField.setEditable(false);
        emailField.setBackground(new Color(240, 240, 240));
        JTextField phoneField = new JTextField(
            member.getPhone() != null ? member.getPhone() : "", 20);
        JTextField bioField   = new JTextField(
            member.getBio() != null ? member.getBio() : "", 20);

        JTextField[] fields = {nameField, emailField, phoneField, bioField};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i + 1; gbc.gridx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
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
        gbc.insets = new Insets(8, 0, 4, 0);
        panel.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                com.trinova.scms.dao.MemberDAO dao =
                    new com.trinova.scms.dao.MemberDAO();
                member.setFullName(nameField.getText().trim());
                member.setPhone(phoneField.getText().trim());
                member.setBio(bioField.getText().trim());
                dao.updateProfile(member);
                statusLabel.setText("Profile updated successfully!");
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        return panel;
    }
}