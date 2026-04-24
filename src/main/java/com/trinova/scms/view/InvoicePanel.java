package com.trinova.scms.view;

import com.trinova.scms.model.Invoice;
import com.trinova.scms.model.Member;
import com.trinova.scms.service.BillingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InvoicePanel extends JPanel {

    private final Member member;
    private BillingService billingService;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public InvoicePanel(Member member) {
        this.member = member;
        try {
            this.billingService = new BillingService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
        }
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_CONTENT);
        initComponents();
        loadInvoices();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BG_CONTENT);
        topPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        JLabel titleLabel = UITheme.sectionTitle("My Invoices");
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = UITheme.secondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadInvoices());
        topPanel.add(refreshBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {
            "Invoice ID", "Booking ID", "Space", "Type",
            "Base (PKR)", "Facilities (PKR)",
            "VAT (PKR)", "Total (PKR)", "Date"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        invoiceTable = new JTable(tableModel);
        UITheme.styleTable(invoiceTable);

        JScrollPane scrollPane = UITheme.tableScrollPane(invoiceTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 20, 0, 20),
            scrollPane.getBorder()));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomPanel.setBackground(UITheme.BG_CONTENT);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));

        statusLabel = new JLabel(
            "Invoices are auto-generated for every booking.");
        statusLabel.setFont(UITheme.FONT_TINY);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadInvoices() {
        tableModel.setRowCount(0);
        try {
            List<Invoice> invoices =
                billingService.getMemberInvoices(member.getMemberId());
            for (Invoice inv : invoices) {
                tableModel.addRow(new Object[]{
                    inv.getInvoiceId(),
                    inv.getBookingId(),
                    inv.getRoomName(),
                    inv.getBookingType(),
                    String.format("%.2f", inv.getBaseAmount()),
                    String.format("%.2f", inv.getFacilityCost()),
                    String.format("%.2f", inv.getVatAmount()),
                    String.format("%.2f", inv.getTotalAmount()),
                    inv.getIssueDate()
                });
            }
            if (invoices.isEmpty()) {
                statusLabel.setText("No invoices yet. Make a booking first.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading invoices: " + e.getMessage());
        }
    }
}