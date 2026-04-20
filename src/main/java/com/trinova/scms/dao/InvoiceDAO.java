package com.trinova.scms.dao;

import com.trinova.scms.model.Invoice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    private Connection conn;

    public InvoiceDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public int create(Invoice inv) throws SQLException {
        String sql = "INSERT INTO invoices " +
                     "(booking_id, member_id, base_amount, " +
                     "facility_cost, vat_amount, total_amount, issue_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, inv.getBookingId());
            ps.setInt(2, inv.getMemberId());
            ps.setDouble(3, inv.getBaseAmount());
            ps.setDouble(4, inv.getFacilityCost());
            ps.setDouble(5, inv.getVatAmount());
            ps.setDouble(6, inv.getTotalAmount());
            ps.setDate(7, Date.valueOf(inv.getIssueDate()));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public void updatePdfPath(int invoiceId, String path) throws SQLException {
        String sql = "UPDATE invoices SET pdf_path = ? WHERE invoice_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setInt(2, invoiceId);
            ps.executeUpdate();
        }
    }

    public List<Invoice> getByMember(int memberId) throws SQLException {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, m.full_name, r.room_name, b.booking_type " +
                     "FROM invoices i " +
                     "JOIN members m ON i.member_id = m.member_id " +
                     "JOIN bookings b ON i.booking_id = b.booking_id " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "WHERE i.member_id = ? ORDER BY i.issue_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Invoice> getAll() throws SQLException {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, m.full_name, r.room_name, b.booking_type " +
                     "FROM invoices i " +
                     "JOIN members m ON i.member_id = m.member_id " +
                     "JOIN bookings b ON i.booking_id = b.booking_id " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "ORDER BY i.issue_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Invoice findById(int invoiceId) throws SQLException {
        String sql = "SELECT i.*, m.full_name, r.room_name, b.booking_type " +
                     "FROM invoices i " +
                     "JOIN members m ON i.member_id = m.member_id " +
                     "JOIN bookings b ON i.booking_id = b.booking_id " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "WHERE i.invoice_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    private Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice inv = new Invoice();
        inv.setInvoiceId(rs.getInt("invoice_id"));
        inv.setBookingId(rs.getInt("booking_id"));
        inv.setMemberId(rs.getInt("member_id"));
        inv.setBaseAmount(rs.getDouble("base_amount"));
        inv.setFacilityCost(rs.getDouble("facility_cost"));
        inv.setVatAmount(rs.getDouble("vat_amount"));
        inv.setTotalAmount(rs.getDouble("total_amount"));
        inv.setIssueDate(rs.getDate("issue_date").toLocalDate());
        inv.setPdfPath(rs.getString("pdf_path"));
        inv.setMemberName(rs.getString("full_name"));
        inv.setRoomName(rs.getString("room_name"));
        inv.setBookingType(rs.getString("booking_type"));
        return inv;
    }
}