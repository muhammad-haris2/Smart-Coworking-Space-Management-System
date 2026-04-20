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
        setBackground(Color.WHITE);
        initComponents();
        loadInvoices();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel titleLabel = new JLabel("My Invoices");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
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
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        invoiceTable.setRowHeight(28);
        invoiceTable.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 13));
        invoiceTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(
            BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, Color.LIGHT_GRAY));

        statusLabel = new JLabel(
            "Invoices are auto-generated for every booking.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);
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
                statusLabel.setText(
                    "No invoices yet. Make a booking first.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading invoices: " + e.getMessage());
        }
    }
}