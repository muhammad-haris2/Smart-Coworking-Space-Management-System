package com.trinova.scms.service;

import com.trinova.scms.dao.BookingDAO;
import com.trinova.scms.model.Facility;
import com.trinova.scms.model.Member;
import com.trinova.scms.model.Room;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class CostCalculatorService {

    private static final double VAT_RATE              = 0.17;
    private static final double DESK_HOURLY_RATE      = 200.0;
    private static final double MEETING_HOURLY_RATE   = 1000.0;
    private static final double BASIC_FREE_HOURS      = 5.0;
    private static final double PREMIUM_FREE_MTG_HRS  = 2.0;

    private final BookingDAO bookingDAO;

    public CostCalculatorService() throws Exception {
        this.bookingDAO = new BookingDAO();
    }

    public CostResult calculate(Member member,
                                 Room room,
                                 String bookingType,
                                 LocalDateTime start,
                                 LocalDateTime end,
                                 List<Facility> selectedFacilities)
            throws Exception {

        double hours = Duration.between(start, end)
                               .toMinutes() / 60.0;
        hours = Math.round(hours * 100.0) / 100.0;

        double baseCost     = 0;
        double facilityCost = 0;
        String planType     = member.getPlanType() != null ?
                              member.getPlanType() : "NONE";

        // ── HOT DESK ──────────────────────────────────────
        if (room.isHotDesk()) {
            if (member.isPremium()) {
                baseCost = 0;

            } else if (member.isBasic()) {
                double usedToday = bookingDAO.getDeskHoursUsedToday(
                    member.getMemberId(),
                    start.toLocalDate());
                double totalAfter = usedToday + hours;

                if (totalAfter <= BASIC_FREE_HOURS) {
                    baseCost = 0;
                } else if (usedToday >= BASIC_FREE_HOURS) {
                    baseCost = hours * DESK_HOURLY_RATE;
                } else {
                    double paidHours = totalAfter - BASIC_FREE_HOURS;
                    baseCost = paidHours * DESK_HOURLY_RATE;
                }
            } else {
                // No plan — full rate
                baseCost = hours * DESK_HOURLY_RATE;
            }
        }

        // ── MEETING ROOM ──────────────────────────────────
        else if (room.isMeetingRoom()) {
            if (member.isPremium()) {
                double usedToday =
                    bookingDAO.getMeetingHoursUsedToday(
                        member.getMemberId(),
                        start.toLocalDate());
                double totalAfter = usedToday + hours;

                if (totalAfter <= PREMIUM_FREE_MTG_HRS) {
                    baseCost = 0;
                } else if (usedToday >= PREMIUM_FREE_MTG_HRS) {
                    baseCost = hours * MEETING_HOURLY_RATE;
                } else {
                    double paidHours =
                        totalAfter - PREMIUM_FREE_MTG_HRS;
                    baseCost = paidHours * MEETING_HOURLY_RATE;
                }
            } else {
                // Basic and No plan — full rate
                baseCost = hours * MEETING_HOURLY_RATE;
            }
        }

        // ── PRIVATE ROOM ──────────────────────────────────
        else if (room.isPrivateRoom()) {
            if (bookingType.equals("MONTHLY")) {
                baseCost = room.getMonthlyPrice();
            } else if (bookingType.equals("DAILY")) {
                baseCost = room.getDailyPrice();
            }
        }

        // ── FACILITIES ────────────────────────────────────
        if (selectedFacilities != null) {
            for (Facility f : selectedFacilities) {
                if (f.getSelectedQuantity() <= 0) continue;
                facilityCost += f.getTotalCost(planType);
            }
        }

        facilityCost = Math.round(facilityCost * 100.0) / 100.0;
        baseCost     = Math.round(baseCost * 100.0) / 100.0;

        double subtotal  = baseCost + facilityCost;
        double vatAmount = Math.round(
            subtotal * VAT_RATE * 100.0) / 100.0;
        double totalCost = Math.round(
            (subtotal + vatAmount) * 100.0) / 100.0;

        return new CostResult(
            hours, baseCost, facilityCost,
            vatAmount, totalCost, planType);
    }

    // ── Result container ──────────────────────────────────
    public static class CostResult {
        public final double hours;
        public final double baseCost;
        public final double facilityCost;
        public final double vatAmount;
        public final double totalCost;
        public final String planType;

        public CostResult(double hours, double baseCost,
                          double facilityCost, double vatAmount,
                          double totalCost, String planType) {
            this.hours        = hours;
            this.baseCost     = baseCost;
            this.facilityCost = facilityCost;
            this.vatAmount    = vatAmount;
            this.totalCost    = totalCost;
            this.planType     = planType;
        }

        public String getSummary() {
            return String.format(
                "Duration: %.1f hrs | Base: PKR %.2f | " +
                "Facilities: PKR %.2f | VAT: PKR %.2f | " +
                "TOTAL: PKR %.2f",
                hours, baseCost, facilityCost,
                vatAmount, totalCost);
        }
    }
}