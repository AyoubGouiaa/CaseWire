package com.casewire.model;

import java.sql.Timestamp;

public class CaseHistory {

    private final int id;
    private final int userId;
    private final int caseId;
    private final String caseTitle;
    private final int score;
    private final int timeTakenSeconds;
    private final Timestamp solvedAt;

    public CaseHistory(int id, int userId, int caseId, String caseTitle,
                       int score, int timeTakenSeconds, Timestamp solvedAt) {
        this.id = id;
        this.userId = userId;
        this.caseId = caseId;
        this.caseTitle = caseTitle;
        this.score = score;
        this.timeTakenSeconds = timeTakenSeconds;
        this.solvedAt = solvedAt;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getCaseId() {
        return caseId;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public int getScore() {
        return score;
    }

    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public Timestamp getSolvedAt() {
        return solvedAt;
    }
}
