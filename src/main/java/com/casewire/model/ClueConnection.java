package com.casewire.model;

public class ClueConnection {
    private int id;
    private int caseId;
    private int evidenceIdA;
    private int evidenceIdB;
    private String relationType;
    private String explanation;

    public ClueConnection(int id, int caseId, int evidenceIdA, int evidenceIdB,
                          String relationType, String explanation) {
        this.id = id;
        this.caseId = caseId;
        this.evidenceIdA = evidenceIdA;
        this.evidenceIdB = evidenceIdB;
        this.relationType = relationType;
        this.explanation = explanation;
    }

    public int getId() { return id; }
    public int getCaseId() { return caseId; }
    public int getEvidenceIdA() { return evidenceIdA; }
    public int getEvidenceIdB() { return evidenceIdB; }
    public String getRelationType() { return relationType; }
    public String getExplanation() { return explanation; }
}
