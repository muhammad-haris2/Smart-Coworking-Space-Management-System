package com.trinova.scms.view;

import com.trinova.scms.model.Member;
import com.trinova.scms.model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final Member admin;

    public AdminDashboard(Member admin) {
        this.admin = admin;
        setTitle("SCMS - Admin Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ── Top bar ────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        JLabel titleLabel = new JLabel("SCMS — Admin Control Panel");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel adminBadge = new JLabel("ADMIN");
        adminBadge.setOpaque(true);
        adminBadge.setBackground(Color.WHITE);
        adminBadge.setForeground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        adminBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        adminBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        adminBadge.putClientProperty("FlatLaf.style", "arc: 20");
        
        JLabel adminName = new JLabel(admin.getFullName());
        adminName.setForeground(Color.WHITE);
        adminName.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.putClientProperty("FlatLaf.style", "background: #ffffff; foreground: #3C1E64; arc: 5; font: 12");
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        rightPanel.add(adminBadge);
        rightPanel.add(adminName);
        rightPanel.add(logoutBtn);
        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Content panels ─────────────────────────────────
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.add(buildAdminHome(),      "Dashboard");
        contentPanel.add(buildAllBookings(),    "All Bookings");
        contentPanel.add(buildAllInvoices(),    "All Invoices");
        contentPanel.add(buildSpaceManager(),   "Manage Spaces");
        contentPanel.add(buildPromoManager(),   "Promo Codes");
        contentPanel.add(buildOccupancyReport(),"Occupancy Report");
        contentPanel.add(buildMemberDirectory(),"Member Directory");

        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();

        // ── Sidebar ────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(com.trinova.scms.util.UIConfig.PURPLE_LIGHT);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JLabel menuTitle = new JLabel("ADMIN MENU");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuTitle.setForeground(new Color(150, 150, 180));
        menuTitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        sidebar.add(menuTitle);

        String[] menuItems = {
            "Dashboard", "All Bookings", "All Invoices", 
            "Manage Spaces", "Promo Codes", "Occupancy Report", "Member Directory"
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
                    b.setBackground(new Color(230, 220, 250));
                    b.setForeground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
                    b.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    b.setOpaque(true);
                } else {
                    b.setBackground(com.trinova.scms.util.UIConfig.PURPLE_LIGHT);
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

    private JPanel buildAdminHome() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Welcome Header
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel title = new JLabel("Admin Control Panel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        header.add(title, gbc);

        gbc.gridy = 1;
        JLabel loginInfo = new JLabel("Logged in as: " + admin.getEmail() + " | Role: ADMINISTRATOR");
        loginInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginInfo.setForeground(Color.GRAY);
        header.add(loginInfo, gbc);

        gbc.gridy = 2;
        JLabel subNote = new JLabel("Use the sidebar to manage all system features.");
        subNote.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subNote.setForeground(Color.GRAY);
        header.add(subNote, gbc);

        panel.add(header, BorderLayout.NORTH);

        // Stats Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        try {
            int members = new com.trinova.scms.dao.MemberDAO().getAllMembers().size();
            int spaces = new com.trinova.scms.dao.RoomDAO().getAllRooms().size();
            int bookings = new com.trinova.scms.service.BookingService().getAllBookings().size();
            double revenue = 124800.00; // Mocked for design parity

            statsPanel.add(statCard("Total Members", String.valueOf(members), new Color(16, 64, 110)));
            statsPanel.add(statCard("Active Spaces", String.valueOf(spaces), new Color(0, 128, 0)));
            statsPanel.add(statCard("Total Bookings", String.valueOf(bookings), new Color(180, 80, 0)));
            statsPanel.add(statCard("Total Revenue", "PKR 1,24,800", com.trinova.scms.util.UIConfig.PURPLE_DARK));
        } catch (Exception e) {}

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        // Quick Actions & Activity
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        // Quick Actions
        JPanel actionsBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        actionsBox.setOpaque(false);
        actionsBox.add(actionButton("View All Bookings →"));
        actionsBox.add(actionButton("Manage Spaces →"));
        actionsBox.add(actionButton("View Reports →"));
        actionsBox.add(actionButton("Member Directory →"));
        
        JLabel actionsTitle = new JLabel("Quick Actions");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JPanel actionsWrapper = new JPanel(new BorderLayout());
        actionsWrapper.setOpaque(false);
        actionsWrapper.add(actionsTitle, BorderLayout.NORTH);
        actionsWrapper.add(actionsBox, BorderLayout.CENTER);
        
        bottomRow.add(actionsWrapper, BorderLayout.NORTH);

        // Recent Activity
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setOpaque(false);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel activityTitle = new JLabel("Recent Activity");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityPanel.add(activityTitle, BorderLayout.NORTH);

        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setOpaque(false);
        activityList.add(activityItem("New booking — Fatima Malik, Alpha Hub (HOURLY)", "2 mins ago"));
        activityList.add(activityItem("New member registered: Ali Raza (ali@email.com)", "14 mins ago"));
        activityList.add(activityItem("Space 'Executive Suite' updated by admin.", "1 hour ago"));
        
        activityPanel.add(activityList, BorderLayout.CENTER);
        bottomRow.add(activityPanel, BorderLayout.CENTER);

        centerPanel.add(bottomRow, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton actionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 30, 100), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.putClientProperty("FlatLaf.style", "borderWidth: 1; borderColor: #3C1E64; arc: 10");
        return btn;
    }

    private JPanel activityItem(String text, String time) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel mainText = new JLabel("•  " + text);
        mainText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(mainText, BorderLayout.WEST);
        
        JLabel timeText = new JLabel(time);
        timeText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeText.setForeground(Color.GRAY);
        p.add(timeText, BorderLayout.EAST);
        
        return p;
    }

    private JPanel statCard(String label, String value, Color color) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 235), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        card.putClientProperty("FlatLaf.style", "borderWidth: 1; borderColor: " + String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.WEST; g.weightx = 1.0;

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(Color.GRAY);
        card.add(lblLabel, g);

        g.gridy = 1;
        JLabel valLabel = new JLabel(value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valLabel.setForeground(color);
        card.add(valLabel, g);

        return card;
    }

    // ── All Bookings ───────────────────────────────────────
    private JPanel buildAllBookings() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("All Bookings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(60, 30, 100));
        title.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {
            "ID", "Member", "Space", "Type",
            "Date", "Start", "End",
            "Total (PKR)", "Status"};
        DefaultTableModel model =
            new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(
                        int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        styleTable(table);

        JButton refreshBtn = new JButton("Refresh");
        JButton cancelBtn  = new JButton(
            "Cancel Selected");
        cancelBtn.setBackground(new Color(180, 30, 30));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.service.BookingService svc =
                    new com.trinova.scms.service
                        .BookingService();
                for (com.trinova.scms.model.Booking b :
                        svc.getAllBookings()) {
                    model.addRow(new Object[]{
                        b.getBookingId(),
                        b.getMemberName(),
                        b.getRoomName(),
                        b.getBookingType(),
                        b.getStartTime().toLocalDate(),
                        b.getStartTime().toLocalTime(),
                        b.getEndTime().toLocalTime(),
                        String.format("%.2f",
                            b.getTotalCost()),
                        b.getStatus()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel,
                    "Please select a booking.");
                return;
            }
            int bookingId =
                (int) model.getValueAt(row, 0);
            String status =
                (String) model.getValueAt(row, 8);
            if (!status.equals("CONFIRMED")) {
                JOptionPane.showMessageDialog(panel,
                    "Only CONFIRMED bookings " +
                    "can be cancelled.");
                return;
            }
            String reason = JOptionPane.showInputDialog(
                panel,
                "Enter cancellation reason " +
                "(required):");
            if (reason == null ||
                reason.trim().isEmpty()) return;
            try {
                com.trinova.scms.service.BookingService
                    svc = new com.trinova.scms
                        .service.BookingService();
                svc.cancel(bookingId,
                    admin.getEmail(), reason);
                JOptionPane.showMessageDialog(panel,
                    "Booking #" + bookingId +
                    " cancelled successfully.");
                refreshBtn.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        JPanel btnBar = btnBar(refreshBtn, cancelBtn);
        panel.add(btnBar, BorderLayout.SOUTH);
        panel.add(new JScrollPane(table),
            BorderLayout.CENTER);
        refreshBtn.doClick();
        return panel;
    }

    // ── All Invoices ───────────────────────────────────────
    private JPanel buildAllInvoices() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("All Invoices");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(60, 30, 100));
        title.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {
            "Invoice ID", "Booking ID", "Member",
            "Space", "Base (PKR)", "Facilities (PKR)",
            "VAT (PKR)", "Total (PKR)", "Date"};
        DefaultTableModel model =
            new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(
                        int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        styleTable(table);

        JButton refreshBtn = new JButton("Refresh ⟳");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.service.BillingService
                    svc = new com.trinova.scms.service.BillingService();
                for (com.trinova.scms.model.Invoice inv : svc.getAllInvoices()) {
                    model.addRow(new Object[]{
                        inv.getInvoiceId(),
                        inv.getBookingId(),
                        inv.getMemberName(),
                        inv.getRoomName(),
                        String.format("%.2f", inv.getBaseAmount()),
                        String.format("%.2f", inv.getFacilityCost()),
                        String.format("%.2f", inv.getVatAmount()),
                        String.format("%.2f", inv.getTotalAmount()),
                        inv.getIssueDate()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage());
            }
        });

        panel.add(btnBar(refreshBtn), BorderLayout.SOUTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshBtn.doClick();
        return panel;
    }

    // ── Manage Spaces ──────────────────────────────────────
    private JPanel buildSpaceManager() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Header with Search and Add
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Manage Spaces");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        header.add(title, BorderLayout.WEST);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightControls.setOpaque(false);

        JTextField searchField = new JTextField(20);
        searchField.putClientProperty("FlatLaf.placeholderText", "Search spaces...");
        searchField.putClientProperty("FlatLaf.style", "arc: 8");
        
        JButton addBtn = new JButton("+ Add Space");
        addBtn.setBackground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.putClientProperty("FlatLaf.style", "arc: 8");

        rightControls.add(searchField);
        rightControls.add(addBtn);
        header.add(rightControls, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Name", "Type", "Capacity", "Hourly Rate", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(45);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(240, 240, 240));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
        panel.add(scroll, BorderLayout.CENTER);

        // Load data (Mocked for design)
        model.addRow(new Object[]{"Alpha Hub - Desk 1", "HOT_DESK", "1", "1,500 PKR", "Available", "Edit | Deactivate"});
        model.addRow(new Object[]{"Alpha Hub - Desk 2", "HOT_DESK", "1", "1,500 PKR", "Available", "Edit | Deactivate"});
        model.addRow(new Object[]{"Alpha Hub - Desk 3", "HOT_DESK", "1", "1,500 PKR", "Available", "Edit | Deactivate"});
        model.addRow(new Object[]{"Alpha Hub - Desk 4", "HOT_DESK", "1", "1,500 PKR", "Available", "Edit | Deactivate"});
        model.addRow(new Object[]{"Alpha Hub - Desk 5", "HOT_DESK", "1", "1,500 PKR", "Available", "Edit | Deactivate"});

        addBtn.addActionListener(e -> openRoomForm(null, panel, null));

        return panel;
    }

    private void openRoomForm(Room existing, JPanel parent, JButton refreshBtn) {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setUndecorated(true);
        dialog.setSize(450, 580);
        dialog.setLocationRelativeTo(this);
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        content.putClientProperty("FlatLaf.style", "arc: 15");

        // Dialog Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        
        JLabel title = new JLabel(existing == null ? "Add New Space" : "Edit Space");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        header.add(title, BorderLayout.WEST);

        JButton closeBtn = new JButton("✕");
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        closeBtn.setForeground(Color.GRAY);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());
        header.add(closeBtn, BorderLayout.EAST);
        
        content.add(header, BorderLayout.NORTH);

        // Form Fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;

        String[] labels = {"Space Name *", "Space Type *", "Capacity *", "Amenities", "Private Room Size", "Hourly Price (PKR)", "Daily Price (PKR)", "Monthly Price (PKR)"};
        String[] placeholders = {"e.g. Alpha Hub", "HOT_DESK", "e.g. 10", "e.g. Wi-Fi, AC, Projector", "--- (None)", "0", "0", "0"};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i * 2;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(new Color(100, 100, 100));
            form.add(lbl, gbc);

            gbc.gridy = i * 2 + 1;
            if (labels[i].contains("Type") || labels[i].contains("Size")) {
                JComboBox<String> combo = new JComboBox<>(new String[]{placeholders[i]});
                combo.putClientProperty("FlatLaf.style", "arc: 8");
                form.add(combo, gbc);
            } else {
                JTextField txt = new JTextField();
                txt.putClientProperty("FlatLaf.placeholderText", placeholders[i]);
                txt.putClientProperty("FlatLaf.style", "arc: 8");
                form.add(txt, gbc);
            }
        }
        
        content.add(new JScrollPane(form), BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 25, 25, 25));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.putClientProperty("FlatLaf.style", "background: #ffffff; foreground: #3C1E64; borderWidth: 1; borderColor: #3C1E64; arc: 8; font: bold 13");
        cancelBtn.setPreferredSize(new Dimension(100, 36));
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = new JButton(existing == null ? "Add Space" : "Save Changes");
        saveBtn.putClientProperty("FlatLaf.style", "background: #3C1E64; foreground: #ffffff; arc: 8; font: bold 13");
        saveBtn.setPreferredSize(new Dimension(140, 36));

        footer.add(cancelBtn);
        footer.add(saveBtn);
        content.add(footer, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.setVisible(true);
    }
    // ── Occupancy Report ───────────────────────────────────
    private JPanel buildOccupancyReport() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Header Row
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        
        JPanel titleBox = new JPanel(new BorderLayout());
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Occupancy Report");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        titleBox.add(title, BorderLayout.NORTH);
        JLabel sub = new JLabel("Daily booking breakdown by space type and total revenue generated.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);
        titleBox.add(sub, BorderLayout.SOUTH);
        top.add(titleBox, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controls.setOpaque(false);
        JComboBox<String> period = new JComboBox<>(new String[]{"Last 7 Days", "Last 30 Days", "This Month"});
        period.setPreferredSize(new Dimension(120, 32));
        JButton genBtn = new JButton("Generate Report");
        genBtn.setBackground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        genBtn.setForeground(Color.WHITE);
        genBtn.setPreferredSize(new Dimension(140, 32));
        JButton expBtn = new JButton("Export CSV");
        expBtn.setPreferredSize(new Dimension(100, 32));
        
        controls.add(period);
        controls.add(genBtn);
        controls.add(expBtn);
        top.add(controls, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        // Stats Row
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        stats.setOpaque(false);
        stats.add(badge("Period: Apr 15-22, 2026", new Color(240, 240, 240), Color.DARK_GRAY));
        stats.add(badge("Total Bookings: 56", new Color(220, 240, 255), new Color(16, 64, 110)));
        stats.add(badge("Total Revenue: PKR 1,24,800", new Color(230, 250, 240), new Color(0, 100, 50)));
        stats.add(badge("Avg Daily: PKR 17,828", new Color(255, 245, 230), new Color(150, 80, 0)));
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(stats, BorderLayout.NORTH);

        // Table
        String[] cols = {"Date", "Hot Desk Bkgs", "Mtg Room Bkgs", "Priv Room Bkgs", "Total Bkgs", "HD Revenue PKR", "Mtg Revenue PKR", "Priv Revenue PKR", "Total Revenue PKR"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(35);
        centerWrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        
        panel.add(centerWrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel badge(String text, Color bg, Color fg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        p.setBackground(bg);
        p.putClientProperty("FlatLaf.style", "arc: 15");
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(fg);
        p.add(l);
        return p;
    }

    // ── Promo Codes ────────────────────────────────────────
    private JPanel buildPromoManager() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel titleBox = new JPanel(new BorderLayout());
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Promo Codes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        titleBox.add(title, BorderLayout.NORTH);
        JLabel sub = new JLabel("Create and manage discount codes for members.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);
        titleBox.add(sub, BorderLayout.SOUTH);
        header.add(titleBox, BorderLayout.WEST);

        JLabel stats = new JLabel("6 codes | 4 active, 2 inactive");
        stats.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        stats.setForeground(Color.GRAY);
        header.add(stats, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"ID", "Code", "Discount Type", "Value", "Status", "Times Used", "Created"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(40);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        btns.setOpaque(false);
        JButton refreshBtn = new JButton("Refresh ⟳");
        JButton addBtn = new JButton("Add Promo Code +");
        addBtn.putClientProperty("FlatLaf.style", "background: #ffffff; foreground: #3C1E64; borderWidth: 1; borderColor: #3C1E64; arc: 8");
        JButton toggleBtn = new JButton("Toggle Active");
        toggleBtn.setBackground(new Color(180, 80, 0));
        toggleBtn.setForeground(Color.WHITE);
        
        btns.add(refreshBtn);
        btns.add(addBtn);
        btns.add(toggleBtn);
        footer.add(btns, BorderLayout.WEST);
        
        JLabel selectedInfo = new JLabel("Row 1 selected: WELCOME20 (Active)");
        selectedInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        selectedInfo.setForeground(Color.GRAY);
        footer.add(selectedInfo, BorderLayout.EAST);
        
        panel.add(footer, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> openPromoForm(panel, null));

        return panel;
    }

    private void openPromoForm(JPanel parent, JButton refreshBtn) {
        JDialog dialog = new JDialog(this, "", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        content.putClientProperty("FlatLaf.style", "arc: 15");

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        JLabel title = new JLabel("Add Promo Code");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        header.add(title, BorderLayout.WEST);
        
        JButton close = new JButton("✕");
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);
        close.addActionListener(e -> dialog.dispose());
        header.add(close, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 0, 8, 0); gbc.gridx = 0;

        String[] labels = {"Promo Code *", "Discount Type *", "Discount Value *", "Max Usage (opt.)", "Expiry Date (opt.)"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i * 2;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            form.add(lbl, gbc);
            
            gbc.gridy = i * 2 + 1;
            if (i == 1) {
                JComboBox<String> combo = new JComboBox<>(new String[]{"PERCENTAGE", "FLAT"});
                form.add(combo, gbc);
            } else {
                JTextField txt = new JTextField();
                txt.putClientProperty("FlatLaf.style", "arc: 8");
                form.add(txt, gbc);
            }
        }
        content.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 25));
        footer.setOpaque(false);
        JButton cancel = new JButton("Cancel");
        cancel.putClientProperty("FlatLaf.style", "background: #ffffff; foreground: #3C1E64; borderWidth: 1; borderColor: #3C1E64; arc: 8");
        JButton save = new JButton("Add Code");
        save.setBackground(com.trinova.scms.util.UIConfig.PURPLE_DARK);
        save.setForeground(Color.WHITE);
        footer.add(cancel); footer.add(save);
        content.add(footer, BorderLayout.SOUTH);

        dialog.add(content);
        dialog.setVisible(true);
    }

    // ── Member Directory ───────────────────────────────────
    private JPanel buildMemberDirectory() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Member Directory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(60, 30, 100));
        title.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {
            "ID", "Full Name", "Email",
            "Phone", "Role", "Plan",
            "Plan Expiry", "Joined"};
        DefaultTableModel model =
            new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(
                        int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        styleTable(table);

        // Search bar
        JPanel searchPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search by name:"));
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);

        JPanel topSection = new JPanel(
            new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(searchPanel, BorderLayout.SOUTH);
        panel.add(topSection, BorderLayout.NORTH);

        JButton refreshBtn = new JButton("Refresh");
        JButton lockBtn    = new JButton(
            "Toggle Lock");
        lockBtn.setFocusPainted(false);

        Runnable loadAll = () -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.dao.MemberDAO dao =
                    new com.trinova.scms.dao
                        .MemberDAO();
                for (Member m : dao.getAllMembers()) {
                    model.addRow(new Object[]{
                        m.getMemberId(),
                        m.getFullName(),
                        m.getEmail(),
                        m.getPhone() != null ?
                            m.getPhone() : "—",
                        m.getRole(),
                        m.getPlanType() != null ?
                            m.getPlanType() : "None",
                        m.getPlanExpiry() != null ?
                            m.getPlanExpiry() : "—",
                        m.getMemberId()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        };

        refreshBtn.addActionListener(
            e -> loadAll.run());

        searchBtn.addActionListener(e -> {
            String query =
                searchField.getText().trim()
                    .toLowerCase();
            if (query.isEmpty()) {
                loadAll.run();
                return;
            }
            model.setRowCount(0);
            try {
                com.trinova.scms.dao.MemberDAO dao =
                    new com.trinova.scms.dao
                        .MemberDAO();
                for (Member m : dao.getAllMembers()) {
                    if (m.getFullName().toLowerCase()
                            .contains(query) ||
                        m.getEmail().toLowerCase()
                            .contains(query)) {
                        model.addRow(new Object[]{
                            m.getMemberId(),
                            m.getFullName(),
                            m.getEmail(),
                            m.getPhone() != null ?
                                m.getPhone() : "—",
                            m.getRole(),
                            m.getPlanType() != null ?
                                m.getPlanType() :
                                "None",
                            m.getPlanExpiry() != null ?
                                m.getPlanExpiry() : "—",
                            m.getMemberId()
                        });
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        showAllBtn.addActionListener(
            e -> loadAll.run());

        lockBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel,
                    "Please select a member.");
                return;
            }
            int memberId =
                (int) model.getValueAt(row, 0);
            String email =
                (String) model.getValueAt(row, 2);
            if (email.equals(admin.getEmail())) {
                JOptionPane.showMessageDialog(panel,
                    "Cannot lock your own account.");
                return;
            }
            try {
                com.trinova.scms.dao.MemberDAO dao =
                    new com.trinova.scms.dao
                        .MemberDAO();
                Member m = dao.findById(memberId);
                if (m.isLocked()) {
                    dao.resetFailedAttempts(
                        m.getEmail());
                    JOptionPane.showMessageDialog(
                        panel, "Account unlocked.");
                } else {
                    // Lock by setting failed attempts
                    // to 5
                    java.sql.Connection conn =
                        com.trinova.scms.dao
                            .DatabaseConnection
                            .getInstance()
                            .getConnection();
                    java.sql.PreparedStatement ps =
                        conn.prepareStatement(
                            "UPDATE members SET " +
                            "is_locked = 1, " +
                            "failed_attempts = 5 " +
                            "WHERE member_id = ?");
                    ps.setInt(1, memberId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(
                        panel, "Account locked.");
                }
                loadAll.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        panel.add(btnBar(
            refreshBtn, lockBtn),
            BorderLayout.SOUTH);
        panel.add(new JScrollPane(table),
            BorderLayout.CENTER);
        loadAll.run();
        return panel;
    }

    // ── Helpers ────────────────────────────────────────────
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(220, 220, 220));
    }

    private JPanel btnBar(JButton... buttons) {
        JPanel bar = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, Color.LIGHT_GRAY));
        for (JButton btn : buttons) {
            btn.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));
            bar.add(btn);
        }
        return bar;
    }
}