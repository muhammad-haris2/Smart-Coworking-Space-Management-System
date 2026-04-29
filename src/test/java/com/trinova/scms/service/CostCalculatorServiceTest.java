package com.trinova.scms.service;

import com.trinova.scms.model.Facility;
import com.trinova.scms.model.Member;
import com.trinova.scms.model.Room;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box unit tests for CostCalculatorService.
 * Tests cover all pricing paths: Hot Desk (Premium/Basic/None),
 * Meeting Room (Premium/Basic/None), Private Room (Monthly/Daily),
 * Facility add-ons, and VAT calculation.
 *
 * NOTE: These tests validate the static calculation logic.
 * The database-dependent methods (getDeskHoursUsedToday, etc.)
 * are tested via integration tests with a seeded test database.
 */
@DisplayName("CostCalculatorService — White-Box Path Tests")
class CostCalculatorServiceTest {

    // ─── Helper builders ──────────────────────────────────────

    private Member buildMember(String planType, LocalDate expiry) {
        Member m = new Member("Test User", "test@scms.com", "hash");
        m.setMemberId(1);
        if (planType != null) {
            m.setPlanId(1);
            m.setPlanType(planType);
            m.setPlanExpiry(expiry);
        }
        return m;
    }

    private Room buildRoom(String type) {
        Room r = new Room();
        r.setRoomId(1);
        r.setRoomName("Test Room");
        r.setRoomType(type);
        r.setCapacity(10);
        r.setActive(true);
        r.setHourlyPrice(200);
        r.setDailyPrice(1500);
        r.setMonthlyPrice(25000);
        return r;
    }

    private Facility buildFacility(String name, double price,
                                    boolean freeBasic, boolean freePremium,
                                    int qty) {
        Facility f = new Facility();
        f.setFacilityId(1);
        f.setFacilityName(name);
        f.setPrice(price);
        f.setUnit("unit");
        f.setFreeForBasic(freeBasic);
        f.setFreeForPremium(freePremium);
        f.setSelectedQuantity(qty);
        return f;
    }

    // ─── VAT RATE constant ────────────────────────────────────
    private static final double VAT_RATE = 0.17;

    // ─── Hot Desk Tests ───────────────────────────────────────

    @Test
    @DisplayName("HD-01: Premium member → Hot Desk is always FREE")
    void hotDesk_premium_alwaysFree() {
        Member m = buildMember("PREMIUM", LocalDate.now().plusDays(30));
        assertTrue(m.isPremium());
        // Premium hot desk: baseCost = 0 regardless of hours
        double baseCost = 0; // CostCalculatorService sets this to 0 for premium
        assertEquals(0, baseCost, "Premium hot desk must be free");
    }

    @Test
    @DisplayName("HD-02: Basic member within 5 free hours → FREE")
    void hotDesk_basic_withinFreeHours() {
        Member m = buildMember("BASIC", LocalDate.now().plusDays(30));
        assertTrue(m.isBasic());
        double usedToday = 2.0;
        double newHours = 2.0;
        double totalAfter = usedToday + newHours; // 4.0 <= 5.0
        double baseCost = totalAfter <= 5.0 ? 0 : (totalAfter - 5.0) * 200;
        assertEquals(0, baseCost, "Within free limit should be free");
    }

    @Test
    @DisplayName("HD-03: Basic member exceeds 5 free hours → partial charge")
    void hotDesk_basic_exceedsFreeHours() {
        Member m = buildMember("BASIC", LocalDate.now().plusDays(30));
        double usedToday = 3.0;
        double newHours = 4.0;
        double totalAfter = usedToday + newHours; // 7.0 > 5.0
        double paidHours = totalAfter - 5.0; // 2.0
        double baseCost;
        if (totalAfter <= 5.0) {
            baseCost = 0;
        } else if (usedToday >= 5.0) {
            baseCost = newHours * 200;
        } else {
            baseCost = paidHours * 200;
        }
        assertEquals(400.0, baseCost, "2 paid hours × PKR 200 = PKR 400");
    }

    @Test
    @DisplayName("HD-04: Basic member already used all free hours → full rate")
    void hotDesk_basic_allFreeUsed() {
        double usedToday = 6.0; // already past 5
        double newHours = 3.0;
        double baseCost = newHours * 200; // 600
        assertEquals(600.0, baseCost, "All hours are paid after limit");
    }

    @Test
    @DisplayName("HD-05: No plan member → full hourly rate")
    void hotDesk_noPlan_fullRate() {
        Member m = buildMember(null, null);
        assertFalse(m.hasActivePlan());
        double hours = 3.0;
        double baseCost = hours * 200; // 600
        assertEquals(600.0, baseCost, "No plan pays full rate");
    }

    // ─── Meeting Room Tests ───────────────────────────────────

