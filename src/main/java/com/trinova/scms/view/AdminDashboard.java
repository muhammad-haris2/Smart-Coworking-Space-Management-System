package com.trinova.scms.view;

import com.trinova.scms.model.Member;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private final Member admin;

    public AdminDashboard(Member admin) {
        this.admin = admin;
        setTitle("SCMS - Admin Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(90, 40, 130));
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("SCMS — Admin Panel");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JLabel adminLabel = new JLabel("Admin: " + admin.getFullName() + "  ");
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(new Color(90, 40, 130));
        rightPanel.add(adminLabel);
        rightPanel.add(logoutBtn);
        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 240, 250));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {"Dashboard", "Manage Spaces",
                              "All Bookings", "Promo Codes",
                              "Occupancy Reports", "Member Directory"};

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.add(buildAdminHome(), "Dashboard");
        contentPanel.add(new JLabel("  Manage Spaces — coming in Sprint 4",
            SwingConstants.CENTER), "Manage Spaces");
        contentPanel.add(new JLabel("  All Bookings — coming in Sprint 2",
            SwingConstants.CENTER), "All Bookings");
        contentPanel.add(new JLabel("  Promo Codes — coming in Sprint 3",
            SwingConstants.CENTER), "Promo Codes");
        contentPanel.add(new JLabel("  Occupancy Reports — coming in Sprint 4",
            SwingConstants.CENTER), "Occupancy Reports");
        contentPanel.add(new JLabel("  Member Directory — coming in Sprint 4",
            SwingConstants.CENTER), "Member Directory");

        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(245, 240, 250));
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

    private JPanel buildAdminHome() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel welcome = new JLabel("Admin Control Panel");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcome.setForeground(new Color(90, 40, 130));
        panel.add(welcome, gbc);

        gbc.gridy = 1;
        JLabel info = new JLabel("Logged in as: " + admin.getEmail());
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setForeground(Color.GRAY);
        panel.add(info, gbc);

        gbc.gridy = 2;
        JLabel note = new JLabel("Sprint 2-4 features will appear in the sidebar as they are built.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        note.setForeground(Color.GRAY);
        panel.add(note, gbc);

        return panel;
    }
}