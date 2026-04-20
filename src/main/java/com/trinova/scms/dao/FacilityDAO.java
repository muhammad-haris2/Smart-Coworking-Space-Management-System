package com.trinova.scms.dao;

import com.trinova.scms.model.Facility;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacilityDAO {

    private Connection conn;

    public FacilityDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public List<Facility> getAllFacilities() throws SQLException {
        List<Facility> list = new ArrayList<>();
        String sql = "SELECT * FROM facilities ORDER BY facility_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void saveBookingFacilities(int bookingId,
            List<Facility> facilities,
            String planType) throws SQLException {
        String sql = "INSERT INTO booking_facilities " +
                     "(booking_id, facility_id, quantity, " +
                     "unit_price, total_price) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Facility f : facilities) {
                if (f.getSelectedQuantity() <= 0) continue;
                double unitPrice = f.getTotalCost(planType) == 0
                    ? 0 : f.getPrice();
                double totalPrice = f.getTotalCost(planType);
                ps.setInt(1, bookingId);
                ps.setInt(2, f.getFacilityId());
                ps.setInt(3, f.getSelectedQuantity());
                ps.setDouble(4, unitPrice);
                ps.setDouble(5, totalPrice);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private Facility mapRow(ResultSet rs) throws SQLException {
        Facility f = new Facility();
        f.setFacilityId(rs.getInt("facility_id"));
        f.setFacilityName(rs.getString("facility_name"));
        f.setPrice(rs.getDouble("price"));
        f.setUnit(rs.getString("unit"));
        f.setFreeForBasic(rs.getBoolean("free_for_basic"));
        f.setFreeForPremium(rs.getBoolean("free_for_premium"));
        return f;
    }
}