package com.casewire.dao;

import com.casewire.model.CaseHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CaseHistoryDAO {

    public void saveHistory(int userId, int caseId, int score, int timeTakenSeconds) {
        String sql = """
                INSERT INTO case_history (user_id, case_id, score, time_taken_seconds)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, caseId);
            ps.setInt(3, score);
            ps.setInt(4, timeTakenSeconds);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to save case history.", e);
        }
    }

    public List<CaseHistory> getHistoryByUser(int userId) {
        List<CaseHistory> history = new ArrayList<>();
        String sql = """
                SELECT ch.id, ch.user_id, ch.case_id, c.title, ch.score, ch.time_taken_seconds, ch.solved_at
                FROM case_history ch
                JOIN cases c ON c.id = ch.case_id
                WHERE ch.user_id = ?
                ORDER BY ch.solved_at DESC, ch.id DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(new CaseHistory(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("case_id"),
                            rs.getString("title"),
                            rs.getInt("score"),
                            rs.getInt("time_taken_seconds"),
                            rs.getTimestamp("solved_at")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load user case history.", e);
        }
        return history;
    }
}
