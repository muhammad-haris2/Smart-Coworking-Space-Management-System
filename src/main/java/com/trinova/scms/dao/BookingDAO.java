package com.trinova.scms.dao;

import com.trinova.scms.model.Booking;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private Connection conn;

    public BookingDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public int create(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings (member_id, room_id, start_time, end_time, status) " +
                     "VALUES (?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getMemberId());
            ps.setInt(2, b.getRoomId());
            ps.setTimestamp(3, Timestamp.valueOf(b.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(b.getEndTime()));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public boolean hasConflict(int roomId,
                               LocalDateTime start,
                               LocalDateTime end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings " +
                     "WHERE room_id = ? AND status = 'ACTIVE' " +
                     "AND start_time < ? AND end_time > ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setTimestamp(3, Timestamp.valueOf(start));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public List<Booking> getBookingsByMember(int memberId) throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, r.room_name, m.full_name " +
                     "FROM bookings b " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "JOIN members m ON b.member_id = m.member_id " +
                     "WHERE b.member_id = ? " +
                     "ORDER BY b.start_time DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, r.room_name, m.full_name " +
                     "FROM bookings b " +
                     "JOIN rooms r ON b.room_id = r.room_id " +
                     "JOIN members m ON b.member_id = m.member_id " +
                     "ORDER BY b.start_time DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void cancel(int bookingId, String reason) throws SQLException {
        String sql = "UPDATE bookings SET status = 'CANCELLED', " +
                     "cancel_reason = ? WHERE booking_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        }
    }

    public void complete(int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET status = 'COMPLETED' " +
                     "WHERE booking_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setMemberId(rs.getInt("member_id"));
        b.setRoomId(rs.getInt("room_id"));
        b.setRoomName(rs.getString("room_name"));
        b.setMemberName(rs.getString("full_name"));
        b.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        b.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        b.setStatus(rs.getString("status"));
        b.setCancelReason(rs.getString("cancel_reason"));
        return b;
    }
}