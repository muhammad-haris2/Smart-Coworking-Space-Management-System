package com.trinova.scms.dao;

import com.trinova.scms.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    private Connection conn;

    public RoomDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_active = 1 " +
                     "ORDER BY room_type, room_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) rooms.add(mapRow(rs));
        }
        return rooms;
    }

    public List<Room> getRoomsByType(String type) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms " +
                     "WHERE is_active = 1 AND room_type = ? " +
                     "ORDER BY room_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) rooms.add(mapRow(rs));
        }
        return rooms;
    }

    public Room findById(int roomId) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public int addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms " +
                     "(room_name, room_type, capacity, amenities, " +
                     "private_size, monthly_price, " +
                     "daily_price, hourly_price) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, room.getRoomName());
            ps.setString(2, room.getRoomType());
            ps.setInt(3, room.getCapacity());
            ps.setString(4, room.getAmenities());
            ps.setString(5, room.getPrivateSize());
            ps.setDouble(6, room.getMonthlyPrice());
            ps.setDouble(7, room.getDailyPrice());
            ps.setDouble(8, room.getHourlyPrice());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_name=?, room_type=?, " +
                     "capacity=?, amenities=?, private_size=?, " +
                     "monthly_price=?, daily_price=?, " +
                     "hourly_price=? WHERE room_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomName());
            ps.setString(2, room.getRoomType());
            ps.setInt(3, room.getCapacity());
            ps.setString(4, room.getAmenities());
            ps.setString(5, room.getPrivateSize());
            ps.setDouble(6, room.getMonthlyPrice());
            ps.setDouble(7, room.getDailyPrice());
            ps.setDouble(8, room.getHourlyPrice());
            ps.setInt(9, room.getRoomId());
            ps.executeUpdate();
        }
    }

    public void deleteRoom(int roomId) throws SQLException {
        String sql = "UPDATE rooms SET is_active = 0 " +
                     "WHERE room_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
        }
    }

    public boolean hasActiveBookings(int roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings " +
                     "WHERE room_id = ? " +
                     "AND status IN ('PENDING','CONFIRMED')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomName(rs.getString("room_name"));
        r.setRoomType(rs.getString("room_type"));
        r.setCapacity(rs.getInt("capacity"));
        r.setAmenities(rs.getString("amenities"));
        r.setPrivateSize(rs.getString("private_size"));
        r.setMonthlyPrice(rs.getDouble("monthly_price"));
        r.setDailyPrice(rs.getDouble("daily_price"));
        r.setHourlyPrice(rs.getDouble("hourly_price"));
        r.setActive(rs.getBoolean("is_active"));
        return r;
    }
}