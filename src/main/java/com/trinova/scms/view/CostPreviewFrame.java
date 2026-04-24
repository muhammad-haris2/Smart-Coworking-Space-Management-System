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
import javax.swing.border.*;
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
    private JPanel              costPanel;

    private static final Color NAVY   = new Color(16, 64, 110);
    private static final Color BG     = new Color(248, 250, 252);
    private static final Color GREEN  = new Color(0, 120, 60);

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
        setSize(460, 700);
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
            for (Facility f : allFacilities) f.setSelectedQuantity(0);
            Room room = new RoomDAO().findById(roomId);
            costResult = new CostCalculatorService().calculate(
                member, room, bookingType, startTime, endTime, allFacilities);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error calculating cost: " + e.getMessage());
        }
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Header ────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));
        JLabel title = new JLabel("Booking Summary");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(NAVY);
        header.add(title, BorderLayout.WEST);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setForeground(Color.GRAY);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Scrollable Center ──────────────────────────────────
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        center.add(buildInfoPanel());
        center.add(Box.createRigidArea(new Dimension(0, 15)));
        center.add(buildFacilitiesPanel());
        center.add(Box.createRigidArea(new Dimension(0, 15)));
        costPanel = buildCostPanel();
        center.add(costPanel);

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        // ── Footer ─────────────────────────────────────────────
        root.add(buildButtonPanel(), BorderLayout.SOUTH);

        add(root);
    }

    private JPanel buildInfoPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);

        JLabel sectionTitle = new JLabel("Booking Details");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sectionTitle.setForeground(NAVY);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        outer.add(sectionTitle, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 248, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 235)),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(4, 0, 4, 15);

        long hours = java.time.Duration.between(startTime, endTime).toHours();
        String duration = startTime.toLocalTime() + " — " + endTime.toLocalTime()
                        + " (" + hours + " hours)";
        String planStr = member.hasActivePlan()
            ? member.getPlanType() + " (expires " + member.getPlanExpiry() + ")"
            : "No Plan";

        String[][] rows = {
            {"Space:", roomName},
            {"Type:", roomType},
            {"Date:", startTime.toLocalDate() + " (" + startTime.getDayOfWeek() + ")"},
            {"Duration:", duration},
            {"Booking Type:", bookingType},
            {"Your Plan:", planStr}
        };

        for (int i = 0; i < rows.length; i++) {
            g.gridy = i; g.gridx = 0; g.weightx = 0;
            JLabel key = new JLabel(rows[i][0]);
            key.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            key.setForeground(Color.GRAY);
            panel.add(key, g);

            g.gridx = 1; g.weightx = 1.0;
            JLabel val = new JLabel(rows[i][1]);
            val.setFont(new Font("Segoe UI", Font.BOLD, 12));
            val.setForeground(
                rows[i][0].equals("Your Plan:") && member.hasActivePlan()
                    ? new Color(0, 100, 50) : Color.DARK_GRAY
            );
            panel.add(val, g);
        }

        outer.add(panel, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildFacilitiesPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel sectionTitle = new JLabel("Add Extra Facilities");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sectionTitle.setForeground(NAVY);
        titleRow.add(sectionTitle, BorderLayout.WEST);
        JLabel subNote = new JLabel("Some are FREE with your Premium plan.");
        subNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        subNote.setForeground(Color.GRAY);
        titleRow.add(subNote, BorderLayout.SOUTH);
        outer.add(titleRow, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Header row
        String[] headers = {"Facility", "Unit Price", "For You", "Qty"};
        for (int col = 0; col < headers.length; col++) {
            gbc.gridx = col; gbc.gridy = 0;
            JLabel h = new JLabel(headers[col]);
            h.setFont(new Font("Segoe UI", Font.BOLD, 12));
            h.setForeground(Color.GRAY);
            grid.add(h, gbc);
        }

        quantitySpinners.clear();
        String planType = member.getPlanType() != null ? member.getPlanType() : "NONE";

        for (int i = 0; i < allFacilities.size(); i++) {
            Facility f = allFacilities.get(i);
            boolean isFree = ("PREMIUM".equals(planType) && f.isFreeForPremium())
                          || ("BASIC".equals(planType) && f.isFreeForBasic());
            int row = i + 1;
            gbc.gridy = row;

            gbc.gridx = 0;
            grid.add(new JLabel(f.getFacilityName()), gbc);

            gbc.gridx = 1;
            grid.add(new JLabel("PKR " + String.format("%.0f", f.getPrice()) + "/" + f.getUnit()), gbc);

            gbc.gridx = 2;
            JLabel freeLabel = new JLabel(isFree ? "FREE ✔" : "Paid");
            freeLabel.setForeground(isFree ? GREEN : Color.DARK_GRAY);
            freeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            grid.add(freeLabel, gbc);

            gbc.gridx = 3;
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
            spinner.setPreferredSize(new Dimension(65, 28));
            quantitySpinners.add(spinner);
            grid.add(spinner, gbc);
        }

        outer.add(grid, BorderLayout.CENTER);

        JButton recalcBtn = new JButton("Update Cost ↺");
        recalcBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recalcBtn.putClientProperty("FlatLaf.style", "arc: 8");
        recalcBtn.addActionListener(e -> recalculate());
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        btnWrap.setOpaque(false);
        btnWrap.add(recalcBtn);
        outer.add(btnWrap, BorderLayout.SOUTH);

        return outer;
    }

    private JPanel buildCostPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(235, 248, 240));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 210, 180)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.add(new JLabel(getCostHTML()), BorderLayout.CENTER);
        return panel;
    }

    private void refreshCostPanel() {
        costPanel.removeAll();
        costPanel.add(new JLabel(getCostHTML()), BorderLayout.CENTER);
        costPanel.revalidate();
        costPanel.repaint();
    }

    private String getCostHTML() {
        if (costResult == null) return "<html>Calculating...</html>";
        double subtotal = costResult.baseCost + costResult.facilityCost;
        return "<html><b>Cost Breakdown</b><br><br>"
             + "Base Cost: &nbsp; PKR " + String.format("%.2f", costResult.baseCost) + "<br>"
             + "Facilities: &nbsp; PKR " + String.format("%.2f", costResult.facilityCost) + "<br>"
             + "<br>Subtotal: &nbsp; PKR " + String.format("%.2f", subtotal) + "<br>"
             + "VAT (17%): &nbsp; PKR " + String.format("%.2f", costResult.vatAmount) + "<br><br>"
             + "<b style='font-size:14px'>TOTAL PAYABLE: &nbsp; PKR "
             + String.format("%.2f", costResult.totalCost) + "</b>"
             + (costResult.totalCost == 0
                 ? "<br><i style='color:green'>This booking is FREE with your plan!</i>" : "")
             + "</html>";
    }

    private void recalculate() {
        try {
            for (int i = 0; i < allFacilities.size(); i++)
                allFacilities.get(i).setSelectedQuantity((int) quantitySpinners.get(i).getValue());
            Room room = new RoomDAO().findById(roomId);
            costResult = new CostCalculatorService().calculate(
                member, room, bookingType, startTime, endTime, allFacilities);
            refreshCostPanel();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(110, 38));
        cancelBtn.addActionListener(e -> dispose());

        boolean isFree = costResult != null && costResult.totalCost == 0;
        JButton confirmBtn = new JButton(isFree ? "Book for FREE" : "Confirm Booking →");
        confirmBtn.setBackground(isFree ? GREEN : NAVY);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmBtn.setPreferredSize(new Dimension(170, 38));
        confirmBtn.putClientProperty("FlatLaf.style", "arc: 8");
        confirmBtn.addActionListener(e -> {
            for (int i = 0; i < allFacilities.size(); i++)
                allFacilities.get(i).setSelectedQuantity((int) quantitySpinners.get(i).getValue());
            if (isFree) confirmBooking("FREE", 0);
            else openPayment();
        });

        panel.add(cancelBtn);
        panel.add(confirmBtn);
        return panel;
    }

    private void openPayment() {
        String[] methods = {"Visa", "Mastercard", "Digital Wallet"};
        String method = (String) JOptionPane.showInputDialog(this,
            "Select payment method:", "Payment",
            JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]);
        if (method == null) return;
        confirmBooking(method, costResult.totalCost);
    }

    private void confirmBooking(String method, double finalTotal) {
        try {
            BookingService bookingService = new BookingService();
            int bookingId = bookingService.book(
                member.getMemberId(), member.getEmail(), roomId,
                bookingType, startTime, endTime,
                costResult.baseCost, costResult.facilityCost,
                costResult.vatAmount, costResult.totalCost);

            if (!allFacilities.isEmpty())
                new FacilityDAO().saveBookingFacilities(bookingId, allFacilities,
                    member.getPlanType() != null ? member.getPlanType() : "NONE");

            if (finalTotal > 0) {
                String txnRef = "TXN" + System.currentTimeMillis();
                new PaymentDAO().createBookingPayment(
                    member.getMemberId(), bookingId, finalTotal, method, txnRef);
                JOptionPane.showMessageDialog(this,
                    "Booking Confirmed!\nSpace: " + roomName
                    + "\nAmount Paid: PKR " + String.format("%.2f", finalTotal)
                    + "\nBooking ID: " + bookingId,
                    "Confirmed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Booking Confirmed — FREE!\nSpace: " + roomName
                    + "\nBooking ID: " + bookingId,
                    "Confirmed", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}