package com.trinova.scms.dao;

import com.trinova.scms.model.Member;
import java.sql.*;

public class MemberDAO {

    private Connection conn;

    public MemberDAO() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public int register(Member m) throws SQLException {
        String sql = "INSERT INTO members " +
                     "(full_name, email, password_hash, role) " +
                     "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getFullName());
            ps.setString(2, m.getEmail());
            ps.setString(3, m.getPasswordHash());
            ps.setString(4, m.getRole());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public Member findByEmail(String email) throws SQLException {
        String sql = "SELECT m.*, sp.plan_type " +
                     "FROM members m " +
                     "LEFT JOIN subscription_plans sp " +
                     "ON m.plan_id = sp.plan_id " +
                     "WHERE m.email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public Member findById(int memberId) throws SQLException {
        String sql = "SELECT m.*, sp.plan_type " +
                     "FROM members m " +
                     "LEFT JOIN subscription_plans sp " +
                     "ON m.plan_id = sp.plan_id " +
                     "WHERE m.member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public void recordFailedAttempt(String email)
            throws SQLException {
        String sql = "UPDATE members SET " +
                     "failed_attempts = failed_attempts + 1, " +
                     "is_locked = CASE " +
                     "WHEN failed_attempts + 1 >= 5 " +
                     "THEN 1 ELSE 0 END " +
                     "WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        }
    }

    public void resetFailedAttempts(String email)
            throws SQLException {
        String sql = "UPDATE members SET " +
                     "failed_attempts = 0, is_locked = 0 " +
                     "WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        }
    }

    public void updateProfile(Member m) throws SQLException {
        String sql = "UPDATE members SET " +
                     "full_name = ?, phone = ?, " +
                     "bio = ?, profile_photo = ? " +
                     "WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getFullName());
            ps.setString(2, m.getPhone());
            ps.setString(3, m.getBio());
            ps.setString(4, m.getProfilePhoto());
            ps.setInt(5, m.getMemberId());
            ps.executeUpdate();
        }
    }

    public void updatePassword(String email,
                                String newHash)
            throws SQLException {
        String sql = "UPDATE members SET " +
                     "password_hash = ? WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, email);
            ps.executeUpdate();
        }
    }

    public boolean emailExists(String email)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM members " +
                     "WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public java.util.List<Member> getAllMembers()
            throws SQLException {
        java.util.List<Member> list = new java.util.ArrayList<>();
        String sql = "SELECT m.*, sp.plan_type " +
                     "FROM members m " +
                     "LEFT JOIN subscription_plans sp " +
                     "ON m.plan_id = sp.plan_id " +
                     "ORDER BY m.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setMemberId(rs.getInt("member_id"));
        m.setFullName(rs.getString("full_name"));
        m.setEmail(rs.getString("email"));
        m.setPasswordHash(rs.getString("password_hash"));
        m.setRole(rs.getString("role"));
        m.setPhone(rs.getString("phone"));
        m.setBio(rs.getString("bio"));
        m.setProfilePhoto(rs.getString("profile_photo"));
        m.setLocked(rs.getBoolean("is_locked"));
        m.setFailedAttempts(rs.getInt("failed_attempts"));

        // Plan details — critical for cost calculation
        int planId = rs.getInt("plan_id");
        m.setPlanId(planId);

        String planType = rs.getString("plan_type");
        m.setPlanType(planType);

        Date expiry = rs.getDate("plan_expiry");
        if (expiry != null)
            m.setPlanExpiry(expiry.toLocalDate());

        return m;
    }
}