package com.casewire.dao;

import com.casewire.model.CaseFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CaseDAO {

    public List<CaseFile> getCasesByLevel(int levelId) {
        List<CaseFile> cases = new ArrayList<>();
        String sql = "SELECT id, level_id, title, description, intro FROM cases WHERE level_id = ? ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, levelId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cases.add(new CaseFile(
                        rs.getInt("id"),
                        rs.getInt("level_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("intro")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cases;
    }

    public CaseFile getCaseById(int caseId) {
        String sql = "SELECT id, level_id, title, description, intro FROM cases WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, caseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CaseFile(
                        rs.getInt("id"),
                        rs.getInt("level_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("intro")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CaseFile> getAllCases() {
        List<CaseFile> cases = new ArrayList<>();
        String sql = "SELECT id, level_id, title, description, intro FROM cases ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cases.add(new CaseFile(
                        rs.getInt("id"),
                        rs.getInt("level_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("intro")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cases;
    }
}
