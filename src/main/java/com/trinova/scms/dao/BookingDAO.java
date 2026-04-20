package com.trinova.scms.dao;

import com.trinova.scms.model.Booking;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private Connection conn;

    public BookingDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public int create(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings " +
                     "(member_id, room_id, booking_type, " +
                     "start_time, end_time, duration_hours, " +
                     "base_cost, facility_cost, vat_amount, " +
                     "total_cost, status) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,'CONFIRMED')";
        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getMemberId());
            ps.setInt(2, b.getRoomId());
            ps.setString(3, b.getBookingType());
            ps.setTimestamp(4,
                Timestamp.valueOf(b.getStartTime()));
            ps.setTimestamp(5,
                Timestamp.valueOf(b.getEndTime()));
            ps.setDouble(6, b.getDurationHours());
            ps.setDouble(7, b.getBaseCost());
            ps.setDouble(8, b.getFacilityCost());
            ps.setDouble(9, b.getVatAmount());
            ps.setDouble(10, b.getTotalCost());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public boolean hasConflict(int roomId,
                                LocalDateTime start,
                                LocalDateTime end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings " +
                     "WHERE room_id = ? " +
                     "AND status IN ('PENDING','CONFIRMED') " +
                     "AND start_time < ? AND end_time > ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setTimestamp(3, Timestamp.valueOf(start));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Get total desk hours used by member on a specific date
    public double getDeskHoursUsedToday(int memberId,
                                         LocalDate date) throws SQLException {
        String sql = "SELECT ISNULL(SUM(duration_hours), 0) " +
                     "FROM bookings " +
                     "WHERE member_id = ? " +
                     "AND status IN ('PENDING','CONFIRMED') " +
                     "AND CAST(start_time AS DATE) = ? " +
                     "AND room_id IN " +
                     "(SELECT room_id FROM rooms " +
                     " WHERE room_type = 'HOT_DESK')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    // Get total meeting room hours used by member on a specific date
    public double getMeetingHoursUsedToday(int memberId,
                                            LocalDate date) throws SQLException {
        String sql = "SELECT ISNULL(SUM(duration_hours), 0) " +
                     "FROM bookings " +
                     "WHERE member_id = ? " +
                     "AND status IN ('PENDING','CONFIRMED') " +
                     "AND CAST(start_time AS DATE) = ? " +
                     "AND room_id IN " +
                     "(SELECT room_id FROM rooms " +
                     " WHERE room_type = 'MEETING_ROOM')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    public List<Booking> getBookingsByMember(int memberId)
            throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, r.room_name, r.room_type, " +
                     "m.full_name " +
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
        String sql = "SELECT b.*, r.room_name, r.room_type, " +
                     "m.full_name " +
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

    public void cancel(int bookingId,
                       String reason) throws SQLException {
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
        b.setRoomType(rs.getString("room_type"));
        b.setMemberName(rs.getString("full_name"));
        b.setBookingType(rs.getString("booking_type"));
        b.setStartTime(
            rs.getTimestamp("start_time").toLocalDateTime());
        b.setEndTime(
            rs.getTimestamp("end_time").toLocalDateTime());
        b.setDurationHours(rs.getDouble("duration_hours"));
        b.setBaseCost(rs.getDouble("base_cost"));
        b.setFacilityCost(rs.getDouble("facility_cost"));
        b.setVatAmount(rs.getDouble("vat_amount"));
        b.setTotalCost(rs.getDouble("total_cost"));
        b.setStatus(rs.getString("status"));
        b.setCancelReason(rs.getString("cancel_reason"));
        return b;
    }
}