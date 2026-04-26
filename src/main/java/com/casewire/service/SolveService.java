package com.casewire.service;

import com.casewire.model.Solution;

import java.util.List;

public class SolveService {

    public int calculateScore(int chosenSuspectId,
                              String chosenMotive,
                              List<Integer> chosenEvidenceIds,
                              boolean discoveredConnection,
                              Solution solution) {
        int score = 0;

        if (chosenSuspectId == solution.getCorrectSuspectId()) {
            score += 50;
        }

        if (chosenMotive != null
                && chosenMotive.trim().equalsIgnoreCase(solution.getCorrectMotive().trim())) {
            score += 20;
        }

        int evidencePoints = 0;
        for (int evidenceId : chosenEvidenceIds) {
            if (solution.getRequiredEvidenceIds().contains(evidenceId)) {
                evidencePoints += 10;
            }
        }
        score += Math.min(evidencePoints, 20);

        if (discoveredConnection) {
            score += 10;
        }

        return Math.min(score, 100);
    }

    public String getVerdict(int score) {
        if (score >= 90) return "Perfect Solve";
        if (score >= 70) return "Solved";
        if (score >= 50) return "Partial Solve";
        return "Failed";
    }
}
