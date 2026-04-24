package com.trinova.scms.view;

import com.trinova.scms.model.Member;
import com.trinova.scms.model.SubscriptionPlan;
import com.trinova.scms.service.BillingService;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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
        setBackground(UITheme.BG_CONTENT);
        initComponents();
        loadPlans();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BG_CONTENT);
        topPanel.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        JLabel titleLabel = UITheme.sectionTitle("Membership Plans");
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Show current plan
        String currentPlan = member.hasActivePlan()
            ? "Current Plan: " + member.getPlanType() +
              " (Expires: " + member.getPlanExpiry() + ")"
            : "No active plan — Pay-as-you-go (full rates apply)";
        JLabel currentLabel = new JLabel(currentPlan);
        currentLabel.setFont(UITheme.FONT_SMALL);
        currentLabel.setForeground(
            member.hasActivePlan() ? UITheme.SUCCESS : UITheme.TEXT_MUTED);
        topPanel.add(currentLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        plansPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        plansPanel.setBackground(UITheme.BG_CONTENT);
        add(new JScrollPane(plansPanel) {{
            setBorder(null);
            getViewport().setBackground(UITheme.BG_CONTENT);
        }}, BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void loadPlans() {
        plansPanel.removeAll();
        try {
            List<SubscriptionPlan> plans = billingService.getAllPlans();
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

        Color accentColor = isCurrent ? UITheme.SUCCESS : UITheme.ACCENT;

        // ── Outer wrapper with rounded border ────────────────
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // white body
                g2.setColor(UITheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 20, 20));
                if (isCurrent) {
                    g2.setColor(accentColor);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.draw(new RoundRectangle2D.Float(
                        1, 1, getWidth() - 3, getHeight() - 3, 20, 20));
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(270, 420));

        // ── Header panel (gradient) ──────────────────────────
        JPanel header = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(
                    0, 0, accentColor,
                    getWidth(), 0, accentColor.darker()));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 10, 20, 20);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 80));
        JLabel nameLabel = new JLabel(plan.getPlanName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        header.add(nameLabel);
        card.add(header, BorderLayout.NORTH);

        // ── Body panel (white) ───────────────────────────────
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Price
        JLabel priceLabel = new JLabel(
            "PKR " + String.format("%.0f", plan.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        priceLabel.setForeground(UITheme.TEXT_PRIMARY);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(priceLabel);

        JLabel perMonth = new JLabel("/month");
        perMonth.setFont(UITheme.FONT_SMALL);
        perMonth.setForeground(UITheme.TEXT_MUTED);
        perMonth.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(perMonth);
        body.add(Box.createRigidArea(new Dimension(0, 16)));

        // Benefits
        String benefits = plan.getPlanType().equals("BASIC")
            ? "<html><center>+ 5 free desk hours/day<br>" +
              "+ WiFi included<br>" +
              "- Meeting rooms paid<br>" +
              "- Extra facilities paid</center></html>"
            : "<html><center>+ Unlimited desk usage<br>" +
              "+ 2 free meeting hrs/day<br>" +
              "+ Coffee, printing free<br>" +
              "+ Locker &amp; parking free</center></html>";
        JLabel benefitsLabel = new JLabel(benefits);
        benefitsLabel.setFont(UITheme.FONT_BODY);
        benefitsLabel.setForeground(UITheme.TEXT_SECONDARY);
        benefitsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(benefitsLabel);
        body.add(Box.createRigidArea(new Dimension(0, 12)));

        // Active badge
        if (isCurrent) {
            JLabel activeLabel = new JLabel("ACTIVE");
            activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            activeLabel.setForeground(UITheme.SUCCESS);
            activeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            body.add(activeLabel);
            body.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        // Button
        JButton selectBtn;
        if (isCurrent) {
            selectBtn = new JButton("Active Plan");
            selectBtn.setEnabled(false);
            selectBtn.setFont(UITheme.FONT_BTN);
        } else {
            selectBtn = UITheme.primaryButton("Subscribe & Pay");
        }
        selectBtn.setMaximumSize(new Dimension(200, 42));
        selectBtn.setPreferredSize(new Dimension(200, 42));
        selectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectBtn.addActionListener(e -> openPayment(plan));
        body.add(selectBtn);

        card.add(body, BorderLayout.CENTER);
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
            this, "Select payment method:", "Payment",
            JOptionPane.QUESTION_MESSAGE,
            null, methods, methods[0]);

        if (method == null) return;

        // Promo code
        String promoCode = JOptionPane.showInputDialog(this,
            "Enter promo code (leave empty if none):",
            "Promo Code", JOptionPane.QUESTION_MESSAGE);

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

            statusLabel.setForeground(UITheme.SUCCESS);
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
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }
}