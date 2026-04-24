package com.trinova.scms.view;

import com.trinova.scms.model.Booking;
import com.trinova.scms.model.Member;
import com.trinova.scms.service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
        }
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_CONTENT);
        initComponents();
        loadBookings();
    }

    private void initComponents() {
        // Title bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BG_CONTENT);
        topPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        JLabel titleLabel = UITheme.sectionTitle("My Bookings");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = UITheme.secondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadBookings());
        topPanel.add(refreshBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "ID", "Space", "Type", "Date",
            "Start", "End", "Total (PKR)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        bookingTable = new JTable(tableModel);
        UITheme.styleTable(bookingTable);

        // Color code rows by status — override the default renderer
        bookingTable.setDefaultRenderer(Object.class,
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable table, Object value,
                        boolean isSelected, boolean hasFocus,
                        int row, int column) {
                    Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                    if (!isSelected) {
                        String status = (String) tableModel.getValueAt(row, 7);
                        switch (status) {
                            case "CONFIRMED" ->
                                c.setBackground(UITheme.SUCCESS_BG);
                            case "CANCELLED" ->
                                c.setBackground(UITheme.DANGER_BG);
                            case "COMPLETED" ->
                                c.setBackground(new Color(224, 237, 255));
                            default ->
                                c.setBackground(row % 2 == 0
                                    ? UITheme.BG_CARD : UITheme.TABLE_ROW_ALT);
                        }
                    }
                    setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                    return c;
                }
            });

        bookingTable.getColumnModel().getColumn(0).setMaxWidth(50);
        bookingTable.getColumnModel().getColumn(2).setMaxWidth(100);
        bookingTable.getColumnModel().getColumn(7).setMaxWidth(110);

        JScrollPane scrollPane = UITheme.tableScrollPane(bookingTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 0, 20),
            scrollPane.getBorder()));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomPanel.setBackground(UITheme.BG_CONTENT);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));

        JButton cancelBtn = UITheme.dangerButton("Cancel Selected Booking");
        cancelBtn.setPreferredSize(new Dimension(220, 38));
        cancelBtn.addActionListener(e -> cancelSelected());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);

        // Legend chips
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        legend.setOpaque(false);
        legend.add(legendChip("Confirmed", UITheme.SUCCESS));
        legend.add(legendChip("Cancelled", UITheme.DANGER));
        legend.add(legendChip("Completed", new Color(59, 130, 246)));

        bottomPanel.add(cancelBtn);
        bottomPanel.add(statusLabel);
        bottomPanel.add(legend);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel legendChip(String text, Color color) {
        JLabel chip = new JLabel("  " + text + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(),
                                      color.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(UITheme.FONT_TINY);
        chip.setForeground(color);
        chip.setOpaque(false);
        chip.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return chip;
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
                    b.getBookingType(),
                    b.getStartTime().toLocalDate(),
                    b.getStartTime().toLocalTime(),
                    b.getEndTime().toLocalTime(),
                    String.format("%.2f", b.getTotalCost()),
                    b.getStatus()
                });
            }
            if (bookings.isEmpty()) {
                statusLabel.setForeground(UITheme.TEXT_MUTED);
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
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Please select a booking to cancel.");
            return;
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 7);

        if (!status.equals("CONFIRMED")) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Only CONFIRMED bookings can be cancelled.");
            return;
        }

        // Check cancellation policy — must cancel 1 hr before
        Object dateObj = tableModel.getValueAt(selectedRow, 3);
        Object timeObj = tableModel.getValueAt(selectedRow, 4);
        try {
            LocalDateTime bookingStart = LocalDateTime.parse(
                dateObj + "T" + timeObj);
            long minutesUntilBooking = ChronoUnit.MINUTES.between(
                LocalDateTime.now(), bookingStart);

            if (minutesUntilBooking < 60 && minutesUntilBooking >= 0) {
                int proceed = JOptionPane.showConfirmDialog(this,
                    "Warning: Cancelling within 1 hour " +
                    "of booking — NO REFUND will be issued.\n" +
                    "Do you still want to cancel?",
                    "Late Cancellation — No Refund",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (proceed != JOptionPane.YES_OPTION) return;
            }
        } catch (Exception ignored) {}

        String reason = JOptionPane.showInputDialog(this,
            "Please enter reason for cancellation:",
            "Cancel Booking", JOptionPane.QUESTION_MESSAGE);

        if (reason == null || reason.trim().isEmpty()) return;

        try {
            bookingService.cancel(bookingId, member.getEmail(), reason);
            statusLabel.setForeground(UITheme.SUCCESS);
            statusLabel.setText(
                "Booking #" + bookingId + " cancelled successfully.");
            loadBookings();
        } catch (Exception e) {
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText(e.getMessage());
        }
    }
}