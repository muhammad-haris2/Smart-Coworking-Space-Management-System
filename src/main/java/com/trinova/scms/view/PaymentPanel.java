package com.trinova.scms.view;

import com.trinova.scms.model.Member;
import com.trinova.scms.service.BillingService;

import javax.swing.*;
import java.awt.*;

public class PaymentPanel extends JPanel {

    private final Member member;
    private BillingService billingService;
    private JTextField cardNumberField;
    private JTextField expiryField;
    private JTextField cvvField;
    private JTextField promoField;
    private JLabel totalLabel;
    private JLabel statusLabel;
    private double baseAmount = 0.0;

    public PaymentPanel(Member member) {
        this.member = member;
        try {
            this.billingService = new BillingService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
    }

    public PaymentPanel(Member member, double amount) {
        this(member);
        this.baseAmount = amount;
        totalLabel.setText("Total: PKR " + String.format("%.2f", amount));
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel titleLabel = new JLabel("Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        topPanel.add(titleLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 60, 10, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Payment method
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel methodLabel = new JLabel("Select Payment Method:");
        methodLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(methodLabel, gbc);

        gbc.gridy = 1;
        JComboBox<String> methodCombo = new JComboBox<>(
            new String[]{"Visa", "Mastercard", "Digital Wallet"});
        methodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(methodCombo, gbc);

        // Card number
        gbc.gridwidth = 1; gbc.gridy = 2; gbc.gridx = 0;
        formPanel.add(new JLabel("Card Number:"), gbc);
        cardNumberField = new JTextField("1234 5678 9012 3456", 20);
        cardNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        formPanel.add(cardNumberField, gbc);

        // Expiry
        gbc.gridy = 3; gbc.gridx = 0;
        formPanel.add(new JLabel("Expiry (MM/YY):"), gbc);
        expiryField = new JTextField("12/27", 10);
        expiryField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        formPanel.add(expiryField, gbc);

        // CVV
        gbc.gridy = 4; gbc.gridx = 0;
        formPanel.add(new JLabel("CVV:"), gbc);
        cvvField = new JPasswordField("123", 5);
        cvvField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        formPanel.add(cvvField, gbc);

        // Promo code
        gbc.gridy = 5; gbc.gridx = 0;
        formPanel.add(new JLabel("Promo Code (optional):"), gbc);

        JPanel promoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        promoPanel.setBackground(Color.WHITE);
        promoField = new JTextField(12);
        promoField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton applyBtn = new JButton("Apply");
        applyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        applyBtn.addActionListener(e -> applyPromo());
        promoPanel.add(promoField);
        promoPanel.add(applyBtn);
        gbc.gridx = 1;
        formPanel.add(promoPanel, gbc);

        // Total
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        totalLabel = new JLabel("Total: PKR 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(new Color(16, 64, 110));
        formPanel.add(totalLabel, gbc);

        // Status
        gbc.gridy = 7;
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(statusLabel, gbc);

        // Pay button
        gbc.gridy = 8;
        JButton payBtn = new JButton("Pay Now (Simulated)");
        payBtn.setBackground(new Color(0, 128, 0));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payBtn.setFocusPainted(false);
        payBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payBtn.setPreferredSize(new Dimension(0, 40));
        payBtn.addActionListener(e -> processPayment(
            (String) methodCombo.getSelectedItem()));
        formPanel.add(payBtn, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void applyPromo() {
        String code = promoField.getText().trim();
        if (code.isEmpty()) return;
        try {
            double discounted =
                billingService.applyPromoCode(baseAmount, code);
            baseAmount = discounted;
            totalLabel.setText("Total: PKR " +
                String.format("%.2f", discounted));
            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText("Promo code applied successfully!");
        } catch (Exception e) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText(e.getMessage());
        }
    }

    private void processPayment(String method) {
        String cardNum = cardNumberField.getText().trim();
        String expiry  = expiryField.getText().trim();
        String cvv     = cvvField.getText().trim();

        if (cardNum.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please fill in all payment details.");
            return;
        }

        // Simulated payment processing
        JOptionPane.showMessageDialog(this,
            "Payment Successful!\n" +
            "Method: " + method + "\n" +
            "Amount: PKR " + String.format("%.2f", baseAmount) + "\n" +
            "Transaction ID: TXN" + System.currentTimeMillis(),
            "Payment Confirmed",
            JOptionPane.INFORMATION_MESSAGE);

        statusLabel.setForeground(new Color(0, 128, 0));
        statusLabel.setText("Payment processed successfully via " + method);

        System.out.println("[PAYMENT] Processed via " + method +
                           " | Amount: PKR " + baseAmount +
                           " | Member: " + member.getEmail());
    }
}