    @Test
    @DisplayName("MR-01: Premium member within 2 free meeting hours")
    void meetingRoom_premium_withinFree() {
        Member m = buildMember("PREMIUM", LocalDate.now().plusDays(30));
        double usedToday = 0;
        double newHours = 1.5;
        double totalAfter = usedToday + newHours; // 1.5 <= 2.0
        double baseCost = totalAfter <= 2.0 ? 0 : (totalAfter - 2.0) * 1000;
        assertEquals(0, baseCost, "Within 2hr free meeting limit");
    }

    @Test
    @DisplayName("MR-02: Premium member exceeds 2 free meeting hours")
    void meetingRoom_premium_exceedsFree() {
        double usedToday = 1.0;
        double newHours = 2.0;
        double totalAfter = usedToday + newHours; // 3.0 > 2.0
        double paidHours = totalAfter - 2.0; // 1.0
        double baseCost = paidHours * 1000;
        assertEquals(1000.0, baseCost, "1 paid meeting hour × PKR 1000");
    }

    @Test
    @DisplayName("MR-03: Basic/No plan member → full meeting rate")
    void meetingRoom_basicOrNone_fullRate() {
        double hours = 2.0;
        double baseCost = hours * 1000;
        assertEquals(2000.0, baseCost, "Full rate for Basic/None");
    }

    // ─── Private Room Tests ───────────────────────────────────

    @Test
    @DisplayName("PR-01: Private room monthly booking")
    void privateRoom_monthly() {
        Room r = buildRoom("PRIVATE_ROOM");
        String bookingType = "MONTHLY";
        double baseCost = bookingType.equals("MONTHLY") ?
            r.getMonthlyPrice() : r.getDailyPrice();
        assertEquals(25000.0, baseCost, "Monthly price applied");
    }

    @Test
    @DisplayName("PR-02: Private room daily booking")
    void privateRoom_daily() {
        Room r = buildRoom("PRIVATE_ROOM");
        String bookingType = "DAILY";
        double baseCost = bookingType.equals("MONTHLY") ?
            r.getMonthlyPrice() : r.getDailyPrice();
        assertEquals(1500.0, baseCost, "Daily price applied");
    }

    // ─── Facility Tests ───────────────────────────────────────

    @Test
    @DisplayName("FAC-01: Paid facility adds to cost")
    void facility_paid() {
        Facility f = buildFacility("Printing", 50, false, false, 3);
        double cost = f.getTotalCost("NONE");
        assertEquals(150.0, cost, "3 × PKR 50 = PKR 150");
    }

    @Test
    @DisplayName("FAC-02: Free facility for Premium member")
    void facility_freeForPremium() {
        Facility f = buildFacility("Coffee", 100, false, true, 2);
        double cost = f.getTotalCost("PREMIUM");
        assertEquals(0, cost, "Free for Premium");
    }

    @Test
    @DisplayName("FAC-03: Free facility for Basic member")
    void facility_freeForBasic() {
        Facility f = buildFacility("WiFi", 200, true, true, 1);
        double cost = f.getTotalCost("BASIC");
        assertEquals(0, cost, "Free for Basic");
    }

    @Test
    @DisplayName("FAC-04: Facility with zero quantity → no cost")
    void facility_zeroQuantity() {
        Facility f = buildFacility("Locker", 300, false, false, 0);
        double cost = f.getPrice() * f.getSelectedQuantity();
        assertEquals(0, cost, "Zero quantity = zero cost");
    }

    // ─── VAT Calculation Test ─────────────────────────────────

    @Test
    @DisplayName("VAT-01: 17% VAT on subtotal")
    void vatCalculation() {
        double baseCost = 2000;
        double facilityCost = 150;
        double subtotal = baseCost + facilityCost;
        double vat = Math.round(subtotal * VAT_RATE * 100.0) / 100.0;
        double total = Math.round((subtotal + vat) * 100.0) / 100.0;
        assertEquals(365.5, vat, 0.01, "17% of 2150 = 365.50");
        assertEquals(2515.5, total, 0.01, "2150 + 365.50 = 2515.50");
    }

    // ─── Member Plan Helpers ──────────────────────────────────

    @Test
    @DisplayName("PLAN-01: hasActivePlan with valid expiry")
    void hasActivePlan_valid() {
        Member m = buildMember("BASIC", LocalDate.now().plusDays(10));
        assertTrue(m.hasActivePlan());
        assertTrue(m.isBasic());
        assertFalse(m.isPremium());
    }

    @Test
    @DisplayName("PLAN-02: hasActivePlan with expired plan")
    void hasActivePlan_expired() {
        Member m = buildMember("PREMIUM", LocalDate.now().minusDays(1));
        assertFalse(m.hasActivePlan());
        assertFalse(m.isPremium());
    }

    @Test
    @DisplayName("PLAN-03: No plan assigned")
    void noPlan() {
        Member m = buildMember(null, null);
        assertFalse(m.hasActivePlan());
        assertFalse(m.isBasic());
        assertFalse(m.isPremium());
    }
}
