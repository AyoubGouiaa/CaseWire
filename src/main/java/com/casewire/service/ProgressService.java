package com.casewire.service;

import com.casewire.dao.CaseDAO;
import com.casewire.dao.ProgressDAO;
import com.casewire.model.CaseFile;
import com.casewire.model.PlayerProgress;

import java.util.List;

public class ProgressService {

    private final ProgressDAO progressDAO = new ProgressDAO();
    private final CaseDAO caseDAO = new CaseDAO();

    public void saveResult(int userId, int caseId, int score) {
        boolean solved = score >= 50;
        progressDAO.updateProgress(userId, caseId, solved, score);

        if (solved) {
            unlockNextCase(userId, caseId);
        }
    }

    public void unlockNextCase(int userId, int solvedCaseId) {
        List<CaseFile> allCases = caseDAO.getAllCases();

        for (int i = 0; i < allCases.size() - 1; i++) {
            if (allCases.get(i).getId() == solvedCaseId) {
                int nextCaseId = allCases.get(i + 1).getId();
                CaseFile nextCase = allCases.get(i + 1);
                CaseFile currentCase = allCases.get(i);

                boolean canUnlock = true;
                if (nextCase.getLevelId() != currentCase.getLevelId()) {
                    canUnlock = allSolvedInLevel(userId, currentCase.getLevelId());
                }

                if (canUnlock) {
                    progressDAO.unlockCase(userId, nextCaseId);
                }
                break;
            }
        }
    }

    public boolean allSolvedInLevel(int userId, int levelId) {
        List<CaseFile> allCases = caseDAO.getAllCases();
        List<PlayerProgress> allProgress = progressDAO.getAllProgress(userId);

        for (CaseFile c : allCases) {
            if (c.getLevelId() == levelId) {
                for (PlayerProgress p : allProgress) {
                    if (p.getCaseId() == c.getId() && !p.isSolved()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public PlayerProgress getProgress(int userId, int caseId) { return progressDAO.getProgressByCase(userId, caseId); }
    public List<PlayerProgress> getAllProgress(int userId) { return progressDAO.getAllProgress(userId); }
}
