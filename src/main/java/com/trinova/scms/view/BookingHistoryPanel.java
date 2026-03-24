package com.trinova.scms.view;

import com.trinova.scms.model.Booking;
import com.trinova.scms.model.Member;
import com.trinova.scms.service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookingHistoryPanel extends JPanel {

    private final Member member;
    private BookingService bookingService;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public BookingHistoryPanel(Member member) {
        this.member = member;
        try {
            this.bookingService = new BookingService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
        loadBookings();
    }

    private void initComponents() {
        // Title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadBookings());
        topPanel.add(refreshBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Space", "Date", "Start", "End", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookingTable = new JTable(tableModel);
        bookingTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bookingTable.setRowHeight(28);
        bookingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.getColumnModel().getColumn(0).setMaxWidth(40);
        bookingTable.getColumnModel().getColumn(5).setMaxWidth(100);

        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel - cancel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton cancelBtn = new JButton("Cancel Selected Booking");
        cancelBtn.setBackground(new Color(180, 30, 30));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        cancelBtn.addActionListener(e -> cancelSelected());

        bottomPanel.add(cancelBtn);
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        try {
            List<Booking> bookings =
                bookingService.getMemberBookings(member.getMemberId());
            for (Booking b : bookings) {
                tableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getRoomName(),
                    b.getStartTime().toLocalDate(),
                    b.getStartTime().toLocalTime(),
                    b.getEndTime().toLocalTime(),
                    b.getStatus()
                });
            }
            if (bookings.isEmpty()) {
                statusLabel.setForeground(Color.GRAY);
                statusLabel.setText("No bookings found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading bookings: " + e.getMessage());
        }
    }

    private void cancelSelected() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please select a booking to cancel.");
            return;
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 5);

        if (!status.equals("ACTIVE")) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Only ACTIVE bookings can be cancelled.");
            return;
        }

        String reason = JOptionPane.showInputDialog(this,
            "Please enter reason for cancellation:",
            "Cancel Booking", JOptionPane.QUESTION_MESSAGE);

        if (reason == null || reason.trim().isEmpty()) return;

        try {
            bookingService.cancel(bookingId, member.getEmail(), reason);
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Booking #" + bookingId + " cancelled successfully.");
            loadBookings();
        } catch (Exception e) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText(e.getMessage());
        }
    }
}