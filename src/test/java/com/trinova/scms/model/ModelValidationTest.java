package com.trinova.scms.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for model classes: Member, Room, Booking, Facility, PromoCode.
 * Validates getters, setters, helper methods, and toString behaviour.
 */
@DisplayName("Model Validation Tests")
class ModelValidationTest {

    // ─── Member ───────────────────────────────────────────────

    @Test
    @DisplayName("MEM-01: Member constructor sets defaults")
    void memberDefaults() {
        Member m = new Member("Ali", "ali@test.com", "hash123");
        assertEquals("Ali", m.getFullName());
        assertEquals("ali@test.com", m.getEmail());
        assertEquals("MEMBER", m.getRole());
        assertFalse(m.isLocked());
        assertEquals(0, m.getFailedAttempts());
    }

    @Test
    @DisplayName("MEM-02: isPremium returns true only when active")
    void memberIsPremium() {
        Member m = new Member();
        m.setPlanId(2);
        m.setPlanType("PREMIUM");
        m.setPlanExpiry(LocalDate.now().plusDays(5));
        assertTrue(m.isPremium());
        assertFalse(m.isBasic());
    }

    @Test
    @DisplayName("MEM-03: Expired plan → hasActivePlan false")
    void memberExpiredPlan() {
        Member m = new Member();
        m.setPlanId(1);
        m.setPlanType("BASIC");
        m.setPlanExpiry(LocalDate.now().minusDays(1));
        assertFalse(m.hasActivePlan());
    }

    // ─── Room ─────────────────────────────────────────────────

    @Test
    @DisplayName("ROOM-01: Room type helpers")
    void roomTypeHelpers() {
        Room r = new Room();
        r.setRoomType("HOT_DESK");
        assertTrue(r.isHotDesk());
        assertFalse(r.isMeetingRoom());
        assertFalse(r.isPrivateRoom());

        r.setRoomType("MEETING_ROOM");
        assertTrue(r.isMeetingRoom());

        r.setRoomType("PRIVATE_ROOM");
        assertTrue(r.isPrivateRoom());
    }

    @Test
    @DisplayName("ROOM-02: Room toString returns name")
    void roomToString() {
        Room r = new Room();
        r.setRoomName("Alpha Hub");
        assertEquals("Alpha Hub", r.toString());
    }

    // ─── Booking ──────────────────────────────────────────────

    @Test
    @DisplayName("BOOK-01: Booking constructor sets PENDING status")
    void bookingDefaults() {
        Booking b = new Booking(1, 2, "HOURLY",
            LocalDateTime.of(2026, 4, 25, 9, 0),
            LocalDateTime.of(2026, 4, 25, 12, 0));
        assertEquals("PENDING", b.getStatus());
        assertEquals(1, b.getMemberId());
        assertEquals(2, b.getRoomId());
    }

    @Test
    @DisplayName("BOOK-02: Booking cost fields")
    void bookingCostFields() {
        Booking b = new Booking();
        b.setBaseCost(1000);
        b.setFacilityCost(200);
        b.setVatAmount(204);
        b.setTotalCost(1404);
        assertEquals(1000, b.getBaseCost());
        assertEquals(200, b.getFacilityCost());
        assertEquals(204, b.getVatAmount());
        assertEquals(1404, b.getTotalCost());
    }

    // ─── Facility ─────────────────────────────────────────────

    @Test
    @DisplayName("FAC-01: getTotalCost respects plan type")
    void facilityTotalCost() {
        Facility f = new Facility();
        f.setPrice(100);
        f.setFreeForPremium(true);
        f.setFreeForBasic(false);
        f.setSelectedQuantity(3);

        assertEquals(0, f.getTotalCost("PREMIUM"), "Free for premium");
        assertEquals(300, f.getTotalCost("BASIC"), "Paid for basic");
        assertEquals(300, f.getTotalCost("NONE"), "Paid for no plan");
    }

    @Test
    @DisplayName("FAC-02: Facility toString format")
    void facilityToString() {
        Facility f = new Facility();
        f.setFacilityName("Printing");
        f.setPrice(50);
        f.setUnit("page");
        assertEquals("Printing (PKR 50.0/page)", f.toString());
    }

    // ─── SubscriptionPlan ─────────────────────────────────────

    @Test
    @DisplayName("SUB-01: SubscriptionPlan toString format")
    void subscriptionToString() {
        SubscriptionPlan sp = new SubscriptionPlan();
        sp.setPlanName("Premium");
        sp.setPrice(8000);
        sp.setDurationDays(30);
        assertEquals("Premium - PKR 8000 / 30 days", sp.toString());
    }

    // ─── PromoCode ────────────────────────────────────────────

    @Test
    @DisplayName("PROMO-01: PromoCode getters/setters")
    void promoCodeFields() {
        PromoCode p = new PromoCode();
        p.setPromoId(5);
        p.setCode("SAVE20");
        p.setDiscountType("PERCENTAGE");
        p.setDiscountValue(20);
        p.setActive(true);
        p.setUsageCount(10);

        assertEquals(5, p.getPromoId());
        assertEquals("SAVE20", p.getCode());
        assertEquals("PERCENTAGE", p.getDiscountType());
        assertEquals(20, p.getDiscountValue());
        assertTrue(p.isActive());
        assertEquals(10, p.getUsageCount());
    }
}
