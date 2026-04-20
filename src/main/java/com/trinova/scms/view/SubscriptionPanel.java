package com.trinova.scms.view;

import com.trinova.scms.model.Member;
import com.trinova.scms.model.SubscriptionPlan;
import com.trinova.scms.service.BillingService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class SubscriptionPanel extends JPanel {

    private final Member member;
    private BillingService billingService;
    private JLabel statusLabel;
    private JPanel plansPanel;

    public SubscriptionPanel(Member member) {
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
        loadPlans();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel titleLabel = new JLabel("Membership Plans");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Show current plan
        String currentPlan = member.hasActivePlan()
            ? "Current Plan: " + member.getPlanType() +
              " (Expires: " + member.getPlanExpiry() + ")"
            : "No active plan — Pay-as-you-go (full rates apply)";
        JLabel currentLabel = new JLabel(currentPlan);
        currentLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        currentLabel.setForeground(
            member.hasActivePlan() ?
            new Color(0, 128, 0) : Color.GRAY);
        topPanel.add(currentLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        plansPanel = new JPanel(new FlowLayout(
            FlowLayout.CENTER, 30, 30));
        plansPanel.setBackground(Color.WHITE);
        add(new JScrollPane(plansPanel), BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setBorder(
            BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void loadPlans() {
        plansPanel.removeAll();
        try {
            List<SubscriptionPlan> plans =
                billingService.getAllPlans();
            for (SubscriptionPlan plan : plans) {
                plansPanel.add(buildPlanCard(plan));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading plans: " + e.getMessage());
        }
        plansPanel.revalidate();
        plansPanel.repaint();
    }

    private JPanel buildPlanCard(SubscriptionPlan plan) {
        boolean isCurrent =
            member.hasActivePlan() &&
            member.getPlanType() != null &&
            member.getPlanType().equalsIgnoreCase(plan.getPlanType());

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(220, 320));
        card.setBackground(isCurrent ?
            new Color(230, 255, 230) : new Color(240, 244, 248));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                isCurrent ?
                new Color(0, 128, 0) : new Color(16, 64, 110), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Plan name
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel(plan.getPlanName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(new Color(16, 64, 110));
        card.add(nameLabel, gbc);

        // Price
        gbc.gridy = 1;
        JLabel priceLabel = new JLabel(
            "PKR " + String.format("%.0f", plan.getPrice()) +
            "/month");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 128, 0));
        card.add(priceLabel, gbc);

        // Benefits
        gbc.gridy = 2;
        String benefits = plan.getPlanType().equals("BASIC")
            ? "<html><center>✔ 5 free desk hrs/day<br>" +
              "✔ WiFi included<br>" +
              "✗ Meeting rooms paid<br>" +
              "✗ Extra facilities paid</center></html>"
            : "<html><center>✔ Unlimited desk usage<br>" +
              "✔ 2 free meeting hrs/day<br>" +
              "✔ Coffee, printing free<br>" +
              "✔ Locker & parking free</center></html>";
        JLabel benefitsLabel = new JLabel(benefits);
        benefitsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(benefitsLabel, gbc);

        // Current plan badge
        if (isCurrent) {
            gbc.gridy = 3;
            JLabel activeLabel = new JLabel("✔ ACTIVE");
            activeLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13));
            activeLabel.setForeground(new Color(0, 128, 0));
            card.add(activeLabel, gbc);
        }

        // Subscribe button
        gbc.gridy = 4;
        JButton selectBtn = new JButton(
            isCurrent ? "Active Plan" : "Subscribe & Pay");
        selectBtn.setBackground(isCurrent ?
            new Color(0, 128, 0) : new Color(16, 64, 110));
        selectBtn.setForeground(Color.WHITE);
        selectBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectBtn.setFocusPainted(false);
        selectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectBtn.setEnabled(!isCurrent);
        selectBtn.setPreferredSize(new Dimension(170, 38));

        selectBtn.addActionListener(e -> openPayment(plan));
        card.add(selectBtn, gbc);

        return card;
    }

    private void openPayment(SubscriptionPlan plan) {
        // Show payment dialog for subscription
        int confirm = JOptionPane.showConfirmDialog(this,
            "Subscribe to " + plan.getPlanName() +
            " Plan\nAmount: PKR " +
            String.format("%.0f", plan.getPrice()) + "\n\n" +
            "Proceed to payment?",
            "Confirm Subscription",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // Payment method selection
        String[] methods = {"Visa", "Mastercard", "Digital Wallet"};
        String method = (String) JOptionPane.showInputDialog(
            this,
            "Select payment method:",
            "Payment",
            JOptionPane.QUESTION_MESSAGE,
            null, methods, methods[0]);

        if (method == null) return;

        // Promo code
        String promoCode = JOptionPane.showInputDialog(
            this,
            "Enter promo code (leave empty if none):",
            "Promo Code",
            JOptionPane.QUESTION_MESSAGE);

        double finalAmount = plan.getPrice();
        try {
            if (promoCode != null && !promoCode.trim().isEmpty()) {
                finalAmount = billingService.applyPromoCode(
                    finalAmount, promoCode.trim());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Promo code error: " + ex.getMessage());
            return;
        }

        // Process payment
        try {
            LocalDate expiry = LocalDate.now()
                .plusDays(plan.getDurationDays());
            billingService.assignPlan(
                member.getMemberId(), plan.getPlanId(), expiry);

            member.setPlanId(plan.getPlanId());
            member.setPlanType(plan.getPlanType());
            member.setPlanExpiry(expiry);

            String txnRef = "TXN" + System.currentTimeMillis();

            JOptionPane.showMessageDialog(this,
                "Payment Successful!\n" +
                "Plan: " + plan.getPlanName() + "\n" +
                "Amount Paid: PKR " +
                String.format("%.2f", finalAmount) + "\n" +
                "Method: " + method + "\n" +
                "Valid Until: " + expiry + "\n" +
                "Transaction ID: " + txnRef,
                "Subscription Activated",
                JOptionPane.INFORMATION_MESSAGE);

            statusLabel.setForeground(new Color(0, 128, 0));
            statusLabel.setText(
                plan.getPlanName() +
                " plan activated! Valid until: " + expiry);

            System.out.println(
                "[PAYMENT] Subscription: " + plan.getPlanName() +
                " | Amount: PKR " + finalAmount +
                " | Method: " + method +
                " | TXN: " + txnRef);

            loadPlans();

        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }
}