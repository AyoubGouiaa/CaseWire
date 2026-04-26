package com.casewire.dao;

import com.casewire.model.Suspect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SuspectDAO {

    public List<Suspect> getSuspectsByCase(int caseId) {
        List<Suspect> suspects = new ArrayList<>();
        String sql = "SELECT id, case_id, name, role, alibi, description FROM suspects WHERE case_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, caseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                suspects.add(new Suspect(
                        rs.getInt("id"),
                        rs.getInt("case_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("alibi"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suspects;
    }
}
