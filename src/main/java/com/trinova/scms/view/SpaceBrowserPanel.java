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
        // Top bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel titleLabel = new JLabel("Browse Spaces");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Filter: "));
        filterCombo = new JComboBox<>(new String[]{
            "ALL", "HOT_DESK", "MEETING_ROOM", "PRIVATE_ROOM"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.addActionListener(e ->
            loadRooms((String) filterCombo.getSelectedItem()));
        filterPanel.add(filterCombo);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e ->
            loadRooms((String) filterCombo.getSelectedItem()));
        filterPanel.add(refreshBtn);
        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "ID", "Space Name", "Type",
            "Capacity", "Amenities", "Rate"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roomTable.setRowHeight(28);
        roomTable.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 13));
        roomTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);
        roomTable.getColumnModel().getColumn(0).setMaxWidth(40);
        roomTable.getColumnModel().getColumn(2).setMaxWidth(120);
        roomTable.getColumnModel().getColumn(3).setMaxWidth(80);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(
            BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom booking panel
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(
                1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date
        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        JTextField dateField = new JTextField(
            LocalDate.now().toString(), 12);
        gbc.gridx = 1;
        bottomPanel.add(dateField, gbc);

        // Start time
        gbc.gridx = 2;
        bottomPanel.add(new JLabel("Start (HH:MM):"), gbc);
        JTextField startField = new JTextField("09:00", 8);
        gbc.gridx = 3;
        bottomPanel.add(startField, gbc);

        // End time
        gbc.gridx = 4;
        bottomPanel.add(new JLabel("End (HH:MM):"), gbc);
        JTextField endField = new JTextField("17:00", 8);
        gbc.gridx = 5;
        bottomPanel.add(endField, gbc);

        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        bottomPanel.add(statusLabel, gbc);

        // Book button
        JButton bookBtn = new JButton("Book Selected Space");
        bookBtn.setBackground(new Color(16, 64, 110));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookBtn.setFocusPainted(false);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 4; gbc.gridy = 1; gbc.gridwidth = 2;
        bottomPanel.add(bookBtn, gbc);

        bookBtn.addActionListener(e -> {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow == -1) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText(
                    "Please select a space from the table.");
                return;
            }

            int roomId = (int) tableModel.getValueAt(selectedRow, 0);
            String roomName =
                (String) tableModel.getValueAt(selectedRow, 1);
            String roomType =
                (String) tableModel.getValueAt(selectedRow, 2);

            try {
                LocalDate date  =
                    LocalDate.parse(dateField.getText().trim());
                LocalTime start =
                    LocalTime.parse(startField.getText().trim());
                LocalTime end   =
                    LocalTime.parse(endField.getText().trim());
                LocalDateTime startDT = LocalDateTime.of(date, start);
                LocalDateTime endDT   = LocalDateTime.of(date, end);

                if (!startDT.isBefore(endDT)) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText(
                        "Start time must be before end time.");
                    return;
                }

                // Open cost preview
                new CostPreviewFrame(
                    member, roomId, roomName, roomType,
                    "HOURLY", startDT, endDT,
                    (JFrame) SwingUtilities.getWindowAncestor(this)
                ).setVisible(true);

            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText(ex.getMessage());
            }
        });

        add(bottomPanel, BorderLayout.SOUTH);
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
                           " | Daily: PKR " +
                           String.format("%.0f", r.getDailyPrice());
                } else {
                    rate = "PKR " +
                           String.format("%.0f", r.getHourlyPrice()) +
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