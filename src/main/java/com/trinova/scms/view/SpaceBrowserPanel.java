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
        setBackground(UITheme.BG_CONTENT);
        initComponents();
        loadRooms("ALL");
    }

    private void initComponents() {
        // ── Top bar ──────────────────────────────────────────
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BG_CONTENT);
        topPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        JLabel titleLabel = UITheme.sectionTitle("Browse Spaces");
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Plan info banner
        String planInfo = member.hasActivePlan()
            ? "Your Plan: " + member.getPlanType() +
              " — expires " + member.getPlanExpiry()
            : "No Plan — full rates apply";
        JLabel planLabel = new JLabel(planInfo);
        planLabel.setFont(UITheme.FONT_SMALL);
        planLabel.setForeground(
            member.hasActivePlan() ? UITheme.SUCCESS : UITheme.WARNING);
        topPanel.add(planLabel, BorderLayout.CENTER);

        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);
        JLabel filterLbl = new JLabel("Filter:");
        filterLbl.setFont(UITheme.FONT_SMALL);
        filterLbl.setForeground(UITheme.TEXT_SECONDARY);
        filterPanel.add(filterLbl);
        filterCombo = new JComboBox<>(new String[]{
            "ALL", "HOT_DESK", "MEETING_ROOM", "PRIVATE_ROOM"});
        filterCombo.setFont(UITheme.FONT_SMALL);
        filterCombo.addActionListener(e -> {
            loadRooms((String) filterCombo.getSelectedItem());
            updateBookingForm((String) filterCombo.getSelectedItem());
        });
        filterPanel.add(filterCombo);

        JButton refreshBtn = UITheme.secondaryButton("Refresh");
        refreshBtn.addActionListener(e ->
            loadRooms((String) filterCombo.getSelectedItem()));
        filterPanel.add(refreshBtn);
        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────
        String[] columns = {
            "ID", "Space Name", "Type",
            "Capacity", "Amenities", "Rate"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        UITheme.styleTable(roomTable);
        roomTable.getColumnModel().getColumn(0).setMaxWidth(50);
        roomTable.getColumnModel().getColumn(2).setMaxWidth(130);
        roomTable.getColumnModel().getColumn(3).setMaxWidth(90);

        JScrollPane scrollPane = UITheme.tableScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 0, 20),
            scrollPane.getBorder()));
        add(scrollPane, BorderLayout.CENTER);

        // ── Bottom booking panel ─────────────────────────────
        add(buildBookingPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildBookingPanel() {
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(UITheme.BG_CARD);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: booking type selector
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel btLbl = new JLabel("Booking Type:");
        btLbl.setFont(UITheme.FONT_HEADING);
        btLbl.setForeground(UITheme.TEXT_PRIMARY);
        bottomPanel.add(btLbl, gbc);

        bookingTypeCombo = new JComboBox<>(new String[]{"HOURLY"});
        bookingTypeCombo.setFont(UITheme.FONT_SMALL);
        gbc.gridx = 1;
        bottomPanel.add(bookingTypeCombo, gbc);

        bookingTypeNote = new JLabel("Select date and time below");
        bookingTypeNote.setFont(UITheme.FONT_TINY);
        bookingTypeNote.setForeground(UITheme.TEXT_MUTED);
        gbc.gridx = 2; gbc.gridwidth = 3;
        bottomPanel.add(bookingTypeNote, gbc);
        gbc.gridwidth = 1;

        // Row 1: hourly fields panel
        hourlyFields = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        hourlyFields.setOpaque(false);

        hourlyFields.add(smallLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().toString(), 12);
        dateField.setFont(UITheme.FONT_SMALL);
        hourlyFields.add(dateField);

        hourlyFields.add(smallLabel("  Start (HH:MM):"));
        startField = new JTextField("09:00", 7);
        startField.setFont(UITheme.FONT_SMALL);
        hourlyFields.add(startField);

        hourlyFields.add(smallLabel("  End (HH:MM):"));
        endField = new JTextField("17:00", 7);
        endField.setFont(UITheme.FONT_SMALL);
        hourlyFields.add(endField);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 6;
        bottomPanel.add(hourlyFields, gbc);
        gbc.gridwidth = 1;

        // Row 2: status + book button
        statusLabel = new JLabel(" ", SwingConstants.LEFT);
        statusLabel.setFont(UITheme.FONT_SMALL);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        bottomPanel.add(statusLabel, gbc);

        JButton bookBtn = UITheme.primaryButton("Book Selected Space →");
        bookBtn.setPreferredSize(new Dimension(220, 40));
        gbc.gridx = 4; gbc.gridy = 2; gbc.gridwidth = 2;
        bottomPanel.add(bookBtn, gbc);

        // Booking type changes form
        bookingTypeCombo.addActionListener(e -> updateTimeFields());
        bookBtn.addActionListener(e -> handleBooking());

        return bottomPanel;
    }

    private JLabel smallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_SECONDARY);
        return l;
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
            bookingTypeNote.setText("Select date and time below");
        }
        updateTimeFields();
    }

    private void updateTimeFields() {
        String type = (String) bookingTypeCombo.getSelectedItem();
        if (type == null) return;

        hourlyFields.removeAll();

        if ("MONTHLY".equals(type)) {
            hourlyFields.add(smallLabel("Start Date (YYYY-MM-DD):"));
            dateField = new JTextField(LocalDate.now().toString(), 12);
            dateField.setFont(UITheme.FONT_SMALL);
            hourlyFields.add(dateField);
            hourlyFields.add(smallLabel("  End date auto-set to +30 days"));

        } else if ("DAILY".equals(type)) {
            hourlyFields.add(smallLabel("Date (YYYY-MM-DD):"));
            dateField = new JTextField(LocalDate.now().toString(), 12);
            dateField.setFont(UITheme.FONT_SMALL);
            hourlyFields.add(dateField);
            hourlyFields.add(smallLabel("  Full day booking"));

        } else {
            // HOURLY
            hourlyFields.add(smallLabel("Date (YYYY-MM-DD):"));
            dateField = new JTextField(LocalDate.now().toString(), 12);
            dateField.setFont(UITheme.FONT_SMALL);
            hourlyFields.add(dateField);

            hourlyFields.add(smallLabel("  Start (HH:MM):"));
            startField = new JTextField("09:00", 7);
            startField.setFont(UITheme.FONT_SMALL);
            hourlyFields.add(startField);

            hourlyFields.add(smallLabel("  End (HH:MM):"));
            endField = new JTextField("17:00", 7);
            endField.setFont(UITheme.FONT_SMALL);
            hourlyFields.add(endField);
        }

        hourlyFields.revalidate();
        hourlyFields.repaint();
    }

    private void handleBooking() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Please select a space from the table.");
            return;
        }

        int roomId = (int) tableModel.getValueAt(selectedRow, 0);
        String roomName = (String) tableModel.getValueAt(selectedRow, 1);
        String roomType = (String) tableModel.getValueAt(selectedRow, 2);
        String bookingType = (String) bookingTypeCombo.getSelectedItem();

        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
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
                LocalTime start = LocalTime.parse(startField.getText().trim());
                LocalTime end   = LocalTime.parse(endField.getText().trim());
                startDT = LocalDateTime.of(date, start);
                endDT   = LocalDateTime.of(date, end);

                if (!startDT.isBefore(endDT)) {
                    statusLabel.setForeground(UITheme.DANGER);
                    statusLabel.setText("Start time must be before end time.");
                    return;
                }

                // Same day check for hourly bookings
                if (!startDT.toLocalDate().equals(endDT.toLocalDate())) {
                    statusLabel.setForeground(UITheme.DANGER);
                    statusLabel.setText(
                        "Hourly bookings must start and end on the same day.");
                    return;
                }
            }

            // Validate private room type
            if ("PRIVATE_ROOM".equals(roomType) &&
                "HOURLY".equals(bookingType)) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText(
                    "Private rooms cannot be booked hourly. Use DAILY or MONTHLY.");
                return;
            }

            // Open cost preview
            new CostPreviewFrame(
                member, roomId, roomName,
                roomType, bookingType,
                startDT, endDT,
                (JFrame) SwingUtilities.getWindowAncestor(this)
            ).setVisible(true);

            statusLabel.setForeground(UITheme.TEXT_MUTED);
            statusLabel.setText("Cost preview opened for " + roomName);

        } catch (Exception ex) {
            statusLabel.setForeground(UITheme.DANGER);
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
                        String.format("%.0f", r.getMonthlyPrice()) +
                        "  |  Daily: PKR " +
                        String.format("%.0f", r.getDailyPrice());
                } else {
                    rate = "PKR " +
                        String.format("%.0f", r.getHourlyPrice()) + "/hr";
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