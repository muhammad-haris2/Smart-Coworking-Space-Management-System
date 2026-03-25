package com.trinova.scms.view;

import com.trinova.scms.model.Booking;
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
            JOptionPane.showMessageDialog(this, "Error connecting: " + e.getMessage());
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
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel titleLabel = new JLabel("Browse Spaces");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Filter: "));
        filterCombo = new JComboBox<>(new String[]{"ALL", "HOT_DESK", "MEETING_ROOM"});
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.addActionListener(e ->
            loadRooms((String) filterCombo.getSelectedItem()));
        filterPanel.add(filterCombo);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshBtn.addActionListener(e ->
            loadRooms((String) filterCombo.getSelectedItem()));
        filterPanel.add(refreshBtn);
        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Space Name", "Type", "Capacity", "Amenities"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roomTable.setRowHeight(28);
        roomTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getColumnModel().getColumn(0).setMaxWidth(40);
        roomTable.getColumnModel().getColumn(2).setMaxWidth(120);
        roomTable.getColumnModel().getColumn(3).setMaxWidth(80);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom booking panel
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
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
        bottomPanel.add(new JLabel("Start Time (HH:MM):"), gbc);
        JTextField startField = new JTextField("09:00", 8);
        gbc.gridx = 3;
        bottomPanel.add(startField, gbc);

        // End time
        gbc.gridx = 4;
        bottomPanel.add(new JLabel("End Time (HH:MM):"), gbc);
        JTextField endField = new JTextField("17:00", 8);
        gbc.gridx = 5;
        bottomPanel.add(endField, gbc);

        // Book button
        JButton bookBtn = new JButton("Book Selected Space");
        bookBtn.setBackground(new Color(16, 64, 110));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookBtn.setFocusPainted(false);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        bottomPanel.add(bookBtn, gbc);

        // Status
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 3; gbc.gridwidth = 3;
        bottomPanel.add(statusLabel, gbc);

        bookBtn.addActionListener(e -> {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow == -1) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Please select a space from the table first.");
                return;
            }
            int roomId = (int) tableModel.getValueAt(selectedRow, 0);
            String roomName = (String) tableModel.getValueAt(selectedRow, 1);

            try {
                LocalDate date   = LocalDate.parse(dateField.getText().trim());
                LocalTime start  = LocalTime.parse(startField.getText().trim());
                LocalTime end    = LocalTime.parse(endField.getText().trim());
                LocalDateTime startDT = LocalDateTime.of(date, start);
                LocalDateTime endDT   = LocalDateTime.of(date, end);

                int bookingId = bookingService.book(
                    member.getMemberId(), member.getEmail(),
                    roomId, startDT, endDT);

                statusLabel.setForeground(new Color(0, 128, 0));
                statusLabel.setText("Booking confirmed! ID: " + bookingId +
                                    " for " + roomName);

                JOptionPane.showMessageDialog(this,
                    "Booking Confirmed!\n" +
                    "Space: " + roomName + "\n" +
                    "Date: " + date + "\n" +
                    "Time: " + start + " to " + end + "\n" +
                    "Booking ID: " + bookingId,
                    "Booking Success", JOptionPane.INFORMATION_MESSAGE);

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
                tableModel.addRow(new Object[]{
                    r.getRoomId(),
                    r.getRoomName(),
                    r.getRoomType(),
                    r.getCapacity(),
                    r.getAmenities()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading rooms: " + e.getMessage());
        }
    }
}