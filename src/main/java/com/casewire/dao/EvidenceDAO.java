package com.casewire.dao;

import com.casewire.model.Evidence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvidenceDAO {

    public List<Evidence> getEvidenceByCase(int caseId) {
        List<Evidence> evidenceList = new ArrayList<>();
        String sql = "SELECT id, case_id, title, type, description, location, why_matters FROM evidence WHERE case_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, caseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                evidenceList.add(new Evidence(
                        rs.getInt("id"),
                        rs.getInt("case_id"),
                        rs.getString("title"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("location"),
                        rs.getString("why_matters")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evidenceList;
    }
}
