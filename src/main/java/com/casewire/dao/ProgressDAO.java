package com.casewire.dao;

import com.casewire.model.PlayerProgress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgressDAO {

    public List<PlayerProgress> getAllProgress(int userId) {
        List<PlayerProgress> list = new ArrayList<>();
        String sql = "SELECT user_id, case_id, is_unlocked, is_solved, score FROM player_progress WHERE user_id = ? ORDER BY case_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapProgress(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load user progress.", e);
        }
        return list;
    }

    public PlayerProgress getProgressByCase(int userId, int caseId) {
        String sql = "SELECT user_id, case_id, is_unlocked, is_solved, score FROM player_progress WHERE user_id = ? AND case_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, caseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapProgress(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load case progress.", e);
        }
        return null;
    }

    public void updateProgress(int userId, int caseId, boolean solved, int score) {
        String sql = "UPDATE player_progress SET is_solved = ?, score = ? WHERE user_id = ? AND case_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, solved);
            ps.setInt(2, score);
            ps.setInt(3, userId);
            ps.setInt(4, caseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to update user progress.", e);
        }
    }

    public void unlockCase(int userId, int caseId) {
        String sql = "UPDATE player_progress SET is_unlocked = TRUE WHERE user_id = ? AND case_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, caseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to unlock case for user.", e);
        }
    }

    public void initializeProgressForUser(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            initializeProgressForUser(userId, conn);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to initialize user progress.", e);
        }
    }

    public void initializeProgressForUser(int userId, Connection conn) throws SQLException {
        String sql = """
                INSERT INTO player_progress (user_id, case_id, is_unlocked, is_solved, score)
                SELECT ?, c.id,
                       CASE WHEN c.id = first_case.min_case_id THEN TRUE ELSE FALSE END,
                       FALSE,
                       0
                FROM cases c
                CROSS JOIN (SELECT MIN(id) AS min_case_id FROM cases) first_case
                ORDER BY c.id
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    private PlayerProgress mapProgress(ResultSet rs) throws SQLException {
        return new PlayerProgress(
                rs.getInt("user_id"),
                rs.getInt("case_id"),
                rs.getBoolean("is_unlocked"),
                rs.getBoolean("is_solved"),
                rs.getInt("score")
        );
    }
}
