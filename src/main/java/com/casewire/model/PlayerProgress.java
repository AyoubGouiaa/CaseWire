package com.casewire.model;

public class PlayerProgress {
    private int userId;
    private int caseId;
    private boolean unlocked;
    private boolean solved;
    private int score;

    public PlayerProgress(int userId, int caseId, boolean unlocked, boolean solved, int score) {
        this.userId = userId;
        this.caseId = caseId;
        this.unlocked = unlocked;
        this.solved = solved;
        this.score = score;
    }

    public int getUserId() { return userId; }
    public int getCaseId() { return caseId; }
    public boolean isUnlocked() { return unlocked; }
    public boolean isSolved() { return solved; }
    public int getScore() { return score; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    public void setSolved(boolean solved) { this.solved = solved; }
    public void setScore(int score) { this.score = score; }
}
