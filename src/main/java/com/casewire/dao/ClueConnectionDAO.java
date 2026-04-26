package com.casewire.dao;

import com.casewire.model.ClueConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClueConnectionDAO {

    public List<ClueConnection> getConnectionsByCase(int caseId) {
        List<ClueConnection> list = new ArrayList<>();
        String sql = "SELECT id, case_id, evidence_id_a, evidence_id_b, relation_type, explanation FROM clue_connections WHERE case_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, caseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ClueConnection(
                        rs.getInt("id"),
                        rs.getInt("case_id"),
                        rs.getInt("evidence_id_a"),
                        rs.getInt("evidence_id_b"),
                        rs.getString("relation_type"),
                        rs.getString("explanation")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void saveDiscoveredConnection(int userId, int connectionId) {
        String sql = "INSERT IGNORE INTO player_discovered_connections (user_id, connection_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, connectionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to save discovered clue connection.", e);
        }
    }

    public Set<Integer> getDiscoveredConnectionIds(int userId) {
        Set<Integer> ids = new LinkedHashSet<>();
        String sql = "SELECT connection_id FROM player_discovered_connections WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("connection_id"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load discovered clue connections.", e);
        }
        return ids;
    }
}
