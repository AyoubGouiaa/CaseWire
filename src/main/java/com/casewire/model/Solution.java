package com.casewire.model;

import java.util.List;

public class Solution {
    private int id;
    private int caseId;
    private int correctSuspectId;
    private String correctMotive;
    private String explanation;
    private List<Integer> requiredEvidenceIds;

    public Solution(int id, int caseId, int correctSuspectId, String correctMotive,
                    String explanation, List<Integer> requiredEvidenceIds) {
        this.id = id;
        this.caseId = caseId;
        this.correctSuspectId = correctSuspectId;
        this.correctMotive = correctMotive;
        this.explanation = explanation;
        this.requiredEvidenceIds = requiredEvidenceIds;
    }

    public int getId() { return id; }
    public int getCaseId() { return caseId; }
    public int getCorrectSuspectId() { return correctSuspectId; }
    public String getCorrectMotive() { return correctMotive; }
    public String getExplanation() { return explanation; }
    public List<Integer> getRequiredEvidenceIds() { return requiredEvidenceIds; }
}
