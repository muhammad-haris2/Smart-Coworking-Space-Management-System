package com.trinova.scms.view;

import com.trinova.scms.dao.FacilityDAO;
import com.trinova.scms.model.Facility;
import com.trinova.scms.model.Member;
import com.trinova.scms.model.Room;
import com.trinova.scms.service.BillingService;
import com.trinova.scms.service.BookingService;
import com.trinova.scms.service.CostCalculatorService;
import com.trinova.scms.service.CostCalculatorService.CostResult;
import com.trinova.scms.dao.PaymentDAO;
import com.trinova.scms.dao.RoomDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CostPreviewFrame extends JFrame {

    private final Member        member;
    private final int           roomId;
    private final String        roomName;
    private final String        roomType;
    private final String        bookingType;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final JFrame        parent;

    private CostResult          costResult;
    private List<Facility>      allFacilities = new ArrayList<>();
    private List<JSpinner>      quantitySpinners = new ArrayList<>();

    public CostPreviewFrame(Member member, int roomId, String roomName,
                             String roomType, String bookingType,
                             LocalDateTime startTime, LocalDateTime endTime,
                             JFrame parent) {
        this.member      = member;
        this.roomId      = roomId;
        this.roomName    = roomName;
        this.roomType    = roomType;
        this.bookingType = bookingType;
        this.startTime   = startTime;
        this.endTime     = endTime;
        this.parent      = parent;

        setTitle("Booking Summary — " + roomName);
        setSize(580, 680);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        loadFacilitiesAndCalculate();
        initComponents();
    }

    private void loadFacilitiesAndCalculate() {
        try {
            FacilityDAO facilityDAO = new FacilityDAO();
            allFacilities = facilityDAO.getAllFacilities();
            for (Facility f : allFacilities) {
                f.setSelectedQuantity(0);
            }
            Room room = new RoomDAO().findById(roomId);
            CostCalculatorService calc = new CostCalculatorService();
            costResult = calc.calculate(
                member, room, bookingType,
                startTime, endTime, allFacilities);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error calculating cost: " + e.getMessage());
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBackground(UITheme.BG_CONTENT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ── Title ────────────────────────────────────────────
        JLabel titleLabel = UITheme.sectionTitle("Booking Summary");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ── Center: booking info + facilities ────────────────
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        centerPanel.add(buildInfoPanel());
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        centerPanel.add(buildFacilitiesPanel());
        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        centerPanel.add(buildCostPanel());

        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG_CONTENT);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // ── Buttons ──────────────────────────────────────────
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel buildInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 12, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        String[][] info = {
            {"Space",    roomName},
            {"Type",     roomType},
            {"Date",     startTime.toLocalDate().toString()},
            {"Time",     startTime.toLocalTime() +
                         " → " + endTime.toLocalTime()},
            {"Plan",     member.hasActivePlan() ?
                         member.getPlanType() +
                         " (expires " + member.getPlanExpiry() + ")" :
                         "No Plan (pay-as-you-go)"}
        };

        for (String[] row : info) {
            JLabel key = new JLabel(row[0] + ":");
            key.setFont(UITheme.FONT_HEADING);
            key.setForeground(UITheme.TEXT_SECONDARY);
            panel.add(key);
            JLabel val = new JLabel(row[1]);
            val.setFont(UITheme.FONT_BODY);
            val.setForeground(UITheme.TEXT_PRIMARY);
            panel.add(val);
        }
        return panel;
    }

    private JPanel buildFacilitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Add Extra Facilities (optional)");
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // Header
        gbc.gridy = 0;
        String[] headers = {"Facility", "Price", "For You", "Qty"};
        for (int i = 0; i < headers.length; i++) {
            gbc.gridx = i;
            JLabel h = new JLabel(headers[i]);
            h.setFont(UITheme.FONT_TABLE_H);
            h.setForeground(UITheme.TEXT_PRIMARY);
            grid.add(h, gbc);
        }

        quantitySpinners.clear();
        String planType = member.getPlanType() != null ?
                          member.getPlanType() : "NONE";

        for (int i = 0; i < allFacilities.size(); i++) {
            Facility f = allFacilities.get(i);
            boolean isFree =
                ("PREMIUM".equals(planType) && f.isFreeForPremium()) ||
                ("BASIC".equals(planType) && f.isFreeForBasic());

            gbc.gridy = i + 1;

            gbc.gridx = 0;
            JLabel nameLbl = new JLabel(f.getFacilityName());
            nameLbl.setFont(UITheme.FONT_TABLE);
            grid.add(nameLbl, gbc);

            gbc.gridx = 1;
            JLabel priceLbl = new JLabel(
                "PKR " + String.format("%.0f", f.getPrice()) +
                "/" + f.getUnit());
            priceLbl.setFont(UITheme.FONT_TABLE);
            priceLbl.setForeground(UITheme.TEXT_SECONDARY);
            grid.add(priceLbl, gbc);

            gbc.gridx = 2;
            JLabel freeLabel = new JLabel(isFree ? "FREE ✔" : "Paid");
            freeLabel.setForeground(isFree ? UITheme.SUCCESS : UITheme.TEXT_SECONDARY);
            freeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            grid.add(freeLabel, gbc);

            gbc.gridx = 3;
            JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(0, 0, 50, 1));
            spinner.setPreferredSize(new Dimension(65, 30));
            quantitySpinners.add(spinner);
            grid.add(spinner, gbc);
        }

        panel.add(grid, BorderLayout.CENTER);

        // Recalculate button
        JButton recalcBtn = UITheme.secondaryButton("Update Cost with Facilities");
        recalcBtn.addActionListener(e -> recalculate());
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnWrap.setOpaque(false);
        btnWrap.add(recalcBtn);
        panel.add(btnWrap, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel costBreakdownLabel;

    private JPanel buildCostPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SUCCESS_BG);
                g2.fill(new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 16, 16));
                // left accent
                g2.setColor(UITheme.SUCCESS);
                g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        costBreakdownLabel = new JLabel(getCostHTML());
        costBreakdownLabel.setFont(UITheme.FONT_BODY);
        panel.add(costBreakdownLabel, BorderLayout.CENTER);
        return panel;
    }

    private String getCostHTML() {
        if (costResult == null)
            return "<html>Calculating...</html>";
        return "<html>" +
            "<b>Base Cost:</b> PKR " +
            String.format("%.2f", costResult.baseCost) + "<br>" +
            "<b>Facilities:</b> PKR " +
            String.format("%.2f", costResult.facilityCost) + "<br>" +
            "<b>VAT (17%):</b> PKR " +
            String.format("%.2f", costResult.vatAmount) + "<br><br>" +
            "<b style='font-size:15px'>TOTAL: PKR " +
            String.format("%.2f", costResult.totalCost) + "</b>" +
            (costResult.totalCost == 0 ?
             "<br><i style='color:#10B981'>" +
             "This booking is FREE with your plan!</i>" : "") +
            "</html>";
    }

    private void recalculate() {
        try {
            for (int i = 0; i < allFacilities.size(); i++) {
                int qty = (int) quantitySpinners.get(i).getValue();
                allFacilities.get(i).setSelectedQuantity(qty);
            }
            Room room = new RoomDAO().findById(roomId);
            CostCalculatorService calc = new CostCalculatorService();
            costResult = calc.calculate(
                member, room, bookingType,
                startTime, endTime, allFacilities);
            costBreakdownLabel.setText(getCostHTML());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage());
        }
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(UITheme.BG_CONTENT);
        panel.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton cancelBtn = UITheme.secondaryButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(130, 40));
        cancelBtn.addActionListener(e -> dispose());

        boolean isFree = costResult != null && costResult.totalCost == 0;
        String confirmText = isFree ? "Book for FREE" : "Proceed to Payment";
        JButton confirmBtn = UITheme.primaryButton(confirmText);
        if (isFree) {
            confirmBtn = new JButton(confirmText) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(UITheme.SUCCESS);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            confirmBtn.setForeground(Color.WHITE);
            confirmBtn.setFont(UITheme.FONT_BTN);
            confirmBtn.setFocusPainted(false);
            confirmBtn.setBorderPainted(false);
            confirmBtn.setContentAreaFilled(false);
            confirmBtn.setOpaque(false);
            confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        confirmBtn.setPreferredSize(new Dimension(200, 40));
        JButton finalConfirmBtn = confirmBtn;
        confirmBtn.addActionListener(e -> {
            // Update quantities before confirming
            for (int i = 0; i < allFacilities.size(); i++) {
                int qty = (int) quantitySpinners.get(i).getValue();
                allFacilities.get(i).setSelectedQuantity(qty);
            }
            if (isFree) {
                confirmBooking("FREE", 0);
            } else {
                openPayment();
            }
        });

        panel.add(cancelBtn);
        panel.add(confirmBtn);
        return panel;
    }

    private void openPayment() {
        String[] methods = {"Visa", "Mastercard", "Digital Wallet"};
        String method = (String) JOptionPane.showInputDialog(
            this, "Select payment method:",
            "Payment", JOptionPane.QUESTION_MESSAGE,
            null, methods, methods[0]);
        if (method == null) return;

        String promoCode = JOptionPane.showInputDialog(this,
            "Enter promo code (leave blank if none):",
            "Promo Code", JOptionPane.QUESTION_MESSAGE);

        double finalTotal = costResult.totalCost;
        if (promoCode != null && !promoCode.trim().isEmpty()) {
            try {
                BillingService billing = new BillingService();
                finalTotal = billing.applyPromoCode(
                    finalTotal, promoCode.trim());
                JOptionPane.showMessageDialog(this,
                    "Promo applied!\nNew total: PKR " +
                    String.format("%.2f", finalTotal));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Promo error: " + ex.getMessage());
                return;
            }
        }

        confirmBooking(method, finalTotal);
    }

    private void confirmBooking(String method, double finalTotal) {
        try {
            BookingService bookingService = new BookingService();
            int bookingId = bookingService.book(
                member.getMemberId(),
                member.getEmail(),
                roomId,
                bookingType,
                startTime,
                endTime,
                costResult.baseCost,
                costResult.facilityCost,
                costResult.vatAmount,
                costResult.totalCost);

            // Save facilities
            if (!allFacilities.isEmpty()) {
                FacilityDAO facilityDAO = new FacilityDAO();
                facilityDAO.saveBookingFacilities(
                    bookingId, allFacilities,
                    member.getPlanType() != null ?
                    member.getPlanType() : "NONE");
            }

            // Save payment record
            if (finalTotal > 0) {
                String txnRef = "TXN" + System.currentTimeMillis();
                PaymentDAO paymentDAO = new PaymentDAO();
                paymentDAO.createBookingPayment(
                    member.getMemberId(), bookingId,
                    finalTotal, method, txnRef);

                JOptionPane.showMessageDialog(this,
                    "Booking Confirmed!\n" +
                    "Space: " + roomName + "\n" +
                    "Date: " + startTime.toLocalDate() + "\n" +
                    "Time: " + startTime.toLocalTime() +
                    " → " + endTime.toLocalTime() + "\n" +
                    "Amount Paid: PKR " +
                    String.format("%.2f", finalTotal) + "\n" +
                    "Method: " + method + "\n" +
                    "Booking ID: " + bookingId + "\n" +
                    "Transaction: " + txnRef,
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);

                System.out.println(
                    "[PAYMENT] BookingID: " + bookingId +
                    " | PKR: " + finalTotal +
                    " | " + method +
                    " | TXN: " + txnRef);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Booking Confirmed — FREE!\n" +
                    "Space: " + roomName + "\n" +
                    "Date: " + startTime.toLocalDate() + "\n" +
                    "Time: " + startTime.toLocalTime() +
                    " → " + endTime.toLocalTime() + "\n" +
                    "Booking ID: " + bookingId,
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Booking failed: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}