package com.casewire.dao;

import com.casewire.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, full_name, created_at FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load user by username.", e);
        }
        return null;
    }

    public User findById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            return findById(id, conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load user by id.", e);
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to check username availability.", e);
        }
    }

    public User createUser(String username, String hashedPassword, String fullName, Connection conn) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            if (fullName == null || fullName.isBlank()) {
                ps.setNull(3, java.sql.Types.VARCHAR);
            } else {
                ps.setString(3, fullName);
            }
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1), conn);
                }
            }
        }

        throw new SQLException("Failed to create user.");
    }

    public User login(String username, String hashedPassword) {
        String sql = "SELECT id, username, password, full_name, created_at FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to log in user.", e);
        }
        return null;
    }

    private User findById(int id, Connection conn) throws SQLException {
        String sql = "SELECT id, username, password, full_name, created_at FROM users WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getTimestamp("created_at")
        );
    }
}
