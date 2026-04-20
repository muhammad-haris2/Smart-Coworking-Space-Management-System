package com.trinova.scms.view;

import com.trinova.scms.model.Member;

import javax.swing.*;
import java.awt.*;

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
        topBar.setBackground(new Color(60, 30, 100));
        topBar.setBorder(
            BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel(
            "SCMS — Admin Control Panel");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(
            new Font("Segoe UI", Font.BOLD, 16));
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JLabel adminLabel = new JLabel(
            "Admin: " + admin.getFullName() + "  ");
        adminLabel.setForeground(Color.WHITE);
        adminLabel.setFont(
            new Font("Segoe UI", Font.PLAIN, 13));

        JPanel rightPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(new Color(60, 30, 100));
        rightPanel.add(adminLabel);
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

        CardLayout cardLayout =
            (CardLayout) contentPanel.getLayout();

        // ── Sidebar ────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(
            new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 240, 252));
        sidebar.setBorder(
            BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {
            "Dashboard", "All Bookings",
            "All Invoices", "Manage Spaces",
            "Promo Codes"
        };

        for (String item : menuItems) {
            JButton btn = sidebarButton(item);
            btn.addActionListener(e ->
                cardLayout.show(contentPanel, item));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(
                new Dimension(0, 5)));
        }

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(245, 240, 252));
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private JPanel buildAdminHome() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(12, 0, 12, 0);

        gbc.gridy = 0;
        JLabel title = new JLabel("Admin Control Panel");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(60, 30, 100));
        panel.add(title, gbc);

        gbc.gridy = 1;
        JLabel sub = new JLabel(
            "Logged in as: " + admin.getEmail());
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(Color.GRAY);
        panel.add(sub, gbc);

        gbc.gridy = 2;
        JLabel note = new JLabel(
            "Use the sidebar to manage bookings, " +
            "invoices, spaces and promo codes.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        note.setForeground(Color.GRAY);
        panel.add(note, gbc);

        return panel;
    }

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
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 13));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.service.BookingService svc =
                    new com.trinova.scms.service.BookingService();
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
                        String.format("%.2f", b.getTotalCost()),
                        b.getStatus()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        // Cancel booking button
        JButton cancelBtn = new JButton(
            "Cancel Selected Booking");
        cancelBtn.setBackground(new Color(180, 30, 30));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel,
                    "Please select a booking.");
                return;
            }
            int bookingId = (int) model.getValueAt(row, 0);
            String status = (String) model.getValueAt(row, 8);
            if (!status.equals("CONFIRMED")) {
                JOptionPane.showMessageDialog(panel,
                    "Only CONFIRMED bookings can be cancelled.");
                return;
            }
            String reason = JOptionPane.showInputDialog(
                panel, "Enter cancellation reason:");
            if (reason == null || reason.trim().isEmpty())
                return;
            try {
                com.trinova.scms.service.BookingService svc =
                    new com.trinova.scms.service
                        .BookingService();
                svc.cancel(bookingId, admin.getEmail(), reason);
                JOptionPane.showMessageDialog(panel,
                    "Booking #" + bookingId + " cancelled.");
                refreshBtn.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        JPanel topBar = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setBackground(Color.WHITE);
        topBar.add(refreshBtn);
        topBar.add(cancelBtn);
        panel.add(topBar, BorderLayout.SOUTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.doClick();
        return panel;
    }

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
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 13));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.service.BillingService svc =
                    new com.trinova.scms.service.BillingService();
                for (com.trinova.scms.model.Invoice inv :
                        svc.getAllInvoices()) {
                    model.addRow(new Object[]{
                        inv.getInvoiceId(),
                        inv.getBookingId(),
                        inv.getMemberName(),
                        inv.getRoomName(),
                        String.format("%.2f",
                            inv.getBaseAmount()),
                        String.format("%.2f",
                            inv.getFacilityCost()),
                        String.format("%.2f",
                            inv.getVatAmount()),
                        String.format("%.2f",
                            inv.getTotalAmount()),
                        inv.getIssueDate()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        JPanel topBar = new JPanel(
            new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Color.WHITE);
        topBar.add(refreshBtn);
        panel.add(topBar, BorderLayout.SOUTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.doClick();
        return panel;
    }

    private JPanel buildSpaceManager() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Manage Spaces");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(60, 30, 100));
        title.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {
            "ID", "Name", "Type", "Capacity",
            "Size", "Hourly (PKR)", "Daily (PKR)",
            "Monthly (PKR)", "Active"};
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 13));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.dao.RoomDAO dao =
                    new com.trinova.scms.dao.RoomDAO();
                for (com.trinova.scms.model.Room r :
                        dao.getAllRooms()) {
                    model.addRow(new Object[]{
                        r.getRoomId(),
                        r.getRoomName(),
                        r.getRoomType(),
                        r.getCapacity(),
                        r.getPrivateSize() != null ?
                            r.getPrivateSize() : "—",
                        r.getHourlyPrice() > 0 ?
                            String.format("%.0f",
                                r.getHourlyPrice()) : "—",
                        r.getDailyPrice() > 0 ?
                            String.format("%.0f",
                                r.getDailyPrice()) : "—",
                        r.getMonthlyPrice() > 0 ?
                            String.format("%.0f",
                                r.getMonthlyPrice()) : "—",
                        r.isActive() ? "Yes" : "No"
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        JButton deleteBtn = new JButton(
            "Deactivate Selected");
        deleteBtn.setBackground(new Color(180, 30, 30));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel,
                    "Please select a space.");
                return;
            }
            int roomId = (int) model.getValueAt(row, 0);
            try {
                com.trinova.scms.dao.RoomDAO dao =
                    new com.trinova.scms.dao.RoomDAO();
                if (dao.hasActiveBookings(roomId)) {
                    JOptionPane.showMessageDialog(panel,
                        "Cannot deactivate — " +
                        "this space has active bookings!");
                    return;
                }
                dao.deleteRoom(roomId);
                JOptionPane.showMessageDialog(panel,
                    "Space deactivated successfully.");
                refreshBtn.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        JPanel topBar = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setBackground(Color.WHITE);
        topBar.add(refreshBtn);
        topBar.add(deleteBtn);
        panel.add(topBar, BorderLayout.SOUTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.doClick();
        return panel;
    }

    private JPanel buildPromoManager() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Promo Codes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(60, 30, 100));
        title.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {
            "ID", "Code", "Type", "Value", "Active", "Used"};
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 13));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.dao.SubscriptionDAO dao =
                    new com.trinova.scms.dao.SubscriptionDAO();
                for (com.trinova.scms.model.PromoCode p :
                        dao.getAllPromoCodes()) {
                    model.addRow(new Object[]{
                        p.getPromoId(),
                        p.getCode(),
                        p.getDiscountType(),
                        p.getDiscountValue(),
                        p.isActive() ? "Yes" : "No",
                        p.getUsageCount()
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        JButton toggleBtn = new JButton(
            "Toggle Active/Inactive");
        toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        toggleBtn.setFocusPainted(false);
        toggleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel,
                    "Please select a promo code.");
                return;
            }
            int promoId = (int) model.getValueAt(row, 0);
            boolean currentlyActive =
                model.getValueAt(row, 4).equals("Yes");
            try {
                com.trinova.scms.dao.SubscriptionDAO dao =
                    new com.trinova.scms.dao.SubscriptionDAO();
                dao.togglePromoCode(promoId, !currentlyActive);
                refreshBtn.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        JPanel topBar = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setBackground(Color.WHITE);
        topBar.add(refreshBtn);
        topBar.add(toggleBtn);
        panel.add(topBar, BorderLayout.SOUTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.doClick();
        return panel;
    }
}