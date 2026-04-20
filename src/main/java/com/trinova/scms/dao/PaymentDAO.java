package com.trinova.scms.dao;

import com.trinova.scms.model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    private Connection conn;

    public PaymentDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public int createBookingPayment(int memberId,
                                     int bookingId,
                                     double amount,
                                     String method,
                                     String txnRef) throws SQLException {
        String sql = "INSERT INTO payments " +
                     "(member_id, booking_id, amount, " +
                     "payment_method, status, transaction_ref) " +
                     "VALUES (?, ?, ?, ?, 'COMPLETED', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            ps.setInt(2, bookingId);
            ps.setDouble(3, amount);
            ps.setString(4, method);
            ps.setString(5, txnRef);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public int createSubscriptionPayment(int memberId,
                                          int planId,
                                          double amount,
                                          String method,
                                          String txnRef) throws SQLException {
        String sql = "INSERT INTO payments " +
                     "(member_id, subscription_id, amount, " +
                     "payment_method, status, transaction_ref) " +
                     "VALUES (?, ?, ?, ?, 'COMPLETED', ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, memberId);
            ps.setInt(2, planId);
            ps.setDouble(3, amount);
            ps.setString(4, method);
            ps.setString(5, txnRef);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public List<Payment> getByMember(int memberId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments " +
                     "WHERE member_id = ? " +
                     "ORDER BY paid_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Payment> getAll() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY paid_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setMemberId(rs.getInt("member_id"));
        p.setBookingId(rs.getInt("booking_id"));
        p.setSubscriptionId(rs.getInt("subscription_id"));
        p.setAmount(rs.getDouble("amount"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setStatus(rs.getString("status"));
        p.setTransactionRef(rs.getString("transaction_ref"));
        Timestamp ts = rs.getTimestamp("paid_at");
        if (ts != null) p.setPaidAt(ts.toLocalDateTime());
        return p;
    }
}