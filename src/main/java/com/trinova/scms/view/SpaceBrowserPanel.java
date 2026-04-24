package com.trinova.scms.view;

import com.trinova.scms.model.Member;
import com.trinova.scms.model.Room;
import com.trinova.scms.service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class SpaceBrowserPanel extends JPanel {

    private final Member member;
    private BookingService bookingService;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JLabel statusLabel;

    // Booking form fields
    private JTextField dateField;
    private JTextField startField;
    private JTextField endField;
    private JComboBox<String> bookingTypeCombo;
    private JPanel hourlyFields;
    private JLabel bookingTypeNote;

    public SpaceBrowserPanel(Member member) {
        this.member = member;
        try {
            this.bookingService = new BookingService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error connecting: " + e.getMessage());
        }
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
        loadRooms("ALL");
    }

    private void initComponents() {
        // ── Top bar ────────────────────────────────────────
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 15, 25));

        JLabel titleLabel = new JLabel("Browse Spaces");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(com.trinova.scms.util.UIConfig.NAVY_DARK);
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Plan info banner
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        banner.setBackground(new Color(230, 250, 240));
        banner.setBorder(BorderFactory.createLineBorder(new Color(180, 230, 200), 1));
        banner.putClientProperty("FlatLaf.style", "arc: 20");
        JLabel planLabel = new JLabel("✔ PREMIUM Plan — expires 2026-05-20");
        planLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        planLabel.setForeground(new Color(0, 100, 50));
        banner.add(planLabel);
        topPanel.add(banner, BorderLayout.CENTER);

        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Filter:"));
        filterCombo = new JComboBox<>(new String[]{"ALL", "HOT_DESK", "MEETING_ROOM", "PRIVATE_ROOM"});
        filterCombo.setPreferredSize(new Dimension(100, 32));
        filterPanel.add(filterCombo);

        JButton refreshBtn = new JButton("Refresh ⟳");
        refreshBtn.setPreferredSize(new Dimension(90, 32));
        refreshBtn.putClientProperty("FlatLaf.style", "arc: 8");
        filterPanel.add(refreshBtn);
        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ── Table ──────────────────────────────────────────
        String[] columns = {"ID", "Space Name", "Type", "Capacity", "Amenities", "Hourly PKR", "Daily PKR", "Monthly PKR"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(40);
        roomTable.setShowHorizontalLines(true);
        roomTable.setGridColor(new Color(245, 245, 245));
        roomTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // Mock data for design parity
        tableModel.addRow(new Object[]{"1", "Alpha Hub", "HOT_DESK", "10", "Wi-Fi, Power Outlets", "500", "—", "—"});
        tableModel.addRow(new Object[]{"2", "Executive Suite", "PRIVATE_ROOM", "4", "AC, Locker, Whiteboard", "—", "3,000", "25,000"});
        tableModel.addRow(new Object[]{"3", "Conference Hall", "MEETING_ROOM", "12", "Projector, AC, Wi-Fi", "800", "—", "—"});
        tableModel.addRow(new Object[]{"4", "Flex Desk Area", "HOT_DESK", "20", "Wi-Fi, Standing Desks", "300", "—", "—"});

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        add(scrollPane, BorderLayout.CENTER);

        // ── Bottom booking panel ───────────────────────────
        add(buildBookingPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildBookingPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 25, 25, 25)
        ));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel bTitle = new JLabel("Book Selected Space");
        bTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        bTitle.setForeground(com.trinova.scms.util.UIConfig.NAVY_DARK);
        header.add(bTitle, BorderLayout.NORTH);
        
        JLabel bSub = new JLabel("Selected: Alpha Hub (HOT_DESK)");
        bSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        bSub.setForeground(Color.GRAY);
        header.add(bSub, BorderLayout.SOUTH);
        bottomPanel.add(header, BorderLayout.NORTH);

        // Form Row 1: Type and Note
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 15);

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Booking Type:"), gbc);
        
        gbc.gridx = 1;
        bookingTypeCombo = new JComboBox<>(new String[]{"HOURLY", "DAILY", "MONTHLY"});
        bookingTypeCombo.setPreferredSize(new Dimension(120, 32));
        form.add(bookingTypeCombo, gbc);

        gbc.gridx = 2;
        JLabel note = new JLabel("Select date and time below");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        note.setForeground(Color.GRAY);
        form.add(note, gbc);

        // Row 2: Fields
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 8;
        gbc.insets = new Insets(10, 0, 10, 0);
        JPanel fieldsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        fieldsRow.setOpaque(false);
        
        fieldsRow.add(createFieldGroup("Date", "YYYY-MM-DD", 100));
        fieldsRow.add(createFieldGroup("Start", "HH:MM", 60));
        fieldsRow.add(createFieldGroup("End", "HH:MM", 60));
        fieldsRow.add(createFieldGroup("Promo", "Code (opt.)", 80));
        
        form.add(fieldsRow, gbc);

        // Footer Row: Status and Button
        gbc.gridy = 2; gbc.gridwidth = 4;
        statusLabel = new JLabel("Fill in the details above to book.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
        form.add(statusLabel, gbc);

        gbc.gridx = 4; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.EAST;
        JButton bookBtn = new JButton("Book Selected Space →");
        bookBtn.setBackground(com.trinova.scms.util.UIConfig.NAVY_DARK);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookBtn.setPreferredSize(new Dimension(200, 38));
        bookBtn.putClientProperty("FlatLaf.style", "arc: 8");
        form.add(bookBtn, gbc);

        bottomPanel.add(form, BorderLayout.CENTER);
        return bottomPanel;
    }

    private JPanel createFieldGroup(String label, String placeholder, int width) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        p.add(new JLabel(label));
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(width, 32));
        txt.putClientProperty("FlatLaf.placeholderText", placeholder);
        txt.putClientProperty("FlatLaf.style", "arc: 8");
        p.add(txt);
        return p;
    }

    private void updateBookingForm(String filter) {
        bookingTypeCombo.removeAllItems();

        if ("PRIVATE_ROOM".equals(filter)) {
            bookingTypeCombo.addItem("MONTHLY");
            bookingTypeCombo.addItem("DAILY");
            bookingTypeNote.setText(
                "For monthly: set start date only. " +
                "End date auto-set to 30 days later.");
        } else {
            bookingTypeCombo.addItem("HOURLY");
            bookingTypeNote.setText(
                "Select date and time below");
        }
        updateTimeFields();
    }

    private void updateTimeFields() {
        String type =
            (String) bookingTypeCombo.getSelectedItem();
        if (type == null) return;

        hourlyFields.removeAll();

        if ("MONTHLY".equals(type)) {
            hourlyFields.add(
                new JLabel("Start Date (YYYY-MM-DD):"));
            dateField = new JTextField(
                LocalDate.now().toString(), 12);
            hourlyFields.add(dateField);
            hourlyFields.add(new JLabel(
                "  End date auto-set to +30 days"));

        } else if ("DAILY".equals(type)) {
            hourlyFields.add(
                new JLabel("Date (YYYY-MM-DD):"));
            dateField = new JTextField(
                LocalDate.now().toString(), 12);
            hourlyFields.add(dateField);
            hourlyFields.add(new JLabel(
                "  Full day booking"));

        } else {
            // HOURLY
            hourlyFields.add(
                new JLabel("Date (YYYY-MM-DD):"));
            dateField = new JTextField(
                LocalDate.now().toString(), 12);
            hourlyFields.add(dateField);

            hourlyFields.add(
                new JLabel("  Start (HH:MM):"));
            startField = new JTextField("09:00", 7);
            hourlyFields.add(startField);

            hourlyFields.add(
                new JLabel("  End (HH:MM):"));
            endField = new JTextField("17:00", 7);
            hourlyFields.add(endField);
        }

        hourlyFields.revalidate();
        hourlyFields.repaint();
    }

    private void handleBooking() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText(
                "Please select a space from the table.");
            return;
        }

        int roomId =
            (int) tableModel.getValueAt(selectedRow, 0);
        String roomName =
            (String) tableModel.getValueAt(selectedRow, 1);
        String roomType =
            (String) tableModel.getValueAt(selectedRow, 2);
        String bookingType =
            (String) bookingTypeCombo.getSelectedItem();

        try {
            LocalDate date =
                LocalDate.parse(dateField.getText().trim());
            LocalDateTime startDT;
            LocalDateTime endDT;

            if ("MONTHLY".equals(bookingType)) {
                startDT = date.atTime(8, 0);
                endDT   = date.plusDays(30).atTime(20, 0);

            } else if ("DAILY".equals(bookingType)) {
                startDT = date.atTime(8, 0);
                endDT   = date.atTime(20, 0);

            } else {
                // HOURLY
                LocalTime start =
                    LocalTime.parse(startField.getText().trim());
                LocalTime end =
                    LocalTime.parse(endField.getText().trim());
                startDT = LocalDateTime.of(date, start);
                endDT   = LocalDateTime.of(date, end);

                if (!startDT.isBefore(endDT)) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(
                        "Start time must be before end time.");
                    return;
                }

                // Same day check for hourly bookings
                if (!startDT.toLocalDate().equals(
                        endDT.toLocalDate())) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(
                        "Hourly bookings must start " +
                        "and end on the same day.");
                    return;
                }
            }

            // Validate private room type
            if ("PRIVATE_ROOM".equals(roomType) &&
                "HOURLY".equals(bookingType)) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText(
                    "Private rooms cannot be booked hourly. " +
                    "Use DAILY or MONTHLY.");
                return;
            }

            // Open cost preview
            new CostPreviewFrame(
                member, roomId, roomName,
                roomType, bookingType,
                startDT, endDT,
                (JFrame) SwingUtilities
                    .getWindowAncestor(this)
            ).setVisible(true);

            statusLabel.setForeground(Color.GRAY);
            statusLabel.setText(
                "Cost preview opened for " + roomName);

        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText(ex.getMessage());
        }
    }

    private void loadRooms(String filter) {
        tableModel.setRowCount(0);
        try {
            List<Room> rooms = filter.equals("ALL")
                ? bookingService.getAllRooms()
                : bookingService.getRoomsByType(filter);

            for (Room r : rooms) {
                String rate;
                if (r.isPrivateRoom()) {
                    rate = "Monthly: PKR " +
                        String.format("%.0f",
                            r.getMonthlyPrice()) +
                        "  |  Daily: PKR " +
                        String.format("%.0f",
                            r.getDailyPrice());
                } else {
                    rate = "PKR " +
                        String.format("%.0f",
                            r.getHourlyPrice()) +
                        "/hr";
                }
                tableModel.addRow(new Object[]{
                    r.getRoomId(),
                    r.getRoomName(),
                    r.getRoomType(),
                    r.getCapacity(),
                    r.getAmenities(),
                    rate
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading rooms: " + e.getMessage());
        }
    }
}