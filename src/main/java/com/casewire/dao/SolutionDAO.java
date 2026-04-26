package com.casewire.dao;

import com.casewire.model.Solution;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SolutionDAO {

    public Solution getSolutionByCase(int caseId) {
        String sql = "SELECT id, case_id, correct_suspect_id, correct_motive, explanation FROM solutions WHERE case_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, caseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int solutionId = rs.getInt("id");
                List<Integer> evidenceIds = getRequiredEvidenceIds(solutionId);
                return new Solution(
                        solutionId,
                        rs.getInt("case_id"),
                        rs.getInt("correct_suspect_id"),
                        rs.getString("correct_motive"),
                        rs.getString("explanation"),
                        evidenceIds
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Integer> getRequiredEvidenceIds(int solutionId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT evidence_id FROM solution_evidence WHERE solution_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, solutionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("evidence_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}
