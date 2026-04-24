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
        JPanel topBar = UITheme.adminTopBar();

        JLabel titleLabel = new JLabel("⬡  SCMS — Admin");
        titleLabel.setForeground(UITheme.TEXT_WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton logoutBtn = UITheme.logoutButton();
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JLabel adminLabel = new JLabel("Admin: " + admin.getFullName() + "  ");
        adminLabel.setForeground(UITheme.TEXT_ON_DARK);
        adminLabel.setFont(UITheme.FONT_SMALL);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(UITheme.initialsAvatar(admin.getFullName(), UITheme.ADMIN_ACCENT));
        rightPanel.add(adminLabel);
        rightPanel.add(logoutBtn);
        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ── Content panels ─────────────────────────────────
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(UITheme.BG_CONTENT);
        contentPanel.add(buildAdminHome(),      "Dashboard");
        contentPanel.add(buildAllBookings(),    "All Bookings");
        contentPanel.add(buildAllInvoices(),    "All Invoices");
        contentPanel.add(buildSpaceManager(),   "Manage Spaces");
        contentPanel.add(buildPromoManager(),   "Promo Codes");
        contentPanel.add(buildOccupancyReport(),"Occupancy Report");
        contentPanel.add(buildMemberDirectory(),"Member Directory");

        CardLayout cardLayout =
            (CardLayout) contentPanel.getLayout();

        // ── Sidebar ────────────────────────────────────────
        JPanel sidebar = UITheme.sidebar(UITheme.ADMIN_DARK);

        String[] menuItems = {
            "Dashboard",
            "All Bookings",
            "All Invoices",
            "Manage Spaces",
            "Promo Codes",
            "Occupancy Report",
            "Member Directory"
        };

        String[] icons = {"#","B","I","S","P","O","M"};
        for (int i = 0; i < menuItems.length; i++) {
            String item = menuItems[i];
            JButton btn = UITheme.sidebarButton(
                item, UITheme.ADMIN_DARK, UITheme.ADMIN_ACCENT);
            btn.addActionListener(e ->
                cardLayout.show(contentPanel, item));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // sidebarButton now provided by UITheme

    // ── Dashboard home ─────────────────────────────────────
    private JPanel buildAdminHome() {
        JPanel panel = UITheme.contentPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(12, 0, 12, 0);

        gbc.gridy = 0;
        JLabel title = new JLabel("Admin Control Panel");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(title, gbc);

        gbc.gridy = 1;
        JLabel sub = new JLabel(
            "Logged in as: " + admin.getEmail());
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(Color.GRAY);
        panel.add(sub, gbc);

        gbc.gridy = 2;
        JLabel note = new JLabel(
            "Use the sidebar to manage all " +
            "system features.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        note.setForeground(Color.GRAY);
        panel.add(note, gbc);

        // Quick stats
        gbc.gridy = 3;
        JPanel statsPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 24, 10));
        statsPanel.setBackground(Color.WHITE);

        try {
            com.trinova.scms.dao.MemberDAO memberDAO =
                new com.trinova.scms.dao.MemberDAO();
            com.trinova.scms.dao.RoomDAO roomDAO =
                new com.trinova.scms.dao.RoomDAO();
            com.trinova.scms.service.BookingService bookSvc =
                new com.trinova.scms.service.BookingService();

            int memberCount =
                memberDAO.getAllMembers().size();
            int roomCount =
                roomDAO.getAllRooms().size();
            int bookingCount =
                bookSvc.getAllBookings().size();

            statsPanel.add(UITheme.statCard(
                "Total Members",
                String.valueOf(memberCount),
                UITheme.ACCENT, "M"));
            statsPanel.add(UITheme.statCard(
                "Total Spaces",
                String.valueOf(roomCount),
                UITheme.SUCCESS, "S"));
            statsPanel.add(UITheme.statCard(
                "Total Bookings",
                String.valueOf(bookingCount),
                UITheme.WARNING, "B"));
        } catch (Exception e) {
            statsPanel.add(new JLabel(
                "Could not load stats: " + e.getMessage()));
        }

        panel.add(statsPanel, gbc);
        return panel;
    }

    // statCard now provided by UITheme

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

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.service.BillingService
                    svc = new com.trinova.scms
                        .service.BillingService();
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

        panel.add(btnBar(refreshBtn),
            BorderLayout.SOUTH);
        panel.add(new JScrollPane(table),
            BorderLayout.CENTER);
        refreshBtn.doClick();
        return panel;
    }

    // ── Manage Spaces ──────────────────────────────────────
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
            "Size", "Hourly", "Daily",
            "Monthly", "Active"};
        DefaultTableModel model =
            new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(
                        int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        styleTable(table);

        JButton refreshBtn  = new JButton("Refresh");
        JButton addBtn      = new JButton("Add New Space");
        JButton editBtn     = new JButton(
            "Edit Selected");
        JButton deactivateBtn = new JButton(
            "Deactivate Selected");
        deactivateBtn.setBackground(
            new Color(180, 30, 30));
        deactivateBtn.setForeground(Color.WHITE);
        deactivateBtn.setFocusPainted(false);

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.dao.RoomDAO dao =
                    new com.trinova.scms.dao.RoomDAO();
                for (Room r : dao.getAllRooms()) {
                    model.addRow(new Object[]{
                        r.getRoomId(),
                        r.getRoomName(),
                        r.getRoomType(),
                        r.getCapacity(),
                        r.getPrivateSize() != null ?
                            r.getPrivateSize() : "—",
                        r.getHourlyPrice() > 0 ?
                            "PKR " + String.format(
                                "%.0f",
                                r.getHourlyPrice()) :
                            "—",
                        r.getDailyPrice() > 0 ?
                            "PKR " + String.format(
                                "%.0f",
                                r.getDailyPrice()) :
                            "—",
                        r.getMonthlyPrice() > 0 ?
                            "PKR " + String.format(
                                "%.0f",
                                r.getMonthlyPrice()) :
                            "—",
                        r.isActive() ? "Yes" : "No"
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        addBtn.addActionListener(e ->
            openRoomForm(null, panel, refreshBtn));

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel,
                    "Please select a space to edit.");
                return;
            }
            int roomId =
                (int) model.getValueAt(row, 0);
            try {
                Room room =
                    new com.trinova.scms.dao.RoomDAO()
                        .findById(roomId);
                openRoomForm(room, panel, refreshBtn);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        deactivateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel,
                    "Please select a space.");
                return;
            }
            int roomId =
                (int) model.getValueAt(row, 0);
            try {
                com.trinova.scms.dao.RoomDAO dao =
                    new com.trinova.scms.dao.RoomDAO();
                if (dao.hasActiveBookings(roomId)) {
                    JOptionPane.showMessageDialog(panel,
                        "Cannot deactivate — " +
                        "this space has active bookings!");
                    return;
                }
                int confirm = JOptionPane
                    .showConfirmDialog(panel,
                        "Deactivate this space?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION)
                    return;
                dao.deleteRoom(roomId);
                JOptionPane.showMessageDialog(panel,
                    "Space deactivated.");
                refreshBtn.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        panel.add(btnBar(
            refreshBtn, addBtn,
            editBtn, deactivateBtn),
            BorderLayout.SOUTH);
        panel.add(new JScrollPane(table),
            BorderLayout.CENTER);
        refreshBtn.doClick();
        return panel;
    }

    private void openRoomForm(Room existing,
                               JPanel parent,
                               JButton refreshBtn) {
        JDialog dialog = new JDialog(this,
            existing == null ?
            "Add New Space" : "Edit Space",
            true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(
            BorderFactory.createEmptyBorder(
                20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        // Fields
        JTextField nameField = new JTextField(
            existing != null ?
            existing.getRoomName() : "", 20);
        JComboBox<String> typeCombo =
            new JComboBox<>(new String[]{
                "HOT_DESK", "MEETING_ROOM",
                "PRIVATE_ROOM"});
        if (existing != null)
            typeCombo.setSelectedItem(
                existing.getRoomType());
        JTextField capacityField = new JTextField(
            existing != null ?
            String.valueOf(
                existing.getCapacity()) : "", 10);
        JTextField amenitiesField = new JTextField(
            existing != null &&
            existing.getAmenities() != null ?
            existing.getAmenities() : "", 20);
        JComboBox<String> sizeCombo =
            new JComboBox<>(new String[]{
                "—", "SMALL", "MEDIUM", "LARGE"});
        if (existing != null &&
            existing.getPrivateSize() != null)
            sizeCombo.setSelectedItem(
                existing.getPrivateSize());
        JTextField hourlyField = new JTextField(
            existing != null ?
            String.valueOf(
                existing.getHourlyPrice()) : "0", 10);
        JTextField dailyField = new JTextField(
            existing != null ?
            String.valueOf(
                existing.getDailyPrice()) : "0", 10);
        JTextField monthlyField = new JTextField(
            existing != null ?
            String.valueOf(
                existing.getMonthlyPrice()) : "0", 10);

        String[][] rows = {
            {"Space Name:", null},
            {"Type:", null},
            {"Capacity:", null},
            {"Amenities:", null},
            {"Private Size:", null},
            {"Hourly Price:", null},
            {"Daily Price:", null},
            {"Monthly Price:", null}
        };
        Component[] comps = {
            nameField, typeCombo, capacityField,
            amenitiesField, sizeCombo,
            hourlyField, dailyField, monthlyField
        };

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            gbc.weightx = 0.3;
            form.add(new JLabel(rows[i][0]), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            form.add(comps[i], gbc);
        }

        JLabel statusLbl = new JLabel(
            " ", SwingConstants.CENTER);
        statusLbl.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = rows.length;
        gbc.gridwidth = 2;
        form.add(statusLbl, gbc);

        JButton saveBtn = new JButton(
            existing == null ?
            "Add Space" : "Save Changes");
        saveBtn.setBackground(new Color(16, 64, 110));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        gbc.gridy = rows.length + 1;
        form.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String name =
                    nameField.getText().trim();
                if (name.isEmpty())
                    throw new Exception(
                        "Space name is required.");

                int capacity = Integer.parseInt(
                    capacityField.getText().trim());
                double hourly = Double.parseDouble(
                    hourlyField.getText().trim());
                double daily = Double.parseDouble(
                    dailyField.getText().trim());
                double monthly = Double.parseDouble(
                    monthlyField.getText().trim());

                Room room = existing != null ?
                    existing : new Room();
                room.setRoomName(name);
                room.setRoomType(
                    (String) typeCombo
                        .getSelectedItem());
                room.setCapacity(capacity);
                room.setAmenities(
                    amenitiesField.getText().trim());
                String size =
                    (String) sizeCombo
                        .getSelectedItem();
                room.setPrivateSize(
                    "—".equals(size) ? null : size);
                room.setHourlyPrice(hourly);
                room.setDailyPrice(daily);
                room.setMonthlyPrice(monthly);

                com.trinova.scms.dao.RoomDAO dao =
                    new com.trinova.scms.dao.RoomDAO();
                if (existing == null) {
                    dao.addRoom(room);
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Space added successfully!");
                } else {
                    dao.updateRoom(room);
                    JOptionPane.showMessageDialog(
                        dialog,
                        "Space updated successfully!");
                }
                refreshBtn.doClick();
                dialog.dispose();

            } catch (NumberFormatException ex) {
                statusLbl.setText(
                    "Capacity and prices " +
                    "must be numbers.");
            } catch (Exception ex) {
                statusLbl.setText(ex.getMessage());
            }
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // ── Promo Codes ────────────────────────────────────────
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
            "ID", "Code", "Type",
            "Value", "Active", "Used"};
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
        JButton addBtn     = new JButton(
            "Add Promo Code");
        JButton toggleBtn  = new JButton(
            "Toggle Active");
        toggleBtn.setFocusPainted(false);

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.dao.SubscriptionDAO
                    dao = new com.trinova.scms.dao
                        .SubscriptionDAO();
                for (com.trinova.scms.model.PromoCode
                        p : dao.getAllPromoCodes()) {
                    model.addRow(new Object[]{
                        p.getPromoId(),
                        p.getCode(),
                        p.getDiscountType(),
                        p.getDiscountType().equals(
                            "PERCENTAGE") ?
                            p.getDiscountValue() +
                            "%" :
                            "PKR " +
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

        addBtn.addActionListener(e ->
            openPromoForm(panel, refreshBtn));

        toggleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(panel,
                    "Please select a promo code.");
                return;
            }
            int promoId =
                (int) model.getValueAt(row, 0);
            boolean active =
                model.getValueAt(row, 4)
                    .equals("Yes");
            try {
                com.trinova.scms.dao.SubscriptionDAO
                    dao = new com.trinova.scms.dao
                        .SubscriptionDAO();
                dao.togglePromoCode(promoId, !active);
                refreshBtn.doClick();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        panel.add(btnBar(
            refreshBtn, addBtn, toggleBtn),
            BorderLayout.SOUTH);
        panel.add(new JScrollPane(table),
            BorderLayout.CENTER);
        refreshBtn.doClick();
        return panel;
    }

    private void openPromoForm(JPanel parent,
                                JButton refreshBtn) {
        JDialog dialog = new JDialog(this,
            "Add Promo Code", true);
        dialog.setSize(380, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(
            BorderFactory.createEmptyBorder(
                20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        JTextField codeField = new JTextField(15);
        JComboBox<String> typeCombo =
            new JComboBox<>(
                new String[]{"PERCENTAGE", "FLAT"});
        JTextField valueField = new JTextField(10);

        String[][] rows = {
            {"Code:", null},
            {"Discount Type:", null},
            {"Value:", null}
        };
        Component[] comps = {
            codeField, typeCombo, valueField};

        for (int i = 0; i < rows.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            form.add(new JLabel(rows[i][0]), gbc);
            gbc.gridx = 1;
            form.add(comps[i], gbc);
        }

        JLabel statusLbl = new JLabel(" ");
        statusLbl.setForeground(Color.RED);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        form.add(statusLbl, gbc);

        JButton saveBtn = new JButton("Add Code");
        saveBtn.setBackground(new Color(16, 64, 110));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        gbc.gridy = 4;
        form.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String code =
                    codeField.getText().trim()
                        .toUpperCase();
                if (code.isEmpty())
                    throw new Exception(
                        "Code cannot be empty.");
                double value = Double.parseDouble(
                    valueField.getText().trim());

                com.trinova.scms.model.PromoCode p =
                    new com.trinova.scms
                        .model.PromoCode();
                p.setCode(code);
                p.setDiscountType(
                    (String) typeCombo
                        .getSelectedItem());
                p.setDiscountValue(value);

                com.trinova.scms.dao.SubscriptionDAO
                    dao = new com.trinova.scms.dao
                        .SubscriptionDAO();
                dao.addPromoCode(p);
                JOptionPane.showMessageDialog(
                    dialog,
                    "Promo code added!");
                refreshBtn.doClick();
                dialog.dispose();

            } catch (NumberFormatException ex) {
                statusLbl.setText(
                    "Value must be a number.");
            } catch (Exception ex) {
                statusLbl.setText(ex.getMessage());
            }
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // ── Occupancy Report ───────────────────────────────────
    private JPanel buildOccupancyReport() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Occupancy Report");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(60, 30, 100));
        title.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {
            "Date", "Hot Desk Bookings",
            "Meeting Room Bookings",
            "Private Room Bookings",
            "Total Bookings",
            "Total Revenue (PKR)"};
        DefaultTableModel model =
            new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(
                        int r, int c) {
                    return false;
                }
            };
        JTable table = new JTable(model);
        styleTable(table);

        JButton refreshBtn = new JButton(
            "Generate Report");
        refreshBtn.setBackground(new Color(16, 64, 110));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            try {
                com.trinova.scms.dao.BookingDAO dao =
                    new com.trinova.scms.dao
                        .BookingDAO();
                List<com.trinova.scms.model.Booking>
                    all = dao.getAllBookings();

                // Group by date
                java.util.Map<java.time.LocalDate,
                    int[]> map = new java.util
                        .TreeMap<>();

                for (com.trinova.scms.model.Booking
                        b : all) {
                    if (b.getStatus().equals(
                            "CANCELLED")) continue;
                    java.time.LocalDate d =
                        b.getStartTime().toLocalDate();
                    map.putIfAbsent(d, new int[5]);
                    int[] counts = map.get(d);
                    switch (b.getRoomType()) {
                        case "HOT_DESK" ->
                            counts[0]++;
                        case "MEETING_ROOM" ->
                            counts[1]++;
                        case "PRIVATE_ROOM" ->
                            counts[2]++;
                    }
                    counts[3]++;
                    counts[4] += (int) b.getTotalCost();
                }

                for (var entry : map.entrySet()) {
                    int[] c = entry.getValue();
                    model.addRow(new Object[]{
                        entry.getKey(),
                        c[0], c[1], c[2],
                        c[3],
                        String.format("%.2f", (double) c[4])
                    });
                }

                if (map.isEmpty())
                    JOptionPane.showMessageDialog(
                        panel,
                        "No booking data found.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                    "Error: " + ex.getMessage());
            }
        });

        panel.add(btnBar(refreshBtn),
            BorderLayout.SOUTH);
        panel.add(new JScrollPane(table),
            BorderLayout.CENTER);
        refreshBtn.doClick();
        return panel;
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
        UITheme.styleTable(table);
    }

    private JPanel btnBar(JButton... buttons) {
        return UITheme.buttonBar(buttons);
    }
}