package com.trinova.scms.dao;

import com.trinova.scms.model.SubscriptionPlan;
import com.trinova.scms.model.PromoCode;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDAO {

    private Connection conn;

    public SubscriptionDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public List<SubscriptionPlan> getAllPlans() throws SQLException {
        List<SubscriptionPlan> list = new ArrayList<>();
        String sql = "SELECT * FROM subscription_plans ORDER BY price";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SubscriptionPlan p = new SubscriptionPlan();
                p.setPlanId(rs.getInt("plan_id"));
                p.setPlanName(rs.getString("plan_name"));
                p.setDurationDays(rs.getInt("duration_days"));
                p.setPrice(rs.getDouble("price"));
                p.setPlanType(rs.getString("plan_type"));
                list.add(p);
            }
        }
        return list;
    }

    public void assignPlan(int memberId, int planId,
                           LocalDate expiry) throws SQLException {
        String sql = "UPDATE members SET plan_id = ?, " +
                     "plan_expiry = ? WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, planId);
            ps.setDate(2, Date.valueOf(expiry));
            ps.setInt(3, memberId);
            ps.executeUpdate();
        }
    }

    public PromoCode findPromoCode(String code) throws SQLException {
        String sql = "SELECT * FROM promo_codes " +
                     "WHERE code = ? AND is_active = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim().toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PromoCode p = new PromoCode();
                p.setPromoId(rs.getInt("promo_id"));
                p.setCode(rs.getString("code"));
                p.setDiscountType(rs.getString("discount_type"));
                p.setDiscountValue(rs.getDouble("discount_value"));
                p.setActive(rs.getBoolean("is_active"));
                p.setUsageCount(rs.getInt("usage_count"));
                return p;
            }
        }
        return null;
    }

    public void incrementPromoUsage(int promoId) throws SQLException {
        String sql = "UPDATE promo_codes SET " +
                     "usage_count = usage_count + 1 " +
                     "WHERE promo_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promoId);
            ps.executeUpdate();
        }
    }

    public void addPromoCode(PromoCode p) throws SQLException {
        String sql = "INSERT INTO promo_codes " +
                     "(code, discount_type, discount_value, is_active) " +
                     "VALUES (?, ?, ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getCode().toUpperCase());
            ps.setString(2, p.getDiscountType());
            ps.setDouble(3, p.getDiscountValue());
            ps.executeUpdate();
        }
    }

    public void togglePromoCode(int promoId,
                                 boolean active) throws SQLException {
        String sql = "UPDATE promo_codes SET is_active = ? " +
                     "WHERE promo_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, promoId);
            ps.executeUpdate();
        }
    }

    public List<PromoCode> getAllPromoCodes() throws SQLException {
        List<PromoCode> list = new ArrayList<>();
        String sql = "SELECT * FROM promo_codes ORDER BY promo_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PromoCode p = new PromoCode();
                p.setPromoId(rs.getInt("promo_id"));
                p.setCode(rs.getString("code"));
                p.setDiscountType(rs.getString("discount_type"));
                p.setDiscountValue(rs.getDouble("discount_value"));
                p.setActive(rs.getBoolean("is_active"));
                p.setUsageCount(rs.getInt("usage_count"));
                list.add(p);
            }
        }
        return list;
    }
}