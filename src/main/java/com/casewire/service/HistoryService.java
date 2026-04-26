package com.casewire.service;

import com.casewire.dao.CaseHistoryDAO;
import com.casewire.model.CaseHistory;

import java.util.List;

public class HistoryService {

    private final CaseHistoryDAO caseHistoryDAO = new CaseHistoryDAO();

    public void saveCaseHistory(int userId, int caseId, int score, int timeTakenSeconds) {
        caseHistoryDAO.saveHistory(userId, caseId, score, timeTakenSeconds);
    }

    public List<CaseHistory> getUserHistory(int userId) {
        return caseHistoryDAO.getHistoryByUser(userId);
    }
}
