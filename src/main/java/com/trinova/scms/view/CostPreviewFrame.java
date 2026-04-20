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

    public CostPreviewFrame(Member member,
                             int roomId,
                             String roomName,
                             String roomType,
                             String bookingType,
                             LocalDateTime startTime,
                             LocalDateTime endTime,
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
        setSize(560, 650);
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
            CostCalculatorService calc =
                new CostCalculatorService();
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
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ── Title ──────────────────────────────────────────
        JLabel titleLabel = new JLabel(
            "Booking Summary", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(16, 64, 110));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ── Center: booking info + facilities ──────────────
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(
            new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // Booking details
        centerPanel.add(buildInfoPanel());
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Facilities section
        centerPanel.add(buildFacilitiesPanel());
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Cost breakdown
        centerPanel.add(buildCostPanel());

        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.setBorder(null);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // ── Buttons ────────────────────────────────────────
        mainPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel buildInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 6));
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(200, 210, 230)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        String[][] info = {
            {"Space",    roomName},
            {"Type",     roomType},
            {"Date",     startTime.toLocalDate().toString()},
            {"Time",     startTime.toLocalTime() +
                         " → " + endTime.toLocalTime()},
            {"Plan",     member.hasActivePlan() ?
                         member.getPlanType() +
                         " (expires " +
                         member.getPlanExpiry() + ")" :
                         "No Plan (pay-as-you-go)"}
        };

        for (String[] row : info) {
            JLabel key = new JLabel(row[0] + ":");
            key.setFont(new Font("Segoe UI", Font.BOLD, 13));
            panel.add(key);
            JLabel val = new JLabel(row[1]);
            val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(val);
        }
        return panel;
    }

    private JPanel buildFacilitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Add Extra Facilities (optional)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(16, 64, 110));
        title.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 8, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // Header
        gbc.gridy = 0; gbc.gridx = 0;
        grid.add(boldLabel("Facility"), gbc);
        gbc.gridx = 1;
        grid.add(boldLabel("Price"), gbc);
        gbc.gridx = 2;
        grid.add(boldLabel("For You"), gbc);
        gbc.gridx = 3;
        grid.add(boldLabel("Qty"), gbc);

        quantitySpinners.clear();
        String planType = member.getPlanType() != null ?
                          member.getPlanType() : "NONE";

        for (int i = 0; i < allFacilities.size(); i++) {
            Facility f = allFacilities.get(i);
            boolean isFree =
                ("PREMIUM".equals(planType) &&
                 f.isFreeForPremium()) ||
                ("BASIC".equals(planType) &&
                 f.isFreeForBasic());

            gbc.gridy = i + 1;

            gbc.gridx = 0;
            grid.add(new JLabel(f.getFacilityName()), gbc);

            gbc.gridx = 1;
            grid.add(new JLabel(
                "PKR " + String.format("%.0f", f.getPrice()) +
                "/" + f.getUnit()), gbc);

            gbc.gridx = 2;
            JLabel freeLabel = new JLabel(
                isFree ? "FREE ✔" : "Paid");
            freeLabel.setForeground(
                isFree ? new Color(0, 128, 0) : Color.DARK_GRAY);
            freeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            grid.add(freeLabel, gbc);

            gbc.gridx = 3;
            JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(0, 0, 50, 1));
            spinner.setPreferredSize(new Dimension(60, 28));
            quantitySpinners.add(spinner);
            grid.add(spinner, gbc);
        }

        panel.add(grid, BorderLayout.CENTER);

        // Recalculate button
        JButton recalcBtn = new JButton(
            "Update Cost with Facilities");
        recalcBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recalcBtn.setBackground(new Color(16, 64, 110));
        recalcBtn.setForeground(Color.WHITE);
        recalcBtn.setFocusPainted(false);
        recalcBtn.addActionListener(e -> recalculate());
        JPanel btnWrap = new JPanel(
            new FlowLayout(FlowLayout.LEFT));
        btnWrap.setBackground(Color.WHITE);
        btnWrap.add(recalcBtn);
        panel.add(btnWrap, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel costBreakdownLabel;

    private JPanel buildCostPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(0, 150, 0)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        costBreakdownLabel = new JLabel(getCostHTML());
        costBreakdownLabel.setFont(
            new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(costBreakdownLabel, BorderLayout.CENTER);
        return panel;
    }

    private String getCostHTML() {
        if (costResult == null)
            return "<html>Calculating...</html>";
        return "<html>" +
            "<b>Base Cost:</b> PKR " +
            String.format("%.2f", costResult.baseCost) +
            "<br>" +
            "<b>Facilities:</b> PKR " +
            String.format("%.2f", costResult.facilityCost) +
            "<br>" +
            "<b>VAT (17%):</b> PKR " +
            String.format("%.2f", costResult.vatAmount) +
            "<br><br>" +
            "<b style='font-size:14px'>TOTAL: PKR " +
            String.format("%.2f", costResult.totalCost) +
            "</b>" +
            (costResult.totalCost == 0 ?
             "<br><i style='color:green'>" +
             "This booking is FREE with your plan!</i>" : "") +
            "</html>";
    }

    private void recalculate() {
        try {
            for (int i = 0; i < allFacilities.size(); i++) {
                int qty = (int)
                    quantitySpinners.get(i).getValue();
                allFacilities.get(i).setSelectedQuantity(qty);
            }
            Room room = new RoomDAO().findById(roomId);
            CostCalculatorService calc =
                new CostCalculatorService();
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
        JPanel panel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, Color.LIGHT_GRAY));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelBtn.setPreferredSize(new Dimension(120, 38));
        cancelBtn.addActionListener(e -> dispose());

        boolean isFree = costResult != null &&
                         costResult.totalCost == 0;
        String confirmText = isFree ?
            "Book for FREE" : "Proceed to Payment";
        JButton confirmBtn = new JButton(confirmText);
        confirmBtn.setBackground(isFree ?
            new Color(0, 128, 0) : new Color(16, 64, 110));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.setPreferredSize(new Dimension(180, 38));
        confirmBtn.addActionListener(e -> {
            // Update quantities before confirming
            for (int i = 0; i < allFacilities.size(); i++) {
                int qty = (int)
                    quantitySpinners.get(i).getValue();
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
        String[] methods =
            {"Visa", "Mastercard", "Digital Wallet"};
        String method = (String) JOptionPane.showInputDialog(
            this, "Select payment method:",
            "Payment", JOptionPane.QUESTION_MESSAGE,
            null, methods, methods[0]);
        if (method == null) return;

        String promoCode = JOptionPane.showInputDialog(
            this,
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
                String txnRef = "TXN" +
                    System.currentTimeMillis();
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

    private JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }
}