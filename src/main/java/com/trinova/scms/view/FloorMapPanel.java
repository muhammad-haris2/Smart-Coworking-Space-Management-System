package com.trinova.scms.view;

import com.trinova.scms.dao.BookingDAO;
import com.trinova.scms.dao.RoomDAO;
import com.trinova.scms.model.Booking;
import com.trinova.scms.model.Member;
import com.trinova.scms.model.Room;
import com.trinova.scms.service.BookingService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class FloorMapPanel extends JPanel {

    // ── Colours ────────────────────────────────────────────
    private static final Color NAVY       = new Color(16, 64, 110);
    private static final Color AVAILABLE  = new Color(80, 200, 120);
    private static final Color BOOKED     = new Color(240, 80, 80);
    private static final Color UNAVAIL    = new Color(190, 200, 215);
    private static final Color ROOM_BG    = new Color(180, 200, 230);
    private static final Color SEL_BORDER = new Color(255, 180, 0);

    // ── State ──────────────────────────────────────────────
    private final Member member;
    private List<Room> allRooms = new ArrayList<>();
    private Set<Integer> bookedRoomIds = new HashSet<>();
    private LocalDate selectedDate = LocalDate.now();
    private Room selectedRoom = null;
    private String viewMode = "Desks"; // "Desks" or "Meeting Rooms"

    // ── UI refs ────────────────────────────────────────────
    private JPanel mapCanvas;
    private JPanel timeSlotsPanel;
    private JLabel monthLabel;
    private JPanel calGrid;
    private JComboBox<String> spaceCombo;
    private JSpinner durationSpinner;
    private JTextField memberNameField;
    private JLabel summaryLabel;
    private JLabel selectedRoomLabel;

    public FloorMapPanel(Member member) {
        this.member = member;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
        loadData();
    }

    // ══════════════════════════════════════════════════════
    //  INIT
    // ══════════════════════════════════════════════════════
    private void initUI() {
        // Top bar
        add(buildTopBar(), BorderLayout.NORTH);

        // Left: calendar + time slots
        JPanel left = new JPanel(new BorderLayout(0, 12));
        left.setBackground(new Color(248, 250, 252));
        left.setBorder(new EmptyBorder(15, 15, 15, 10));
        left.setPreferredSize(new Dimension(200, 0));
        left.add(buildCalendar(), BorderLayout.NORTH);
        timeSlotsPanel = new JPanel();
        timeSlotsPanel.setLayout(new BoxLayout(timeSlotsPanel, BoxLayout.Y_AXIS));
        timeSlotsPanel.setBackground(new Color(248, 250, 252));
        JScrollPane tsScroll = new JScrollPane(timeSlotsPanel);
        tsScroll.setBorder(null);
        JPanel tsWrapper = new JPanel(new BorderLayout());
        tsWrapper.setBackground(new Color(248, 250, 252));
        JLabel tsTitle = new JLabel("Time Slots");
        tsTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tsTitle.setForeground(Color.GRAY);
        tsTitle.setBorder(new EmptyBorder(10, 0, 6, 0));
        tsWrapper.add(tsTitle, BorderLayout.NORTH);
        tsWrapper.add(tsScroll, BorderLayout.CENTER);
        left.add(tsWrapper, BorderLayout.CENTER);
        add(left, BorderLayout.WEST);

        // Center: map canvas
        mapCanvas = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        mapCanvas.setBackground(Color.WHITE);
        mapCanvas.setBorder(new EmptyBorder(15, 15, 15, 15));
        JScrollPane mapScroll = new JScrollPane(mapCanvas);
        mapScroll.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240)));
        add(mapScroll, BorderLayout.CENTER);

        // Right: quick booking
        add(buildQuickBooking(), BorderLayout.EAST);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 235, 240)),
            new EmptyBorder(14, 25, 14, 25)
        ));

        JLabel title = new JLabel("Browse Spaces — Interactive Floor Map");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(NAVY);
        bar.add(title, BorderLayout.WEST);

        // Toggle buttons + legend
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);

        JButton desksBtn = toggleBtn("Desks", true);
        JButton roomsBtn = toggleBtn("Meeting Rooms", false);
        desksBtn.addActionListener(e -> { viewMode = "Desks"; renderMap(); desksBtn.setBackground(NAVY); desksBtn.setForeground(Color.WHITE); roomsBtn.setBackground(Color.WHITE); roomsBtn.setForeground(NAVY); });
        roomsBtn.addActionListener(e -> { viewMode = "Meeting Rooms"; renderMap(); roomsBtn.setBackground(NAVY); roomsBtn.setForeground(Color.WHITE); desksBtn.setBackground(Color.WHITE); desksBtn.setForeground(NAVY); });

        right.add(desksBtn);
        right.add(roomsBtn);

        // Legend
        right.add(legendDot(AVAILABLE, "Available"));
        right.add(legendDot(BOOKED, "Booked"));
        right.add(legendDot(UNAVAIL, "Unavailable"));

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton toggleBtn(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(130, 32));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.putClientProperty("FlatLaf.style", "arc: 8");
        if (active) { b.setBackground(NAVY); b.setForeground(Color.WHITE); }
        else { b.setBackground(Color.WHITE); b.setForeground(NAVY); b.setBorder(BorderFactory.createLineBorder(NAVY)); }
        return b;
    }

    private JPanel legendDot(Color c, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel dot = new JLabel("●");
        dot.setForeground(c);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        p.add(dot);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(lbl);
        return p;
    }

    // ══════════════════════════════════════════════════════
    //  MINI CALENDAR
    // ══════════════════════════════════════════════════════
    private JPanel buildCalendar() {
        JPanel cal = new JPanel(new BorderLayout(0, 4));
        cal.setOpaque(false);

        // Month nav
        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        JButton prev = navBtn("‹");
        JButton next = navBtn("›");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        prev.addActionListener(e -> { selectedDate = selectedDate.minusMonths(1).withDayOfMonth(1); refreshCalendar(); });
        next.addActionListener(e -> { selectedDate = selectedDate.plusMonths(1).withDayOfMonth(1); refreshCalendar(); });
        nav.add(prev, BorderLayout.WEST);
        nav.add(monthLabel, BorderLayout.CENTER);
        nav.add(next, BorderLayout.EAST);
        cal.add(nav, BorderLayout.NORTH);

        // Day headers
        JPanel headers = new JPanel(new GridLayout(1, 7, 0, 0));
        headers.setOpaque(false);
        for (String d : new String[]{"Su","Mo","Tu","We","Th","Fr","Sa"}) {
            JLabel h = new JLabel(d, SwingConstants.CENTER);
            h.setFont(new Font("Segoe UI", Font.BOLD, 10));
            h.setForeground(Color.GRAY);
            headers.add(h);
        }
        cal.add(headers, BorderLayout.CENTER);

        calGrid = new JPanel(new GridLayout(0, 7, 2, 2));
        calGrid.setOpaque(false);
        cal.add(calGrid, BorderLayout.SOUTH);

        refreshCalendar();
        return cal;
    }

    private void refreshCalendar() {
        LocalDate first = selectedDate.withDayOfMonth(1);
        monthLabel.setText(first.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH)
            + " " + first.getYear());
        calGrid.removeAll();

        int startDow = first.getDayOfWeek().getValue() % 7; // Sun=0
        for (int i = 0; i < startDow; i++) calGrid.add(new JLabel());

        int days = first.lengthOfMonth();
        LocalDate today = LocalDate.now();
        for (int d = 1; d <= days; d++) {
            LocalDate date = first.withDayOfMonth(d);
            JButton btn = new JButton(String.valueOf(d));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setPreferredSize(new Dimension(24, 24));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (date.equals(today) || date.equals(selectedDate)) {
                btn.setBackground(NAVY);
                btn.setForeground(Color.WHITE);
                btn.setOpaque(true);
            } else {
                btn.setContentAreaFilled(false);
                btn.setForeground(Color.DARK_GRAY);
            }
            final LocalDate fd = date;
            btn.addActionListener(e -> { selectedDate = fd; refreshCalendar(); loadData(); });
            calGrid.add(btn);
        }
        calGrid.revalidate();
        calGrid.repaint();
    }

    private JButton navBtn(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setForeground(NAVY);
        return b;
    }

    // ══════════════════════════════════════════════════════
    //  QUICK BOOKING PANEL (RIGHT)
    // ══════════════════════════════════════════════════════
    private JPanel buildQuickBooking() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(270, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 235, 240)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

        JLabel qbTitle = new JLabel("Quick Booking");
        qbTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        qbTitle.setForeground(NAVY);
        form.add(qbTitle);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        // Selected space label
        selectedRoomLabel = new JLabel("No space selected");
        selectedRoomLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        selectedRoomLabel.setForeground(Color.GRAY);
        form.add(selectedRoomLabel);
        form.add(Box.createRigidArea(new Dimension(0, 8)));

        // Space selector
        form.add(fieldLabel("Select Space:"));
        spaceCombo = new JComboBox<>();
        spaceCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        spaceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(spaceCombo);
        form.add(Box.createRigidArea(new Dimension(0, 12)));

        // Duration
        form.add(fieldLabel("Duration (minutes):"));
        durationSpinner = new JSpinner(new SpinnerNumberModel(60, 30, 480, 30));
        durationSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(durationSpinner);
        form.add(Box.createRigidArea(new Dimension(0, 12)));

        // Member name (optional walk-in)
        form.add(fieldLabel("Member Name:"));
        memberNameField = new JTextField();
        memberNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        memberNameField.putClientProperty("FlatLaf.placeholderText", "Member Name");
        memberNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(memberNameField);
        JLabel walkInNote = new JLabel("For walk-in or bulk allocation");
        walkInNote.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        walkInNote.setForeground(Color.GRAY);
        form.add(walkInNote);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        // Summary
        JLabel sumTitle = new JLabel("Summary");
        sumTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        form.add(sumTitle);
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        summaryLabel = new JLabel("<html><font color='gray'>Select a space tile on the map<br>to begin booking.</font></html>");
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(summaryLabel);

        panel.add(form, BorderLayout.CENTER);

        // Confirm button
        JButton confirmBtn = new JButton("CONFIRM BOOKING");
        confirmBtn.setBackground(NAVY);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        confirmBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        confirmBtn.putClientProperty("FlatLaf.style", "arc: 0");
        confirmBtn.addActionListener(e -> handleQuickBook());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottom.add(confirmBtn, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.DARK_GRAY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    // ══════════════════════════════════════════════════════
    //  DATA LOADING
    // ══════════════════════════════════════════════════════
    private void loadData() {
        bookedRoomIds.clear();
        try {
            RoomDAO roomDAO = new RoomDAO();
            allRooms = roomDAO.getAllRooms();

            BookingDAO bookingDAO = new BookingDAO();
            List<Booking> dayBookings = new ArrayList<>();
            // Get all bookings and filter for selected date
            for (Booking b : bookingDAO.getAllBookings()) {
                if (b.getStatus().equals("CONFIRMED") || b.getStatus().equals("PENDING")) {
                    LocalDate bookDate = b.getStartTime().toLocalDate();
                    if (bookDate.equals(selectedDate)) {
                        bookedRoomIds.add(b.getRoomId());
                        dayBookings.add(b);
                    }
                }
            }

            refreshSpaceCombo();
            renderMap();
            renderTimeSlots(dayBookings);

        } catch (Exception ex) {
            mapCanvas.removeAll();
            JLabel err = new JLabel("Could not load spaces: " + ex.getMessage());
            err.setForeground(Color.RED);
            mapCanvas.add(err);
            mapCanvas.revalidate();
        }
    }

    private void refreshSpaceCombo() {
        spaceCombo.removeAllItems();
        for (Room r : allRooms) {
            if (!bookedRoomIds.contains(r.getRoomId())) {
                spaceCombo.addItem(r.getRoomName() + " (" + r.getRoomType() + ") [#" + r.getRoomId() + "]");
            }
        }
    }

    // ══════════════════════════════════════════════════════
    //  FLOOR MAP RENDER
    // ══════════════════════════════════════════════════════
    private void renderMap() {
        mapCanvas.removeAll();

        List<Room> filtered = new ArrayList<>();
        for (Room r : allRooms) {
            if (viewMode.equals("Desks") && r.getRoomType().equals("HOT_DESK")) filtered.add(r);
            else if (viewMode.equals("Meeting Rooms") && !r.getRoomType().equals("HOT_DESK")) filtered.add(r);
        }

        if (filtered.isEmpty()) {
            JLabel none = new JLabel(viewMode.equals("Desks")
                ? "No hot desks found. Admin can add spaces from the Admin Dashboard."
                : "No meeting/private rooms found.");
            none.setForeground(Color.GRAY);
            none.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            mapCanvas.add(none);
        }

        for (Room r : filtered) {
            mapCanvas.add(buildTile(r));
        }

        mapCanvas.revalidate();
        mapCanvas.repaint();
    }

    private JPanel buildTile(Room room) {
        boolean isBooked = bookedRoomIds.contains(room.getRoomId());
        boolean isMeetingRoom = !room.getRoomType().equals("HOT_DESK");

        int w = isMeetingRoom ? 140 : 80;
        int h = isMeetingRoom ? 90 : 80;

        JPanel tile = new JPanel(new GridBagLayout());
        tile.setPreferredSize(new Dimension(w, h));
        tile.setMaximumSize(new Dimension(w, h));
        tile.setBackground(isBooked ? BOOKED : (isMeetingRoom ? ROOM_BG : AVAILABLE));
        tile.setCursor(isBooked ? Cursor.getDefaultCursor()
            : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boolean isSelected = selectedRoom != null && selectedRoom.getRoomId() == room.getRoomId();
        tile.setBorder(isSelected
            ? BorderFactory.createLineBorder(SEL_BORDER, 3)
            : BorderFactory.createLineBorder(new Color(180, 190, 200), 1));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0;

        // Room number / short name
        String display = isMeetingRoom ? room.getRoomName() : String.valueOf(room.getRoomId());
        JLabel numLabel = new JLabel(display, SwingConstants.CENTER);
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, isMeetingRoom ? 12 : 18));
        numLabel.setForeground(isBooked ? Color.WHITE : (isMeetingRoom ? NAVY : Color.WHITE));
        tile.add(numLabel, g);

        g.gridy = 1;
        g.insets = new Insets(4, 2, 0, 2);
        String subText = isMeetingRoom ? "Cap: " + room.getCapacity() : "Desk " + room.getRoomId();
        JLabel subLabel = new JLabel(subText, SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLabel.setForeground(isBooked ? new Color(255, 220, 220) : new Color(40, 40, 80));
        tile.add(subLabel, g);

        if (!isBooked) {
            tile.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { selectRoom(room); }
                @Override public void mouseEntered(MouseEvent e) {
                    if (selectedRoom == null || selectedRoom.getRoomId() != room.getRoomId())
                        tile.setBorder(BorderFactory.createLineBorder(NAVY, 2));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (selectedRoom == null || selectedRoom.getRoomId() != room.getRoomId())
                        tile.setBorder(BorderFactory.createLineBorder(new Color(180, 190, 200), 1));
                }
            });
        }

        return tile;
    }

    private void selectRoom(Room room) {
        selectedRoom = room;
        // Update combo
        for (int i = 0; i < spaceCombo.getItemCount(); i++) {
            String item = (String) spaceCombo.getItemAt(i);
            if (item.contains("[#" + room.getRoomId() + "]")) {
                spaceCombo.setSelectedIndex(i);
                break;
            }
        }
        // Update labels
        selectedRoomLabel.setText("Selected: " + room.getRoomName() + " (" + room.getRoomType() + ")");
        String rate = room.getRoomType().equals("HOT_DESK")
            ? "PKR " + String.format("%.0f", room.getHourlyPrice()) + "/hr"
            : "PKR " + String.format("%.0f", room.getDailyPrice()) + "/day or "
              + String.format("%.0f", room.getMonthlyPrice()) + "/month";
        summaryLabel.setText("<html><b>" + room.getRoomName() + "</b><br>"
            + "Type: " + room.getRoomType() + "<br>"
            + "Capacity: " + room.getCapacity() + "<br>"
            + "Rate: " + rate + "<br>"
            + "Date: " + selectedDate + "</html>");
        renderMap(); // re-render to show selection highlight
    }

    // ══════════════════════════════════════════════════════
    //  TIME SLOTS
    // ══════════════════════════════════════════════════════
    private void renderTimeSlots(List<Booking> bookings) {
        timeSlotsPanel.removeAll();
        if (bookings.isEmpty()) {
            JLabel none = new JLabel("No bookings today");
            none.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            none.setForeground(Color.GRAY);
            timeSlotsPanel.add(none);
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("h:mm a");
        for (Booking b : bookings) {
            JPanel slot = new JPanel(new BorderLayout());
            slot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            slot.setBackground(new Color(240, 244, 250));
            slot.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, NAVY),
                new EmptyBorder(4, 8, 4, 6)
            ));
            String dayStr = b.getStartTime().toLocalDate().equals(LocalDate.now()) ? "Today" :
                b.getStartTime().getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);
            JLabel lbl = new JLabel(dayStr + " — "
                + b.getStartTime().toLocalTime().format(fmt));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            slot.add(lbl, BorderLayout.CENTER);
            timeSlotsPanel.add(slot);
            timeSlotsPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        }
        timeSlotsPanel.revalidate();
        timeSlotsPanel.repaint();
    }

    // ══════════════════════════════════════════════════════
    //  QUICK BOOK ACTION
    // ══════════════════════════════════════════════════════
    private void handleQuickBook() {
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Please click a space tile on the map first.",
                "No Space Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int minutes = (int) durationSpinner.getValue();
        LocalDateTime start = selectedDate.atTime(LocalTime.now().withSecond(0).withNano(0));
        LocalDateTime end = start.plusMinutes(minutes);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Book " + selectedRoom.getRoomName() + " for " + minutes + " minutes?\n"
            + "From: " + start.toLocalTime() + " → " + end.toLocalTime(),
            "Confirm Quick Booking", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            new CostPreviewFrame(member, selectedRoom.getRoomId(),
                selectedRoom.getRoomName(), selectedRoom.getRoomType(),
                "HOURLY", start, end,
                (JFrame) SwingUtilities.getWindowAncestor(this))
                .setVisible(true);
            selectedRoom = null;
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════
    //  WrapLayout helper (wraps tiles like FlowLayout)
    // ══════════════════════════════════════════════════════
    private static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                int width = 0, height = 0, rowWidth = 0, rowHeight = 0;
                int nmembers = target.getComponentCount();
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            width = Math.max(width, rowWidth);
                            height += rowHeight + vgap;
                            rowWidth = 0; rowHeight = 0;
                        }
                        rowWidth += d.width + hgap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                width = Math.max(width, rowWidth);
                height += rowHeight + insets.top + insets.bottom + vgap * 2;
                return new Dimension(width, height);
            }
        }
    }
}
