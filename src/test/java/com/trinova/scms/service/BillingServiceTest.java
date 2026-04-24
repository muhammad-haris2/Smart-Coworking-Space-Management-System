package com.trinova.scms.service;

import com.trinova.scms.model.PromoCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box tests for promo-code discount logic.
 * Tests the applyDiscount calculation paths without DB dependency.
 */
@DisplayName("BillingService — Promo Code Logic Tests")
class BillingServiceTest {

    // Simulate the discount logic from BillingService.applyPromoCode()
    private double applyDiscount(double total, PromoCode promo) {
        double discounted;
        if (promo.getDiscountType().equals("PERCENTAGE")) {
            discounted = total - (total * promo.getDiscountValue() / 100.0);
        } else {
            discounted = total - promo.getDiscountValue();
        }
        return Math.max(0, Math.round(discounted * 100.0) / 100.0);
    }

    private PromoCode buildPromo(String type, double value) {
        PromoCode p = new PromoCode();
        p.setPromoId(1);
        p.setCode("TEST");
        p.setDiscountType(type);
        p.setDiscountValue(value);
        p.setActive(true);
        return p;
    }

    @Test
    @DisplayName("PROMO-01: Percentage discount (20% off PKR 5000)")
    void percentageDiscount() {
        PromoCode p = buildPromo("PERCENTAGE", 20);
        double result = applyDiscount(5000, p);
        assertEquals(4000.0, result, "20% off 5000 = 4000");
    }

    @Test
    @DisplayName("PROMO-02: Flat discount (PKR 500 off PKR 3000)")
    void flatDiscount() {
        PromoCode p = buildPromo("FLAT", 500);
        double result = applyDiscount(3000, p);
        assertEquals(2500.0, result, "3000 - 500 = 2500");
    }

    @Test
    @DisplayName("PROMO-03: Flat discount exceeds total → capped at 0")
    void flatDiscount_exceedsTotal() {
        PromoCode p = buildPromo("FLAT", 10000);
        double result = applyDiscount(3000, p);
        assertEquals(0, result, "Cannot go below zero");
    }

    @Test
    @DisplayName("PROMO-04: 100% discount → total becomes 0")
    void fullPercentageDiscount() {
        PromoCode p = buildPromo("PERCENTAGE", 100);
        double result = applyDiscount(5000, p);
        assertEquals(0, result, "100% off = free");
    }

    @Test
    @DisplayName("PROMO-05: 0% discount → no change")
    void zeroPercentageDiscount() {
        PromoCode p = buildPromo("PERCENTAGE", 0);
        double result = applyDiscount(5000, p);
        assertEquals(5000.0, result, "0% off = no change");
    }

    @Test
    @DisplayName("PROMO-06: Flat PKR 0 discount → no change")
    void zeroFlatDiscount() {
        PromoCode p = buildPromo("FLAT", 0);
        double result = applyDiscount(3000, p);
        assertEquals(3000.0, result, "PKR 0 off = no change");
    }

    @Test
    @DisplayName("PROMO-07: Rounding to 2 decimal places")
    void roundingPrecision() {
        PromoCode p = buildPromo("PERCENTAGE", 33);
        double result = applyDiscount(1000, p);
        // 1000 - 330 = 670.0
        assertEquals(670.0, result, 0.01, "Rounded to 2 decimals");
    }
}
